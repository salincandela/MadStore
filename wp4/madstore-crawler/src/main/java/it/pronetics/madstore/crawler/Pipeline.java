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
package it.pronetics.madstore.crawler;

import it.pronetics.madstore.crawler.model.Page;

import java.util.List;

/**
 * Pipeline interface for processing crawled pages by executing a list of {@link Stage} objects.
 * 
 * @author Salvatore Incandela
 * @author Sergio Bossa
 * @author Christian Mongillo
 */
public interface Pipeline {

    /**
     * Start the pipeline execution with the processing of the given page.<br>
     * Pipeline execution can be prematurely aborted by {@link Stage} implementations returning <code>null</code>.
     * @param page The {@link Page} to process.
     * @return The page processed by this pipeline
     */
    public Page execute(Page page);

    /**
     * Get the list of stage objects making up this pipeline: the order of the returned list represents the execution order.
     * @return The list of stage objects executed by this pipeline.
     */
    public List<Stage> getStages();
}
