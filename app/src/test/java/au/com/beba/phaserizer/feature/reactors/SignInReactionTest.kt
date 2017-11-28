package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class SignInReactionTest {
    @Test
    fun testSignIn() {
        val l = LoggingInReaction()
        val s = SignInReaction()
        s.addReaction(Reaction(type = "SIGN-IN", task = {
            if (s.getReactionResult() == "S") {
                s.setReactionStatus(ChainReactionCallback.Status.SUCCESS)
            }
        }))
        val ps = PostSignInReaction()

        l.addToChain(s)
        l.addToChain(ps)

        val chainReactionCallback = object : ChainReactionCallback {
            override fun onDone(status: ChainReactionCallback.Status) {
                ConsoleLogger.log("--- ASSERT START ---")
                // ASSERT L
                assertEquals("L", l.getReactionResult())
                assertEquals(ChainReactionCallback.Status.NOT_STARTED, l.getReactionStatus())

                // ASSERT S
                assertEquals("S", s.getReactionResult())
                assertEquals(ChainReactionCallback.Status.SUCCESS, s.getReactionStatus())

                // ASSERT PS
                assertEquals("PS", ps.getReactionResult())

                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        l.startReaction(chainReactionCallback)
        ConsoleLogger.log("--- END ---")
    }
}
