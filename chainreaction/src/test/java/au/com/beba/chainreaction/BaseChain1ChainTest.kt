package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.testData.A1ChainSuccess
import au.com.beba.chainreaction.testData.A2ChainError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OneBaseChainTest {
    @Suppress("PrivatePropertyName")
    private val TAG: String = OneBaseChainTest::class.java.simpleName

    @Test
    fun testSuccess() {
        var callbackExecuted = false

        val chainA1 = A1ChainSuccess()

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                callbackExecuted = true
                ConsoleLogger.log(TAG, "--- ASSERT START ---")
                // ASSERT CHAIN Result AND Status
                ConsoleLogger.log(TAG, "asserting Result")
                assertEquals("A1", chainA1.getChainResult())
                ConsoleLogger.log(TAG, "asserting ChainStatus")
                assertEquals(ChainCallback.Status.SUCCESS, chainA1.getChainStatus())

                ConsoleLogger.log(TAG, "--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log(TAG, "--- START ---")
        chainA1.startChain(chainReactionCallback)
        assertTrue(callbackExecuted)
        ConsoleLogger.log(TAG, "--- END ---")
    }

    @Test
    fun testError() {
        var callbackExecuted = false

        val chainA2 = A2ChainError()

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                callbackExecuted = true
                ConsoleLogger.log(TAG, "--- ASSERT START ---")
                // ASSERT CHAIN Result AND Status
                ConsoleLogger.log(TAG, "asserting Result")
                assertEquals("A2", chainA2.getChainResult())
                ConsoleLogger.log(TAG, "asserting ChainStatus")
                assertEquals(ChainCallback.Status.ERROR, chainA2.getChainStatus())

                ConsoleLogger.log(TAG, "--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log(TAG, "--- START ---")
        chainA2.startChain(chainReactionCallback)
        assertTrue(callbackExecuted)
        ConsoleLogger.log(TAG, "--- END ---")
    }
}


