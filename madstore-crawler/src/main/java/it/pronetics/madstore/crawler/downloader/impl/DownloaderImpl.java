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
package it.pronetics.madstore.crawler.downloader.impl;

import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link it.pronetics.madstore.crawler.downloader.Downloader} implementation.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class DownloaderImpl implements Downloader {

    private static final Logger LOG = LoggerFactory.getLogger(DownloaderImpl.class);

    public Page download(Link link) {
        String url = link.getLink();
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        try {
            LOG.info("Downloading page from: {}", url);
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                String data = method.getResponseBodyAsString();
                return new Page(link, data);
            } else {
                return new Page(link);
            }
        } catch (Exception e) {
            LOG.warn("Download failed: {}", url);
            LOG.warn(e.getMessage());
            LOG.debug(e.getMessage(), e);
            return new Page(link);
        } finally {
            method.releaseConnection();
        }
    }
}
