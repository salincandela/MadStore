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

import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
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
public class ProcessorActor implements Actor {

    private static final transient Logger LOG = LoggerFactory.getLogger(ProcessorActor.class);
    //
    private final Channel<ActorMessage> processorChannel = new MemoryChannel<ActorMessage>();
    private final Fiber processorFiber;
    //
    private final AtomPublisher publisher;
    private final Pipeline pipeline;

    public ProcessorActor(AtomPublisher publisher, Pipeline pipeline) {
        this.publisher = publisher;
        this.pipeline = pipeline;
        this.processorFiber = new PoolFiberFactory(Executors.newCachedThreadPool()).create();
    }

    public void start() {
        processorChannel.subscribe(processorFiber, new Callback<ActorMessage>() {

            public void onMessage(ActorMessage message) {
                try {
                    message.executeOn(ProcessorActor.this);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    Actor crawlerActor = message.getActorsTopology().get(CrawlerActor.class);
                    crawlerActor.send(new ErrorMessage(message.getActorsTopology(), ex));
                }
            }
        });
        processorFiber.start();
    }

    public void stop() {
        processorFiber.dispose();
    }

    public void join() {
        return;
    }

    public void send(ActorMessage message) {
        processorChannel.publish(message);
    }

    void processPage(ParsedPageMessage parsedPageMessage) {
        Page page = parsedPageMessage.getPage();
        Link sourceLink = page.getLink();
        Collection<Link> outgoingLinks = parsedPageMessage.getOutgoingLinks();
        Page processedPage = pipeline.execute(page);
        if (processedPage != null) {
            LOG.info("Publishing page at: {}", sourceLink);
            publisher.publish(processedPage);
        } else {
            LOG.info("Page at {} will not be published!", sourceLink);
        }
        OutgoingLinksMessage outgoingLinksMessage = new OutgoingLinksMessage(parsedPageMessage.getActorsTopology(), sourceLink, outgoingLinks);
        Actor crawlerActor = parsedPageMessage.getActorsTopology().get(CrawlerActor.class);
        crawlerActor.send(outgoingLinksMessage);
    }
}
