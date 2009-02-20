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
package it.pronetics.madstore.repository.jcr.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.repository.test.util.Utils;
import it.pronetics.madstore.repository.util.PagingList;

import java.util.Arrays;
import java.util.List;
import javax.xml.transform.dom.DOMSource;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.w3c.dom.Element;

import static org.custommonkey.xmlunit.XMLAssert.*;

public class JcrRepositoryBehaviorTest extends AbstractDependencyInjectionSpringContextTests {

    private static final String FOO_COLLECTION_XML = "fooCollection.xml";
    private static final String FOO_ENTRY_XML = "fooEntry.xml";
    private static final String FOO_ENTRY_NO_MILLIS_XML = "fooEntryNoMillis.xml";
    private static final String FOO_ENTRY_WITH_ENTITY_REF_XML = "fooEntryEntityRef.xml";
    private static final String FOO2_ENTRY_XML = "fooEntry2.xml";
    private static final String BAR_ENTRY_XML = "barEntry.xml";
    private static final String NEWER_ENTRY_XML = "newerEntry.xml";
    private CollectionRepository collectionRepository;
    private EntryRepository entryRepository;


    static {
        XMLUnit.setIgnoreWhitespace(true);
    }

    public JcrRepositoryBehaviorTest() {
        setAutowireMode(AUTOWIRE_BY_TYPE);
    }

    @Override
    protected void onTearDown() throws Exception {
        List<Element> collections = collectionRepository.readCollections();
        for (Element collection : collections) {
            String collectionKey = collection.getAttribute(AtomConstants.ATOM_KEY);
            List<Element> elements = entryRepository.readEntries(collectionKey);
            for (Element element : elements) {
                String entryKey = element.getAttribute(AtomConstants.ATOM_KEY);
                entryRepository.delete(collectionKey, entryKey);
            }
            collectionRepository.delete(collectionKey);
        }
    }

    public void testPutAndReadEmptyCollection() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);

        Element readCollectionElement = collectionRepository.read(collectionKey);
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceCollectionElement), new DOMSource(readCollectionElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutAndReadCollectionWithEntries() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        Element readCollectionElement = collectionRepository.read(collectionKey);
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceCollectionElement), new DOMSource(readCollectionElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testCannotDeleteNotEmptyCollection() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        boolean deleted = collectionRepository.delete(collectionKey);
        assertFalse(deleted);
    }

    public void testCanDeleteEmptyCollection() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);

        boolean deleted = collectionRepository.delete(collectionKey);
        assertTrue(deleted);
    }

    public void testPutAndReadEntry() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        Element readEntryElement = entryRepository.read(collectionKey, entryKey);
        System.out.println(DomHelper.getStringFromDomElement(readEntryElement));
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceEntryElement), new DOMSource(readEntryElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutAndReadEntryWithNoMillisInDates() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_NO_MILLIS_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        Element readEntryElement = entryRepository.read(collectionKey, entryKey);
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceEntryElement), new DOMSource(readEntryElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutAndReadEntryWithEntityReferences() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_WITH_ENTITY_REF_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        Element readEntryElement = entryRepository.read(collectionKey, entryKey);
        System.out.println(DomHelper.getStringFromDomElement(readEntryElement));
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceEntryElement), new DOMSource(readEntryElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutAndFindEntry() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);

        List<Element> elements = entryRepository.findEntries(collectionKey, Arrays.asList("fooEntry"));
        assertEquals(1, elements.size());
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceEntryElement), new DOMSource(elements.get(0))));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutEntriesAndFindWithPagination() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element fooEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        Element barEntryElement = Utils.getDoc(BAR_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String fooKey = entryRepository.put(collectionKey, fooEntryElement);
        assertNotNull(fooKey);
        String barKey = entryRepository.put(collectionKey, barEntryElement);
        assertNotNull(barKey);

        PagingList<Element> elements = entryRepository.findEntries(collectionKey, Arrays.asList("bla"), 1, 1);
        assertEquals(1, elements.size());

        assertEquals(2, elements.getTotal());
        assertEquals(1, elements.getMax());
        assertEquals(1, elements.getOffset());

        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(barEntryElement), new DOMSource(elements.get(0))));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testPutIfAbsentEntryDoesNotOverwriteExistentEntry() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        Element sameKeySourceEntryElement = Utils.getDoc(FOO2_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);
        String sameKey = entryRepository.putIfAbsent(collectionKey, sameKeySourceEntryElement);
        assertNull(sameKey);

        Element readEntryElement = entryRepository.read(collectionKey, entryKey);
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceEntryElement), new DOMSource(readEntryElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void testUpdateWithNewerAndReadUpdatedEntry() throws Exception {
        Element sourceCollectionElement = Utils.getDoc(FOO_COLLECTION_XML).getDocumentElement();
        Element sourceEntryElement = Utils.getDoc(FOO_ENTRY_XML).getDocumentElement();
        Element sourceNewerEntryElement = Utils.getDoc(NEWER_ENTRY_XML).getDocumentElement();
        String collectionKey = collectionRepository.putIfAbsent(sourceCollectionElement);
        assertNotNull(collectionKey);
        String entryKey = entryRepository.put(collectionKey, sourceEntryElement);
        assertNotNull(entryKey);
        String newerEntryKey = entryRepository.updateIfNewer(collectionKey, sourceNewerEntryElement);
        assertNotNull(newerEntryKey);
        assertEquals(entryKey, newerEntryKey);

        Element readEntryElement = entryRepository.read(collectionKey, newerEntryKey);
        DetailedDiff diff = new DetailedDiff(new Diff(new DOMSource(sourceNewerEntryElement), new DOMSource(readEntryElement)));
        assertXMLEqual(diff.toString(), diff, true);
    }

    public void setCollectionRepository(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:repositoryApplicationContext.xml"};
    }
}
