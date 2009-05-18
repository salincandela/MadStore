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
import com.googlecode.actorom.local.LocalTopology;
import com.googlecode.actorom.support.ThreadingPolicies;
import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.impl.CrawlerTask;
import it.pronetics.madstore.crawler.model.Link;

import it.pronetics.madstore.crawler.parser.Parser;

import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link it.pronetics.madstore.crawler.impl.CrawlerTask} implementation based on the actors concurrency
 * model. <br>
 * It splits the crawling process in the following parallel actors:
 * <ul>
 * <li>{@link CrawlerActor}</li>
 * <li>{@link DownloaderActor}</li>
 * <li>{@link ParserActor}</li>
 * <li>{@link ProcessorActor}</li>
 * </ul>
 * @author Sergio Bossa
 */
public class LocalCrawlerTask implements CrawlerTask {

    private static final transient Logger LOG = LoggerFactory.getLogger(LocalCrawlerTask.class);
    //
    private final Downloader downloader;
    private final Parser parser;
    private final AtomPublisher publisher;
    private final Pipeline pipeline;
    private final int maxConcurrentDownloads;
    private int maxVisitedLinks;

    public LocalCrawlerTask(Downloader downloader, Parser parser, AtomPublisher publisher, Pipeline pipeline, int maxConcurrentDownloads, int maxVisitedLinks) {
        this.downloader = downloader;
        this.parser = parser;
        this.publisher = publisher;
        this.pipeline = pipeline;
        this.maxConcurrentDownloads = maxConcurrentDownloads;
        this.maxVisitedLinks = maxVisitedLinks;
    }

    public void execute(Link startLink) {
        Topology actorsTopology = new LocalTopology(ThreadingPolicies.newOSThreadingPolicy(4));
        try {
            LOG.info("Crawling process started from {}", startLink);

            Address crawlerAddress = actorsTopology.spawnActor(CrawlerActor.class.toString(), new CrawlerActor(maxVisitedLinks));
            Address processorAddress = actorsTopology.spawnActor(ProcessorActor.class.toString(), new ProcessorActor(publisher, pipeline, crawlerAddress));
            Address parserAddress = actorsTopology.spawnActor(ParserActor.class.toString(), new ParserActor(parser, processorAddress));
            Address downloaderAddress = actorsTopology.spawnActor(DownloaderActor.class.toString(), new DownloaderActor(maxConcurrentDownloads, downloader, crawlerAddress, parserAddress));

            Actor crawlerActor = actorsTopology.getActor(crawlerAddress);
            Actor downloaderActor = actorsTopology.getActor(downloaderAddress);
            Actor parserActor = actorsTopology.getActor(parserAddress);
            Actor processorActor = actorsTopology.getActor(processorAddress);

            crawlerActor.link(downloaderActor);
            crawlerActor.link(parserActor);
            crawlerActor.link(processorActor);

            CountDownLatch finishLatch = new CountDownLatch(1);
            crawlerActor.send(new StartCrawlingMessage(startLink, downloaderAddress, finishLatch));
            finishLatch.await();

            LOG.info("Crawling process stopped!");
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            actorsTopology.shutdown();
        }
    }
}
