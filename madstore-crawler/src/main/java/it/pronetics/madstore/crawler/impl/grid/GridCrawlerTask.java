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
package it.pronetics.madstore.crawler.impl.grid;

import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.impl.CrawlerTask;
import it.pronetics.madstore.crawler.impl.grid.support.MadStoreGrid;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.gridgain.grid.GridException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grid-based implementation for {@link it.pronetics.madstore.crawler.impl.CrawlerTask}.
 * @author Sergio Bossa
 * @author Christian Mongillo
 */
public class GridCrawlerTask implements CrawlerTask {

    private static final transient Logger LOG = LoggerFactory.getLogger(GridCrawlerTask.class);
    private final MadStoreGrid madStoreGrid;
    private final Downloader downloader;
    private final Parser parser;
    private final AtomPublisher publisher;
    private final Pipeline pipeline;
    private final int maxConcurrentDownload;
    private final HashMap<String, Page> visitedLinks = new HashMap<String, Page>();
    private final HashMap<String, Link> toParseLinks = new HashMap<String, Link>();
    private int maxVisitedLinks;
    private int visitedLinksCounter = 1;

    public GridCrawlerTask(MadStoreGrid madStoreGrid, Downloader downloader, Parser parser, AtomPublisher publisher, Pipeline pipeline, int maxConcurrentDownload, int maxVisitedLinks) {
        this.madStoreGrid = madStoreGrid;
        this.downloader = downloader;
        this.parser = parser;
        this.publisher = publisher;
        this.pipeline = pipeline;
        this.maxConcurrentDownload = maxConcurrentDownload;
        this.maxVisitedLinks = maxVisitedLinks;
    }

    /**
     * Execute the crawling process on the target site, starting from the given link. <br>
     * The process is composed by the following sequential steps:
     * <ol>
     * <li>Crawling and harvesting of page links, executed by distributed grid nodes.</li>
     * <li>Pipeline-based processing of linked pages, executed by distributed grid nodes.</li>
     * <li>Storing of extracted Atom feeds, executed locally.</li>
     * </ol>
     */
    public void execute(Link startLink) {
        try {
            toParseLinks.put(startLink.getLink(), startLink);
            Collection<Page> extractedPages = doParsing();
            Collection<Page> processedPages = doProcessing(extractedPages);
            doPublishing(processedPages);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private Collection<Page> doParsing() throws GridException {
        do {
            Collection<Link> linksToVisit = getLinksToVisit();
            LOG.info("Downloading and parsing links ...", linksToVisit.size());
            if (linksToVisit.size() > 0) {
                LOG.info("Downloading and parsing {} links.", linksToVisit.size());
                ParserTask task = new ParserTask(parser, downloader);
                Collection<ParserTaskResult> results = madStoreGrid.<Collection<Link>, Collection<ParserTaskResult>>executeInGrid(task, linksToVisit);
                for (ParserTaskResult result : results) {
                    if (result != null) {
                        pushVisitedPage(result.getPage());
                        pushExtractedLinks(result.getExtractedLinks());
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("To parse links : {}", toParseLinks.keySet());
                    LOG.debug("Visited links : {}", visitedLinks.keySet());
                }
            }
        } while (!toParseLinks.isEmpty());
        return Collections.unmodifiableCollection(visitedLinks.values());
    }

    private Collection<Link> getLinksToVisit() {
        ArrayList<Link> linksToVisit = new ArrayList<Link>();
        int limit = 1;
        while (!toParseLinks.isEmpty() && limit <= maxConcurrentDownload) {
            Collection<String> linksToParse = toParseLinks.keySet();
            String link = (String) linksToParse.toArray()[0];
            if (!visitedLinks.containsKey(link)) {
                linksToVisit.add(toParseLinks.get(link));
                limit++;
            }
            toParseLinks.remove(link);
        }
        LOG.debug("Link to visit : {}", linksToVisit);
        return linksToVisit;
    }

    private void pushVisitedPage(Page page) {
        visitedLinks.put(page.getLink().getLink(), page);
    }

    private void pushExtractedLinks(Collection<Link> links) {
        for (Link link : links) {
            if ((visitedLinksCounter < maxVisitedLinks) && (!toParseLinks.containsKey(link.getLink()) && !visitedLinks.containsKey(link.getLink()))) {
                toParseLinks.put(link.getLink(), link);
                ++visitedLinksCounter;
            }
        }
    }

    private Collection<Page> doProcessing(Collection<Page> extractedPages) {
        LOG.info("Start page processing ...");
        ProcessorTask task = new ProcessorTask(pipeline);
        try {
            Collection<ProcessorTaskResult> results = madStoreGrid.<Collection<Page>, Collection<ProcessorTaskResult>>executeInGrid(task, extractedPages);
            Collection<Page> processedPages = new LinkedList<Page>();
            for (ProcessorTaskResult result : results) {
                if (result != null) {
                    processedPages.add(result.getPage());
                }
            }
            return processedPages;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return new ArrayList<Page>(0);
        }
    }

    private void doPublishing(Collection<Page> processedPages) {
        LOG.info("Start page publishing ...");
        for (Page page : processedPages) {
            publisher.publish(page);
        }
    }
}
