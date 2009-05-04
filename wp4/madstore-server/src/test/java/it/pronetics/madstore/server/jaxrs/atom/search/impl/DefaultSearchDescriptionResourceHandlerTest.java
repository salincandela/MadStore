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

import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.server.jaxrs.atom.pub.impl.DefaultCollectionResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.pub.impl.DefaultEntryResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.pub.impl.DefaultServiceResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver;
import it.pronetics.madstore.server.test.util.Utils;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import static org.easymock.EasyMock.*;

public class DefaultSearchDescriptionResourceHandlerTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultSearchDescriptionResourceHandlerTest.class);

    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("os", OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public void testGetSearchDescriptionResource() throws Exception {
        CollectionRepository collectionRepository = getCollectionRepositoryMock();
        EntryRepository entryRepository = null;
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String osDescription = "description";
        String osShortName = "shortName";
        DefaultSearchDescriptionResourceHandler handler = new DefaultSearchDescriptionResourceHandler();
        replay(collectionRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setDescription(osDescription);
        handler.setShortName(osShortName);
        OpenSearchDescription description = (OpenSearchDescription) handler.getSearchDescription().getEntity();
        StringWriter writer = new StringWriter();
        description.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("description", "/os:OpenSearchDescription/os:Description", content);
        assertXpathEvaluatesTo("shortName", "/os:OpenSearchDescription/os:ShortName", content);
        assertXpathEvaluatesTo("application/atom+xml", "/os:OpenSearchDescription/os:Url/@type", content);
        assertXpathEvaluatesTo("http://localhost/search/entries?terms={searchTerms}&max={count?}&page={startPage?}",
                "/os:OpenSearchDescription/os:Url/@template", content);
        verify(collectionRepository, uriInfo);
    }

    private CollectionRepository getCollectionRepositoryMock() throws Exception {
        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        expect(collectionRepository.readCollections()).andReturn(Arrays.asList(getCollectionElement())).anyTimes();
        return collectionRepository;
    }

    private UriInfo getUriInfoMock() throws Exception {
        UriInfo uriInfo = createMock(UriInfo.class);
        expect(uriInfo.getBaseUri()).andReturn(new URL("http://localhost").toURI()).anyTimes();
        return uriInfo;
    }

    private Element getCollectionElement() throws Exception {
        return Utils.getDoc("serverCollection.xml").getDocumentElement();
    }

    private ResourceResolver makeResourceResolver() {
        Map<String, Class> resourceClasses = new HashMap<String, Class>();
        resourceClasses.put(ResourceName.COLLECTION, DefaultCollectionResourceHandler.class);
        resourceClasses.put(ResourceName.COLLECTION_SEARCH, DefaultCollectionSearchResourceHandler.class);
        resourceClasses.put(ResourceName.ENTRY, DefaultEntryResourceHandler.class);
        resourceClasses.put(ResourceName.SEARCH_DESCRIPTION, DefaultSearchDescriptionResourceHandler.class);
        resourceClasses.put(ResourceName.SERVICE, DefaultServiceResourceHandler.class);
        return new ResourceResolver(resourceClasses);
    }
}
