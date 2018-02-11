package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.testData.A1ChainSuccess
import org.junit.Assert

class OneSuccessChainTest : AbstractUnitTest() {
    private val chainA1 = A1ChainSuccess()

    override fun buildChain(): Chain {
        return chainA1
    }

    override fun assertChain() {
        // ASSERT CHAIN Result AND Status
        log("asserting Result")
        Assert.assertEquals("A1", chainA1.getChainResult())
        log("asserting ChainStatus")
        Assert.assertEquals(ChainCallback.Status.SUCCESS, chainA1.getChainStatus())
    }
}