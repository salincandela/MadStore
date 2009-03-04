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

import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;

/**
 * Factory interface for creating specific {@link CrawlerTask} implementations.
 * 
 * @author Sergio Bossa
 */
public interface CrawlerTaskFactory {

    /**
     * Make and configure a fresh crawler task instance.
     *
     * @param downloader The {@link it.pronetics.madstore.crawler.downloader.Downloader} to use for downloading web pages.
     * @param parser The {@link it.pronetics.madstore.crawler.parser.Parser} to use for parsing web pages.
     * @param publisher The {@link it.pronetics.madstore.crawler.publisher.AtomPublisher} to use for publishing Atom contents to repositories.
     * @param pipeline The {@link it.pronetics.madstore.crawler.Pipeline} to use for processing web pages.
     * @param maxConcurrentDownloads The max number of concurrent page downloads on a single target site.
     * @param maxVisitedLinks The max number of visited links on a single target site.
     * @return The crawler task instance.
     */
    public CrawlerTask makeCrawlerTask(Downloader downloader, Parser parser, AtomPublisher publisher, Pipeline pipeline, int maxConcurrentDownloads, int maxVisitedLinks);
}
