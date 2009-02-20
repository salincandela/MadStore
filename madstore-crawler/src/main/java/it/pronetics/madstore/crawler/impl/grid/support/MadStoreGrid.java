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
package it.pronetics.madstore.crawler.impl.grid.support;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridException;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridTask;
import org.gridgain.grid.GridTaskFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation for managing the MadStore grid instance by wrapping
 * the more complex {@link org.gridgain.grid.GridFactory} class.
 *
 * @author Sergio Bossa
 * @author Christian Mongillo
 */
public class MadStoreGrid implements ApplicationContextAware {

    private static final transient Logger LOG = LoggerFactory.getLogger(MadStoreGrid.class);
    private GridConfiguration gridConfiguration;
    private ApplicationContext gridContext;
    private boolean started = false;

    public MadStoreGrid(GridConfiguration gridConfiguration) {
        this.gridConfiguration = gridConfiguration;
    }

    public synchronized void startGrid() throws GridException {
        try {
            if (!started) {
                LOG.info("Starting grid...");
                GridFactory.start(gridConfiguration, gridContext);
                started = true;
                LOG.info("Grid started!");
                notifyAll();
            }
        } catch (Exception ex) {
            throw new GridException(ex.getMessage(), ex);
        }
    }

    public synchronized void stopGrid() throws GridException {
        if (started) {
            LOG.info("Stopping grid...");
            boolean stopped = GridFactory.stop(gridConfiguration.getGridName(), false);
            if (!stopped) {
                throw new GridException("Unable to stop grid with name: " + gridConfiguration.getGridName());
            } else {
                started = false;
                LOG.info("Grid stopped!");
            }
        }
    }

    public synchronized <T, R> R executeInGrid(GridTask<T, R> task, T data) throws GridException {
        try {
            while (!started) {
                LOG.warn("Grid {} is still not started, please wait ...", gridConfiguration.getGridName());
                wait();
            }
            Grid grid = GridFactory.getGrid(gridConfiguration.getGridName());
            LOG.info("Executing in grid : {}", gridConfiguration.getGridName());
            GridTaskFuture<R> future = grid.execute(task, data);
            return future.get();
        } catch (Exception ex) {
            throw new GridException(ex.getMessage(), ex);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.gridContext = applicationContext;
    }
}
