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
package it.pronetics.madstore.crawler.transformer.impl;

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.test.util.Utils;
import it.pronetics.madstore.crawler.transformer.Transformer;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;

public class HAtomToAtomTransformerTest extends XMLTestCase {

    private Transformer transformer;

    protected void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        transformer = new HAtomToAtomTransformer();
    }

    public void testTrasformerWithExplicitFeed() throws Exception {
        String expected = new String(Utils.getUtf8BytesFromFile("expectedatom.xml"));
        byte[] result = transformer.transform(new Page(new Link("http://localhost/dummyLink.html"), new String(Utils.getUtf8BytesFromFile("hatom1.html"))));
        Diff diff = new Diff(expected, new String(result));
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + new String(result), diff.identical());
    }

    public void testTrasformerWithImplicitFeed() throws Exception {
        String expected = new String(Utils.getUtf8BytesFromFile("expectedatom.xml"));
        byte[] result = transformer.transform(new Page(new Link("http://localhost/dummyLink.html"), new String(Utils.getUtf8BytesFromFile("hatom2.html"))));
        Diff diff = new Diff(expected, new String(result, "UTF-8"));
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + new String(result), diff.identical());
    }

    public void testTrasformerWithSpecialCharacters() throws Exception {
        String expected = new String(Utils.getUtf8BytesFromFile("expectedatomSpecialCharacters.xml"));
        byte[] result = transformer.transform(new Page(new Link("http://localhost/dummyLink.html"), new String(Utils.getUtf8BytesFromFile("specialCharacters.html"))));
        Diff diff = new Diff(expected, new String(result, "UTF-8"));
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + new String(result), diff.identical());
    }

    public void testTrasformerWithEscapedCharacters() throws Exception {
        String expected = new String(Utils.getUtf8BytesFromFile("expectedatomEscapedCharacters.xml"));
        byte[] result = transformer.transform(new Page(new Link("http://localhost/dummyLink.html"), new String(Utils.getUtf8BytesFromFile("escapedCharacters.html"))));
        Diff diff = new Diff(expected, new String(result, "UTF-8"));
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + new String(result), diff.identical());
    }
}
