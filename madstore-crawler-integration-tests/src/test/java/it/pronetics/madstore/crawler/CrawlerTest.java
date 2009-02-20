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
package it.pronetics.madstore.crawler;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class CrawlerTest extends AbstractDependencyInjectionSpringContextTests {

    private static final String FEED_KEY_MALFORMED = "feedkeymalformed";
    private static final String FEED_KEY_TEST = "feedkeytest";
    private static final String FEED_KEY_TEST2 = "feedkeytest2";
    private EntryRepository entryRepository;
    private CollectionRepository collectionRepository;
    private static final int SLEEP_TIME = 15000;
    private MadStoreCrawler madStoreCrawler;

    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("atom", AtomConstants.ATOM_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }

    public CrawlerTest() {
        setAutowireMode(AUTOWIRE_BY_TYPE);
    }

    @Override
    protected void onTearDown() throws Exception {
        List<Element> collections = collectionRepository.readCollections();
        for (Element collection : collections) {
            String collectionKey = collection.getAttribute(AtomConstants.ATOM_KEY);
            List<Element> elements = entryRepository.readEntries(collectionKey);
            for (Element element : elements) {
                String entryKey = element.getAttribute(AtomConstants.ATOM_KEY);
                entryRepository.delete(collectionKey, entryKey);
            }
            collectionRepository.delete(collectionKey);
        }
    }

    public void testAllCrawledPage() throws Exception {
        madStoreCrawler.start();
        Thread.sleep(SLEEP_TIME);
        Element entry1 = entryRepository.read(FEED_KEY_TEST, "entrykeytest1");
        assertXpathEvaluatesTo("Test title 1", "/atom:entry/atom:title", getInputSourceFromDom(entry1));
        Element entry2 = entryRepository.read(FEED_KEY_TEST, "entrykeytest2");
        assertXpathEvaluatesTo("Test title 2", "/atom:entry/atom:title", getInputSourceFromDom(entry2));
        Element entry3 = entryRepository.read(FEED_KEY_TEST, "entrykeytest3");
        assertXpathEvaluatesTo("Test title 3", "/atom:entry/atom:title", getInputSourceFromDom(entry3));
        Element entry4 = entryRepository.read(FEED_KEY_TEST2, "entrykeytest4");
        assertXpathEvaluatesTo("Test title 4", "/atom:entry/atom:title", getInputSourceFromDom(entry4));
        Element entry5 = entryRepository.read(FEED_KEY_TEST2, "entrykeytest5");
        assertXpathEvaluatesTo("Test title 5", "/atom:entry/atom:title", getInputSourceFromDom(entry5));
        Element entry6 = entryRepository.read(FEED_KEY_TEST2, "entrykeytest6");
        assertXpathEvaluatesTo("Test title 6", "/atom:entry/atom:title", getInputSourceFromDom(entry6));
        Element entryMalFormed = entryRepository.read(FEED_KEY_MALFORMED, "entrykeymalformed1");
        assertXpathEvaluatesTo("test entry entryKeyMalFormed", "/atom:entry/atom:title", getInputSourceFromDom(entryMalFormed));
    }

    public void testFindInCrawledPage() throws Exception {
        madStoreCrawler.start();
        Thread.sleep(SLEEP_TIME);
        PagingList<Element> elements = entryRepository.findEntries(FEED_KEY_TEST, Arrays.asList("uniquecode"), 0, 1);
        assertEquals(1, elements.size());
        assertEquals(1, elements.getTotal());
        assertEquals(1, elements.getMax());
        assertEquals(0, elements.getOffset());
        assertXpathEvaluatesTo("uniquecode", "/atom:entry/atom:summary", getInputSourceFromDom(elements.iterator().next()));
    }

    public void testLimitedCrawledPages() throws Exception {
        madStoreCrawler.getCrawlerConfigurations().get(0).setMaxVisitedLinks(3);
        madStoreCrawler.start();
        Thread.sleep(SLEEP_TIME);
        List<Element> collections = collectionRepository.readCollections();
        assertNotNull(collections);
        assertEquals(3, collections.size());
    }

    private InputSource getInputSourceFromDom(Element element) throws Exception {
        return new InputSource(DomHelper.getStreamFromDomElement(element));
    }

    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public void setCollectionRepository(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public void setMadStoreCrawler(MadStoreCrawler madStoreCrawler) {
        this.madStoreCrawler = madStoreCrawler;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "classpath:repositoryApplicationContext.xml", "classpath:crawlerApplicationContext.xml" };
    }
}
