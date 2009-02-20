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
package it.pronetics.madstore.crawler.impl.grid;

import it.pronetics.madstore.crawler.downloader.Downloader;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.parser.Parser;
import it.pronetics.madstore.crawler.parser.filter.impl.ServerFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.resources.GridSpringResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grid task which splits the downloads and parsing of links into several distributed jobs,
 * each one downloading and parsing a single link.
 *
 * @author Christian Mongillo
 * @author Sergio Bossa
 */
public class ParserTask extends GridTaskSplitAdapter<Collection<Link>, Collection<ParserTaskResult>> {

    private static final transient Logger LOG = LoggerFactory.getLogger(ParserTask.class);
    @GridSpringResource(resourceName = "parser")
    private transient Parser parser;
    @GridSpringResource(resourceName = "downloader")
    private transient Downloader downloader;

    public ParserTask(Parser parser, Downloader downloader) {
        this.parser = parser;
        this.downloader = downloader;
    }

    @Override
    protected Collection<? extends GridJob> split(int gridSize, Collection<Link> links) throws GridException {
        Collection<GridJob> jobs = new ArrayList<GridJob>(links.size());
        for (final Link link : links) {
            jobs.add(new GridJobAdapter() {

                public Serializable execute() throws GridException {
                    try {
                        Page page = downloader.download(link);
                        if (page != null && !page.isEmpty()) {
                            Collection<Link> newLinks = parser.parse(page, new ServerFilter(page.getLink()));
                            ParserTaskResult result = new ParserTaskResult(page, newLinks);
                            return result;
                        } else {
                            return null;
                        }
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        return null;
                    }
                }
            });
        }
        return jobs;
    }

    public Collection<ParserTaskResult> reduce(List<GridJobResult> jobResults) throws GridException {
        Collection<ParserTaskResult> parserResults = new ArrayList<ParserTaskResult>(jobResults.size());
        for(GridJobResult jobResult : jobResults) {
            parserResults.add(jobResult.<ParserTaskResult>getData());
        }
        return parserResults;
    }
}
