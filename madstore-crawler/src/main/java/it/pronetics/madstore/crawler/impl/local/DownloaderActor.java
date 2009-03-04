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

import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementation for downloading links.
 *
 * @author Sergio Bossa
 */
public class DownloaderActor implements Actor {

    private static final transient Logger LOG = LoggerFactory.getLogger(DownloaderActor.class);
    //
    private final Channel<ActorMessage> downloaderChannel = new MemoryChannel<ActorMessage>();
    private final Fiber downloaderFiber;
    //
    private final Downloader downloader;

    public DownloaderActor(Downloader downloader, int maxConcurrentDownloads) {
        this.downloader = downloader;
        this.downloaderFiber = new PoolFiberFactory(Executors.newFixedThreadPool(maxConcurrentDownloads)).create();
    }

    public void start() {
        downloaderChannel.subscribe(downloaderFiber, new Callback<ActorMessage>() {

            public void onMessage(ActorMessage message) {
                try {
                    message.executeOn(DownloaderActor.this);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    Actor crawlerActor = message.getActorsTopology().get(CrawlerActor.class);
                    crawlerActor.send(new ErrorMessage(message.getActorsTopology(), ex));
                }
            }
        });
        downloaderFiber.start();
    }

    public void stop() {
        downloaderFiber.dispose();
    }

    public void join() {
        return;
    }

    public void send(ActorMessage message) {
        downloaderChannel.publish(message);
    }

    void downloadLink(DownloadLinkMessage message) {
        Link link = message.getLink();
        Page page = downloader.download(link);
        if (page != null && !page.isEmpty()) {
            LOG.info("Downloaded page at: {}", link);
            DownloadedPageMessage downloadedPageMessage = new DownloadedPageMessage(message.getActorsTopology(), page);
            Actor parserActor = message.getActorsTopology().get(ParserActor.class);
            parserActor.send(downloadedPageMessage);
        } else {
            LOG.info("Failed to download page at: {}", link);
            OutgoingLinksMessage outgoingLinksMessage = new OutgoingLinksMessage(message.getActorsTopology(), link, new ArrayList<Link>(0));
            Actor crawlerActor = message.getActorsTopology().get(CrawlerActor.class);
            crawlerActor.send(outgoingLinksMessage);
        }
    }
}
