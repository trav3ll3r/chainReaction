package au.com.beba.chainReaction.feature.reactor

import au.com.beba.chainReaction.demo.*
import au.com.beba.chainReaction.feature.logger.ConsoleLogger
import au.com.beba.chainReaction.feature.chain.ChainCallback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInReactionTest {
    @Test
    fun testBaseChain() {
        var callbackExecuted = false

        val l = LoggingInChain()
        val s = SignInChain()
//        s.addReaction(Reaction(type = "SIGN-IN", task = {
//            ConsoleLogger.log("SignInTAG","******** REACTION SIGN-IN ********")
//            if (s.getChainResult() == "S") {
//                s.setChainStatus(ChainCallback.Status.SUCCESS)
//            }
//        }))
        val ps = PostSignInChain()

        l.addToChain(s)
        l.addToChain(ps)

        val sga = GetAccountsChain()
        val sgc = GetCardsChain()
        ps.addToChain(sga)
        ps.addToChain(sgc)

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                callbackExecuted = true
                ConsoleLogger.log("--- ASSERT START ---")
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

                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        l.startChain(chainReactionCallback)
        assertTrue(callbackExecuted)
        ConsoleLogger.log("--- END ---")

    }
}
