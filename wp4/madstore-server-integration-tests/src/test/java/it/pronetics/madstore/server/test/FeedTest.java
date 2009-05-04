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
package it.pronetics.madstore.server.test;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import it.pronetics.madstore.common.AtomConstants;
import java.util.HashMap;
import java.util.Map;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(FeedTest.class);
    private WebConversation wc;
    
    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("app", AtomConstants.APP_NS);
        ns.put("atom", AtomConstants.ATOM_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }
    
    protected void setUp() throws Exception {
        wc = new WebConversation();
    }

    public void testCacheControlResponseHeaderContainsProperMaxAge() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL);
        assertEquals(200, response.getResponseCode());
        assertTrue("Wrong header: " + response.getHeaderField("Cache-Control"), response.getHeaderField("Cache-Control").contains("max-age=5"));
    }

    public void testCollectionFeedWithDefaultPagination() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testCollectionFeedWithExplicitMaxAndAllEntries() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL_M);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testCollectionFeedWithPagination1() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL_P1);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/entries?page=2&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testCollectionFeedWithPagination2() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL_P2);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/entries?page=1&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/entries?page=3&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testCollectionFeedWithPagination3() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL_P3);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/entries?page=2&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testCollectionFeedWithOutOfBoundPaginationIsEmpty() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRIES_FEED_URL_P4);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:collection:entries", "/atom:feed/atom:id", content);
        assertXpathEvaluatesTo("Entries", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathNotExists("/atom:feed/atom:entry", content);
    }
}