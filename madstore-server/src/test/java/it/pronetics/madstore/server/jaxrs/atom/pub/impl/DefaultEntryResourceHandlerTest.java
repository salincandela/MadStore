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
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver;
import it.pronetics.madstore.server.jaxrs.atom.search.impl.DefaultCollectionSearchResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.search.impl.DefaultSearchDescriptionResourceHandler;
import it.pronetics.madstore.server.test.util.Utils;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import org.apache.abdera.model.Entry;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import static org.easymock.EasyMock.*;

public class DefaultEntryResourceHandlerTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultEntryResourceHandlerTest.class);

    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("atom", AtomConstants.ATOM_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public void testGetEntryResource() throws Exception {
        CollectionRepository collectionRepository = null;
        EntryRepository entryRepository = getEntryRepositoryMock();
        UriInfo uriInfo = getUriInfoMock();
        ResourceResolver resolver = makeResourceResolver();
        String collectionKey = "entries";
        String entryKey = "entry1";
        DefaultEntryResourceHandler handler = new DefaultEntryResourceHandler();
        replay(entryRepository, uriInfo);
        handler.setCollectionRepository(collectionRepository);
        handler.setEntryRepository(entryRepository);
        handler.setUriInfo(uriInfo);
        handler.setResourceResolver(resolver);
        handler.setCollectionKey(collectionKey);
        handler.setEntryKey(entryKey);
        Entry entry = handler.getEntryResource();
        StringWriter writer = new StringWriter();
        entry.writeTo(writer);
        String content = writer.toString();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:entry/atom:id", content);
        assertXpathEvaluatesTo("http://localhost/entries/entry1", "/atom:entry/atom:link[@rel=\"self\"]/@href", content);
        assertXpathEvaluatesTo("http://localhost/entries/entry1", "/atom:entry/atom:link[@rel=\"edit\"]/@href", content);
        verify(entryRepository, uriInfo);
    }

    private EntryRepository getEntryRepositoryMock() throws Exception {
        EntryRepository entryRepository = createMock(EntryRepository.class);
        expect(entryRepository.read("entries", "entry1")).andReturn(getEntryElement()).anyTimes();
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
