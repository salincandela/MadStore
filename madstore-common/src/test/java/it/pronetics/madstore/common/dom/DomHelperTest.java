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
package it.pronetics.madstore.common.dom;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.common.test.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DomHelperTest extends XMLTestCase {

    private static String FEED_FROM_STRING = "domHelperTestFeed.xml";
    private static String ENTRY_FROM_STRING = "domHelperTestEntry.xml";

    public void testGetDomFeedFromString() throws Exception {
        String xml = new String(Utils.getUtf8BytesFromFile(FEED_FROM_STRING));
        Element feed = DomHelper.getDomFeedFromString(xml);
        assertNotNull(feed);

        Document actual = feed.getOwnerDocument();
        Document expected = Utils.getDoc(FEED_FROM_STRING);

        Diff diff = null;
        diff = new Diff(expected, actual);
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + actual, diff.identical());
    }

    public void testGetDomEntryFromString() throws Exception {
        String xml = new String(Utils.getUtf8BytesFromFile(ENTRY_FROM_STRING));
        Element entry = DomHelper.getDomEntryFromString(xml);
        assertNotNull(entry);

        Document actual = entry.getOwnerDocument();
        Document expected = Utils.getDoc(ENTRY_FROM_STRING);

        Diff diff = null;
        diff = new Diff(expected, actual);
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + actual, diff.identical());
    }

    public void testGetDomFromString() throws Exception {
        String xml = new String(Utils.getUtf8BytesFromFile(FEED_FROM_STRING));
        Element feed = DomHelper.getDomFeedFromString(xml);
        assertNotNull(feed);

        Document actual = feed.getOwnerDocument();
        Document expected = Utils.getDoc(FEED_FROM_STRING);

        Diff diff = null;
        diff = new Diff(expected, actual);
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + actual, diff.identical());
    }

    public void testGetStreamFromDomElement() throws Exception {
        byte[] expectedBytes = Utils.getUtf8BytesFromFile(FEED_FROM_STRING);
        Document document = Utils.getDoc(FEED_FROM_STRING);
        Element element = document.getDocumentElement();
        InputStream inputStream = DomHelper.getStreamFromDomElement(element);
        InputSource actual = new InputSource(inputStream);
        InputSource expected = new InputSource(new ByteArrayInputStream(expectedBytes));

        Diff diff = null;
        diff = new Diff(expected, actual);
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + actual, diff.identical());
    }

    public void testGetStringFromDomElement() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        String expected = new String(Utils.getUtf8BytesFromFile(FEED_FROM_STRING));
        Document document = Utils.getDoc(FEED_FROM_STRING);
        Element element = document.getDocumentElement();
        String actual = DomHelper.getStringFromDomElement(element);

        Diff diff = null;
        diff = new Diff(expected, actual);
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected + "\nActual: " + actual, diff.identical());
    }

    public void testDomFeedEntriesHaveCorrectNamespace() throws Exception {
        String xml = new String(Utils.getUtf8BytesFromFile(FEED_FROM_STRING));
        Element feed = DomHelper.getDomFeedFromString(xml);
        assertNotNull(feed);
        NodeList entryNodes = feed.getOwnerDocument().getElementsByTagNameNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY);
        assertTrue(entryNodes.getLength() > 0);
        for (int i = 0; i < entryNodes.getLength(); i++) {
            Element entry = (Element) entryNodes.item(i);
            assertEquals(AtomConstants.ATOM_NS, entry.getNamespaceURI());
        }
    }
}
