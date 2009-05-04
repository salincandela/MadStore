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

import it.pronetics.madstore.crawler.Stage;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class PipelineImplTest extends TestCase {

    public void testPipelineIsSuccessful() {
        Page page1 = new Page(new Link("testlink page1"), new String("test data"));
        Page page2 = new Page(new Link("testlink page2"), new String("test data"));
        Page page3 = new Page(new Link("testlink page3"), new String("test data"));
        Page page4 = new Page(new Link("testlink page4"), new String("test data"));

        Stage stage1 = createMock(Stage.class);
        Stage stage2 = createMock(Stage.class);
        Stage stage3 = createMock(Stage.class);

        expect(stage1.execute(page1)).andReturn(page2).once();
        expect(stage2.execute(page2)).andReturn(page3).once();
        expect(stage3.execute(page3)).andReturn(page4).once();

        PipelineImpl pipeline = new PipelineImpl();

        List<Stage> stages = new ArrayList<Stage>();
        stages.add(stage1);
        stages.add(stage2);
        stages.add(stage3);

        pipeline.setStages(stages);

        replay(stage1, stage2, stage3);
        pipeline.execute(page1);
        verify(stage1, stage2, stage3);
    }

    public void testPipelineIsAborted() {
        Page page = new Page(new Link("testlink page1"), new String("test data"));

        Stage stage1 = createMock(Stage.class);
        Stage stage2 = createMock(Stage.class);
        Stage stage3 = createMock(Stage.class);

        expect(stage1.execute(page)).andReturn(null).once();

        PipelineImpl pipeline = new PipelineImpl();

        List<Stage> stages = new ArrayList<Stage>();
        stages.add(stage1);
        stages.add(stage2);
        stages.add(stage3);

        pipeline.setStages(stages);

        replay(stage1, stage2, stage3);
        pipeline.execute(page);
        verify(stage1, stage2, stage3);
    }
}
