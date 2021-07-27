package com.changui.dashcoregroupchallenge

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * One of the interesting problems LiveData solves is to ensure the observer is always
 * called on the main thread.
 * JVM unit tests don’t know anything about the Android main thread!
 * Hence all unit test are executed on a random (background) thread.
 * So to ensure our classes under tests who observe data changes (via livedata.setValue() or livedata.value)
 * are well-managed we need to define a rule in our unit tests
 * In JUnit 5, the concept of Rule and TestRunner are merged into one single concept of Extensions
 * This basically does two things:
 * 1- set a delegate before each test that updates live data values immediately on the calling thread
 * 2- remove the delegate after each tests to avoid influencing other tests.
 */

class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}