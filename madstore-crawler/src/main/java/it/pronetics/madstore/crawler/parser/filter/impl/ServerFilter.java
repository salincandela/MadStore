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
package it.pronetics.madstore.crawler.parser.filter.impl;

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.parser.filter.LinkFilter;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link it.pronetics.madstore.crawler.parser.filter.LinkFilter} implementation accepting only those links belonging to a given host link.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class ServerFilter implements LinkFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ServerFilter.class);
    private Link sourceLink;

    public ServerFilter(Link link) {
        this.sourceLink = link;
    }

    public boolean accept(Link link) {
        try {
            URI sourceUri = new URI(sourceLink.toString());
            String base = sourceUri.getScheme() + "://" + sourceUri.getHost() + (sourceUri.getPort() > 0 ? ":" + sourceUri.getPort() : "") + "/";
            if (link.startsWith(base)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }
}