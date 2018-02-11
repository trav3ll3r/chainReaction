package au.com.beba.chainreaction

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.testData.*
import org.junit.Assert.assertEquals

class SignInReactionTest : AbstractUnitTest() {
    private val l = LoggingInChain()
    private val s = SignInChain()
    private val ps = PostSignInChain()
    private val sga = GetAccountsChain()
    private val sgc = GetCardsChain()

    override fun buildChain(): Chain {
//        s.addReaction(Reaction(type = "SIGN-IN", task = {
//            ConsoleLogger.log("SignInTAG","******** REACTION SIGN-IN ********")
//            if (s.getChainResult() == "S") {
//                s.setChainStatus(ChainCallback.Status.SUCCESS)
//            }
//        }))

        l.addToChain(s)
        l.addToChain(ps)

        ps.addToChain(sga)
        ps.addToChain(sgc)

        return l
    }

    override fun assertChain() {
        // ASSERT L
        assertEquals("L", l.getChainResult())
        assertEquals(ChainCallback.Status.SUCCESS, l.getChainStatus())

        // ASSERT S
        assertEquals("S", s.getChainResult())
        assertEquals(ChainCallback.Status.SUCCESS, s.getChainStatus())

        // ASSERT S-GA
        assertEquals("S-GA", sga.getChainResult())
        assertEquals(ChainCallback.Status.SUCCESS, sga.getChainStatus())

        // ASSERT S-GC
        assertEquals("S-GC", sgc.getChainResult())
        assertEquals(ChainCallback.Status.SUCCESS, sgc.getChainStatus())

        // ASSERT PS
        assertEquals("PS", ps.getChainResult())
        assertEquals(ChainCallback.Status.SUCCESS, ps.getChainStatus())
    }
}
