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
package it.pronetics.madstore.repository.spring;

import it.pronetics.madstore.common.configuration.spring.AbstractMadStoreConfigurationFactoryBean;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean;

/**
 * Spring-based <code>FactoryBean</code> for obtaining the jcr repository username out of the
 * {@link it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean} configuration.
 *
 * @author Salvatore Incandela
 */
public class UsernameFactoryBean extends AbstractMadStoreConfigurationFactoryBean {

    public Object getObject() throws Exception {
        MadStoreConfigurationBean madstoreConfiguration = getMadStoreConfiguration();
        return madstoreConfiguration.getJcrConfiguration().getUsername();
    }

    public Class getObjectType() {
        return String.class;
    }
}
