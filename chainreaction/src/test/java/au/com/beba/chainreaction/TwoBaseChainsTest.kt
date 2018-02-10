package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.testData.A1ChainSuccess
import au.com.beba.chainreaction.testData.B1ChainSuccess
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TwoBaseChainsTest {
    @Suppress("PrivatePropertyName")
    private val TAG: String = TwoBaseChainsTest::class.java.simpleName

    @Test
    fun testSuccessBoth() {
        var callbackExecuted = false

        val chainA1 = A1ChainSuccess()
        val chainB1 = B1ChainSuccess()

        chainA1.addToChain(chainB1)
        
        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                callbackExecuted = true
                ConsoleLogger.log(TAG, "--- ASSERT START ---")
                // ASSERT A1 CHAIN Result AND Status
                ConsoleLogger.log(TAG, "asserting A1 Result")
                assertEquals("A1", chainA1.getChainResult())
                ConsoleLogger.log(TAG, "asserting A1 ChainStatus")
                assertEquals(ChainCallback.Status.SUCCESS, chainA1.getChainStatus())

                // ASSERT B1 CHAIN Result AND Status
                ConsoleLogger.log(TAG, "asserting B1 Result")
                assertEquals("B1", chainB1.getChainResult())
                ConsoleLogger.log(TAG, "asserting B1 ChainStatus")
                assertEquals(ChainCallback.Status.SUCCESS, chainB1.getChainStatus())

                ConsoleLogger.log(TAG, "--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log(TAG, "--- START ---")
        chainA1.startChain(chainReactionCallback)
        assertTrue(callbackExecuted)
        ConsoleLogger.log(TAG, "--- END ---")
    }
}


