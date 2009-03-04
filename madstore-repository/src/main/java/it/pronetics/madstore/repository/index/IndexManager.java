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
package it.pronetics.madstore.repository.index;

import it.pronetics.madstore.repository.util.PagingList;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Interface providing methods for indexing, deleting and searching Atom entries.
 * <br>
 * Indexed Atom entries are identified by their (collection key, entry key) pair; indexed properties
 * are defined through a list of {@link PropertyPath}s.
 * <br>
 * Please note that implementors must be thread safe.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface IndexManager {

    /**
     * Index an {@link org.w3c.dom.Element} representing an Atom entry, using the provided
     * (collection key, entry key) pair as an identifier for the indexed entry.
     *
     * @param collectionKey The key of the collection containing the entry.
     * @param entryKey The key of the entry to index.
     * @param entry The {@link org.w3c.dom.Element} representing the Atom entry to index.
     */
    public void index(String collectionKey, String entryKey, Element entry);

    /**
     * Delete the specified Atom entry from the index.
     *
     * @param collectionKey The key of the collection containing the Atom entry to delete from the index.
     * @param entryKey The key of the Atom entry to delete from the index.
     */
    public void delete(String collectionKey, String entryKey);

    /**
     * Search a given collection for all entries containing the given list of terms into its indexed properties.
     * <br>
     * Searched terms are case-insensitive and combined through the logical AND operator.
     *
     * @param collectionKey The key of the collection to search.
     * @param terms The list of terms to search.
     * @param offset The index of the first {@link it.pronetics.madstore.repository.index.SearchResult} to read.
     * @param max The max number of {@link it.pronetics.madstore.repository.index.SearchResult}s to read, starting from offset.
     * @return The list of {@link it.pronetics.madstore.repository.index.SearchResult}s found.
     */
    public PagingList<SearchResult> searchCollectionByFullText(String collectionKey, List<String> terms, int offset, int max);

    /**
     * Get the list of Atom entry properties indexed and searhed by this <code>IndexManager</code>.
     * 
     * @return A list of {@link PropertyPath}s.
     */
    public List<PropertyPath> getIndexedProperties();
    
}
