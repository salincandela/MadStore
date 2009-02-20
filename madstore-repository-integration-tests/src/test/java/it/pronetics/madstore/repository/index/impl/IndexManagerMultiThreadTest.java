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
package it.pronetics.madstore.repository.index.impl;

import it.pronetics.madstore.repository.index.SearchResult;
import it.pronetics.madstore.repository.index.IndexManager;
import it.pronetics.madstore.repository.test.util.TesterThreadFactory;

import it.pronetics.madstore.repository.test.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.w3c.dom.Element;

public class IndexManagerMultiThreadTest extends AbstractDependencyInjectionSpringContextTests {

    private static final String COLLECTION_KEY = "testcollection";
    private IndexManager indexManager;
    private final TesterThreadFactory threadFactory = new TesterThreadFactory();

    public IndexManagerMultiThreadTest() {
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void testIndexManagerSharedByMultipleThreads() throws Exception {
        final CountDownLatch firstStepLatch = new CountDownLatch(1);
        final CountDownLatch secondStepLatch = new CountDownLatch(1);
        final CountDownLatch thirdStepLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(4);
        final List<String> terms = new ArrayList<String>();
        final Element entry1 = Utils.getDoc("luceneentry1.xml").getDocumentElement();
        final Element entry2 = Utils.getDoc("luceneentry2.xml").getDocumentElement();

        terms.add("bla");

        Thread t1 = threadFactory.newThread(new Runnable() {

            public void run() {
                try {
                    indexManager.index(COLLECTION_KEY, "entry1", entry1);
                    firstStepLatch.countDown();
                } finally {
                    stopLatch.countDown();
                }
            }
        });
        t1.start();
        Thread t2 = threadFactory.newThread(new Runnable() {

            public void run() {
                try {
                    firstStepLatch.await(5, TimeUnit.SECONDS);
                    List<SearchResult> entryKeyPair = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
                    assertEquals(1, entryKeyPair.size());
                    secondStepLatch.countDown();
                } catch (InterruptedException ex) {
                    fail(ex.getMessage());
                } finally {
                    stopLatch.countDown();
                }
            }
        });
        t2.start();
        Thread t3 = threadFactory.newThread(new Runnable() {

            public void run() {
                try {
                    secondStepLatch.await(5, TimeUnit.SECONDS);
                    indexManager.index(COLLECTION_KEY, "entry2", entry2);
                    thirdStepLatch.countDown();
                } catch (InterruptedException ex) {
                    fail(ex.getMessage());
                } finally {
                    stopLatch.countDown();
                }
            }
        });
        t3.start();
        Thread t4 = threadFactory.newThread(new Runnable() {

            public void run() {
                try {
                    thirdStepLatch.await(5, TimeUnit.SECONDS);
                    List<SearchResult> entryKeyPair = indexManager.searchCollectionByFullText(COLLECTION_KEY, terms, 0, 0);
                    assertEquals(2, entryKeyPair.size());
                } catch (InterruptedException ex) {
                    fail(ex.getMessage());
                } finally {
                    stopLatch.countDown();
                }
            }
        });
        t4.start();

        stopLatch.await();

        threadFactory.verifyThreads();
        threadFactory.resetThreads();
    }

    @Override
    protected void onTearDown() throws Exception {
        indexManager.delete(COLLECTION_KEY, "entry1");
        indexManager.delete(COLLECTION_KEY, "entry2");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:repositoryApplicationContext.xml"};
    }
}
