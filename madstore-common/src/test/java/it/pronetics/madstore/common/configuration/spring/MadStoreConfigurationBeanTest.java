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

import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean.SimpleTriggerConfiguration;
import junit.framework.TestCase;

/**
 * @author Salvatore Incandela
 */
public class MadStoreConfigurationBeanTest extends TestCase {

    private MadStoreConfigurationBean madStoreConfiguration;

    private static final String CRAWLER_TASK = "crawlerTask";
    private static final String CLEAN_REPOSITORY_HISTORY_TASK = "cleanRepositoryHistoryTask";

    public void testMadStoreConfiguration() {
        this.madStoreConfiguration = MadStoreConfigurationManager.getInstance().getMadStoreConfiguration();
        String madStoreHome = System.getProperty("MADSTORE_HOME");
        assertEquals(madStoreHome, madStoreConfiguration.getMadStoreHome());
        // crawler target site
        assertEquals("http://localhost:8080", madStoreConfiguration.getCrawlerConfigurations().get(0).getHostName());
        assertEquals("hatom1.html", madStoreConfiguration.getCrawlerConfigurations().get(0).getStartLink());
        assertEquals(2, madStoreConfiguration.getCrawlerConfigurations().get(0).getMaxConcurrentDownloads());
        assertEquals(100, madStoreConfiguration.getCrawlerConfigurations().get(0).getMaxVisitedLinks());
        // crawler simple trigger
        SimpleTriggerConfiguration crawlerSimpleTriggerConfiguration = madStoreConfiguration.getTasks().get(CRAWLER_TASK);
        assertNotNull(crawlerSimpleTriggerConfiguration);
        assertEquals(1, crawlerSimpleTriggerConfiguration.getStartDelay());
        assertEquals(5, crawlerSimpleTriggerConfiguration.getRepeatInterval());

        SimpleTriggerConfiguration cleanHistorySimpleTriggerConfiguration = madStoreConfiguration.getTasks().get(CLEAN_REPOSITORY_HISTORY_TASK);
        assertNotNull(cleanHistorySimpleTriggerConfiguration);
        assertEquals(2, cleanHistorySimpleTriggerConfiguration.getStartDelay());
        assertEquals(6, cleanHistorySimpleTriggerConfiguration.getRepeatInterval());

        // crawler grid
        assertEquals(madStoreHome + "/gridgain", madStoreConfiguration.getGridConfiguration().getHomeDir());
        // repository jcr
        assertEquals(new Integer(2000), madStoreConfiguration.getJcrConfiguration().getMaxHistory());
        assertEquals(madStoreHome + "/jcr", madStoreConfiguration.getJcrConfiguration().getHomeDir());
        assertEquals("", madStoreConfiguration.getJcrConfiguration().getUsername());
        assertEquals("", new String(madStoreConfiguration.getJcrConfiguration().getPassword()));
        // repository index
        assertEquals(madStoreHome + "/index", madStoreConfiguration.getIndexConfiguration().getIndexDir());
        // indexed properties
        assertEquals("title", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(0).getName());
        assertEquals("//atom:entry/atom:title", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(0).getXPath());
        assertEquals(1, madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(0).getBoost());
        assertEquals("summary", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(1).getName());
        assertEquals("//atom:entry/atom:summary", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(1).getXPath());
        assertEquals(1, madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(1).getBoost());
        assertEquals("author", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(2).getName());
        assertEquals("//atom:entry/atom:author/atom:name", madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(2).getXPath());
        assertEquals(1, madStoreConfiguration.getIndexConfiguration().getIndexedProperties().get(2).getBoost());
        assertEquals(10, madStoreConfiguration.getHttpCacheConfiguration().getMaxAge());
        // server open search
        assertEquals("sampleName", madStoreConfiguration.getOpenSearchConfiguration().getShortName());
        assertEquals("sample description", madStoreConfiguration.getOpenSearchConfiguration().getDescription());
        // server atom publishing protocol
        assertEquals("MadStore", madStoreConfiguration.getAtomPublishingProtocolConfiguration().getWorkspace());
    }
}
