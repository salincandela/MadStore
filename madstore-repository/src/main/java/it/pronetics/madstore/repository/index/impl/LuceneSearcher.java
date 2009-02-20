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

import it.pronetics.madstore.repository.index.PropertyPath;
import it.pronetics.madstore.repository.index.SearchResult;
import it.pronetics.madstore.repository.support.AtomIndexingException;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Low-level Lucene-based class for searching Atom entries.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class LuceneSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneSearcher.class);
    private ThreadLocalSearcher threadLocalSeacher = new ThreadLocalSearcher();
    private Directory directory;
    private List<PropertyPath> indexedProperties;

    public LuceneSearcher(Directory directory, List<PropertyPath> indexedProperties) {
        this.directory = directory;
        this.indexedProperties = indexedProperties;
    }

    public List<SearchResult> searchCollectionByFullText(String collectionKey, List<String> terms) {
        return searchCollectionByFullText(collectionKey, terms, 0, 0);
    }

    public PagingList<SearchResult> searchCollectionByFullText(String collectionKey, List<String> terms, int offset, int max) {
        try {
            Query query = makeQueryFor(collectionKey, terms);
            return doSearch(query, offset, max);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private Query makeQueryFor(String collectionKey, List<String> terms) {
        BooleanQuery collectionKeyQuery = new BooleanQuery();
        collectionKeyQuery.add(new TermQuery(new Term(LuceneIndexManager.INDEX_COLLECTION_KEY, collectionKey)), BooleanClause.Occur.MUST);
        BooleanQuery termsQuery = new BooleanQuery();
        for (String term : terms) {
            BooleanQuery fieldQuery = new BooleanQuery();
            for (PropertyPath property : indexedProperties) {
                String field = property.getName();
                String lowerCaseTerm = term.toLowerCase();
                fieldQuery.add(new TermQuery(new Term(field, lowerCaseTerm)), BooleanClause.Occur.SHOULD);
            }
            termsQuery.add(fieldQuery, BooleanClause.Occur.MUST);
        }
        BooleanQuery result = new BooleanQuery();
        result.add(collectionKeyQuery, BooleanClause.Occur.MUST);
        result.add(termsQuery, BooleanClause.Occur.MUST);
        return result;
    }

    private PagingList<SearchResult> doSearch(Query query, int offset, int max) throws Exception {
        if (max == 0) {
            max = LuceneIndexManager.DEFAULT_MAX_SEARCH_RESULTS;
        }
        int limit = offset + max;
        IndexSearcher searcher = threadLocalSeacher.get();
        TopFieldDocs topFieldDocs = searcher.search(query, null, limit, new Sort(SortField.FIELD_SCORE));
        PagingList<SearchResult> entryItems = new PagingList<SearchResult>(
                new ArrayList<SearchResult>(),
                offset,
                max,
                topFieldDocs.totalHits);
        for (int i = offset; i < (offset + max) && i < topFieldDocs.totalHits; i++) {
            Document doc = searcher.doc(topFieldDocs.scoreDocs[i].doc, new MapFieldSelector(new String[]{LuceneIndexManager.INDEX_COLLECTION_KEY, LuceneIndexManager.INDEX_ENTRY_KEY}));
            String collectionKey = doc.get(LuceneIndexManager.INDEX_COLLECTION_KEY);
            String entryKey = doc.get(LuceneIndexManager.INDEX_ENTRY_KEY);
            if (collectionKey != null && entryKey != null) {
                entryItems.add(new SearchResult(collectionKey, entryKey));
            } else {
                LOG.warn("Found an entry with missing collection ({}) or entry ({}) key.", collectionKey, entryKey);
            }
        }
        return entryItems;
    }

    private class ThreadLocalSearcher extends ThreadLocal<IndexSearcher> {

        @Override
        protected IndexSearcher initialValue() {
            try {
                return new IndexSearcher(directory);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        }

        @Override
        public IndexSearcher get() {
            try {
                IndexSearcher currentSearcher = super.get();
                boolean isCurrent = currentSearcher.getIndexReader().isCurrent();
                if (isCurrent) {
                    return currentSearcher;
                } else {
                    currentSearcher.close();
                    IndexSearcher newSearcher = new IndexSearcher(directory);
                    super.set(newSearcher);
                    return newSearcher;
                }
            } catch (Exception e) {
                throw new AtomIndexingException(e.getMessage(), e);
            }
        }
    }
}
