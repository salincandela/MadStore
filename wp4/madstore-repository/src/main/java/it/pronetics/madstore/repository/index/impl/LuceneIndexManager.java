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
import it.pronetics.madstore.repository.index.PropertyPath;
import it.pronetics.madstore.repository.index.SearchResult;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.store.Directory;
import org.w3c.dom.Element;

/**
 * Lucene-based {@link it.pronetics.madstore.repository.index.IndexManager} implementation. <br>
 * Please note: this class is thread-safe.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class LuceneIndexManager implements IndexManager {

    // TODO: Make configurable?
    public static final int DEFAULT_MAX_SEARCH_RESULTS = 10000;
    //
    public static final String INDEX_PRIMARY_KEY = "primaryKey";
    public static final String INDEX_COLLECTION_KEY = "collectionKey";
    public static final String INDEX_ENTRY_KEY = "entryKey";
    private ReadWriteLock rwIndexLock = new ReentrantReadWriteLock();
    private List<PropertyPath> indexedProperties;
    private LuceneIndexer indexer;
    private LuceneSearcher searcher;

    public LuceneIndexManager(LuceneDirectoryFactory directoryFactory, List<PropertyPath> indexedProperties) {
        Directory directory = directoryFactory.makeDirectory();
        this.indexer = new LuceneIndexer(directory, indexedProperties);
        this.searcher = new LuceneSearcher(directory, indexedProperties);
        this.indexedProperties = indexedProperties;
    }

    public void index(String collectionKey, String entryKey, Element entry) {
        try {
            rwIndexLock.writeLock().lock();
            indexer.index(collectionKey, entryKey, entry);
        } finally {
            rwIndexLock.writeLock().unlock();
        }
    }

    public void delete(String collectionKey, String entryKey) {
        try {
            rwIndexLock.writeLock().lock();
            indexer.delete(collectionKey, entryKey);
        } finally {
            rwIndexLock.writeLock().unlock();
        }
    }

    public PagingList<SearchResult> searchCollectionByFullText(String collectionKey, List<String> terms, int offset, int max) {
        if (offset < 0 || max < 0) {
            throw new IllegalArgumentException("Offset and max cannot be negative integers!");
        }
        try {
            rwIndexLock.readLock().lock();
            return searcher.searchCollectionByFullText(collectionKey, terms, offset, max);
        } finally {
            rwIndexLock.readLock().unlock();
        }
    }

    public List<PropertyPath> getIndexedProperties() {
        return indexedProperties;
    }
}
