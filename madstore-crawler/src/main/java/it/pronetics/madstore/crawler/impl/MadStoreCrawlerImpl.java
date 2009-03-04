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
package it.pronetics.madstore.crawler.impl;

import it.pronetics.madstore.crawler.CrawlerConfiguration;
import it.pronetics.madstore.crawler.MadStoreCrawler;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.parser.Parser;

import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link it.pronetics.madstore.crawler.MadStoreCrawler} implementation.<br>
 * The actual crawling process execution is defined by a {@link CrawlerTask} implementation,
 * created through the configured {@link CrawlerTaskFactory}.
 * <br>
 * Each site is crawled concurrently by a different {@link CrawlerTask} instance: the whole
 * crawling cycle ends once all configured sites are crawled.
 *
 * @author Sergio Bossa
 * @author Salvatore Incandela
 */
public class MadStoreCrawlerImpl implements MadStoreCrawler {

    private static final transient Logger LOG = LoggerFactory.getLogger(MadStoreCrawlerImpl.class);
    private ExecutorService crawlerExecutor = Executors.newCachedThreadPool();
    private List<CrawlerConfiguration> crawlerConfigurations;
    private CrawlerTaskFactory crawlerTaskFactory;
    private Parser parser;
    private Downloader downloader;
    private AtomPublisher publisher;

    public void setCrawlerConfigurations(List<CrawlerConfiguration> crawlerConfigurations) {
        this.crawlerConfigurations = new LinkedList<CrawlerConfiguration>(crawlerConfigurations);
    }

    public List<CrawlerConfiguration> getCrawlerConfigurations() {
        return Collections.unmodifiableList(crawlerConfigurations);
    }

    /**
     * Start the crawling process, composed by a concurrent crawling task for each site to crawl.
     * <br>
     * This method call is blocking: it ends once all sites are crawled.
     */
    public void start() {
        try {
            LOG.info("Start crawling process.");
            Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>(crawlerConfigurations.size());
            for (final CrawlerConfiguration configuration : this.crawlerConfigurations) {
                final String server = configuration.getServer();
                final String startLink = configuration.getStartLink();
                tasks.add(new Callable() {

                    public Object call() throws Exception {
                        CrawlerTask task = crawlerTaskFactory.makeCrawlerTask(
                                downloader, parser, publisher,
                                configuration.getPipeline(),
                                configuration.getMaxConcurrentDownloads(),configuration.getMaxVisitedLinks());
                        task.execute(new Link(server + "/" + startLink));
                        return null;
                    }
                });
            }
            crawlerExecutor.invokeAll(tasks);
            LOG.info("Finished crawling process.");
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public void setCrawlerTaskFactory(CrawlerTaskFactory crawlerTaskFactory) {
        this.crawlerTaskFactory = crawlerTaskFactory;
    }

    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public void setPublisher(AtomPublisher publisher) {
        this.publisher = publisher;
    }
}
