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
package it.pronetics.madstore.repository.index.impl;

import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.repository.index.PropertyPath;
import it.pronetics.madstore.repository.support.AtomIndexingException;

import java.io.InputStream;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Low-level Lucene-based class for indexing Atom entry properties.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class LuceneIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneIndexer.class);
    private Directory directory;
    private List<PropertyPath> indexedProperties;

    public LuceneIndexer(Directory directory, List<PropertyPath> propertyPaths) {
        this.directory = directory;
        this.indexedProperties = propertyPaths;
    }

    public void index(String collectionKey, String entryKey, Element element) {
        if (collectionKey == null || entryKey == null || element == null) {
            throw new AtomIndexingException("Parameters collectionKey, entryKey and element cannot be null.");
        }
        try {
            IndexWriter indexWriter = newIndexWriter(directory);
            Document document = getDocumentFromElement(collectionKey, entryKey, element);
            indexWriter.addDocument(document);
            indexWriter.close();
        } catch (Exception e) {
            throw new AtomIndexingException(e.getMessage(), e);
        }
    }

    public void delete(String collectionKey, String entryKey) {
        try {
            if (collectionKey == null || entryKey == null) {
                throw new AtomIndexingException("Parameters collectionKey and entryKey cannot be null.");
            }
            Term primaryKeyTerm = new Term(LuceneIndexManager.INDEX_PRIMARY_KEY, collectionKey + entryKey);
            IndexWriter indexWriter = newIndexWriter(directory);
            indexWriter.deleteDocuments(primaryKeyTerm);
            indexWriter.close();
        } catch (Exception e) {
            throw new AtomIndexingException(e.getMessage(), e);
        }
    }

    private Document getDocumentFromElement(String collectionKey, String entryKey, Element element) throws Exception {
        Document doc = new Document();
        doc.add(new Field(LuceneIndexManager.INDEX_COLLECTION_KEY, collectionKey, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(LuceneIndexManager.INDEX_ENTRY_KEY, entryKey, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(LuceneIndexManager.INDEX_PRIMARY_KEY, collectionKey + entryKey, Field.Store.YES, Field.Index.UN_TOKENIZED));
        for (PropertyPath propertyPath : indexedProperties) {
            InputStream elementStream = DomHelper.getStreamFromDomElement(element);
            String value = propertyPath.getXPathExpression().evaluate(new InputSource(elementStream));
            Field field = new Field(propertyPath.getName(), value, Field.Store.YES, Field.Index.TOKENIZED);
            field.setBoost(propertyPath.getBoost());
            doc.add(field);
        }
        return doc;
    }

    private IndexWriter newIndexWriter(Directory directory) throws Exception {
        return new IndexWriter(directory, false, new StandardAnalyzer());
    }
}
