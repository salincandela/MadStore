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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for performing transformations from String contents to {@link org.w3c.dom.Element} objects, and vice-versa.
 * 
 * @author Sergio Bossa
 * @author Salvatore Incandela
 */
public class DomHelper {

    public static Element getDomFeedFromString(String xml) throws Exception {
        if (xml == null) {
            throw new IllegalArgumentException("Null argument!");
        }
        return getDomElementByTagName(xml, AtomConstants.ATOM_FEED);
    }

    public static Element getDomEntryFromString(String xml) throws Exception {
        if (xml == null) {
            throw new IllegalArgumentException("Null argument!");
        }
        return getDomElementByTagName(xml, AtomConstants.ATOM_ENTRY);
    }

    public static Element getDomFromString(String xml) throws Exception {
        if (xml == null) {
            throw new IllegalArgumentException("Null argument!");
        }
        Document doc = readDomDocument(xml);
        return doc.getDocumentElement();
    }

    public static InputStream getStreamFromDomElement(Element domElement) throws Exception {
        ByteArrayOutputStream domStream = new ByteArrayOutputStream();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(domElement), new StreamResult(domStream));
        } finally {
            domStream.close();
        }
        return new ByteArrayInputStream(domStream.toByteArray());
    }

    public static String getStringFromDomElement(Element domElement) throws Exception {
        ByteArrayOutputStream domStream = new ByteArrayOutputStream();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(domElement), new StreamResult(domStream));
        } finally {
            domStream.close();
        }
        return new String(domStream.toByteArray());
    }

    private static Element getDomElementByTagName(String xml, String type) throws Exception {
        Document doc = readDomDocument(xml);
        if (doc.getDocumentElement().getNodeName().equals(type)) {
            return doc.getDocumentElement();
        } else {
            return null;
        }
    }

    private static Document readDomDocument(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document result = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes("UTF-8")));
        return result;
    }
}