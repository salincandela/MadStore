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

import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Abstract class to be extended by all <code>FactoryBean</code> implementations, providing
 * helper methods for accessing the current {@link MadStoreConfigurationBean} and beans declared
 * in the current Spring context.
 *
 * @author Sergio Bossa
 */
public abstract class AbstractMadStoreConfigurationFactoryBean implements ApplicationContextAware, FactoryBean {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public boolean isSingleton() {
        return true;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected MadStoreConfigurationBean getMadStoreConfiguration() {
        return MadStoreConfigurationManager.getInstance().getMadStoreConfiguration();
    }

    protected <T> T getUniqueBean(Class<T> beanClass) {
        Collection beans = applicationContext.getBeansOfType(beanClass).values();
        if (beans.size() == 1) {
            return (T) beans.iterator().next();
        } else if (beans.size() == 0) {
            throw new IllegalStateException("No " + beanClass + " found in Spring application context!");
        } else {
            throw new IllegalStateException("More than one " + beanClass + " found in Spring application context!");
        }
    }

    protected <T> T getUniqueBean(String beanName) {
        Object bean = applicationContext.getBean(beanName);
        return (T) bean;
    }
}
