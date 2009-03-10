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

public class EntryTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(EntryTest.class);
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
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRY_1_URL);
        assertEquals(200, response.getResponseCode());
        assertTrue("Wrong header: " + response.getHeaderField("Cache-Control"), response.getHeaderField("Cache-Control").contains("max-age=5"));
    }

    public void testEntry() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.ENTRY_1_URL);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("tag:localhost:entry:entries:entry1", "/atom:entry/atom:id", content);
    }
}
