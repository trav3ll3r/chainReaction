package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.testData.A1ChainSuccess
import au.com.beba.chainreaction.testData.B1ChainSuccess
import au.com.beba.chainreaction.testData.C1ChainSuccess
import org.junit.Assert

class ThreeSuccessChainTest : AbstractUnitTest() {
    private val chainA1 = A1ChainSuccess()
    private val chainB1 = B1ChainSuccess()
    private val chainC1 = C1ChainSuccess()

    override fun buildChain(): Chain {
        chainA1.addToChain(chainB1)
        chainB1.addToChain(chainC1)
        return chainA1
    }

    override fun assertChain() {
        // ASSERT A1 CHAIN Result AND Status
        log("asserting A1 Result")
        Assert.assertEquals("A1", chainA1.getChainResult())
        log("asserting A1 ChainStatus")
        Assert.assertEquals(ChainCallback.Status.SUCCESS, chainA1.getChainStatus())

        // ASSERT B1 CHAIN Result AND Status
        log("asserting B1 Result")
        Assert.assertEquals("B1", chainB1.getChainResult())
        log("asserting B1 ChainStatus")
        Assert.assertEquals(ChainCallback.Status.SUCCESS, chainB1.getChainStatus())

        // ASSERT C1 CHAIN Result AND Status
        log("asserting C1 Result")
        Assert.assertEquals("C1", chainC1.getChainResult())
        log("asserting C1 ChainStatus")
        Assert.assertEquals(ChainCallback.Status.SUCCESS, chainC1.getChainStatus())
    }
}