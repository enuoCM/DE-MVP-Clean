/*
 * Copyright (C) 2016 mc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xixicm.de.domain.base.scheduler;

/**
 * Interface for request schedulers
 *
 * @author mc
 */
public interface UseCaseRequestScheduler {
    /**
     * Executes the {@link Runnable} in a new thread or current thread
     *
     * @param runnable The class that implements {@link Runnable} interface.
     */
    void execute(Runnable runnable);
}
