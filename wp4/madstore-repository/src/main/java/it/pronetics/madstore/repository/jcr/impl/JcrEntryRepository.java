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
package it.pronetics.madstore.repository.jcr.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.index.SearchResult;
import it.pronetics.madstore.repository.support.AtomRepositoryException;
import it.pronetics.madstore.repository.util.PagingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springmodules.jcr.JcrCallback;
import org.w3c.dom.Element;

/**
 * {@link it.pronetics.madstore.repository.EntryRepository} implementation based on Java Content Repository
 * APIs.
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public final class JcrEntryRepository extends AbstractJcrRepository implements EntryRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JcrEntryRepository.class);
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String put(final String collectionKey, final Element entryElement) {
        if (entryElement == null || collectionKey == null || collectionKey.equals("")) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        } else if (entryElement.getAttribute(AtomConstants.ATOM_KEY).equals("")) {
            throw new AtomRepositoryException("The entry element has no key!");
        }
        return (String) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                String entryKey = entryElement.getAttribute(AtomConstants.ATOM_KEY);
                if (contains(collectionKey, entryKey)) {
                    delete(collectionKey, entryKey);
                    indexManager.delete(collectionKey, entryKey);
                }
                importNodeFromDomEntry(collectionKey, entryKey, entryElement, session);
                indexManager.index(collectionKey, entryKey, entryElement);
                session.save();
                return entryKey;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String putIfAbsent(final String collectionKey, final Element entryElement) {
        if (entryElement == null || collectionKey == null || collectionKey.equals("")) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        } else if (entryElement.getAttribute(AtomConstants.ATOM_KEY).equals("")) {
            throw new AtomRepositoryException("The entry element has no key!");
        }
        return (String) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                String entryKey = entryElement.getAttribute(AtomConstants.ATOM_KEY);
                if (!contains(collectionKey, entryKey)) {
                    Node collection = getCollectionNode(collectionKey, session);
                    if (collection != null) {
                        importNodeFromDomEntry(collectionKey, entryKey, entryElement, session);
                        indexManager.index(collectionKey, entryKey, entryElement);
                        session.save();
                        return entryKey;
                    }
                }
                return null;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String updateIfNewer(final String collectionKey, final Element entryElement) {
        if (entryElement == null || collectionKey == null || collectionKey.equals("")) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        } else if (entryElement.getAttribute(AtomConstants.ATOM_KEY).equals("")) {
            throw new AtomRepositoryException("The entry element has no key!");
        }
        return (String) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                String entryKey = entryElement.getAttribute(AtomConstants.ATOM_KEY);
                if (contains(collectionKey, entryKey)) {
                    Element repositoryEntry = read(collectionKey, entryKey);

                    String repositoryEntryUpdated = repositoryEntry.getElementsByTagNameNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY_UPDATED).item(0).getTextContent();
                    String newEntryUpdated = entryElement.getElementsByTagNameNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY_UPDATED).item(0).getTextContent();
                    DateTime repositoryEntryDateTime = new DateTime(repositoryEntryUpdated);
                    DateTime newEntryDateTime = new DateTime(newEntryUpdated);

                    if (newEntryDateTime.isAfter(repositoryEntryDateTime)) {
                        if (delete(collectionKey, entryKey) != null) {
                            importNodeFromDomEntry(collectionKey, entryKey, entryElement, session);
                            indexManager.delete(collectionKey, entryKey);
                            indexManager.index(collectionKey, entryKey, entryElement);
                            session.save();
                            return entryKey;
                        } else {
                            throw new IllegalStateException("Unable to delete: " + entryKey);
                        }
                    } else {
                        return null;
                    }
                } else {
                    LOG.warn("Entry {} does not exist in collection {}. Nothing to update.", entryKey, collectionKey);
                    return null;
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean delete(final String collectionKey, final String entryKey) {
        if ((entryKey == null) || (collectionKey == null)) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        }
        return (Boolean) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                Node node = getEntryNode(collectionKey, entryKey, session);
                if (node != null) {
                    Node container = node.getParent();
                    container.remove();
                    indexManager.delete(collectionKey, entryKey);
                    session.save();
                    return true;
                }
                return false;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Element read(final String collectionKey, final String entryKey) {
        if ((entryKey == null) || (collectionKey == null)) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        }
        return (Element) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                Node node = getEntryNode(collectionKey, entryKey, session);
                if (node != null) {
                    return exportNodeToDom(node.getPath(), session);
                } else {
                    return null;
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<Element> readEntries(final String collectionKey) {
        return this.readEntries(collectionKey, 0, 0);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public PagingList<Element> readEntries(final String collectionKey, final int offset, final int max) {
        if (collectionKey == null) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        }
        return (PagingList<Element>) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                PagingList<Node> nodes = getEntryNodes(collectionKey, offset, max, session);
                PagingList<Element> elements = new PagingList<Element>(
                        new ArrayList<Element>(nodes.size()),
                        offset,
                        max,
                        nodes.getTotal());
                for (Node node : nodes) {
                    Element element = exportNodeToDom(node.getPath(), session);
                    elements.add(element);
                }
                return elements;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<Element> findEntries(String collectionKey, List<String> terms) {
        return findEntries(collectionKey, terms, 0, 0);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public PagingList<Element> findEntries(String collectionKey, List<String> terms, int offset, int max) {
        PagingList<SearchResult> entryItems = indexManager.searchCollectionByFullText(collectionKey, terms, offset, max);
        PagingList<Element> elements = new PagingList<Element>(
                new ArrayList<Element>(),
                offset,
                max,
                entryItems.getTotal());
        for (SearchResult entryItem : entryItems) {
            Element element = read(entryItem.getColletionKey(), entryItem.getEntryKey());
            if (element != null) {
                elements.add(element);
            }
        }
        return elements;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public boolean contains(final String collectionKey, final String entryKey) {
        if ((entryKey == null) || (collectionKey == null)) {
            throw new IllegalArgumentException("Parameters cannot be null or empty strings!");
        }
        return (Boolean) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                Node contained = getEntryNode(collectionKey, entryKey, session);
                return contained != null;
            }
        });
    }
}
