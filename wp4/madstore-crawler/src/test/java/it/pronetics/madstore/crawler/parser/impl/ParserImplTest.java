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
package it.pronetics.madstore.crawler.parser.impl;

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.parser.filter.LinkFilter;
import it.pronetics.madstore.crawler.test.util.Utils;

import java.util.Collection;

import junit.framework.TestCase;

public class ParserImplTest extends TestCase {

    private static final String PARSER_TEST_PAGE = "parserTestPage.html";
    private Parser parser;
    private Page page;

    protected void setUp() throws Exception {
        parser = new ParserImpl();
        page = new Page(new Link("http://localhost/siteexample/hatom.html"), new String(Utils.getUtf8BytesFromFile(PARSER_TEST_PAGE)));
    }

    public void testParsing() {
        Collection<Link> links = parser.parse(page, new LinkFilter() {

            public boolean accept(Link link) {
                return true;
            }
        });
        assertNotNull(links);
        assertEquals(3, links.size());
        assertTrue(links.contains(new Link("http://localhost/siteexample/hatom2.html")));
        assertTrue(links.contains(new Link("http://externallinktest.com/siteexample/hatom2.html")));
        assertTrue(links.contains(new Link("http://localhost/siteexample/hatom3454.html")));
    }

    public void testParsingWithLinkFilter() {
        Collection<Link> links = parser.parse(page, new LinkFilter() {

            public boolean accept(Link link) {
                return link.getLink().contains("localhost");
            }
        });
        assertNotNull(links);
        assertEquals(2, links.size());
        assertTrue(links.contains(new Link("http://localhost/siteexample/hatom2.html")));
        assertTrue(links.contains(new Link("http://localhost/siteexample/hatom3454.html")));
    }
}
