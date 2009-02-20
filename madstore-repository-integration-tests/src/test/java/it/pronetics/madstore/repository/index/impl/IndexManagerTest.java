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

import it.pronetics.madstore.repository.index.IndexManager;
import it.pronetics.madstore.repository.index.SearchResult;
import it.pronetics.madstore.repository.test.util.Utils;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class IndexManagerTest extends AbstractDependencyInjectionSpringContextTests {

    private static final String COLLECTION_KEY = "testCollection";
    private IndexManager indexManager;

    public IndexManagerTest() {
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void testIndexThenSearchCollectionByFullTextThenDelete() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry2", Utils.getDoc("luceneentry2.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry3", Utils.getDoc("luceneentry3.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(1, elements.size());

        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry2");
        indexManager.delete(COLLECTION_KEY, "entry3");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    public void testIndexThenSearchCollectionByFullTextWithPaginationThenDelete() {
        List<String> terms = new ArrayList<String>();
        terms.add("bla");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry2", Utils.getDoc("luceneentry2.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry3", Utils.getDoc("luceneentry3.xml").getDocumentElement());

        PagingList<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 1, 0);
        List<String> keys = Arrays.<String> asList(elements.get(0).getEntryKey(), elements.get(1).getEntryKey());
        assertEquals(2, elements.size());
        assertTrue(keys.contains("entry2"));
        assertTrue(keys.contains("entry3"));
        
        assertEquals(3, elements.getTotal());
        assertEquals(LuceneIndexManager.DEFAULT_MAX_SEARCH_RESULTS, elements.getMax());
        assertEquals(1, elements.getOffset());

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 1);
        assertEquals(1, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry1", elements.get(0).getEntryKey());

        assertEquals(3, elements.getTotal());
        assertEquals(1, elements.getMax());
        assertEquals(0, elements.getOffset());
        
        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 1, 1);
        assertEquals(1, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry2", elements.get(0).getEntryKey());
        
        assertEquals(3, elements.getTotal());
        assertEquals(1, elements.getMax());
        assertEquals(1, elements.getOffset());

        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry2");
        indexManager.delete(COLLECTION_KEY, "entry3");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        
        assertEquals(0, elements.getTotal());
        assertEquals(LuceneIndexManager.DEFAULT_MAX_SEARCH_RESULTS, elements.getMax());
        assertEquals(0, elements.getOffset());
        assertEquals(0, elements.size());
    }

    public void testIndexThenSearchCollectionByFullTextWithBoostThenDelete() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry11", Utils.getDoc("luceneentry11.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(2, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry1", elements.get(0).getEntryKey());
        assertEquals(COLLECTION_KEY, elements.get(1).getColletionKey());
        assertEquals("entry11", elements.get(1).getEntryKey());

        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry11");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    public void testIndexThenSearchCollectionByFullTextWithTwoTermsInLogicalANDOnTheSameField() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1");
        terms.add("here");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry2", Utils.getDoc("luceneentry2.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(1, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry1", elements.get(0).getEntryKey());

        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry2");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    public void testIndexThenSearchCollectionByFullTextWithTwoTermsInLogicalANDOnDifferentFields() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1");
        terms.add("bla");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());
        indexManager.index(COLLECTION_KEY, "entry2", Utils.getDoc("luceneentry2.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(1, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry1", elements.get(0).getEntryKey());

        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry2");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    public void testSearchCollectionByFullTextIsCaseSensitiveForTheCollectionKey() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1");

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY.toLowerCase(), terms, 0, 0);
        assertEquals(0, elements.size());

        indexManager.delete(COLLECTION_KEY, "entry1");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    public void testSearchCollectionByFullTextIsCaseInsensitiveForTheSearchTerms() {
        List<String> terms = new ArrayList<String>();
        terms.add("textToFind1".toLowerCase());

        indexManager.index(COLLECTION_KEY, "entry1", Utils.getDoc("luceneentry1.xml").getDocumentElement());

        List<SearchResult> elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(1, elements.size());
        assertEquals(COLLECTION_KEY, elements.get(0).getColletionKey());
        assertEquals("entry1", elements.get(0).getEntryKey());

        indexManager.delete(COLLECTION_KEY, "entry1");

        elements = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
        assertEquals(0, elements.size());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:repositoryApplicationContext.xml"};
    }
}
