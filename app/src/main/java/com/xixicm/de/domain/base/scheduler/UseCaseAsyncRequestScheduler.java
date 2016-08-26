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

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Executes asynchronous task using a {@link ThreadPoolExecutor} or {@link SerialExecutor}.
 * <p/>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 *
 * @author mc
 */
public class UseCaseAsyncRequestScheduler implements UseCaseRequestScheduler {
    private static final String LOG_TAG = "UseCaseAsyncScheduler";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 2;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 3 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, LOG_TAG + " #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * An {@link Executor} that executes tasks one at a time in serial
     * order.  This serialization is global to a particular process.
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    Executor mDefaultExecutor;


    private static UseCaseAsyncRequestScheduler sParallelInstance;
    private static UseCaseAsyncRequestScheduler sSerialInstance;

    /**
     * @return Scheduler using ThreadPoolExecutor
     */
    public synchronized static UseCaseAsyncRequestScheduler getParallelInstance() {
        if (sParallelInstance == null) {
            sParallelInstance = new UseCaseAsyncRequestScheduler();
        }
        return sParallelInstance;
    }

    /**
     * @return Scheduler using SerialExecutor
     */
    public synchronized static UseCaseAsyncRequestScheduler getSerialInstance() {
        if (sSerialInstance == null) {
            sSerialInstance = new UseCaseAsyncRequestScheduler(true);
        }
        return sSerialInstance;
    }

    /**
     * Default to use ThreadPoolExecutor
     */
    UseCaseAsyncRequestScheduler() {
        this(false);
    }

    /**
     * @param serial if true, using SerialExecutor, otherwise ThreadPoolExecutor
     */
    UseCaseAsyncRequestScheduler(boolean serial) {
        mDefaultExecutor = serial ? SERIAL_EXECUTOR : THREAD_POOL_EXECUTOR;
    }

    /**
     * Executes the {@link Runnable} in a new thread
     *
     * @param runnable The class that implements {@link Runnable} interface.
     */
    @Override
    public void execute(Runnable runnable) {
        mDefaultExecutor.execute(runnable);
    }
}
