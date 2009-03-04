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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementing the main crawling process.
 *
 * @author Sergio Bossa
 */
public class CrawlerActor implements Actor {

    private static final transient Logger LOG = LoggerFactory.getLogger(CrawlerActor.class);
    //
    private final Channel<ActorMessage> crawlerChannel = new MemoryChannel<ActorMessage>();
    private final Fiber crawlerFiber = new ThreadFiber();
    private final Object finishMonitor = new Object();
    private volatile boolean finished = false;
    //
    private final Map<String, Link> visitedLinks = new HashMap<String, Link>();
    private final Map<String, Link> toParseLinks = new HashMap<String, Link>();
    private int maxVisitedLinks;
    private int visitedLinksCounter = 1;

    public CrawlerActor(int maxVisitedLinks) {
        this.maxVisitedLinks = maxVisitedLinks;
    }

    public void start() {
        crawlerChannel.subscribe(crawlerFiber, new Callback<ActorMessage>() {

            public void onMessage(ActorMessage message) {
                try {
                    message.executeOn(CrawlerActor.this);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    finish();
                }
            }
        });
        crawlerFiber.start();
    }

    public void send(ActorMessage message) {
        crawlerChannel.publish(message);
    }

    public void join() throws InterruptedException {
        synchronized (finishMonitor) {
            while (!finished) {
                finishMonitor.wait();
            }
        }
    }

    public void stop() {
        finish();
        crawlerFiber.dispose();
    }

    void startCrawling(DownloadLinkMessage message) {
        visitedLinks.clear();
        toParseLinks.clear();
        sendDownloadLinkMessage(message.getActorsTopology(), message.getLink());
    }

    void crawlLinks(OutgoingLinksMessage message) {
        Collection<Link> outgoingLinks = message.getOutgoingLinks();
        for (Link outgoingLink : outgoingLinks) {
            if (((visitedLinksCounter < maxVisitedLinks)) && (!visitedLinks.containsKey(outgoingLink.getLink()))) {
                LOG.info("Crawling link-{}: {}", visitedLinksCounter, outgoingLink);
                sendDownloadLinkMessage(message.getActorsTopology(), outgoingLink);
                ++visitedLinksCounter;
            }
        }
        toParseLinks.remove(message.getSourceLink().getLink());
        if (toParseLinks.isEmpty()) {
            visitedLinks.clear();
            toParseLinks.clear();
            finish();
        }
    }

    void stopOnError(Throwable error) {
        LOG.error(error.getMessage(), error);
        finish();
    }

    private void sendDownloadLinkMessage(Map<Class, Actor> actorsTopology, Link link) {
        Actor actor = actorsTopology.get(DownloaderActor.class);
        ActorMessage message = new DownloadLinkMessage(actorsTopology, link);
        visitedLinks.put(link.getLink(), link);
        toParseLinks.put(link.getLink(), link);
        actor.send(message);

    }

    private void finish() {
        synchronized (finishMonitor) {
            finished = true;
            finishMonitor.notifyAll();
        }
    }
}
