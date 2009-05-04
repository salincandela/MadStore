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
package it.pronetics.madstore.repository.test.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * {@link TesterThread} factory, with facility methods for verifying execeptions and assertions
 * of created threads.
 */
public class TesterThreadFactory implements ThreadFactory {

    private final List<TesterThread> threads = Collections.<TesterThread>synchronizedList(new LinkedList<TesterThread>());
    
    public TesterThread newThread(Runnable runnable) {
        TesterThread thread = new TesterThread(runnable);
        this.threads.add(thread);
        return thread;
    }

    public void resetThreads() {
        this.threads.clear();
    }
    
    public void verifyThreads() throws AssertionError, Exception {
        for (TesterThread thread : this.threads) {
            thread.verifyAndThrow();
        }
    }
}
