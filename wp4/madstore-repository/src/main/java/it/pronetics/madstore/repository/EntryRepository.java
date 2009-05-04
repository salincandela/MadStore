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
package it.pronetics.madstore.repository;

import it.pronetics.madstore.repository.index.IndexManager;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Repository interface for storing Atom entries.<br>
 * Atom entries are read and written as {@link org.w3c.dom.Element} objects.
 * @author Sergio Bossa
 * @author Salvatore Incandela
 */
public interface EntryRepository {

    /**
     * Puts an entry {@link org.w3c.dom.Element} into the repository under the specified collection key,
     * overwriting the already existent one, if any.
     * @param collectionKey the parent collection key.
     * @param entryElement the {@link org.w3c.dom.Element} that represent the entry.
     * @return the key of the inserted entry.
     */
    public String put(String collectionKey, Element entryElement);

    /**
     * Puts an entry {@link org.w3c.dom.Element} into the repository under the specified collection key only
     * if not already present.
     * @param collectionKey the parent collection key.
     * @param entryElement the {@link org.w3c.dom.Element} that represent the entry.
     * @return the key of the inserted entry or null if the entry as not be inserted because already present.
     */
    public String putIfAbsent(String collectionKey, Element entryElement);

    /**
     * Puts an entry {@link org.w3c.dom.Element} into the repository under the specified collection key only
     * if the update date is newer than the existent. Please note that the entry specified must be present
     * into the repository.
     * @param collectionKey The parent collection key.
     * @param entryElement the {@link org.w3c.dom.Element} that represent the entry.
     * @return the key of the updated entry or null if the specified entry is older on equal to the one
     *         already present.
     */
    public String updateIfNewer(final String collectionKey, final Element entryElement);

    /**
     * Deletes the entry {@link org.w3c.dom.Element} under the specified collection key.
     * @param collectionKey the parent collection key.
     * @param entryKey the entry key to delete.
     * @return true if entry was deleted, false if wasn't or if the specified entry is not present into the
     *         repository.
     */
    public Boolean delete(String collectionKey, String entryKey);

    /**
     * @param collectionKey the parent collection.
     * @param entryKey the entry key to read.
     * @return the entry {@link org.w3c.dom.Element} found or null if the specified entry is not present into
     *         the repository.
     */
    public Element read(String collectionKey, String entryKey);

    /**
     * Lists all the entries {@link org.w3c.dom.Element} contained under the collection key.
     * @param collectionKey the parent collection key.
     * @return the {@link java.util.List} of entry {@link org.w3c.dom.Element}s found.
     */
    public List<Element> readEntries(String collectionKey);

    /**
     * Lists the specified number of entries {@link org.w3c.dom.Element} contained under the collection key.
     * @param collectionKey the parent collection key.
     * @param offset represents the index of the first entry to read.
     * @param max the max number of entries to read, starting from offset.
     * @return the {@link it.pronetics.madstore.repository.util.PagingList} of entry
     *         {@link org.w3c.dom.Element}s found.
     */
    public PagingList<Element> readEntries(String collectionKey, int offset, int max);

    /**
     * Checks if the entry under the specified collection key exists.
     * @param collectionKey the parent collection key.
     * @param entryKey the entry key to check.
     * @return true if the specified entry exists or false if it doesn't.
     */
    public boolean contains(String collectionKey, String entryKey);

    /**
     * Finds all the entries {@link org.w3c.dom.Element} into the specified collection containing the
     * specified {@link java.util.List} of terms.
     * @param collectionKey The parent collection key.
     * @param terms The {@link java.util.List} of terms to search.
     * @return The {@link java.util.List} of entry {@link org.w3c.dom.Element}s found.
     */
    public List<Element> findEntries(String collectionKey, List<String> terms);

    /**
     * Finds the entries {@link org.w3c.dom.Element} into the specified collection containing the specified
     * {@link java.util.List} of terms.
     * @param collectionKey The parent collection key.
     * @param terms The {@link java.util.List} of terms to search.
     * @param offset represents the index of the first entry to return.
     * @param max the max number of entries to return starting from offset.
     * @return The {@link it.pronetics.madstore.repository.util.PagingList} of {@link org.w3c.dom.Element}s
     *         found.
     */
    public PagingList<Element> findEntries(String collectionKey, List<String> terms, int offset, int max);

    /**
     * Get the {@link it.pronetics.madstore.repository.index.IndexManager} that will be used by this
     * repository for indexing and finding entries.
     * @return The {@link it.pronetics.madstore.repository.index.IndexManager}.
     */
    public IndexManager getIndexManager();

}
