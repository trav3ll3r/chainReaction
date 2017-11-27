package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger
import org.junit.Assert.assertEquals
import org.junit.Test

class SignInReactionTest {
    @Test
    fun testSignIn() {
        val l = LoggingInReaction()
        val s = SignInReaction()
        val ps = PostSignInReaction()

        l.addToChain(s)
        l.addToChain(ps)

        val chainReactionCallback = object : ChainReactionCallback {
            override fun onDone(status: ChainReactionCallback.Status) {
                ConsoleLogger.log("--- ASSERT START ---")
                assertEquals("L", l.getReactionResult())
                assertEquals("S", s.getReactionResult())
                assertEquals("PS", ps.getReactionResult())
                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        l.startReaction(chainReactionCallback)
        ConsoleLogger.log("--- END ---")
    }
}
