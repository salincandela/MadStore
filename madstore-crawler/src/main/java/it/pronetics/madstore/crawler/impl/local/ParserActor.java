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
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.parser.filter.impl.ServerFilter;
import java.util.Collection;
import java.util.concurrent.Executors;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergio Bossa
 */
public class ParserActor implements Actor {

    private static final transient Logger LOG = LoggerFactory.getLogger(ParserActor.class);
    //
    private final Channel<ActorMessage> parserChannel = new MemoryChannel<ActorMessage>();
    private final Fiber parserFiber;
    //
    private final Parser parser;

    public ParserActor(Parser parser) {
        this.parser = parser;
        this.parserFiber = new PoolFiberFactory(Executors.newCachedThreadPool()).create();
    }

    public void start() {
        parserChannel.subscribe(parserFiber, new Callback<ActorMessage>() {

            public void onMessage(ActorMessage message) {
                try {
                    message.executeOn(ParserActor.this);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    Actor crawlerActor = message.getActorsTopology().get(CrawlerActor.class);
                    crawlerActor.send(new ErrorMessage(message.getActorsTopology(), ex));
                }
            }
        });
        parserFiber.start();
    }

    public void stop() {
        parserFiber.dispose();
    }

    public void join() {
        return;
    }

    public void send(ActorMessage message) {
        parserChannel.publish(message);
    }

    void parsePage(DownloadedPageMessage downloadedPageMessage) {
        Page page = downloadedPageMessage.getPage();
        Link sourceLink = page.getLink();
        Collection<Link> outgoingLinks = parser.parse(page, new ServerFilter(page.getLink()));
        LOG.info("Parsed page at: {}", sourceLink);
        LOG.info("Outgoing links: {}", outgoingLinks);
        ParsedPageMessage parsedPageMessage = new ParsedPageMessage(downloadedPageMessage.getActorsTopology(), page, outgoingLinks);
        Actor processorActor = downloadedPageMessage.getActorsTopology().get(ProcessorActor.class);
        processorActor.send(parsedPageMessage);
    }
}
