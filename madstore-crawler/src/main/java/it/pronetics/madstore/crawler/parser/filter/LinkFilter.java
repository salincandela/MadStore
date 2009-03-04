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
package it.pronetics.madstore.crawler.parser.filter;

import it.pronetics.madstore.crawler.model.Link;

/**
 * A filter interface defining what kind of {@link it.pronetics.madstore.crawler.model.Link}s to accept.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface LinkFilter {

    /**
     * Accept a {@link it.pronetics.madstore.crawler.model.Link}.
     * @param link The link to accept.
     * @return True if accepted, false otherwise.
     */
    public boolean accept(Link link);
}
