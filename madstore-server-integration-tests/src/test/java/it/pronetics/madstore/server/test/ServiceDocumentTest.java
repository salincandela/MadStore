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

public class ServiceDocumentTest extends XMLTestCase {

    private static final transient Logger LOG = LoggerFactory.getLogger(ServiceDocumentTest.class);
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

    public void testCacheControlResponseHeaderContainsZeroMaxAge() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.SERVICE_DOCUMENT_URL);
        assertEquals(200, response.getResponseCode());
        assertTrue("Wrong header: " + response.getHeaderField("Cache-Control"), response.getHeaderField("Cache-Control").contains("max-age=0"));
    }

    public void testServiceDocument() throws Exception {
        WebResponse response = wc.getResponse(HttpConstants.SERVER_URL + HttpConstants.SERVICE_DOCUMENT_URL);
        assertEquals(200, response.getResponseCode());
        String content = response.getText();
        LOG.info(content);
        assertXpathEvaluatesTo("MadStore", "/app:service/app:workspace/atom:title", content);
        assertXpathEvaluatesTo("Entries", "/app:service/app:workspace/app:collection/atom:title", content);
    }
}
