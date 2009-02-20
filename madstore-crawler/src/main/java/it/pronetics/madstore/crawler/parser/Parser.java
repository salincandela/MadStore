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
package it.pronetics.madstore.crawler.parser;

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.filter.LinkFilter;

import java.util.Collection;

/**
 * Parser interface for extracting {@link it.pronetics.madstore.crawler.model.Link}s from a given 
 * {@link it.pronetics.madstore.crawler.model.Page}.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface Parser {
    
    public Collection<Link> parse(Page data, LinkFilter linkFilter);
}
