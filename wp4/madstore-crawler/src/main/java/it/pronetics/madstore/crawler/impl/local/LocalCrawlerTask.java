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
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.impl.CrawlerTask;
import it.pronetics.madstore.crawler.model.Link;

import it.pronetics.madstore.crawler.parser.Parser;

import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.HashMap;
import java.util.Map;
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
        try {
            CrawlerActor crawlerActor = startCrawlerActor();
            DownloaderActor downloaderActor = startDownloaderActor();
            ParserActor parserActor = startParserActor();
            ProcessorActor processorActor = startProcessorActor();
            //
            LOG.info("Crawling process started from {}", startLink);
            Map<Class, Actor> actorsTopology = new HashMap<Class, Actor>();
            actorsTopology.put(CrawlerActor.class, crawlerActor);
            actorsTopology.put(DownloaderActor.class, downloaderActor);
            actorsTopology.put(ParserActor.class, parserActor);
            actorsTopology.put(ProcessorActor.class, processorActor);
            DownloadLinkMessage startLinkMessage = new DownloadLinkMessage(actorsTopology, startLink);
            crawlerActor.send(startLinkMessage);
            crawlerActor.join();
            LOG.info("Crawling process stopped!");
            //
            stopActor(crawlerActor);
            stopActor(downloaderActor);
            stopActor(parserActor);
            stopActor(processorActor);
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private CrawlerActor startCrawlerActor() {
        CrawlerActor actor = new CrawlerActor(maxVisitedLinks);
        actor.start();
        return actor;
    }

    private DownloaderActor startDownloaderActor() {
        DownloaderActor actor = new DownloaderActor(downloader, maxConcurrentDownloads);
        actor.start();
        return actor;
    }

    private ParserActor startParserActor() {
        ParserActor actor = new ParserActor(parser);
        actor.start();
        return actor;
    }

    private ProcessorActor startProcessorActor() {
        ProcessorActor actor = new ProcessorActor(publisher, pipeline);
        actor.start();
        return actor;
    }

    private void stopActor(Actor actor) {
        actor.stop();
    }
}
