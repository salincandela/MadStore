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
package it.pronetics.madstore.crawler.publisher.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.test.util.Utils;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import junit.framework.TestCase;
import org.easymock.Capture;
import org.w3c.dom.Element;
import static org.easymock.EasyMock.*;

public class AtomPublisherImplTest extends TestCase {

    private static final String PUBLISHER_IMPL_TEST_FEED = "publisherImplTestFeed.xml";
    private static final String PUBLISHER_IMPL_TEST_FEED_WITH_NO_KEYS = "publisherImplTestFeedWithNoKeys.xml";
    private static final String PUBLISHER_IMPL_TEST_FEED_WITH_NO_UPDATED_DATES = "publisherImplTestFeedWithNoUpdatedDates.xml";
    private static final String COLLECTION_KEY = "feedKeyTest";
    private static final String ENTRY_KEY_1 = "entryKeyTest1";
    private static final String ENTRY_KEY_2 = "entryKeyTest2";
    private static final String ENTRY_KEY_3 = "entryKeyTest3";

    public void testSuccessfulPublishNewEntries() throws Exception {
        Page inputPage = new Page(new Link("test link"), new String(Utils.getUtf8BytesFromFile(PUBLISHER_IMPL_TEST_FEED)));

        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        EntryRepository entryRepository = createMock(EntryRepository.class);

        expect(collectionRepository.putIfAbsent(isA(Element.class))).andReturn(COLLECTION_KEY).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_1).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_2).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_3).once();

        AtomPublisherImpl publisherImpl = new AtomPublisherImpl();
        publisherImpl.setCollectionRepository(collectionRepository);
        publisherImpl.setEntryRepository(entryRepository);

        replay(collectionRepository, entryRepository);
        publisherImpl.publish(inputPage);
        verify(collectionRepository, entryRepository);
    }

    public void testSuccessfulPublishUpdateEntries() throws Exception {
        Page inputPage = new Page(new Link("test link"), new String(Utils.getUtf8BytesFromFile(PUBLISHER_IMPL_TEST_FEED)));

        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        EntryRepository entryRepository = createMock(EntryRepository.class);

        expect(collectionRepository.putIfAbsent(isA(Element.class))).andReturn(COLLECTION_KEY).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_1).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(null).once();
        expect(entryRepository.updateIfNewer(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_2).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), isA(Element.class))).andReturn(ENTRY_KEY_3).once();

        AtomPublisherImpl publisherImpl = new AtomPublisherImpl();
        publisherImpl.setCollectionRepository(collectionRepository);
        publisherImpl.setEntryRepository(entryRepository);

        replay(collectionRepository, entryRepository);
        publisherImpl.publish(inputPage);
        verify(collectionRepository, entryRepository);
    }
    
    public void testSuccessfulPublishFeedWithNoKeys() throws Exception {
        Page inputPage = new Page(new Link("http://www.acme.org/test/link.html"), new String(Utils.getUtf8BytesFromFile(PUBLISHER_IMPL_TEST_FEED_WITH_NO_KEYS)));

        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        EntryRepository entryRepository = createMock(EntryRepository.class);

        Capture<Element> collectionCapture = new Capture<Element>();
        Capture<Element> entryCapture = new Capture<Element>();
        expect(collectionRepository.putIfAbsent(capture(collectionCapture))).andReturn("www_acme_org").once();
        expect(entryRepository.putIfAbsent(eq("www_acme_org"), capture(entryCapture))).andReturn("").once();

        AtomPublisherImpl publisherImpl = new AtomPublisherImpl();
        publisherImpl.setCollectionRepository(collectionRepository);
        publisherImpl.setEntryRepository(entryRepository);

        replay(collectionRepository, entryRepository);
        publisherImpl.publish(inputPage);
        verify(collectionRepository, entryRepository);

        assertEquals("www_acme_org", collectionCapture.getValue().getAttribute(AtomConstants.ATOM_KEY));
        assertFalse(entryCapture.getValue().getAttribute(AtomConstants.ATOM_KEY).equals(""));
    }

    public void testSuccessfulPublishEntryWithNoUpdatedDates() throws Exception {
        Page inputPage = new Page(new Link("test link"), new String(Utils.getUtf8BytesFromFile(PUBLISHER_IMPL_TEST_FEED_WITH_NO_UPDATED_DATES)));

        CollectionRepository collectionRepository = createMock(CollectionRepository.class);
        EntryRepository entryRepository = createMock(EntryRepository.class);

        Capture<Element> entryCapture = new Capture<Element>();
        expect(collectionRepository.putIfAbsent(isA(Element.class))).andReturn(COLLECTION_KEY).once();
        expect(entryRepository.putIfAbsent(eq(COLLECTION_KEY), capture(entryCapture))).andReturn(ENTRY_KEY_1).once();

        AtomPublisherImpl publisherImpl = new AtomPublisherImpl();
        publisherImpl.setCollectionRepository(collectionRepository);
        publisherImpl.setEntryRepository(entryRepository);

        replay(collectionRepository, entryRepository);
        publisherImpl.publish(inputPage);
        verify(collectionRepository, entryRepository);

        assertFalse(entryCapture.getValue().getElementsByTagName(AtomConstants.ATOM_ENTRY_UPDATED).item(0).getTextContent().equals(""));
    }
}
