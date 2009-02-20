/**
 * Copyright 2008 - 2009 Pro-Netics S.P.A.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.pronetics.madstore.server.jaxrs.atom.search.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.server.HttpConstants;
import it.pronetics.madstore.server.jaxrs.atom.AbstractResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.pub.impl.DefaultServiceResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.search.SearchDescriptionResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceUriFor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.apache.abdera.ext.opensearch.model.Url;
import org.apache.abdera.factory.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

/**
 * {@link it.pronetics.madstore.server.jaxrs.atom.search.SearchDescriptionResourceHandler} implementation based on
 * JBoss Resteasy and Abdera atom model.
 *
 * @author Sergio Bossa
 */
@Path("/")
public class DefaultSearchDescriptionResourceHandler extends AbstractResourceHandler implements SearchDescriptionResourceHandler<OpenSearchDescription> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceResourceHandler.class);
    private String shortName;
    private String description;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @ResourceUriFor(resource = ResourceName.SEARCH_DESCRIPTION)
    @GET
    @Path("/search")
    @Produces(AtomConstants.OPENSEARCH_DESCRIPTION_MEDIA_TYPE)
    public OpenSearchDescription getSearchDescription() {
        try {
            Factory abderaFactory = Abdera.getInstance().getFactory();
            OpenSearchDescription openSearchDescription = abderaFactory.<OpenSearchDescription>newExtensionElement(OpenSearchConstants.OPENSEARCH_DESCRIPTION);
            openSearchDescription.setShortName(shortName);
            openSearchDescription.setDescription(description);
            configureOpenSearchUrls(openSearchDescription);
            return openSearchDescription;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new WebApplicationException(Response.serverError().build());
        }
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void configureOpenSearchUrls(OpenSearchDescription openSearchDescription) {
        List<Element> collectionElements = collectionRepository.readCollections();
        List<Url> urls = new ArrayList<Url>(collectionElements.size());
        for (Element collectionElement : collectionElements) {
            String collectionKey = collectionElement.getAttribute(AtomConstants.ATOM_KEY);
            URL baseUrl = resourceResolver.resolveResourceUriFor(
                    ResourceName.COLLECTION_SEARCH,
                    uriInfo.getBaseUri().toString(),
                    collectionKey);
            Url osUrl = createOpenSearchUrlForFullTextSearch(baseUrl);
            urls.add(osUrl);
        }
        openSearchDescription.addUrls(urls.toArray(new Url[urls.size()]));
    }

    private Url createOpenSearchUrlForFullTextSearch(URL baseUrl) {
        StringBuilder urlTemplate = new StringBuilder(baseUrl.toString());
        urlTemplate.append("?");
        addParameterToOpenSearchUrl(HttpConstants.TERMS_PARAMETER, OpenSearchConstants.QUERY_SEARCHTERMS_LN, urlTemplate, false);
        urlTemplate.append("&");
        addParameterToOpenSearchUrl(HttpConstants.MAX_PARAMETER, OpenSearchConstants.QUERY_COUNT_LN, urlTemplate, true);
        urlTemplate.append("&");
        addParameterToOpenSearchUrl(HttpConstants.PAGE_PARAMETER, OpenSearchConstants.QUERY_STARTPAGE_LN, urlTemplate, true);
        return getOpenSearchUrlFromTemplate(urlTemplate);
    }

    private void addParameterToOpenSearchUrl(String parameterName, String parameterTemplate, StringBuilder urlTemplate, boolean optional) {
        urlTemplate.append(parameterName).append("=").append("{").append(parameterTemplate);
        if (optional) {
            urlTemplate.append("?");
        }
        urlTemplate.append("}");
    }

    private Url getOpenSearchUrlFromTemplate(StringBuilder urlTemplate) {
        Factory abderaFactory = Abdera.getInstance().getFactory();
        Url url = abderaFactory.<Url>newExtensionElement(OpenSearchConstants.URL);
        url.setTemplate(urlTemplate.toString());
        url.setType(AtomConstants.ATOM_MEDIA_TYPE);
        return url;
    }
}
