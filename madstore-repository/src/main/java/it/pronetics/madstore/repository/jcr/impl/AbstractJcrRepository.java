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
import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.repository.index.IndexManager;
import it.pronetics.madstore.repository.jcr.xml.JcrContentHandlerFactory;
import it.pronetics.madstore.repository.support.AtomRepositoryException;
import it.pronetics.madstore.repository.util.PagingList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.springframework.extensions.jcr.JcrTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;

/**
 * Base abstract class for repositories implementations.
 * <br/>
 * This class contains methods to read and write content from the repository, defining its inner structure.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public abstract class AbstractJcrRepository {

    private static final String TEMPLATES_CLASSPATH_FOLDER = "templates/";
    private static final String GET_ENTRY_NODE_PATH_TEMPLATE = TEMPLATES_CLASSPATH_FOLDER + "getEntryNode";
    private static final String GET_COLLECTION_NODE_PATH_TEMPLATE = TEMPLATES_CLASSPATH_FOLDER + "getCollectionNode";
    private static final String GET_ENTRY_NODES_QUERY_TEMPLATE = TEMPLATES_CLASSPATH_FOLDER + "getEntryNodes";
    private static final String GET_COLLECTION_NODES_QUERY_TEMPLATE = TEMPLATES_CLASSPATH_FOLDER + "getCollectionNodes";
    private static final String COLLECTION_TEMPLATE_PARAMETER = "collection";
    private static final String ENTRY_TEMPLATE_PARAMETER = "entry";
    private static final StringTemplateGroup PATH_GROUP = new StringTemplateGroup("pathGroup");
    private static final StringTemplateGroup QUERY_GROUP = new StringTemplateGroup("queryGroup");
    private static final String MADSTORE_NAMESPACE = "mds:";
    private static final String APP_COLLECTION = "app:collection";
    private static final String ATOM_ENTRY = "atom:entry";
    private static final String JCR_LAST_MODIFIED = "jcr:lastModified";
    private static final String MIX_REFERENCEABLE = "mix:referenceable";
    private static final String COLLECTION_REF = "collectionRef";
    protected JcrTemplate jcrTemplate;
    protected JcrContentHandlerFactory jcrContentHandlerFactory;
    protected IndexManager indexManager;

    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public void setJcrContentHandlerFactory(JcrContentHandlerFactory jcrContentHandlerFactory) {
        this.jcrContentHandlerFactory = jcrContentHandlerFactory;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public IndexManager getIndexManager() {
        return indexManager;
    }

    protected final void importNodeFromDomCollection(String collectionKey, Element collectionElement, Session session) {
        try {
            InputStream collectionStream = DomHelper.getStreamFromDomElement(collectionElement);
            Node root = session.getRootNode();
            Node collectionContainerNode = root.addNode(MADSTORE_NAMESPACE + collectionKey);
            session.importXML(collectionContainerNode.getPath(), collectionStream, ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
            Node collectionNode = collectionContainerNode.getNode(APP_COLLECTION);
            postProcessCollectionImport(collectionElement, collectionContainerNode, collectionNode);
        } catch (Exception ex) {
            throw new AtomRepositoryException(ex.getMessage(), ex);
        }
    }

    protected final void importNodeFromDomEntry(String collectionKey, String entryKey, Element entryElement, Session session) {
        try {
            InputStream entryStream = DomHelper.getStreamFromDomElement(entryElement);
            Node root = session.getRootNode();
            Node collectionContainerNode = root.getNode(MADSTORE_NAMESPACE + collectionKey);
            Node entryContainerNode = collectionContainerNode.addNode(MADSTORE_NAMESPACE + entryKey);
            session.importXML(entryContainerNode.getPath(), entryStream, ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
            Node entryNode = entryContainerNode.getNode(ATOM_ENTRY);
            postProcessEntryImport(entryElement, collectionContainerNode, entryContainerNode, entryNode);
        } catch (Exception ex) {
            throw new AtomRepositoryException(ex.getMessage(), ex);
        }
    }

    protected final Element exportNodeToDom(String path, Session session) {
        try {
            Document exportedDocument = getDomDocument();
            ContentHandler atomContentHandler = jcrContentHandlerFactory.makeExportContentHandler(exportedDocument);
            session.exportDocumentView(path, atomContentHandler, false, false);
            return exportedDocument.getDocumentElement();
        } catch (Exception ex) {
            throw new AtomRepositoryException(ex.getMessage(), ex);
        }
    }

    protected final void postProcessCollectionImport(Element collectionElement, Node collectionContainerNode, Node collectionNode) {
        try {
            collectionContainerNode.addMixin(MIX_REFERENCEABLE);
        } catch (Exception ex) {
            throw new AtomRepositoryException(ex.getMessage(), ex);
        }
    }

    protected final void postProcessEntryImport(Element entryElement, Node collectionContainerNode, Node entryContainerNode, Node entryNode) {
        try {
            entryContainerNode.addMixin(MIX_REFERENCEABLE);
            entryContainerNode.setProperty(COLLECTION_REF, collectionContainerNode);
            org.w3c.dom.NodeList nodes = entryElement.getElementsByTagNameNS(AtomConstants.ATOM_NS, AtomConstants.ATOM_ENTRY_UPDATED);
            if (nodes.getLength() == 1) {
                org.w3c.dom.Node updated = nodes.item(0);
                entryNode.setProperty(JCR_LAST_MODIFIED, updated.getTextContent());
            }
        } catch (Exception ex) {
            throw new AtomRepositoryException(ex.getMessage(), ex);
        }
    }

    protected final Node getCollectionNode(String collectionKey, Session session) throws RepositoryException {
        StringTemplate pathTemplate = PATH_GROUP.getInstanceOf(GET_COLLECTION_NODE_PATH_TEMPLATE);
        pathTemplate.setAttribute(COLLECTION_TEMPLATE_PARAMETER, collectionKey);
        String path = pathTemplate.toString();
        Node root = session.getRootNode();
        if (root.hasNode(path)) {
            return root.getNode(path);
        } else {
            return null;
        }
    }

    protected final Node getEntryNode(String collectionKey, String entryKey, Session session) throws RepositoryException {
        StringTemplate pathTemplate = PATH_GROUP.getInstanceOf(GET_ENTRY_NODE_PATH_TEMPLATE);
        pathTemplate.setAttribute(COLLECTION_TEMPLATE_PARAMETER, collectionKey);
        pathTemplate.setAttribute(ENTRY_TEMPLATE_PARAMETER, entryKey);
        String path = pathTemplate.toString();
        Node root = session.getRootNode();
        if (root.hasNode(path)) {
            return root.getNode(path);
        } else {
            return null;
        }
    }

    protected final List<Node> getCollectionNodes(Session session) throws RepositoryException {
        StringTemplate queryTemplate = QUERY_GROUP.getInstanceOf(GET_COLLECTION_NODES_QUERY_TEMPLATE);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(queryTemplate.toString(), Query.XPATH);
        QueryResult queryResult = query.execute();
        List<Node> elements = new ArrayList<Node>();
        NodeIterator nodeIterator = queryResult.getNodes();
        while (nodeIterator.hasNext()) {
            elements.add(nodeIterator.nextNode());
        }
        return elements;
    }

    protected final PagingList<Node> getEntryNodes(String collectionKey, int offset, int max, Session session) throws RepositoryException {
        if (offset < 0 || max < 0) {
            throw new IllegalArgumentException("Parameters offset and max cannot be negative!");
        }
        StringTemplate queryTemplate = QUERY_GROUP.getInstanceOf(GET_ENTRY_NODES_QUERY_TEMPLATE);
        queryTemplate.setAttribute(COLLECTION_TEMPLATE_PARAMETER, collectionKey);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(queryTemplate.toString(), Query.XPATH);
        QueryResult queryResult = query.execute();
        NodeIterator nodeIterator = queryResult.getNodes();
        int total = (int) nodeIterator.getSize();
        PagingList<Node> elements = new PagingList<Node>(
                new ArrayList<Node>(),
                offset,
                max,
                total);
        if (nodeIterator.getSize() > offset) {
            nodeIterator.skip(offset);
            while (nodeIterator.hasNext()) {
                if (max > 0 && !((nodeIterator.getPosition() - offset + 1) <= max)) {
                    break;
                }
                elements.add(nodeIterator.nextNode());
            }
        }
        return elements;
    }

    private Document getDomDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}
