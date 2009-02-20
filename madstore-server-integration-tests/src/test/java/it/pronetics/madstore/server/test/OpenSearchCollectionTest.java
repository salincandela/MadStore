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
import org.apache.abdera.ext.opensearch.OpenSearchConstants;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchCollectionTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(OpenSearchCollectionTest.class);
    private WebConversation wc;
    
    static {
        Map<String, String> ns = new HashMap<String, String>();
        ns.put("app", AtomConstants.APP_NS);
        ns.put("atom", AtomConstants.ATOM_NS);
        ns.put("os", OpenSearchConstants.OPENSEARCH_NS);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(ns));
    }
    
    protected void setUp() throws Exception {
        wc = new WebConversation();
    }

    public void testSearchCollection1() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollection2() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_1);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollection3() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_2);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollection4() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_3);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollectionWithPagination1() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_P1);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/search/entries?title=MadStore&terms=entry&page=2&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathEvaluatesTo("3", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:itemsPerPage", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollectionWithPagination2() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_P2);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/search/entries?title=MadStore&terms=entry&page=3&max=1", "/atom:feed/atom:link[@rel='next']/@href", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/search/entries?title=MadStore&terms=entry&page=1&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathEvaluatesTo("3", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("2", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:itemsPerPage", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollectionWithPagination3() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_P3);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathEvaluatesTo("http://localhost:8080/it/search/entries?title=MadStore&terms=entry&page=2&max=1", "/atom:feed/atom:link[@rel='previous']/@href", content);
        assertXpathEvaluatesTo("3", "/atom:feed/os:totalResults", content);
        assertXpathEvaluatesTo("3", "/atom:feed/os:startIndex", content);
        assertXpathEvaluatesTo("1", "/atom:feed/os:itemsPerPage", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry1']", content);
        assertXpathNotExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry2']", content);
        assertXpathExists("/atom:feed/atom:entry/atom:id[text()='tag:localhost:entry:entries:entry3']", content);
    }

    public void testSearchCollectionWithPagination4() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.OPENSEARCH_URL_P4);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/atom:feed/atom:title", content);
        assertXpathNotExists("/atom:feed/atom:entry", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='next']", content);
        assertXpathNotExists("/atom:feed/atom:link[@rel='previous']", content);
        assertXpathNotExists("/atom:feed/os:totalResults", content);
        assertXpathNotExists("/atom:feed/os:startIndex", content);
        assertXpathNotExists("/atom:feed/os:itemsPerPage", content);
    }
}
