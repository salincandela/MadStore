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

import java.util.Map;

/**
 * Interface for representing messages actors exchange to implement the crawling process.<br>
 * Each specific message implementation can be executed on one or more specific actor
 * implementations.
 *
 * @author Sergio Bossa
 */
public interface ActorMessage {

    public void executeOn(CrawlerActor actor);

    public void executeOn(DownloaderActor actor);

    public void executeOn(ParserActor actor);

    public void executeOn(ProcessorActor actor);

    public Map<Class, Actor> getActorsTopology();
}
