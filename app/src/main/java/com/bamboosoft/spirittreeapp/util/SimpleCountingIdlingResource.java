/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.util;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An simple counter implementation of {@link IdlingResource} that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle. This is very similar to the way a {@link java.util.concurrent.Semaphore}
 * behaves.
 * { @ link IdlingResource }的一个简单的计数器实现，它通过维护一个内部计数器来决定懒惰。
 * 当计数器为0时，它被认为是空闲的，当它是非零的时候它不是空闲的。
 * 这和{ @ link java.util . concurrent的方式非常相似。信号量}的行为。
 * <p>
 * This class can then be used to wrap up operations that while in progress should block tests from
 * accessing the UI.
 * 然后，这个类可以用来包装操作，而在进度中，应该阻止测试访问UI。
 */
public final class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;

    private final AtomicInteger counter = new AtomicInteger(0);

    // written from main thread, read from any thread.
	// 从主线程写，从任何线程读取。
    private volatile ResourceCallback resourceCallback;

    /**
     * Creates a SimpleCountingIdlingResource
     * 创建一个SimpleCountingIdlingResource
	 *
     * @param resourceName the resource name this resource should report to Espresso.
     * @ param resourceName资源名称这个资源应该向Espresso报告。
	 */
    public SimpleCountingIdlingResource(String resourceName) {
        mResourceName = checkNotNull(resourceName);
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     * 增加对正在监视的资源的内部事务的计数。
	 */
    public void increment() {
        counter.getAndIncrement();
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     * 减少对正在监视的资源的飞行事务的计数。
	 * 
     * If this operation results in the counter falling below 0 - an exception is raised.
     * 如果此操作导致计数器低于0，则会引发异常。
	 *
     * @throws IllegalStateException if the counter is below 0.
	 * 如果计数器小于0，则抛出IllegalStateException。
     */
    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            // 我们从非零到零。那意味着我们现在是空闲的!告诉咖啡。
			if (null != resourceCallback) {
                resourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0) {
            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}
