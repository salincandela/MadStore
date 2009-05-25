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
import it.pronetics.madstore.repository.CollectionRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.extensions.jcr.JcrCallback;
import org.w3c.dom.Element;

/**
 * {@link it.pronetics.madstore.repository.CollectionRepository} implementation based on Java Content
 * Repository APIs.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public final class JcrCollectionRepository extends AbstractJcrRepository implements CollectionRepository {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String putIfAbsent(final Element collectionElement) {
        if (collectionElement == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        return (String) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                String key = collectionElement.getAttribute(AtomConstants.ATOM_KEY);
                if (!contains(key)) {
                    importNodeFromDomCollection(key, collectionElement, session);
                    session.save();
                    return key;
                } else {
                    return null;
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean delete(final String collectionKey) {
        if (collectionKey == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        return (Boolean) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = getCollectionNode(collectionKey, session);
                if (node != null) {
                    Node container = node.getParent();
                    if (!container.getReferences().hasNext()) {
                        container.remove();
                        session.save();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Element read(final String collectionKey) {
        if (collectionKey == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        return (Element) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                Node node = getCollectionNode(collectionKey, session);
                if (node != null) {
                    return exportNodeToDom(node.getPath(), session);
                } else {
                    return null;
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<Element> readCollections() {
        return (List<Element>) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                List<Node> nodes = getCollectionNodes(session);
                List<Element> elements = new ArrayList<Element>(nodes.size());
                for (Node node : nodes) {
                    Element element = exportNodeToDom(node.getPath(), session);
                    elements.add(element);
                }
                return elements;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public boolean contains(final String collectionKey) {
        if (collectionKey == null) {
            throw new IllegalArgumentException("Parameters cannot be null!");
        }
        return (Boolean) jcrTemplate.execute(new JcrCallback() {

            public Object doInJcr(final Session session) throws IOException, RepositoryException {
                Node contained = getCollectionNode(collectionKey, session);
                return contained != null;
            }
        });
    }
}
