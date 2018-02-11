package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.testData.A1ChainSuccess
import au.com.beba.chainreaction.testData.B1ChainSuccess
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch

abstract class AbstractUnitTest {
    private fun getTag():String = this.javaClass.simpleName

    protected abstract fun buildChain(): Chain

    protected abstract fun assertChain()

    @Test
    fun runTest() {
        val latch = CountDownLatch(1)

        var callbackExecuted = false

        val testChain = buildChain()

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                callbackExecuted = true
                log("--- ASSERT START ---")

                assertChain()

                log("--- ASSERT DONE ---")
                latch.countDown()
            }
        }

        log("--- START ---")
        testChain.startChainOnSameThread(chainReactionCallback)
        latch.await()
        Assert.assertTrue(callbackExecuted)
        log("--- END ---")
    }

    protected fun log(message: String) {
        ConsoleLogger.log(getTag(), message)
    }
}