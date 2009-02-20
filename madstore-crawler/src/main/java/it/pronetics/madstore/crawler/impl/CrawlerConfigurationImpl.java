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
import it.pronetics.madstore.crawler.Pipeline;

/**
 * Default {@link it.pronetics.madstore.crawler.CrawlerConfiguration} implementation.
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class CrawlerConfigurationImpl implements CrawlerConfiguration {

    private String server;
    private String startLink;
    private int maxConcurrentDownloads;
    private Pipeline pipeline;
    private int maxVisitedLinks;

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        if (server.endsWith("/")) {
            this.server = server.substring(0, server.length() - 1);
        } else {
            this.server = server;
        }
    }

    public String getStartLink() {
        return this.startLink;
    }

    public void setStartLink(String startLink) {
        if (startLink.startsWith("/")) {
            this.startLink = startLink.substring(1);
        } else {
            this.startLink = startLink;
        }
    }

    public int getMaxConcurrentDownloads() {
        return maxConcurrentDownloads;
    }

    public void setMaxConcurrentDownloads(int maxConcurrentDownloads) {
        this.maxConcurrentDownloads = maxConcurrentDownloads;
    }

    public int getMaxVisitedLinks() {
        return this.maxVisitedLinks;
    }

    public void setMaxVisitedLinks(int maxVisitedLinks) {
        this.maxVisitedLinks = maxVisitedLinks;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

}
