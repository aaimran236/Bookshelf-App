package com.example.bookshelfapp.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//The TestWatcher class enables you to take actions on different execution phases of a test.
class TestDispatcherRule(
    /*
     *The UnconfinedTestDispatcher class inherits from the TestDispatcher class and it specifies
     *that tasks must not be executed in any particular order. This pattern of execution is good
     *for simple tests as coroutines are handled automatically.
     */


    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher(){

    /*
     *Since the Main dispatcher is only available in a UI context, you must replace it with a
     *unit-test-friendly dispatcher. The Kotlin Coroutines library provides a coroutine
     *dispatcher for this purpose called TestDispatcher. The TestDispatcher needs to be used
     *instead of the Main dispatcher for any unit test in which a new coroutine is made, as
     * is the case with the getMarsPhotos() function from the view model.

     *To replace the Main dispatcher with a TestDispatcher in all cases, use the
     *Dispatchers.setMain() function. You can use the Dispatchers.resetMain() function to reset
     *the thread dispatcher back to the Main dispatcher. To avoid duplicating the code that
     *replaces the Main dispatcher in each test, you can extract it into a JUnit test rule.
     */
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}