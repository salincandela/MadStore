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

import it.pronetics.madstore.repository.support.AtomRepositoryException;
import java.util.List;
import org.w3c.dom.Element;

/**
 * Repository interface for storing Atom Publishing Protocol collections.<br>
 * Atom collections are read and written as {@link org.w3c.dom.Element} objects.
 * @author Sergio Bossa
 * @author Salvatore Incandela
 */
public interface CollectionRepository {

    /**
     * Puts the specified collection {@link org.w3c.dom.Element} into the repository only if is not already
     * present.
     * @param collectionElement the {@link org.w3c.dom.Element} that represent the collection.
     * @return the key of the inserted collection or null if the collection as not be inserted because already
     *         present.
     */
    public String putIfAbsent(Element collectionElement) throws AtomRepositoryException;

    /**
     * Deletes the collection {@link org.w3c.dom.Element} with the specified collection key.
     * <br>
     * Only collections with no referring entries can be deleted. In order to delete a collection
     * referred by one or more entries, you have to delete the entries first.
     * @param collectionKey the collection key to delete.
     * @return true if collection was deleted, false if wasn't or if the specified collection is not present
     *         into the repository.
     */
    public Boolean delete(final String collectionKey) throws AtomRepositoryException;

    /**
     * Reads the collection {@link org.w3c.dom.Element} with the specified collection key.
     * @param collectionKey the collection key to read.
     * @return the collection {@link org.w3c.dom.Element} found or null if the specified collection is not
     *         present into the repository.
     */
    public Element read(String collectionKey) throws AtomRepositoryException;

    /**
     * Lists all the collections {@link org.w3c.dom.Element} contained into the repository.
     * @return the {@link java.util.List} of collection {@link org.w3c.dom.Element}s found.
     */
    public List<Element> readCollections() throws AtomRepositoryException;

    /**
     * Checks if the collection with the specified collection key exists.
     * @param collectionKey the entry key to check.
     * @return true if the specified collection exists or false if it doesn't.
     */
    public boolean contains(String collectionKey);
}
