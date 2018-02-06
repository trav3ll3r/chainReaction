package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class SignInReactionTest {
    @Test
    fun testSignIn() {
        val l = LoggingInChain()
        val s = SignInChain()
        s.addReaction(Reaction(type = "SIGN-IN", task = {
            if (s.getChainResult() == "S") {
                s.setChainStatus(ChainReactionCallback.Status.SUCCESS)
            }
        }))
        val ps = PostSignInChain()

        l.addToChain(s)
        l.addToChain(ps)

        ps.addToChain(GetAccountsChain())
        ps.addToChain(GetCardsChain())

        val chainReactionCallback = object : ChainReactionCallback {
            override fun onDone(status: ChainReactionCallback.Status) {
                ConsoleLogger.log("--- ASSERT START ---")
                // ASSERT L
                assertEquals("L", l.getChainResult())
                assertEquals(ChainReactionCallback.Status.NOT_STARTED, l.getChainStatus())

                // ASSERT S
                assertEquals("S", s.getChainResult())
                assertEquals(ChainReactionCallback.Status.SUCCESS, s.getChainStatus())

                // ASSERT PS
                assertEquals("PS", ps.getChainResult())

                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        l.startReaction(chainReactionCallback)
        ConsoleLogger.log("--- END ---")
    }
}
