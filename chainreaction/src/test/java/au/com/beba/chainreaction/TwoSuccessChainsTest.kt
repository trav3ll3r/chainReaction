package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.testData.A1ChainSuccess
import au.com.beba.chainreaction.testData.B1ChainSuccess
import org.junit.Assert.assertEquals

class TwoSuccessChainsTest : AbstractUnitTest() {
    private val chainA1 = A1ChainSuccess()
    private val chainB1 = B1ChainSuccess()

    override fun buildChain(): Chain {
        chainA1.addToChain(chainB1)
        return chainA1
    }

    override fun assertChain() {
        // ASSERT A1 CHAIN Result AND Status
        log("asserting A1 Result")
        assertEquals("A1", chainA1.getChainResult())
        log("asserting A1 ChainStatus")
        assertEquals(ChainCallback.Status.SUCCESS, chainA1.getChainStatus())

        // ASSERT B1 CHAIN Result AND Status
        log("asserting B1 Result")
        assertEquals("B1", chainB1.getChainResult())
        log("asserting B1 ChainStatus")
        assertEquals(ChainCallback.Status.SUCCESS, chainB1.getChainStatus())
    }
}


