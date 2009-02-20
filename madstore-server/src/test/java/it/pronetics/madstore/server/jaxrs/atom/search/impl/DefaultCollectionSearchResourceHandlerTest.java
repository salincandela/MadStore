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
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.util.PagingList;
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
import org.apache.abdera.model.Feed;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import static org.easymock.EasyMock.*;

public class DefaultCollectionSearchResourceHandlerTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultCollectionSearchResourceHandlerTest.class);

    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("atom", AtomConstants.ATOM_NS);
        ns.put("os", OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public void testGetCollectionSearchResourceWithNoNextLink() throws Exception {
        CollectionRepository collectionRepository = null;
        EntryRepository entryRepository = getEntryRepositoryMockWithNoNextLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        String title = "search";
        String terms = "term1 term2";
        DefaultCollectionSearchResourceHandler handler = new DefaultCollectionSearchResourceHandler();
        replay(entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setSearchTitle(title);
        handler.setSearchTerms(terms);
        handler.setPageNumberOfEntries(1);
        handler.setMaxNumberOfEntries(10);
        Feed feed = handler.getCollectionSearchResource();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collectionSearch:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("search", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("10", "/atom:feed/os:itemsPerPage", content);

        verify(entryRepository, uriInfo);
    }

    public void testGetCollectionSearchResourceWithNextLink() throws Exception {
        CollectionRepository collectionRepository = null;
        EntryRepository entryRepository = getEntryRepositoryMockWithNextLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        String title = "search";
        String terms = "term1 term2";
        DefaultCollectionSearchResourceHandler handler = new DefaultCollectionSearchResourceHandler();
        replay(entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setSearchTitle(title);
        handler.setSearchTerms(terms);
        handler.setPageNumberOfEntries(1);
        handler.setMaxNumberOfEntries(1);
        Feed feed = handler.getCollectionSearchResource();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collectionSearch:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("search", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/search/entries?title=search&terms=term1%20term2&page=2&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("2", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:itemsPerPage", content);

        verify(entryRepository, uriInfo);
    }

    public void testGetCollectionSearchResourceWithPrevLink() throws Exception {
        CollectionRepository collectionRepository = null;
        EntryRepository entryRepository = getEntryRepositoryMockWithPrevLink();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        String title = "search";
        String terms = "term1 term2";
        DefaultCollectionSearchResourceHandler handler = new DefaultCollectionSearchResourceHandler();
        replay(entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setSearchTitle(title);
        handler.setSearchTerms(terms);
        handler.setPageNumberOfEntries(2);
        handler.setMaxNumberOfEntries(1);
        Feed feed = handler.getCollectionSearchResource();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collectionSearch:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("search", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:feed/atom:entry[1]/atom:id", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathEvaluatesTo("http://localhost/search/entries?title=search&terms=term1%20term2&page=1&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathEvaluatesTo("2", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("2", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:itemsPerPage", content);

        verify(entryRepository, uriInfo);
    }

    public void testGetCollectionSearchResourceOutOfBounds() throws Exception {
        CollectionRepository collectionRepository = null;
        EntryRepository entryRepository = getEntryRepositoryMockOutOfBounds();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        String title = "search";
        String terms = "term1 term2";
        DefaultCollectionSearchResourceHandler handler = new DefaultCollectionSearchResourceHandler();
        replay(entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setSearchTitle(title);
        handler.setSearchTerms(terms);
        handler.setPageNumberOfEntries(2);
        handler.setMaxNumberOfEntries(1);
        Feed feed = handler.getCollectionSearchResource();
        StringWriter writer = new StringWriter();
        feed.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collectionSearch:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("search", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:entry", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathNotExists("/atom:feed/os:totalResults", content);
        assertXpathNotExists("/atom:feed/os:startIndex", content);
        assertXpathNotExists("/atom:feed/os:itemsPerPage", content);

        verify(entryRepository, uriInfo);
    }

    private EntryRepository getEntryRepositoryMockWithNoNextLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.findEntries("entries", Arrays.asList("term1", "term2"), 0, 10)).andReturn(new PagingList<Element>(Arrays.asList(getEntryElement()), 0, 10, 1)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockWithNextLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.findEntries("entries", Arrays.asList("term1", "term2"), 0, 1)).andReturn(new PagingList<Element>(Arrays.asList(getEntryElement()), 0, 1, 2)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockWithPrevLink() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.findEntries("entries", Arrays.asList("term1", "term2"), 1, 1)).andReturn(new PagingList<Element>(Arrays.asList(getEntryElement()), 1, 1, 2)).anyTimes();
        return entryRepository;
    }

    private EntryRepository getEntryRepositoryMockOutOfBounds() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.findEntries("entries", Arrays.asList("term1", "term2"), 1, 1)).andReturn(new PagingList<Element>(Arrays.<Element>asList(), 1, 1, 1)).anyTimes();
        return entryRepository;
    }

    private UriInfo getUriInfoMock() throws Exception {
        UriInfo uriInfo = createMock(UriInfo.class);
        expect(uriInfo.getBaseUri()).andReturn(new URL("http://localhost").toURI()).anyTimes();
        return uriInfo;
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
