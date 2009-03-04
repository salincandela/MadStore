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

import org.apache.lucene.store.Directory;

/**
 * Factory for creating Lucene {@link org.apache.lucene.store.Directory} implementations.
 *
 * @author Salvatore Incandela
 */
public interface LuceneDirectoryFactory {

    /**
     * Make the Lucene directory.
     * @return The directory instance.
     */
    public Directory makeDirectory();
}
