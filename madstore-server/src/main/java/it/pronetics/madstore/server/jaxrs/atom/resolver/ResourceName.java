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
package it.pronetics.madstore.server.jaxrs.atom.resolver;

/**
 * Logical resource names for the Atom Server.
 *
 * @author Sergio Bossa
 */
public class ResourceName {

    /**
     * Atom Service Document resource.
     */
    public static final String SERVICE = "service";
    /**
     * Atom Collection Feed resource.
     */
    public static final String COLLECTION = "collection";
    /**
     * Atom Entry resource.
     */
    public static final String ENTRY = "entry";
    /**
     * Open Search based collection search.
     */
    public static final String COLLECTION_SEARCH = "collectionSearch";
    /**
     * Open Search description document.
     */
    public static final String SEARCH_DESCRIPTION = "searchDescription";
}
