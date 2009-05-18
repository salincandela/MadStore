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
import com.googlecode.actorom.KillActorException;
import com.googlecode.actorom.Topology;
import com.googlecode.actorom.annotation.OnMessage;
import com.googlecode.actorom.annotation.TopologyInstance;
import it.pronetics.madstore.crawler.model.Link;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementing the main crawling process.
 *
 * @author Sergio Bossa
 */
public class CrawlerActor {

    private static final transient Logger LOG = LoggerFactory.getLogger(CrawlerActor.class);
    //
    @TopologyInstance
    private Topology actorsTopology;
    private Address downloaderAddress;
    //
    private CountDownLatch finishLatch;
    //
    private final Map<String, Link> visitedLinks = new HashMap<String, Link>();
    private final Map<String, Link> toParseLinks = new HashMap<String, Link>();
    private int maxVisitedLinks;
    private int visitedLinksCounter = 1;

    public CrawlerActor(int maxVisitedLinks) {
        this.maxVisitedLinks = maxVisitedLinks;
    }

    @OnMessage(type=StartCrawlingMessage.class)
    public void startCrawling(StartCrawlingMessage message) {
        downloaderAddress = message.getDownloaderAddress();
        finishLatch = message.getFinishLatch();
        visitedLinks.clear();
        toParseLinks.clear();
        sendDownloadLinkMessage(message.getLink());
        
    }

    @OnMessage(type=OutgoingLinksMessage.class)
    public void crawlLinks(OutgoingLinksMessage message) {
        Collection<Link> outgoingLinks = message.getOutgoingLinks();
        for (Link outgoingLink : outgoingLinks) {
            if (((visitedLinksCounter < maxVisitedLinks)) && (!visitedLinks.containsKey(outgoingLink.getLink()))) {
                LOG.info("Crawling link-{}: {}", visitedLinksCounter, outgoingLink);
                sendDownloadLinkMessage(outgoingLink);
                ++visitedLinksCounter;
            }
        }
        toParseLinks.remove(message.getSourceLink().getLink());
        if (toParseLinks.isEmpty()) {
            visitedLinks.clear();
            toParseLinks.clear();
            finishCrawling();
        }
    }

    private void sendDownloadLinkMessage(Link link) {
        Actor downloader = actorsTopology.getActor(downloaderAddress);
        DownloadLinkMessage message = new DownloadLinkMessage(link);
        visitedLinks.put(link.getLink(), link);
        toParseLinks.put(link.getLink(), link);
        downloader.send(message);

    }

    private void finishCrawling() {
        finishLatch.countDown();
        throw new KillActorException();
    }
}
