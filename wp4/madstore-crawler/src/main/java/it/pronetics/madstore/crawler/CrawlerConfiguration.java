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

/**
 * Crawler configuration interface, defining how to crawl a specific target site..
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public interface CrawlerConfiguration {

    /**
     * Sets the server URL address, i.e.: http://www.acme.org.
     * @param server The server address.
     */
    public void setServer(String server);

    /**
     * Gets the server URL address, i.e.: http://www.acme.org.
     * @return The server address.
     */
    public String getServer();

    /**
     * Sets the first link which start crawling from, i.e.: /inddex.html.<br>
     * This link will be appended to the server address provided in {@link #setServer(String )}.
     * @param startLink The link which start crawling from.
     */
    public void setStartLink(String startLink);

    /**
     * Gets the first link which start crawling from, i.e.: /inddex.html.
     * @return The link which start crawling from.
     */
    public String getStartLink();

    /**
     * Gets the max number of page request allowed on each second.
     * @return The max number of requests per second.
     */
    public int getMaxConcurrentDownloads();

    /**
     * Sets the max number of page request allowed on each second.
     * @param requests The max number of requests per second.
     */
    public void setMaxConcurrentDownloads(int requests);

    /**
     * Sets the {@link Pipeline} used for processing crawled pages.
     * @param pipeline The pipeline to execute.
     */
    public void setPipeline(Pipeline pipeline);

    /**
     * Gets the{@link Pipeline}.
     * @return The pipeline.
     */
    public Pipeline getPipeline();
    
    /**
     * Gets the max number of links to visit.
     * @return the max number of links.
     */
    public int getMaxVisitedLinks();
    
    /**
     * Sets the max number of links to visit.
     * @param maxVisitedLinks The max number of links.
     */
    public void setMaxVisitedLinks(int maxVisitedLinks);
}
