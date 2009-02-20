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
package it.pronetics.madstore.repository.tasks;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Task implementation for removing Atom entries from the repository when the number of entries exceeds 
 * the maximum history value.
 *
 * @author Salvatore Incandela
 */
public class CleanRepositoryHistoryTask {

    private static final Logger LOG = LoggerFactory.getLogger(CleanRepositoryHistoryTask.class);
    private EntryRepository entryRepository;
    private CollectionRepository collectionRepository;
    private int maxHistory;

    /**
     * Create the task, setting the maximum history value.
     */
    public CleanRepositoryHistoryTask(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public void setEntryRepository(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public EntryRepository getEntryRepository() {
        return entryRepository;
    }

    public void setCollectionRepository(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public CollectionRepository getCollectionRepository() {
        return collectionRepository;
    }

    public void clean() {
        LOG.info("Clean history start.");
        List<Element> collectionElements = collectionRepository.readCollections();
        for (Element collectionElement : collectionElements) {
            String collectionKey = collectionElement.getAttribute(AtomConstants.ATOM_KEY);
            int deleteCounter = this.cleanHistoryFromCollection(collectionKey);
            LOG.info("Deleted elements: {}", deleteCounter);
        }
        LOG.info("Clean history end.");
    }

    private Integer cleanHistoryFromCollection(final String collectionKey) {
        List<Element> entryElements = entryRepository.readEntries(collectionKey, maxHistory, 0);
        int toRemove = entryElements.size();
        int deleteCounter = 0;
        LOG.info("{} entries will be removed from the collection {} history", toRemove, collectionKey);
        for (Element entryElement : entryElements) {
            String entryKey = entryElement.getAttribute(AtomConstants.ATOM_KEY);
            boolean result = entryRepository.delete(collectionKey, entryKey);
            if (result) {
                ++deleteCounter;
            }
        }
        return deleteCounter;
    }
}
