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

import com.googlecode.actorom.Actor;
import com.googlecode.actorom.Address;
import com.googlecode.actorom.Topology;
import com.googlecode.actorom.annotation.OnMessage;
import com.googlecode.actorom.annotation.TopologyInstance;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.parser.filter.impl.ServerFilter;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementation for parsing pages and extracting outgoing links.
 *
 * @author Sergio Bossa
 */
public class ParserActor {

    private static final transient Logger LOG = LoggerFactory.getLogger(ParserActor.class);
    //
    @TopologyInstance
    private Topology actorsTopology;
    //
    private final Address processorAddress;
    //
    private final Parser parser;

    public ParserActor(Parser parser, Address processorAddress) {
        this.parser = parser;
        this.processorAddress = processorAddress;
    }

    @OnMessage(type = DownloadedPageMessage.class)
    public void parsePage(DownloadedPageMessage downloadedPageMessage) {
        Page page = downloadedPageMessage.getPage();
        Link sourceLink = page.getLink();
        Collection<Link> outgoingLinks = parser.parse(page, new ServerFilter(page.getLink()));
        LOG.info("Parsed page at: {}", sourceLink);
        LOG.info("Outgoing links: {}", outgoingLinks);
        ParsedPageMessage parsedPageMessage = new ParsedPageMessage(page, outgoingLinks);
        Actor processorActor = actorsTopology.getActor(processorAddress);
        processorActor.send(parsedPageMessage);
    }
}
