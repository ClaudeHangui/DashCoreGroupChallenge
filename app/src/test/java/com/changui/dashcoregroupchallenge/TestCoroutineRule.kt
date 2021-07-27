package com.changui.dashcoregroupchallenge

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * This class is a unit test rule which watches for tests starting and finishing.
 * To test regular suspend functions or coroutines started with launch or async we use the runBlockingTest coroutine builder
 * that provides extra test control to coroutines.
 */
@ExperimentalCoroutinesApi
class TestCoroutineRule: TestRule {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            base.evaluate()
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }
}