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
package it.pronetics.madstore.server.jaxrs.atom;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.util.PagingList;
import it.pronetics.madstore.server.abdera.util.AbderaHelper;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Base abstract implementation of {@link ResourceHandler}.
 * 
 * @author Sergio Bossa
 */
public abstract class AbstractResourceHandler implements ResourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceHandler.class);
    protected CollectionRepository collectionRepository;
    protected EntryRepository entryRepository;
    protected ResourceResolver resourceResolver;
    protected UriInfo uriInfo;

    public CollectionRepository getCollectionRepository() {
        return collectionRepository;
    }

    public void setCollectionRepository(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public EntryRepository getEntryRepository() {
        return entryRepository;
    }

    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public void setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    protected final Collection readCollectionFromRepository(String collectionKey) {
        Element collectionElement = collectionRepository.read(collectionKey);
        Collection collectionModel = null;
        if (collectionElement != null) {
            try {
                collectionModel = AbderaHelper.getCollectionFromDom(collectionElement);
                URL url = resourceResolver.resolveResourceUriFor(ResourceName.COLLECTION, uriInfo.getBaseUri().toString(), collectionKey);
                collectionModel.setHref(url.toString());
            } catch (Exception ex) {
                LOG.warn("Error converting collection element: {}", collectionElement.toString());
            }
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return collectionModel;
    }

    protected final List<Collection> readCollectionsFromRepository() {
        List<Element> collectionElements = collectionRepository.readCollections();
        List<Collection> collectionModels = new ArrayList<Collection>(collectionElements.size());
        for (Element collectionElement : collectionElements) {
            try {
                Collection appCollection = AbderaHelper.getCollectionFromDom(collectionElement);
                String collectionKey = appCollection.getAttributeValue(AtomConstants.ATOM_KEY);
                URL url = resourceResolver.resolveResourceUriFor(ResourceName.COLLECTION, uriInfo.getBaseUri().toString(), collectionKey);
                appCollection.setHref(url.toString());
                collectionModels.add(appCollection);
            } catch (Exception ex) {
                LOG.warn("Error converting collection element: {}", collectionElement.toString());
            }
        }
        return collectionModels;
    }

    protected final Entry readEntryFromRepository(String collectionKey, String entryKey) throws Exception {
        Element entryElement = entryRepository.read(collectionKey, entryKey);
        if (entryElement != null) {
            Entry entryModel = AbderaHelper.getEntryFromDom(entryElement);
            configureEntry(entryModel, collectionKey, entryKey);
            return entryModel;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    protected final PagingList<Entry> readEntriesFromRepository(String collectionKey, int offset, int max) throws Exception {
        PagingList<Element> entryElements = entryRepository.readEntries(collectionKey, offset, max);
        PagingList<Entry> entryModels = new PagingList<Entry>(
                new ArrayList<Entry>(entryElements.size()),
                entryElements.getOffset(),
                entryElements.getMax(),
                entryElements.getTotal());
        for (Element entryElement : entryElements) {
            Entry entry = AbderaHelper.getEntryFromDom(entryElement);
            String entryKey = entry.getAttributeValue(AtomConstants.ATOM_KEY);
            configureEntry(entry, collectionKey, entryKey);
            entryModels.add(entry);
        }
        return entryModels;
    }

    protected final PagingList<Entry> findEntriesFromRepository(String collectionKey, List<String> terms, int offset, int max) throws Exception {
        PagingList<Element> entryElements = entryRepository.findEntries(collectionKey, terms, offset, max);
        PagingList<Entry> entryModels = new PagingList<Entry>(
                new ArrayList<Entry>(entryElements.size()),
                entryElements.getOffset(),
                entryElements.getMax(),
                entryElements.getTotal());
        for (Element entryElement : entryElements) {
            Entry entry = AbderaHelper.getEntryFromDom(entryElement);
            String entryKey = entry.getAttributeValue(AtomConstants.ATOM_KEY);
            configureEntry(entry, collectionKey, entryKey);
            entryModels.add(entry);
        }
        return entryModels;
    }

    private void configureEntry(Entry entry, String collectionKey, String entryKey) {
        URL selfUrl = resourceResolver.resolveResourceUriFor(
                ResourceName.ENTRY,
                uriInfo.getBaseUri().toString(),
                collectionKey, entryKey);
        String id = resourceResolver.resolveResourceIdFor(uriInfo.getBaseUri().toString(),
                ResourceName.ENTRY,
                collectionKey, entryKey);

        entry.setId(id);
        entry.addLink(selfUrl.toString(), "self");
        entry.addLink(selfUrl.toString(), "edit");
    }
}
