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
package it.pronetics.madstore.crawler.impl.local;

import com.googlecode.actorom.Actor;
import com.googlecode.actorom.Address;
import com.googlecode.actorom.Topology;
import com.googlecode.actorom.annotation.OnMessage;
import com.googlecode.actorom.annotation.TopologyInstance;
import it.pronetics.madstore.crawler.Pipeline;
import it.pronetics.madstore.crawler.model.Link;
import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.publisher.AtomPublisher;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Actor} implementation processing pages thorugh a {@link it.pronetics.madstore.crawler.Pipeline}.
 *
 * @author Sergio Bossa
 */
public class ProcessorActor {

    private static final transient Logger LOG = LoggerFactory.getLogger(ProcessorActor.class);
    //
    @TopologyInstance
    private Topology actorsTopology;
    //
    private final Address crawlerAddress;
    //
    private final AtomPublisher publisher;
    private final Pipeline pipeline;

    public ProcessorActor(AtomPublisher publisher, Pipeline pipeline, Address crawlerAddress) {
        this.publisher = publisher;
        this.pipeline = pipeline;
        this.crawlerAddress = crawlerAddress;
    }

    @OnMessage(type=ParsedPageMessage.class)
    public void processPage(ParsedPageMessage parsedPageMessage) {
        Page page = parsedPageMessage.getPage();
        Link sourceLink = page.getLink();
        Collection<Link> outgoingLinks = parsedPageMessage.getOutgoingLinks();
        Page processedPage = pipeline.execute(page);
        if (processedPage != null) {
            LOG.info("Publishing page at: {}", sourceLink);
            publisher.publish(processedPage);
        } else {
            LOG.info("Page at {} will not be published!", sourceLink);
        }
        OutgoingLinksMessage outgoingLinksMessage = new OutgoingLinksMessage(sourceLink, outgoingLinks);
        Actor crawlerActor = actorsTopology.getActor(crawlerAddress);
        crawlerActor.send(outgoingLinksMessage);
    }
}
