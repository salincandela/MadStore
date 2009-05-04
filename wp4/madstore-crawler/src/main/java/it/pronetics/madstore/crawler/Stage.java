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
package it.pronetics.madstore.crawler;

import it.pronetics.madstore.crawler.model.Page;

/**
 * Stage interface rapresenting a single execution step of a {@link Pipeline}.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface Stage {

    /**
     * Process the input {@link it.pronetics.madstore.crawler.model.Page} and
     * returns an output {@link it.pronetics.madstore.crawler.model.Page},
     * whose content depends on the particular stage implementation.<br>
     * 
     * @param page The page to process.
     * @return The processed page, or null if no suitable page can be returned.
     */
    public Page execute(Page page);
}
