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
package it.pronetics.madstore.common.spring;

import it.pronetics.madstore.common.configuration.spring.AbstractMadStoreConfigurationFactoryBean;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.quartz.JobDetail;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * @author Sergio Bossa
 * @author Salvatore Incandela
 */
public class SimpleTriggerListFactoryBean extends AbstractMadStoreConfigurationFactoryBean {

    public Object getObject() throws Exception {
        List<SimpleTriggerBean> simpleTriggerBeanList = new ArrayList<SimpleTriggerBean>();
        MadStoreConfigurationBean madstoreConfiguration = getMadStoreConfiguration();
        Set<String> taskNames = madstoreConfiguration.getTasks().keySet();
        for (String taskName : taskNames) {
            SimpleTriggerBean trigger = new SimpleTriggerBean();
            long startDelayInMillisecs = madstoreConfiguration.getTasks().get(taskName).getStartDelay() * 60000;
            long repeatIntervalInMillisecs = madstoreConfiguration.getTasks().get(taskName).getRepeatInterval() * 60000;
            JobDetail jobDetail = getUniqueBean(taskName);
            trigger.setName(SimpleTriggerListFactoryBean.class.getName());
            trigger.setStartDelay(startDelayInMillisecs);
            trigger.setRepeatInterval(repeatIntervalInMillisecs);
            trigger.setJobDetail(jobDetail);
            trigger.afterPropertiesSet();
            simpleTriggerBeanList.add(trigger);
        }
        return simpleTriggerBeanList;
    }

    public Class getObjectType() {
        return List.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
