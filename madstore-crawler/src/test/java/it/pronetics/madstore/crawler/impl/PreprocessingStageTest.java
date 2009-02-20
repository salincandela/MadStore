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
import it.pronetics.madstore.crawler.test.util.Utils;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;

public class PreprocessingStageTest extends XMLTestCase {

    @Override
    protected void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
    }

    public void testTransformerWithMalformedPage() throws Exception {
        Page malformedPage = new Page(new Link("malformed"), new String(Utils.getUtf8BytesFromFile("preprocessingStageTestMalformed.html")));
        Page wellformedPage = new Page(new Link("wellformed"), new String(Utils.getUtf8BytesFromFile("preprocessingStageTestWellformed.html")));
        PreprocessingStage preprocessingStage = new PreprocessingStage();
        Page result = preprocessingStage.execute(malformedPage);

        assertNotNull(result);
        assertNotNull(result.getData());
        Diff diff = null;
        diff = new Diff(wellformedPage.getData(), result.getData());
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + wellformedPage.getData() + "\nActual: " + result.getData(), diff.similar());
    }

    public void testTrasformerWithJavascriptPage() throws Exception {
        Page expected = new Page(new Link("javaScript"), new String(Utils.getUtf8BytesFromFile("expectedJavaScriptPage.html")));
        Page source = new Page(new Link("testlink"), new String(Utils.getUtf8BytesFromFile("javaScriptPage.html")));
        PreprocessingStage preprocessingStage = new PreprocessingStage();
        Page result = preprocessingStage.execute(source);
        assertNotNull(result);
        assertNotNull(result.getData());
        Diff diff = null;
        diff = new Diff(expected.getData(), result.getData());
        assertTrue("Not identical pieces: " + diff + "\nExpected: " + expected.getData() + "\nActual: " + result.getData(), diff.similar());
    }
}
