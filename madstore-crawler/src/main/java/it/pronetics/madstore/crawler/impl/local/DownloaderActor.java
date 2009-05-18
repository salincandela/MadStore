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
import com.googlecode.actorom.annotation.OnKill;
import com.googlecode.actorom.annotation.OnMessage;
import com.googlecode.actorom.annotation.TopologyInstance;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementation for downloading links.
 *
 * @author Sergio Bossa
 */
public class DownloaderActor {

    private static final transient Logger LOG = LoggerFactory.getLogger(DownloaderActor.class);
    //
    @TopologyInstance
    private Topology actorsTopology;
    //
    private final Address crawlerAddress;
    private final Address parserAddress;
    //
    private final int maxConcurrentDownloads;
    private final ExecutorService threadPool;
    private final Downloader downloader;

    public DownloaderActor(int maxConcurrentDownloads, Downloader downloader, Address crawlerAddress, Address parserAddress) {
        this.maxConcurrentDownloads = maxConcurrentDownloads;
        this.threadPool = Executors.newFixedThreadPool(maxConcurrentDownloads);
        this.downloader = downloader;
        this.crawlerAddress = crawlerAddress;
        this.parserAddress = parserAddress;
    }

    @OnMessage(type = DownloadLinkMessage.class)
    public void downloadLink(DownloadLinkMessage message) {
        final Link link = message.getLink();
        threadPool.execute(new Runnable() {

            public void run() {
                Page page = null;
                try {
                    page = downloader.download(link);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                if (page != null && !page.isEmpty()) {
                    LOG.info("Downloaded page at: {}", link);
                    DownloadedPageMessage downloadedPageMessage = new DownloadedPageMessage(page);
                    Actor parserActor = actorsTopology.getActor(parserAddress);
                    parserActor.send(downloadedPageMessage);
                } else {
                    LOG.info("Failed to download page at: {}", link);
                    OutgoingLinksMessage outgoingLinksMessage = new OutgoingLinksMessage(link, new ArrayList<Link>(0));
                    Actor crawlerActor = actorsTopology.getActor(crawlerAddress);
                    crawlerActor.send(outgoingLinksMessage);
                }
            }
        });
    }

    @OnKill
    public void onKill() {
        threadPool.shutdown();
    }

}
