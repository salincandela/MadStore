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
package it.pronetics.madstore.server.test;

public class HttpConstants {

    public static final String SERVER_URL = "http://localhost:8080/it/";

    public static final String SERVICE_DOCUMENT_URL = "service";

    public static final String ENTRIES_FEED_URL = "entries";
    public static final String ENTRIES_FEED_URL_M = "entries?max=3";
    public static final String ENTRIES_FEED_URL_P1 = "entries?page=1&max=1";
    public static final String ENTRIES_FEED_URL_P2 = "entries?page=2&max=1";
    public static final String ENTRIES_FEED_URL_P3 = "entries?page=3&max=1";
    public static final String ENTRIES_FEED_URL_P4 = "entries?page=4&max=1";

    public static final String ENTRY_1_URL = "entries/entry1"
            ;
    public static final String OPENSEARCH_DESCRIPTION_URL = "search";
    
    public static final String OPENSEARCH_URL = "search/entries?title=MadStore&terms=entry";
    public static final String OPENSEARCH_URL_1 = "search/entries?title=MadStore&terms=entry+one";
    public static final String OPENSEARCH_URL_2 = "search/entries?title=MadStore&terms=entry+two";
    public static final String OPENSEARCH_URL_3 = "search/entries?title=MadStore&terms=entry+three";
    public static final String OPENSEARCH_URL_P1 = "search/entries?title=MadStore&terms=entry&max=1&page=1";
    public static final String OPENSEARCH_URL_P2 = "search/entries?title=MadStore&terms=entry&max=1&page=2";
    public static final String OPENSEARCH_URL_P3 = "search/entries?title=MadStore&terms=entry&max=1&page=3";
    public static final String OPENSEARCH_URL_P4 = "search/entries?title=MadStore&terms=entry&max=1&page=4";
}
