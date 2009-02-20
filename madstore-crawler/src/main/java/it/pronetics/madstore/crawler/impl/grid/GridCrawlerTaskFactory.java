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
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.impl.CrawlerTask;
import it.pronetics.madstore.crawler.impl.CrawlerTaskFactory;
import it.pronetics.madstore.crawler.impl.grid.support.MadStoreGrid;

/**
 * {@link it.pronetics.madstore.crawler.impl.CrawlerTaskFactory} implementation for
 * {@link GridCrawlerTask}.
 *
 * @author Christian Mongillo
 * @author Sergio Bossa
 */
public class GridCrawlerTaskFactory implements CrawlerTaskFactory {

    private final MadStoreGrid madStoreGrid;

    public GridCrawlerTaskFactory(MadStoreGrid madStoreGrid) {
        this.madStoreGrid = madStoreGrid;
    }
    
    public CrawlerTask makeCrawlerTask(Downloader downloader, Parser parser, AtomPublisher publisher, Pipeline pipeline, int maxConcurrentDownloads, int maxVisitedLinks) {
        return new GridCrawlerTask(madStoreGrid, downloader, parser, publisher, pipeline, maxConcurrentDownloads, maxVisitedLinks);
    }
}
