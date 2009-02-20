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

import java.util.Collections;
import java.util.Map;

/**
 * @author Sergio Bossa
 */
public abstract class AbstractActorMessage implements ActorMessage {

    private final Map<Class, Actor> actorsTopology;

    public AbstractActorMessage(Map<Class, Actor> actorsTopology) {
        this.actorsTopology = actorsTopology;
    }

    public void executeOn(CrawlerActor actor) {
        throw new UnsupportedOperationException("Message not supported by actor: " + actor);
    }

    public void executeOn(DownloaderActor actor) {
        throw new UnsupportedOperationException("Message not supported by actor: " + actor);
    }

    public void executeOn(ParserActor actor) {
        throw new UnsupportedOperationException("Message not supported by actor: " + actor);
    }

    public void executeOn(ProcessorActor actor) {
        throw new UnsupportedOperationException("Message not supported by actor: " + actor);
    }

    public Map<Class, Actor> getActorsTopology() {
        return Collections.unmodifiableMap(actorsTopology);
    }
}
