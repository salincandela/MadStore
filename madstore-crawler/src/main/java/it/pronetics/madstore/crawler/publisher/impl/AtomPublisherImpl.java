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
package it.pronetics.madstore.crawler.publisher.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link it.pronetics.madstore.crawler.publisher.AtomPublisher} implementation publishing an Atom feed as an AtomPub collection
 * into the {@link it.pronetics.madstore.repository.CollectionRepository}, and all related entries
 * into the {@link it.pronetics.madstore.repository.EntryRepository}.
 * <br><br>
 * Atom feeds are published only if not already existent into the repository, while entries are updated if their
 * publishing date is newer than the one of the already stored entry.
 * <br><br>
 * Atom feeds and entries should have a proper feed and entry key, in order to properly manage updating of entries: if no
 * key is provided, surrogated keys will be automatically generated based on page and URL heuristics, more specifically:
 * <ul>
 * <li>The host name of the crawled site will be used for generating the feed key.</li>
 * <li>An hash of the entry title will be used for generating each entry key.</li>
 * <li>All entries will be inserted under the same collection.</li>
 * </ul>
 * <br>
 * Atom entries should have a proper updated date, too: if no such a date is found, the current one will be used.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class AtomPublisherImpl implements AtomPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(AtomPublisherImpl.class);
    private EntryRepository entryRepository;
    private CollectionRepository collectionRepository;

    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public void setCollectionRepository(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void publish(Page page) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Publishing feed:\n{}.", page.getData());
            }
            Element feed = DomHelper.getDomFeedFromString(page.getData());
            String collectionKey = getOrGenerateCollectionKey(page, feed);
            String collectionTitle = getOrGenerateCollectionTitle(page, feed);
            String collectionHref = collectionKey;
            Element collectionElement = createCollectionElement(collectionKey, collectionHref, collectionTitle);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Publishing collection:\n{}.", DomHelper.getStringFromDomElement(collectionElement));
            }
            String newCollectionKey = collectionRepository.putIfAbsent(collectionElement);
            if (newCollectionKey != null) {
                LOG.info("Inserted collection with key {}.", collectionKey);
            } else {
                LOG.info("Collection {} already existent.", collectionKey);
            }
            NodeList entryNodes = feed.getElementsByTagNameNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY);
            if (entryNodes != null && entryNodes.getLength() > 0) {
                for (int i = 0; i < entryNodes.getLength(); i++) {
                    Element entry = (Element) entryNodes.item(i);
                    String entryKey = getOrGenerateEntryKey(entry);
                    setUpdatedDateTimeIfNecessary(entry);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Publishing entry:\n{}.", DomHelper.getStringFromDomElement(entry));
                    }
                    String newEntryKey = entryRepository.putIfAbsent(collectionKey, entry);
                    if (newEntryKey != null) {
                        LOG.info("Entry with key {} inserted in collection {}.", entryKey, collectionKey);
                    } else {
                        LOG.info("Entry with key {} already existent in collection {}.", entryKey, collectionKey);
                        String updatedEntryKey = entryRepository.updateIfNewer(collectionKey, entry);
                        if (updatedEntryKey != null) {
                            LOG.info("Entry with key {} in collection {} was updated.", entryKey, collectionKey);
                        } else {
                            LOG.info("Entry with key {} wasn't updated because is older.", entryKey);
                        }
                    }
                }
            } else {
                LOG.info("No entries for {}", page.getLink());
            }
        } catch (Exception e) {
            LOG.info("Publishing abnormally terminated: {}", page.getLink());
            LOG.warn(e.getMessage());
            LOG.debug(e.getMessage(), e);
        }
    }

    private Element createCollectionElement(String key, String href, String title) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document collectionDocument = builder.newDocument();
        Element collectionelElement = collectionDocument.createElementNS(AtomConstants.APP_NS, AtomConstants.ATOM_COLLECTION);
        collectionelElement.setAttribute(AtomConstants.ATOM_KEY, key);
        collectionelElement.setAttribute(AtomConstants.ATOM_COLLECTION_HREF, href);
        Element acceptElement = collectionDocument.createElementNS(AtomConstants.APP_NS, AtomConstants.ATOM_COLLECTION_ACCEPT);
        Element titleElement = collectionDocument.createElementNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_COLLECTION_TITLE);
        titleElement.setTextContent(title);
        collectionelElement.appendChild(acceptElement);
        collectionelElement.appendChild(titleElement);
        collectionDocument.appendChild(collectionelElement);
        return collectionelElement;
    }

    private String getOrGenerateCollectionKey(Page page, Element feed) throws Exception {
        String key = feed.getAttribute(AtomConstants.ATOM_KEY);
        if (key == null || key.equals("")) {
            LOG.warn("No feed key found, generating surrogate key ...");
            URL url = new URL(page.getLink().getLink());
            String path = url.getHost();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            key = path.replaceAll("\\.", "_").replaceAll("/", "-").replaceAll("\\:", "-").replaceAll("\\,", "-");
            LOG.warn("Surrogated feed key: {}", key);
        }
        return key;
    }

    private String getOrGenerateCollectionTitle(Page page, Element feed) throws Exception {
        String key = feed.getAttribute(AtomConstants.ATOM_KEY);
        if (key == null || key.equals("")) {
            URL url = new URL(page.getLink().getLink());
            return url.getHost();
        } else {
            return feed.getElementsByTagName(AtomConstants.ATOM_COLLECTION_TITLE).item(0).getTextContent();
        }
    }

    private String getOrGenerateEntryKey(Element entry) throws Exception {
        String key = entry.getAttribute(AtomConstants.ATOM_KEY);
        if (key == null || key.equals("")) {
            LOG.warn("No entry key found, generating surrogate key ...");
            NodeList titleNodes = entry.getElementsByTagName(AtomConstants.ATOM_ENTRY_TITLE);
            Node titleNode = titleNodes.item(0);
            if (titleNode != null) {
                int keyCode = titleNode.getTextContent().hashCode();
                if (keyCode < 0) {
                    key = "e" + Integer.toString(keyCode * -1) + "n";
                } else {
                    key = "e" + Integer.toString(keyCode) + "p";
                }
            } else {
                key = Long.toString(System.currentTimeMillis());
            }
            entry.setAttribute(AtomConstants.ATOM_KEY, key);
            LOG.warn("Surrogated entry key: {}", key);
        }
        return key;
    }

    private void setUpdatedDateTimeIfNecessary(Element entry) {
        NodeList updatedNodes = entry.getElementsByTagName(AtomConstants.ATOM_ENTRY_UPDATED);
        Node updatedNode = updatedNodes.item(0);
        if (updatedNode != null) {
            String entryUpdatedDateTime = updatedNode.getTextContent();
            if (entryUpdatedDateTime == null || entryUpdatedDateTime.equals("")) {
                LOG.warn("The entry has no updated date, using current time ...");
                updatedNode.setTextContent(ISODateTimeFormat.dateTime().print(System.currentTimeMillis()));
            }
        } else {
            LOG.warn("The entry has no updated date, using current time ...");
            updatedNode = entry.getOwnerDocument().createElementNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY_UPDATED);
            updatedNode.setTextContent(ISODateTimeFormat.dateTime().print(System.currentTimeMillis()));
            entry.appendChild(updatedNode);
        }
    }
}
