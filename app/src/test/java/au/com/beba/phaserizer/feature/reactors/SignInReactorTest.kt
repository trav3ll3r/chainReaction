package au.com.beba.phaserizer.feature.reactors

import org.junit.Assert.*
import org.junit.Test

class SignInReactorTest {
//    @Test
//    fun simpleTest() {
//        assertTrue(true)
//    }

    @Test
    fun testSignIn() {
        val l = LoggingInReactor()
        val s = SignInReactor()
        val ps = PostSignInReactor()

        l.addToChain(s)
        l.addToChain(ps)

        l.startReaction()

        assertEquals("L", l.getReactionResult())
        assertEquals("S", s.getReactionResult())
        assertEquals("PS", ps.getReactionResult())
    }
}
