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

import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationManager;
import org.gridgain.grid.GridException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Collection;

/*
 * Context listener for starting and stopping the madstore grid instance when running in grid-enabled mode.
 *
 * @author Sergio Bossa
 * @author Christian Mongillo
 */
public class MadStoreGridListener implements ServletContextListener {

    private volatile MadStoreGrid madstoreGrid;

    public void contextInitialized(ServletContextEvent event) {
        if (MadStoreConfigurationManager.getInstance().getMadStoreConfiguration().isGridModeEnabled()) {
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
            Collection beans = context.getBeansOfType(MadStoreGrid.class).values();
            try {
                if (beans.size() == 1) {
                    madstoreGrid = (MadStoreGrid) beans.iterator().next();
                    madstoreGrid.startGrid();
                } else if (beans.size() == 0) {
                    throw new IllegalStateException("No grid factory found in Spring application context!");
                } else {
                    throw new IllegalStateException("More than one grid factory found in Spring application context!");
                }
            } catch (GridException ex) {
                throw new RuntimeException("Error during grid startup", ex);
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (MadStoreConfigurationManager.getInstance().getMadStoreConfiguration().isGridModeEnabled()) {
            try {
                madstoreGrid.stopGrid();
            } catch (GridException ex) {
                throw new RuntimeException("Error during grid shutdown", ex);
            }
        }
    }
}