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

import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.transformer.Transformer;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

public class TransformerStageTest extends TestCase {

    public void testSuccessfullTransformerStage() {
        Page original = new Page(new Link("testlink original page"), new String("test original data"));
        Page expected = new Page(new Link("testlink atom page"), new String("test atom data"));
        byte[] expectedBytes = expected.getData().getBytes();

        Transformer transformer = createMock(Transformer.class);
        expect(transformer.transform(same(original))).andReturn(expectedBytes).once();

        TransformerStage transformerStage = new TransformerStage();
        transformerStage.setTransformer(transformer);

        replay(transformer);
        transformerStage.execute(original);
        verify(transformer);
    }

    public void testUnSuccessfullTransformerStage() {
        Page original = new Page(new Link("testlink original page"), new String("test original data"));
        Page expected = new Page(new Link("testlink atom page"), new String(""));
        byte[] expectedBytes = expected.getData().getBytes();

        Transformer transformer = createMock(Transformer.class);
        expect(transformer.transform(same(original))).andReturn(expectedBytes).once();

        TransformerStage transformerStage = new TransformerStage();
        transformerStage.setTransformer(transformer);

        replay(transformer);
        assertNull(transformerStage.execute(original));
        verify(transformer);
    }
}
