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
package it.pronetics.madstore.common.configuration.spring;

import it.pronetics.madstore.common.configuration.support.MadStoreConfigurationException;

import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Sergio Bossa
 */
public class MadStoreConfigurationManager {

    private static final Logger LOG = LoggerFactory.getLogger(MadStoreConfigurationManager.class);
    private static final String MADSTORE_HOME = "MADSTORE_HOME";
    private static final String MADSTORE_CONFIGURATION_NAME = "madstoreConfiguration.xml";
    private static final MadStoreConfigurationManager instance = new MadStoreConfigurationManager();
    //
    private String madStoreHome;
    private MadStoreConfigurationBean madStoreConfiguration;

    private MadStoreConfigurationManager() {
        safelySetMadStoreHome();
    }

    public static MadStoreConfigurationManager getInstance() {
        return instance;
    }

    public String getMadStoreHome() {
        return madStoreHome;
    }

    public MadStoreConfigurationBean getMadStoreConfiguration() {
        synchronized (this) {
            try {
                if (madStoreConfiguration == null) {
                    loadMadStoreConfigurationFromFilesystem();
                    loadMadStoreConfigurationFromClasspathIfNotFound();
                    if (madStoreConfiguration == null) {
                        LOG.error("Unable to load MadStore configuration!");
                        madStoreConfiguration = new MadStoreConfigurationBean();
                    }
                }
            } catch (Exception ex) {
                LOG.debug(ex.getMessage(), ex);
                throw new MadStoreConfigurationException(ex.getMessage(), ex);
            }
            return madStoreConfiguration;
        }
    }

    private void safelySetMadStoreHome() {
        madStoreHome = System.getenv(MADSTORE_HOME);
        madStoreHome = madStoreHome != null ? madStoreHome : "";
        File test = new File(madStoreHome);
        if (!test.exists()) {
            LOG.debug("Wrong path: {}", madStoreHome);
            madStoreHome = System.getProperty(MADSTORE_HOME, System.getProperty("user.dir"));
            test = new File(madStoreHome);
            if (!test.exists()) {
                LOG.debug("Wrong path: {}", madStoreHome);
                madStoreHome = System.getProperty("user.dir");
            }
        }
        LOG.info("MADSTORE_HOME set to: {}", madStoreHome);
    }

    private void loadMadStoreConfigurationFromFilesystem() {
        String contextPath = "file:" + madStoreHome + "/conf/" + MADSTORE_CONFIGURATION_NAME;
        try {
            ApplicationContext context = new FileSystemXmlApplicationContext(contextPath);
            Map configurations = context.getBeansOfType(MadStoreConfigurationBean.class);
            if (configurations.size() != 1) {
                LOG.warn("Error loading MadStore configuration from path: {}", contextPath);
                madStoreConfiguration = null;
            } else {
                LOG.info("MadStore configuration successfully loaded from path: {}", contextPath);
                madStoreConfiguration = (MadStoreConfigurationBean) configurations.values().iterator().next();
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            LOG.warn("Error loading MadStore configuration from path: {}", contextPath);
            throw new MadStoreConfigurationException(ex.getMessage(), ex);
        }
    }

    private void loadMadStoreConfigurationFromClasspathIfNotFound() {
        String contextPath = "classpath:" + MADSTORE_CONFIGURATION_NAME;
        try {
            if (madStoreConfiguration == null) {
                ApplicationContext context = new ClassPathXmlApplicationContext(contextPath);
                Map configurations = context.getBeansOfType(MadStoreConfigurationBean.class);
                if (configurations.size() != 1) {
                    LOG.warn("Error loading MadStore configuration from path: {}", contextPath);
                    madStoreConfiguration = null;
                } else {
                    LOG.info("MadStore configuration successfully loaded from path: {}", contextPath);
                    madStoreConfiguration = (MadStoreConfigurationBean) configurations.values().iterator().next();
                }
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            LOG.warn("Error loading MadStore configuration from path: {}", contextPath);
        }
    }
}
