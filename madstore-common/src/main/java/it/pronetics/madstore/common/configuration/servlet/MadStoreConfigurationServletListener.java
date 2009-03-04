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
package it.pronetics.madstore.common.configuration.servlet;

import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationManager;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean.SimpleTriggerConfiguration;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean.IndexConfiguration.Property;
import it.pronetics.madstore.common.configuration.support.MadStoreConfigurationException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Servlet listener for initializing and logging the MadStore configuration.
 * <br>
 * Requires the <i>madstore-logback.xml</i> configuration file in the classpath.
 *
 * @author Salvatore Incandela
 */
public class MadStoreConfigurationServletListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(MadStoreConfigurationServletListener.class);
    private static final String MADSTORE_LOGGER_CONFIGURATION = "madstore-logback.xml";

    public void contextInitialized(ServletContextEvent event) {
        initLogger();
        printASCIIArt();
        MadStoreConfigurationBean madStoreConfigurationBean = MadStoreConfigurationManager.getInstance().getMadStoreConfiguration();
        LOG.info("         MadStore home directory {}", madStoreConfigurationBean.getMadStoreHome());
        LOG.info("         index directory {}", madStoreConfigurationBean.getIndexConfiguration().getIndexDir());
        LOG.info("         repository directory {}", madStoreConfigurationBean.getJcrConfiguration().getHomeDir());
        LOG.info("");
        if (madStoreConfigurationBean.getCrawlerConfigurations() != null) {
            LOG.info("         ---Crawler module is active----");
            if (madStoreConfigurationBean.isGridModeEnabled()) {
                LOG.info("         MadStore crawler starting in distributed grid mode.");
                LOG.info("         Grid Gain home directory {}", madStoreConfigurationBean.getGridConfiguration().getHomeDir());
            } else {
                LOG.info("         MadStore crawler starting in local mode.");
            }
            LOG.info("         ----------Target sites---------");
            for (MadStoreConfigurationBean.CrawlerConfiguration crawlerConfiguration : madStoreConfigurationBean.getCrawlerConfigurations()) {
                LOG.info("         Target hostname: {}, start link: {}, max concurrent downloads: {}, max number of links: {}", new Object[] { crawlerConfiguration.getHostName(),
                        crawlerConfiguration.getStartLink(), crawlerConfiguration.getMaxConcurrentDownloads(), crawlerConfiguration.getMaxVisitedLinks() });
            }
            LOG.info("");
        }
        LOG.info("         --Indexed properties namespaces--");
        Map<String, String> registeredNamespaces = madStoreConfigurationBean.getIndexConfiguration().getIndexedPropertiesNamespaces();
        for (String prefix : registeredNamespaces.keySet()) {
            LOG.info("         Prefix: {} url: {}", prefix, registeredNamespaces.get(prefix));
        }
        LOG.info("");
        LOG.info("         --------Indexed properties-------");
        List<Property> properties = madStoreConfigurationBean.getIndexConfiguration().getIndexedProperties();
        for (Property property : properties) {
            LOG.info("         Name: {} xpath: {} boost: {}", new Object[] { property.getName(), property.getXPath(), property.getBoost() });
        }
        if (madStoreConfigurationBean.getOpenSearchConfiguration() != null && madStoreConfigurationBean.getAtomPublishingProtocolConfiguration() != null) {
            LOG.info("         ----Server module is active----");
            LOG.info("         Atom Publishing Protocol workspace: {}", madStoreConfigurationBean.getAtomPublishingProtocolConfiguration().getWorkspace());
            LOG.info("         Open search description: {}", madStoreConfigurationBean.getOpenSearchConfiguration().getDescription());
            LOG.info("         Open search short name: {}", madStoreConfigurationBean.getOpenSearchConfiguration().getShortName());
            LOG.info("         -------------------------------");
        }
        if (madStoreConfigurationBean.getTasks() != null) {
            LOG.info("         -------Scheduled tasks---------");
            Set<String> taskNames = madStoreConfigurationBean.getTasks().keySet();
            for (String taskName : taskNames) {
                SimpleTriggerConfiguration simpleTriggerConfiguration = madStoreConfigurationBean.getTasks().get(taskName);
                if (simpleTriggerConfiguration != null) {
                    LOG.info("         Task name: {}", taskName);
                    LOG.info("         Task is scheduled to run after {} minutes, every {} minutes.", simpleTriggerConfiguration.getStartDelay(), simpleTriggerConfiguration.getRepeatInterval());
                    LOG.info("         -------------------------------");
                }
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

    private void initLogger() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MADSTORE_LOGGER_CONFIGURATION);
        if (inputStream == null) {
            throw new MadStoreConfigurationException("Logging configuration file " + MADSTORE_LOGGER_CONFIGURATION + " cannot be found");
        } else {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                loggerContext.reset();
                configurator.doConfigure(inputStream);
            } catch (JoranException ex) {
                loggerContext.reset();
                LOG.error(ex.getMessage(), ex);
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
        }
    }

    private void printASCIIArt() {
        LOG.info("**********************************************************");
        LOG.info("*                  _ __ _                                *");
        LOG.info("*  /\\/\\   __ _  __| / _\\ |_ ___  _ __ ___                *");
        LOG.info("* /    \\ / _` |/ _` \\ \\| __/ _ \\| '__/ _ \\               *");
        LOG.info("*/ /\\/\\ \\ (_| | (_| |\\ \\ || (_) | | |  __/               *");
        LOG.info("*\\/    \\/\\__,_|\\__,_\\__/\\__\\___/|_|  \\___|               *");
        LOG.info("*                                                        *");
        LOG.info("**********************************************************");
    }
}
