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
package it.pronetics.madstore.server.jaxrs.atom.pub.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.util.PagingList;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver;
import it.pronetics.madstore.server.jaxrs.atom.search.impl.DefaultCollectionSearchResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.search.impl.DefaultSearchDescriptionResourceHandler;
import it.pronetics.madstore.server.test.util.Utils;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import org.apache.abdera.model.Feed;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import static org.easymock.EasyMock.*;

public class DefaultCollectionResourceHandlerTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultCollectionResourceHandlerTest.class);


    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("atom", AtomConstants.ATOM_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public void testGetCollectionResourceHasNoNextLink() throws Exception {
        CollectionRepository collectionRepository = getCollectionRepositoryMock();
        EntryRepository entryRepository = getEntryRepositoryMockWithNoNextLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        DefaultCollectionResourceHandler handler = new DefaultCollectionResourceHandler();
        replay(collectionRepository, entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setPageNumberOfEntries(1);
        handler.setMaxNumberOfEntries(10);
        Feed feed = (Feed) handler.getCollectionResource().getEntity();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/entries", "/atom:feed/atom:link[@rel='self']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        verify(collectionRepository, entryRepository, uriInfo);
    }

    public void testGetCollectionResourceHasNextLink() throws Exception {
        CollectionRepository collectionRepository = getCollectionRepositoryMock();
        EntryRepository entryRepository = getEntryRepositoryMockWithNextLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        DefaultCollectionResourceHandler handler = new DefaultCollectionResourceHandler();
        replay(collectionRepository, entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setPageNumberOfEntries(1);
        handler.setMaxNumberOfEntries(1);
        Feed feed = (Feed) handler.getCollectionResource().getEntity();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/entries", "/atom:feed/atom:link[@rel='self']/@href", content);
        assertXpathEvaluatesTo("http://localhost/entries?page=2&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        verify(collectionRepository, entryRepository, uriInfo);
    }

    public void testGetCollectionResourceHasPrevLink() throws Exception {
        CollectionRepository collectionRepository = getCollectionRepositoryMock();
        EntryRepository entryRepository = getEntryRepositoryMockWithPrevLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        DefaultCollectionResourceHandler handler = new DefaultCollectionResourceHandler();
        replay(collectionRepository, entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setPageNumberOfEntries(2);
        handler.setMaxNumberOfEntries(1);
        Feed feed = (Feed) handler.getCollectionResource().getEntity();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/entries", "/atom:feed/atom:link[@rel='self']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathEvaluatesTo("http://localhost/entries?page=1&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        verify(collectionRepository, entryRepository, uriInfo);
    }

    public void testGetCollectionResourceOutOfBounds() throws Exception {
        CollectionRepository collectionRepository = getCollectionRepositoryMock();
        EntryRepository entryRepository = getEntryRepositoryMockOutOfBounds();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        DefaultCollectionResourceHandler handler = new DefaultCollectionResourceHandler();
        replay(collectionRepository, entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setPageNumberOfEntries(2);
        handler.setMaxNumberOfEntries(1);
        Feed feed = (Feed) handler.getCollectionResource().getEntity();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/entries", "/atom:feed/atom:link[@rel='self']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathNotExists("/atom:feed/atom:entry", content);
        verify(collectionRepository, entryRepository, uriInfo);
    }

    private CollectionRepository getCollectionRepositoryMock() throws Exception {
        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        expect(collectionRepository.read("entries")).andReturn(getCollectionElement()).anyTimes();
        return collectionRepository;
    }

    private EntryRepository getEntryRepositoryMockWithNoNextLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.readEntries("entries", 0, 10)).andReturn(new PagingList<Element>(Arrays.<Element>asList(new Element[]{getEntryElement()}), 0, 10, 1)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockWithNextLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.readEntries("entries", 0, 1)).andReturn(new PagingList<Element>(Arrays.<Element>asList(new Element[]{getEntryElement()}), 0, 1, 2)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockWithPrevLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.readEntries("entries", 1, 1)).andReturn(new PagingList<Element>(Arrays.<Element>asList(new Element[]{getEntryElement()}), 1, 1, 2)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockOutOfBounds() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.readEntries("entries", 1, 1)).andReturn(new PagingList<Element>(Arrays.<Element>asList(), 1, 1, 1)).anyTimes();
        return entryRepository;
    }

    private UriInfo getUriInfoMock() throws Exception {
        UriInfo uriInfo = createMock(UriInfo.class);
        expect(uriInfo.getBaseUri()).andReturn(new URL("http://localhost").toURI()).anyTimes();
        return uriInfo;
    }

    private Element getCollectionElement() throws Exception {
        return Utils.getDoc("serverCollection.xml").getDocumentElement();
    }

    private Element getEntryElement() throws Exception {
        return Utils.getDoc("serverEntry.xml").getDocumentElement();
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
