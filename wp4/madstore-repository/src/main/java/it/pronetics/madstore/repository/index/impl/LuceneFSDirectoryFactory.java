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

import it.pronetics.madstore.repository.support.AtomIndexingException;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LuceneDirectoryFactory} implementation for creating a local filesystem based directory.
 *
 * @author Salvatore Incandela
 */
public class LuceneFSDirectoryFactory implements LuceneDirectoryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneFSDirectoryFactory.class);

    private String indexPath;

    public LuceneFSDirectoryFactory(String indexPath) {
        this.indexPath = indexPath;
    }

    public Directory makeDirectory() {
        if (indexPath == null || indexPath.length() == 0) {
            throw new IllegalStateException("The indexPath cannot be an empty or null string!");
        }
        try {
            File indexDir = new File(indexPath);
            Directory directory = FSDirectory.getDirectory(indexDir);
            return directory;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new AtomIndexingException(e.getMessage(), e);
        }
    }
}
