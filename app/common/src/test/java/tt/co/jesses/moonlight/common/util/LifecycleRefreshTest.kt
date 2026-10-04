package tt.co.jesses.moonlight.common.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class LifecycleRefreshTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshes on every cycle while started`() = runTest(dispatcher) {
        val owner = TestLifecycleOwner(Lifecycle.State.STARTED, dispatcher)
        var count = 0
        val job = launch { owner.lifecycle.refreshWhileStarted(30.seconds) { count++ } }

        advanceTimeBy(95.seconds)
        assertEquals(3, count)
        job.cancel()
    }

    @Test
    fun `does not refresh while the app is in the background`() = runTest(dispatcher) {
        val owner = TestLifecycleOwner(Lifecycle.State.STARTED, dispatcher)
        var count = 0
        val job = launch { owner.lifecycle.refreshWhileStarted(30.seconds) { count++ } }
        advanceTimeBy(35.seconds)
        assertEquals(1, count)

        owner.setCurrentState(Lifecycle.State.CREATED) // pressing Home: the activity is stopped
        advanceTimeBy(10.minutes)

        assertEquals(1, count)
        job.cancel()
    }

    @Test
    fun `refreshes straight away when it comes back, then carries on`() = runTest(dispatcher) {
        val owner = TestLifecycleOwner(Lifecycle.State.STARTED, dispatcher)
        var count = 0
        val job = launch { owner.lifecycle.refreshWhileStarted(30.seconds) { count++ } }
        advanceTimeBy(35.seconds)
        owner.setCurrentState(Lifecycle.State.CREATED)
        advanceTimeBy(5.minutes)
        assertEquals(1, count)

        owner.setCurrentState(Lifecycle.State.STARTED)
        advanceTimeBy(1.seconds)
        assertEquals(2, count) // refreshed on return, without waiting a full cycle

        advanceTimeBy(30.seconds)
        assertEquals(3, count) // and the normal cycle resumes
        job.cancel()
    }

    @Test
    fun `nothing runs before the first start, and the first start does not refresh twice`() = runTest(dispatcher) {
        val owner = TestLifecycleOwner(Lifecycle.State.CREATED, dispatcher)
        var count = 0
        val job = launch { owner.lifecycle.refreshWhileStarted(30.seconds) { count++ } }
        advanceTimeBy(5.minutes)
        assertEquals(0, count)

        owner.setCurrentState(Lifecycle.State.STARTED)
        advanceTimeBy(1.seconds)
        assertEquals(0, count) // the initial load already covers the first start
        job.cancel()
    }
}
