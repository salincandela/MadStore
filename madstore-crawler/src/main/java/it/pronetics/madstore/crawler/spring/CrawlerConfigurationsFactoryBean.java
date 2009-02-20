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
package it.pronetics.madstore.crawler.spring;

import it.pronetics.madstore.common.configuration.spring.AbstractMadStoreConfigurationFactoryBean;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean;
import it.pronetics.madstore.crawler.CrawlerConfiguration;
import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.impl.CrawlerConfigurationImpl;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergio Bossa
 */
public class CrawlerConfigurationsFactoryBean extends AbstractMadStoreConfigurationFactoryBean {

    public Object getObject() throws Exception {
        MadStoreConfigurationBean madstoreConfiguration = getMadStoreConfiguration();
        List<CrawlerConfiguration> crawlerConfigurationsResult = new LinkedList<CrawlerConfiguration>();
        List<MadStoreConfigurationBean.CrawlerConfiguration> crawlerConfigurations = madstoreConfiguration.getCrawlerConfigurations();
        for (MadStoreConfigurationBean.CrawlerConfiguration tmpConfig : crawlerConfigurations) {
            CrawlerConfiguration crawlerConfiguration = new CrawlerConfigurationImpl();
            crawlerConfiguration.setServer(tmpConfig.getHostName());
            crawlerConfiguration.setStartLink(tmpConfig.getStartLink());
            crawlerConfiguration.setMaxConcurrentDownloads(tmpConfig.getMaxConcurrentDownloads());
            crawlerConfiguration.setMaxVisitedLinks(tmpConfig.getMaxVisitedLinks());
            crawlerConfiguration.setPipeline(getPipeline(tmpConfig));
            crawlerConfigurationsResult.add(crawlerConfiguration);
        }
        return crawlerConfigurationsResult;
    }

    public Class getObjectType() {
        return List.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private Pipeline getPipeline(MadStoreConfigurationBean.CrawlerConfiguration tmpConfig) {
        String pipelineName = tmpConfig.getPipelineName();
        if (pipelineName != null) {
            return getUniqueBean(pipelineName);
        } else {
            return getUniqueBean(Pipeline.class);
        }
    }
}
