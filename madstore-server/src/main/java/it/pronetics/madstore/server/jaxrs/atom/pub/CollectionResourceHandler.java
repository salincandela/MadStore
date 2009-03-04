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
package it.pronetics.madstore.server.jaxrs.atom.pub;

import it.pronetics.madstore.server.jaxrs.atom.ResourceHandler;

/**
 * Resource handler for the Atom collection feed.
 *
 * @param <R> Type of the response object representing the Atom collection feed.
 *
 * @author Sergio Bossa
 */
public interface CollectionResourceHandler<R> extends ResourceHandler {

    /**
     * Get the resource object representing the Atom collection feed.
     *
     * @return The Atom collection feed object.
     */
    public R getCollectionResource();

    /**
     * Set the collection key of the Atom collection feed to retrieve.
     * @param collectionKey
     */
    public void setCollectionKey(String collectionKey);

    /**
     * Set the max number of Atom entries contained in the retrieved feed.
     * @param maxNumberOfEntries
     */
    public void setMaxNumberOfEntries(int maxNumberOfEntries);

    /**
     * Set the page number of the entries contained in the retrieved feed.
     * @param pageNumberOfEntries
     */
    public void setPageNumberOfEntries(int pageNumberOfEntries);
}
