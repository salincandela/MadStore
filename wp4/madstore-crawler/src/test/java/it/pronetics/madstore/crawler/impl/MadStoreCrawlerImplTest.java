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
import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

public class MadStoreCrawlerImplTest extends TestCase {

    public MadStoreCrawlerImplTest(String testName) {
        super(testName);
    }

    public void testStart() {
        List<CrawlerConfiguration> configurations = getCrawlerConfigurationMocks();
        CrawlerTask task = getCrawlerTaskMock();
        CrawlerTaskFactory factory = getCrawlerTaskFactoryMock(task);
        replay(configurations.get(0), configurations.get(1), task, factory);
        MadStoreCrawlerImpl crawler = new MadStoreCrawlerImpl();
        crawler.setCrawlerConfigurations(configurations);
        crawler.setCrawlerTaskFactory(factory);
        crawler.start();
        verify(configurations.get(0), configurations.get(1), task, factory);
    }

    private List<CrawlerConfiguration> getCrawlerConfigurationMocks() {
        CrawlerConfiguration configuration1 = createMock(CrawlerConfiguration.class);
        makeThreadSafe(configuration1, true);
        expect(configuration1.getMaxConcurrentDownloads()).andReturn(2).once();
        expect(configuration1.getMaxVisitedLinks()).andReturn(100).once();
        expect(configuration1.getServer()).andReturn("http://www.foo.org").once();
        expect(configuration1.getStartLink()).andReturn("index.html").once();
        expect(configuration1.getPipeline()).andReturn(new PipelineImpl()).once();
        CrawlerConfiguration configuration2 = createMock(CrawlerConfiguration.class);
        makeThreadSafe(configuration2, true);

        expect(configuration2.getMaxConcurrentDownloads()).andReturn(3).once();
        expect(configuration2.getMaxVisitedLinks()).andReturn(100).once();
        expect(configuration2.getServer()).andReturn("http://www.bar.org").once();
        expect(configuration2.getStartLink()).andReturn("index.html").once();
        expect(configuration2.getPipeline()).andReturn(new PipelineImpl()).once();
        return Arrays.asList(configuration1, configuration2);
    }

    private CrawlerTaskFactory getCrawlerTaskFactoryMock(CrawlerTask task) {
        CrawlerTaskFactory factory = createMock(CrawlerTaskFactory.class);
        expect(factory.makeCrawlerTask(EasyMock.<Downloader> anyObject(), EasyMock.<Parser> anyObject(), EasyMock.<AtomPublisher> anyObject(), EasyMock.<Pipeline> anyObject(), eq(2), eq(100))).andReturn(
                task).once();
        expect(factory.makeCrawlerTask(EasyMock.<Downloader> anyObject(), EasyMock.<Parser> anyObject(), EasyMock.<AtomPublisher> anyObject(), EasyMock.<Pipeline> anyObject(), eq(3), eq(100))).andReturn(
                task).once();
        makeThreadSafe(factory, true);
        return factory;
    }

    private CrawlerTask getCrawlerTaskMock() {
        CrawlerTask task = createMock(CrawlerTask.class);
        task.execute(eq(new Link("http://www.foo.org/index.html")));
        expectLastCall().once();
        task.execute(eq(new Link("http://www.bar.org/index.html")));
        expectLastCall().once();
        makeThreadSafe(task, true);
        return task;
    }
}
