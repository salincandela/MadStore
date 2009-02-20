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
package it.pronetics.madstore.crawler.transformer;

import it.pronetics.madstore.crawler.model.Page;

/**
 * Transformer interface for transforming {@link it.pronetics.madstore.crawler.model.Page} data.
 * 
 * @author Salvatore Incandela
 */
public interface Transformer {

    /**
     * Transforms {@link it.pronetics.madstore.crawler.model.Page} data into a byte array containing the transformation result.
     * 
     * @param data The page to transform.
     * @return The byte array, as resulted from the transformation.
     */
    public byte[] transform(Page data);
}
