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
package it.pronetics.madstore.crawler;

import java.util.List;

/**
 * Crawler interface for starting the crawling process as defined by a list of {@link CrawlerConfiguration} objects.<br>
 * Each {@link CrawlerConfiguration} defines a different site to crawl, and related configuration parameters: a <code>MadStoreCrawler</code>,
 * so, can be configued to crawl several sites.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface MadStoreCrawler {
    
    /**
     * Get a list of crawler configuration objects.
     * @return A list of crawler configurations.
     */
    public List<CrawlerConfiguration> getCrawlerConfigurations();

    /**
     * Set the list of crawler configuration objects.
     * @param crawlerConfigurations A list of crawler configurations. 
     */
    public void setCrawlerConfigurations(List<CrawlerConfiguration> crawlerConfigurations);

    /**
     * Start the crawling process.<br>
     * It may be blocking or non-blocking, depending on the concrete implementation.
     */
    public void start();
}
