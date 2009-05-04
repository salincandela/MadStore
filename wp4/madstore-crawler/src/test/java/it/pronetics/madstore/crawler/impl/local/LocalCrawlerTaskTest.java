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
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.parser.filter.LinkFilter;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

public class LocalCrawlerTaskTest extends TestCase {

    private static final Link START_LINK = new Link("http://localhost/start.html");
    private static final Link LINK_1 = new Link("http://localhost/1.html");
    private static final Link LINK_2 = new Link("http://localhost/2.html");
    private static final Link LINK_3 = new Link("http://localhost/3.html");
    private static final Page START_PAGE = new Page(START_LINK, "start");
    private static final Page PAGE_1 = new Page(LINK_1, "link1");
    private static final Page PAGE_2 = new Page(LINK_2, "link2");
    private static final Page PAGE_3 = new Page(LINK_3, "link3");

    public void testExecuteSucceeds() {
        Downloader downloader = getDownloaderMock();
        Parser parser = getParserMock();
        Pipeline pipeline = getPipelineMock();
        AtomPublisher publisher = getPublisherMock();
        replay(downloader, parser, pipeline, publisher);
        LocalCrawlerTask crawlerTask = new LocalCrawlerTask(downloader, parser, publisher, pipeline, 2, 100);
        crawlerTask.execute(START_LINK);
        verify(downloader, parser, pipeline, publisher);
    }

    public void testExecuteWithDownloadException() {
        Downloader downloader = getDownloaderMockThrowingException();
        Parser parser = getParserMockDoingNothing();
        Pipeline pipeline = getPipelineMockDoingNothing();
        AtomPublisher publisher = getPublisherMockDoingNothing();
        replay(downloader, parser, pipeline, publisher);
        LocalCrawlerTask crawlerTask = new LocalCrawlerTask(downloader, parser, publisher, pipeline, 2, 100);
        crawlerTask.execute(START_LINK);
        verify(downloader, parser, pipeline, publisher);
    }

    public void testExecuteWithFailedDownload() {
        Downloader downloader = getDownloaderMockReturningNull();
        Parser parser = getParserMockDoingNothing();
        Pipeline pipeline = getPipelineMockDoingNothing();
        AtomPublisher publisher = getPublisherMockDoingNothing();
        replay(downloader, parser, pipeline, publisher);
        LocalCrawlerTask crawlerTask = new LocalCrawlerTask(downloader, parser, publisher, pipeline, 2, 100);
        crawlerTask.execute(START_LINK);
        verify(downloader, parser, pipeline, publisher);
    }

    public void testExecuteWithThreeVisitedLinks() {
        Downloader downloader = getDownloaderMockInvokedForThreeLinks();
        Parser parser = getParserMockInvokedForThreeLinks();
        Pipeline pipeline = getPipelineMockInvokedForThreeLinks();
        AtomPublisher publisher = getPublisherMock();
        replay(downloader, parser, pipeline, publisher);
        LocalCrawlerTask crawlerTask = new LocalCrawlerTask(downloader, parser, publisher, pipeline, 2, 3);
        crawlerTask.execute(START_LINK);
        verify(downloader, parser, pipeline, publisher);
    }

    private Downloader getDownloaderMock() {
        Downloader downloader = createMock(Downloader.class);
        makeThreadSafe(downloader, true);
        expect(downloader.download(START_LINK)).andReturn(START_PAGE).once();
        expect(downloader.download(LINK_1)).andReturn(PAGE_1).once();
        expect(downloader.download(LINK_2)).andReturn(PAGE_2).once();
        expect(downloader.download(LINK_3)).andReturn(PAGE_3).once();
        return downloader;
    }

    private Parser getParserMock() {
        Parser parser = createMock(Parser.class);
        makeThreadSafe(parser, true);
        expect(parser.parse(eq(START_PAGE), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(LINK_1, LINK_2)).once();
        expect(parser.parse(eq(PAGE_1), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(START_LINK)).once();
        expect(parser.parse(eq(PAGE_2), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(LINK_3)).once();
        expect(parser.parse(eq(PAGE_3), EasyMock.<LinkFilter> anyObject())).andReturn(new ArrayList<Link>()).once();
        return parser;
    }

    private Pipeline getPipelineMock() {
        Pipeline pipeline = createMock(Pipeline.class);
        makeThreadSafe(pipeline, true);
        expect(pipeline.execute(START_PAGE)).andReturn(START_PAGE).once();
        expect(pipeline.execute(PAGE_1)).andReturn(PAGE_1).once();
        expect(pipeline.execute(PAGE_2)).andReturn(PAGE_2).once();
        expect(pipeline.execute(PAGE_3)).andReturn(null).once();
        return pipeline;
    }

    private AtomPublisher getPublisherMock() {
        AtomPublisher publisher = createMock(AtomPublisher.class);
        makeThreadSafe(publisher, true);
        publisher.publish(START_PAGE);
        expectLastCall().once();
        publisher.publish(PAGE_1);
        expectLastCall().once();
        publisher.publish(PAGE_2);
        expectLastCall().once();
        return publisher;
    }

    private Downloader getDownloaderMockThrowingException() {
        Downloader downloader = createMock(Downloader.class);
        makeThreadSafe(downloader, true);
        expect(downloader.download(START_LINK)).andThrow(new RuntimeException("Test Exception, no worries!"));
        return downloader;
    }

    private Downloader getDownloaderMockReturningNull() {
        Downloader downloader = createMock(Downloader.class);
        makeThreadSafe(downloader, true);
        expect(downloader.download(START_LINK)).andReturn(null);
        return downloader;
    }

    private Downloader getDownloaderMockInvokedForThreeLinks() {
        Downloader downloader = createMock(Downloader.class);
        makeThreadSafe(downloader, true);
        expect(downloader.download(START_LINK)).andReturn(START_PAGE).once();
        expect(downloader.download(LINK_1)).andReturn(PAGE_1).once();
        expect(downloader.download(LINK_2)).andReturn(PAGE_2).once();
        return downloader;
    }

    private Parser getParserMockDoingNothing() {
        Parser parser = createMock(Parser.class);
        makeThreadSafe(parser, true);
        return parser;
    }

    private Pipeline getPipelineMockDoingNothing() {
        Pipeline pipeline = createMock(Pipeline.class);
        makeThreadSafe(pipeline, true);
        return pipeline;
    }

    private AtomPublisher getPublisherMockDoingNothing() {
        AtomPublisher publisher = createMock(AtomPublisher.class);
        makeThreadSafe(publisher, true);
        return publisher;
    }

    private Pipeline getPipelineMockInvokedForThreeLinks() {
        Pipeline pipeline = createMock(Pipeline.class);
        makeThreadSafe(pipeline, true);
        expect(pipeline.execute(START_PAGE)).andReturn(START_PAGE).once();
        expect(pipeline.execute(PAGE_1)).andReturn(PAGE_1).once();
        expect(pipeline.execute(PAGE_2)).andReturn(PAGE_2).once();
        return pipeline;
    }

    private Parser getParserMockInvokedForThreeLinks() {
        Parser parser = createMock(Parser.class);
        makeThreadSafe(parser, true);
        expect(parser.parse(eq(START_PAGE), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(LINK_1, LINK_2)).once();
        expect(parser.parse(eq(PAGE_1), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(START_LINK)).once();
        expect(parser.parse(eq(PAGE_2), EasyMock.<LinkFilter> anyObject())).andReturn(Arrays.asList(LINK_3)).once();
        return parser;
    }
}
