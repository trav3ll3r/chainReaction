package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class SignInReactionTest {
    @Test
    fun testSignIn() {
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

        ps.addToChain(GetAccountsChain())
        ps.addToChain(GetCardsChain())

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                ConsoleLogger.log("--- ASSERT START ---")
                // ASSERT L
                assertEquals("L", l.getChainResult())
                assertEquals(ChainCallback.Status.SUCCESS, l.getChainStatus())

                // ASSERT S
                assertEquals("S", s.getChainResult())
                assertEquals(ChainCallback.Status.SUCCESS, s.getChainStatus())

                // ASSERT PS
                assertEquals("PS", ps.getChainResult())
                assertEquals(ChainCallback.Status.SUCCESS, ps.getChainStatus())

                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        l.startChain(chainReactionCallback)
        ConsoleLogger.log("--- END ---")
    }
}
