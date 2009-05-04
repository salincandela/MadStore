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
package it.pronetics.madstore.server.jaxrs.atom.search;

import it.pronetics.madstore.server.jaxrs.atom.ResourceHandler;
import javax.ws.rs.core.Response;

/**
 * Resource handler for the Atom feed representing an Open Search collection search result.
 * <br>
 * Search is executed over the entries of a given Atom collection, by using a list of case insensitive terms combined
 * through the AND logical operator.
 *
 * @param <R> Type of the response object representing the Atom feed.
 *
 * @author Sergio Bossa
 */
public interface CollectionSearchResourceHandler<R> extends ResourceHandler {

    /**
     * Get the resource object representing the Atom feed resulted from the collection search.
     *
     * @return The web response containing the Atom feed object.
     */
    public Response getCollectionSearchResource();

    /**
     * Set the key of the collection to search.
     * @param collectionKey
     */
    public void setCollectionKey(String collectionKey);

    /**
     * Set the max number of entries contained in the retrieved Atom feed.
     * @param maxNumberOfEntries
     */
    public void setMaxNumberOfEntries(int maxNumberOfEntries);

    /**
     * Set the page number of the entries contained in the retrieved Atom feed.
     * @param pageNumberOfEntries
     */
    public void setPageNumberOfEntries(int pageNumberOfEntries);

    /**
     * Set the search title.
     * @param searchTitle
     */
    public void setSearchTitle(String searchTitle);

    /**
     * Set the terms to search.
     * @param searchTerms
     */
    public void setSearchTerms(String searchTerms);
}
