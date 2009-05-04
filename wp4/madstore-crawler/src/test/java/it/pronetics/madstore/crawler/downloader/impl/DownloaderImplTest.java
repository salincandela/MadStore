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

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.test.util.Utils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.custommonkey.xmlunit.XMLTestCase;
import org.mortbay.jetty.HttpStatus;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

public class DownloaderImplTest extends XMLTestCase {

    private static String DOWNLOADER_TEST_FILE = "downloaderTest.html";

    private DownloaderImpl downloader = new DownloaderImpl();
    private Server server = new Server(6666);

    @Override
    protected void setUp() throws Exception {
        server.addHandler(new AbstractHandler() {

            public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
                if (target.contains("successful")) {
                    response.setStatus(HttpStatus.ORDINAL_200_OK);
                    response.getWriter().println(new String(Utils.getUtf8BytesFromFile(DOWNLOADER_TEST_FILE)));
                    ((Request) request).setHandled(true);
                } else {
                    response.setStatus(HttpStatus.ORDINAL_404_Not_Found);
                    ((Request) request).setHandled(true);
                }
            }
        });
        server.start();
    }

    @Override
    protected void tearDown() throws Exception {
        server.stop();
    }

    public void testSuccessfulDownload() throws Exception {
        String expectedPage = new String(Utils.getUtf8BytesFromFile(DOWNLOADER_TEST_FILE));
        Page downloadedPage = downloader.download(new Link("http://localhost:6666/successful"));
        assertNotNull(downloadedPage);
        assertFalse(downloadedPage.isEmpty());
        assertXMLEqual(expectedPage, downloadedPage.getData());
    }

    public void testUnsuccessfulDownload() {
        Page downloadedPage = downloader.download(new Link("http://localhost:6666/notfound"));
        assertNotNull(downloadedPage);
        assertTrue(downloadedPage.isEmpty());
    }
}
