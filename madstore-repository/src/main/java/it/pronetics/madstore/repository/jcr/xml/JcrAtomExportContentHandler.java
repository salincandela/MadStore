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
package it.pronetics.madstore.repository.jcr.xml;

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * {@link org.xml.sax.ContentHandler} implementation for building Atom contents out of a stream of SAX events.
 * <br>
 * This implementation is not thread safe and must be used for a single transformation only.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class JcrAtomExportContentHandler implements ContentHandler {

    private Document atomDocument;
    private Node currentElement;
    private boolean skipped;
    private Map<String, String> allowedNamespaces;

    public JcrAtomExportContentHandler(Document atomDocument, Map<String, String> allowedNamespaces) {
        this.atomDocument = atomDocument;
        this.allowedNamespaces = allowedNamespaces;
    }

    public void startDocument() throws SAXException {
        this.currentElement = atomDocument;
    }

    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
        if (allowedNamespaces.values().contains(namespaceURI)) {
            Element newElement = atomDocument.createElementNS(namespaceURI, qualifiedName);
            for (int i = 0; i < attributes.getLength(); i++) {
                boolean output = false;
                if (attributes.getURI(i).equals("") || allowedNamespaces.values().contains(attributes.getURI(i))) {
                    // Outputs an attribute with no namespace, or whose namespace is allowed:
                    output = true;
                } else if (attributes.getQName(i).startsWith("xmlns") && allowedNamespaces.values().contains(attributes.getValue(i))) {
                    // Outputs an attribute declaring an allowed namespace:
                    output = true;
                }
                if (output) {
                    newElement.setAttribute(attributes.getQName(i), attributes.getValue(i));
                }
            }
            currentElement.appendChild(newElement);
            currentElement = newElement;
            skipped = false;
        } else {
            skipped = true;
        }
    }

    public void characters(char[] text, int start, int length) throws SAXException {
        if (!skipped) {
            currentElement.setTextContent(new String(text));
        }
    }

    public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
        if (!skipped) {
            currentElement = currentElement.getParentNode();
        }
    }

    // Do nothing methods:
    public void setDocumentLocator(Locator locator) {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void ignorableWhitespace(char[] text, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }
}
