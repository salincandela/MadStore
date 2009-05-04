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
package it.pronetics.madstore.server.abdera.util;

import it.pronetics.madstore.server.test.util.Utils;
import junit.framework.TestCase;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.w3c.dom.Element;

public class AbderaHelperTest extends TestCase {

    private Element collection;
    private Element entry;

    protected void setUp() throws Exception {
        this.collection = Utils.getDoc("serverCollection.xml").getDocumentElement();
        this.entry = Utils.getDoc("serverEntry.xml").getDocumentElement();
    }

    public void testGetCollectionFromDom() throws Exception {
        Collection abderaCollection = AbderaHelper.getCollectionFromDom(this.collection);
        assertNotNull(abderaCollection);
        assertEquals("Entries", abderaCollection.getTitle());
        assertEquals("entries", abderaCollection.getHref().toString());
        assertEquals(1, abderaCollection.getAccept().length);
        assertEquals("application/atom+xml;type=entry", abderaCollection.getAccept()[0]);
    }

    public void testGetEntryFromDom() throws Exception {
        Entry abderaEntry = AbderaHelper.getEntryFromDom(this.entry);
        assertNotNull(abderaEntry);
        assertEquals("Server Entry 1", abderaEntry.getTitle());
        assertEquals("urn:madstore:entry1", abderaEntry.getId().toString());
    }
}
