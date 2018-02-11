package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.testData.A2ChainError
import org.junit.Assert

class OneErrorChainTest : AbstractUnitTest() {
    private val chainA2 = A2ChainError()

    override fun buildChain(): Chain {
        return chainA2
    }

    override fun assertChain() {
        // ASSERT CHAIN Result AND Status
        log("asserting Result")
        Assert.assertEquals("A2", chainA2.getChainResult())
        log("asserting ChainStatus")
        Assert.assertEquals(ChainCallback.Status.ERROR, chainA2.getChainStatus())
    }
}