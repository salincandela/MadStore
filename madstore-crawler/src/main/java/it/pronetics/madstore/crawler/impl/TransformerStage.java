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
package it.pronetics.madstore.crawler.impl;

import it.pronetics.madstore.crawler.Stage;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.transformer.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link it.pronetics.madstore.crawler.Stage} which transforms the input page through a
 * configured {@link it.pronetics.madstore.crawler.transformer.Transformer}.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class TransformerStage implements Stage {

    private static final transient Logger LOG = LoggerFactory.getLogger(TransformerStage.class);
    private Transformer transformer;

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public Page execute(Page page) {
        try {
            LOG.info("Transforming page {}", page.getLink());
            LOG.debug("Transforming page:\n{}", page.getData());
            byte[] transformedBytes = transformer.transform(page);
            if (transformedBytes.length > 0) {
                String transformedContent = new String(transformedBytes, "UTF-8");
                return new Page(page.getLink(), transformedContent);
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
