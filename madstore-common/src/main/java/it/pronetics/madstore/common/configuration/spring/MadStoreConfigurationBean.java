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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plain old java bean used as a Spring bean for setting and getting configuration values.
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class MadStoreConfigurationBean {

    private String madStoreHome;
    private List<CrawlerConfiguration> crawlerConfigurations;
    private GridConfiguration gridConfiguration;
    private JcrConfiguration jcrConfiguration;
    private IndexConfiguration indexConfiguration;
    private AtomPublishingProtocolConfiguration atomPublishingProtocolConfiguration;
    private OpenSearchConfiguration openSearchConfiguration;
    private HttpCacheConfiguration httpCacheConfiguration;
    private Map<String, SimpleTriggerConfiguration> tasks = new HashMap<String, SimpleTriggerConfiguration>();

    public String getMadStoreHome() {
        return madStoreHome;
    }

    public void setMadStoreHome(String madStoreHome) {
        this.madStoreHome = madStoreHome;
    }

    public List<CrawlerConfiguration> getCrawlerConfigurations() {
        return Collections.unmodifiableList(crawlerConfigurations);
    }

    public void setCrawlerConfigurations(List<CrawlerConfiguration> crawlerConfigurations) {
        this.crawlerConfigurations = crawlerConfigurations;
    }

    public GridConfiguration getGridConfiguration() {
        return gridConfiguration;
    }

    public void setGridConfiguration(GridConfiguration gridConfiguration) {
        this.gridConfiguration = gridConfiguration;
    }

    public JcrConfiguration getJcrConfiguration() {
        return jcrConfiguration;
    }

    public void setJcrConfiguration(JcrConfiguration jcrConfiguration) {
        this.jcrConfiguration = jcrConfiguration;
    }

    public IndexConfiguration getIndexConfiguration() {
        return indexConfiguration;
    }

    public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
        this.indexConfiguration = indexConfiguration;
    }

    public HttpCacheConfiguration getHttpCacheConfiguration() {
        return httpCacheConfiguration;
    }

    public void setHttpCacheConfiguration(HttpCacheConfiguration httpCacheConfiguration) {
        this.httpCacheConfiguration = httpCacheConfiguration;
    }

    public AtomPublishingProtocolConfiguration getAtomPublishingProtocolConfiguration() {
        return atomPublishingProtocolConfiguration;
    }

    public void setAtomPublishingProtocolConfiguration(AtomPublishingProtocolConfiguration atomPublishingProtocolConfiguration) {
        this.atomPublishingProtocolConfiguration = atomPublishingProtocolConfiguration;
    }

    public OpenSearchConfiguration getOpenSearchConfiguration() {
        return openSearchConfiguration;
    }

    public void setOpenSearchConfiguration(OpenSearchConfiguration openSearchConfiguration) {
        this.openSearchConfiguration = openSearchConfiguration;
    }

    public boolean isGridModeEnabled() {
        return this.gridConfiguration != null ? true : false;
    }

    public void setTasks(Map<String, SimpleTriggerConfiguration> tasks) {
        this.tasks = tasks;
    }

    public Map<String, SimpleTriggerConfiguration> getTasks() {
        return tasks;
    }

    public static class CrawlerConfiguration {

        private String hostName;
        private String startLink;
        private int maxConcurrentDownloads;
        private String pipelineName;
        private int maxVisitedLinks;

        public String getHostName() {
            return this.hostName;
        }

        public void setHostName(String hostName) {
            if (hostName.endsWith("/")) {
                this.hostName = hostName.substring(0, hostName.length() - 1);
            } else {
                this.hostName = hostName;
            }
        }

        public String getStartLink() {
            return this.startLink;
        }

        public void setStartLink(String startLink) {
            if (startLink.startsWith("/")) {
                this.startLink = startLink.substring(1);
            } else {
                this.startLink = startLink;
            }
        }

        public int getMaxConcurrentDownloads() {
            return maxConcurrentDownloads;
        }

        public void setMaxConcurrentDownloads(int maxConcurrentDownloads) {
            this.maxConcurrentDownloads = maxConcurrentDownloads;
        }

        public int getMaxVisitedLinks() {
            return maxVisitedLinks;
        }

        public void setMaxVisitedLinks(int maxVisitedLinks) {
            this.maxVisitedLinks = maxVisitedLinks;
        }

        public String getPipelineName() {
            return pipelineName;
        }

        public void setPipelineName(String pipelineName) {
            this.pipelineName = pipelineName;
        }

    }

    public static class SimpleTriggerConfiguration {

        private int startDelay;
        private int repeatInterval;

        public int getRepeatInterval() {
            return repeatInterval;
        }

        public void setRepeatInterval(int repeatInterval) {
            this.repeatInterval = repeatInterval;
        }

        public int getStartDelay() {
            return startDelay;
        }

        public void setStartDelay(int startDelay) {
            this.startDelay = startDelay;
        }
    }

    public static class GridConfiguration {

        private String homeDir;

        public String getHomeDir() {
            return homeDir;
        }

        public void setHomeDir(String homeDir) {
            this.homeDir = homeDir;
        }
    }

    public static class JcrConfiguration {

        private String homeDir;
        private String username;
        private char[] password;
        private Integer maxHistory;

        public String getHomeDir() {
            return homeDir;
        }

        public void setHomeDir(String homeDir) {
            this.homeDir = homeDir;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public char[] getPassword() {
            return password;
        }

        public void setPassword(char[] password) {
            this.password = password;
        }

        public void setMaxHistory(Integer maxHistory) {
            this.maxHistory = maxHistory;
        }

        public Integer getMaxHistory() {
            return maxHistory;
        }
    }

    public static class IndexConfiguration {

        private String indexDir;
        private Map<String, String> indexedPropertiesNamespaces;
        private List<Property> indexedProperties;

        public String getIndexDir() {
            return indexDir;
        }

        public void setIndexDir(String indexDir) {
            this.indexDir = indexDir;
        }

        public void setIndexedPropertiesNamespaces(Map<String, String> indexedPropertiesNamespaces) {
            this.indexedPropertiesNamespaces = indexedPropertiesNamespaces;
        }

        public Map<String, String> getIndexedPropertiesNamespaces() {
            return indexedPropertiesNamespaces;
        }

        public void setIndexedProperties(List<Property> indexedProperties) {
            this.indexedProperties = indexedProperties;
        }

        public List<Property> getIndexedProperties() {
            return indexedProperties;
        }

        public class Property {
            private String name;
            private String xPath;
            private int boost;

            public Property(String name, String xPath, int boost) {
                this.name = name;
                this.xPath = xPath;
                this.boost = boost;
            }

            public String getName() {
                return name;
            }

            public String getXPath() {
                return xPath;
            }

            public int getBoost() {
                return boost;
            }
        }

    }

    public static class AtomPublishingProtocolConfiguration {

        private String workspace;

        public String getWorkspace() {
            return workspace;
        }

        public void setWorkspace(String workspace) {
            this.workspace = workspace;
        }
    }

    public static class OpenSearchConfiguration {

        private String shortName;
        private String description;

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getShortName() {
            return shortName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class HttpCacheConfiguration {
        
        private int maxAge;

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }

}
