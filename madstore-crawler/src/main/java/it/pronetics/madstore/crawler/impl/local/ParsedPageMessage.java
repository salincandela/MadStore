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
package it.pronetics.madstore.crawler.impl.local;

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import java.util.Collection;
import java.util.Collections;

/**
 * Message implementation notifying the parsing of a page, and carrying on its outgoing links.
 *
 * @author Sergio Bossa
 */
public class ParsedPageMessage {

    private final Page page;
    private final Collection<Link> outgoingLinks;

    public ParsedPageMessage(Page page, Collection<Link> outgoingLinks) {
        this.page = page;
        this.outgoingLinks = outgoingLinks;
    }

    public Page getPage() {
        return page;
    }

    public Collection<Link> getOutgoingLinks() {
        return Collections.unmodifiableCollection(outgoingLinks);
    }
}
