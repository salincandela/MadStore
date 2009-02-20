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
 * Resource handler for the Atom entry.
 *
 * @param <R> Type of the response object representing the Atom entry.
 *
 * @author Sergio Bossa
 */
public interface EntryResourceHandler<R> extends ResourceHandler {

    public R getEntryResource();

    public void setCollectionKey(String collectionKey);

    public void setEntryKey(String entryKey);
}
