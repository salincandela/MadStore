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
import java.util.Map;

/**
 * @author Sergio Bossa
 */
public class ParsedPageMessage extends AbstractActorMessage {

    private final Page page;
    private final Collection<Link> outgoingLinks;

    public ParsedPageMessage(Map<Class, Actor> actorsTopology, Page page, Collection<Link> outgoingLinks) {
        super(actorsTopology);
        this.page = page;
        this.outgoingLinks = outgoingLinks;
    }

    public Page getPage() {
        return page;
    }

    public Collection<Link> getOutgoingLinks() {
        return Collections.unmodifiableCollection(outgoingLinks);
    }

    public void executeOn(ProcessorActor actor) {
        actor.processPage(this);
    }
}
