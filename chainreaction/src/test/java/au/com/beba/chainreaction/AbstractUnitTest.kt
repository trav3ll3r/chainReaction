package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class AbstractUnitTest {
    private fun getTag(): String = this.javaClass.simpleName

    protected abstract fun buildChain(): Chain

    protected abstract fun assertChain()

    @Test
    fun runTest() {
//        val latch = CountDownLatch(1)

        var callbackExecuted = false

        val testChain = buildChain()

        testChain.setParentCallback(object : ChainCallback<Chain> {
            override fun onDone(completedChain: Chain) {
                callbackExecuted = true
                log("--- ASSERT START ---")

                assertChain()

                log("--- ASSERT DONE ---")
                //latch.countDown()
            }
        })

        log("--- START ---")
//        testChain.startChainOnSameThread(chainReactionCallback)
//        latch.await()
        val parallelExecutor: ExecutorService = Executors.newCachedThreadPool()
        parallelExecutor.submit(testChain).get()
        Assert.assertTrue(callbackExecuted)
        log("--- END ---")
    }

    protected fun log(message: String) {
        ConsoleLogger.log(getTag(), message)
    }
}