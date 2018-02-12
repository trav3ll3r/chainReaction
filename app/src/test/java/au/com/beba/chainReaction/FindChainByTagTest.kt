package au.com.beba.chainReaction

import au.com.beba.chainreaction.chain.Chain
import org.junit.Assert.assertEquals
import org.junit.Test

class FindChainByTagTest {

    @Test
    fun findA1() {
        val topChain = buildChain()
        val tag = findInChain("A1ChainSuccess", topChain)

        assertEquals(topChain, tag)
    }

    @Test
    fun findB1() {
        val topChain = buildChain()
        val tag = findInChain("B1ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[0], tag)
    }

    @Test
    fun findC1() {
        val topChain = buildChain()
        val tag = findInChain("C1ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[1], tag)
    }

    @Test
    fun findC11() {
        val topChain = buildChain()
        val tag = findInChain("C11ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[1].getChainLinks()[0], tag)
    }

    @Test
    fun findC12() {
        val topChain = buildChain()
        val tag = findInChain("C12ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[1].getChainLinks()[1], tag)
    }

    @Test
    fun findC121() {
        val topChain = buildChain()
        val tag = findInChain("C121ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[1].getChainLinks()[1].getChainLinks()[0], tag)
    }

    @Test
    fun findD1() {
        val topChain = buildChain()
        val tag = findInChain("D1ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[2], tag)
    }

    @Test
    fun findE1() {
        val topChain = buildChain()
        val tag = findInChain("E1ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[3], tag)
    }

    @Test
    fun findE11() {
        val topChain = buildChain()
        val tag = findInChain("E11ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[3].getChainLinks()[0], tag)
    }

    @Test
    fun findF1() {
        val topChain = buildChain()
        val tag = findInChain("F1ChainSuccess", topChain)

        assertEquals(topChain.getChainLinks()[4], tag)
    }

    private fun findInChain(needle: String, chain: Chain): Chain? {
        var result: Chain? = null

        val haystackTag = buildChainTag(chain)
        if (haystackTag == needle) {
            result = chain
        } else {
            val children = chain.getChainLinks().size
            if (children > 0) {
                (0 until children).forEach {
                    val interim = findInChain(needle, chain.getChainLinks()[it])
                    if (interim != null) {
                        result = interim
                        return@forEach
                    }
                }
            }
        }
        return result
    }

    private fun buildChainTag(chain: Chain): String {
        return chain::class.java.simpleName
    }

    private fun buildChain(): Chain {
        return A1ChainSuccess().addToChain(
                B1ChainSuccess(),
                C1ChainSuccess().addToChain(C11ChainSuccess(), C12ChainSuccess().addToChain(C121ChainSuccess())),
                D1ChainSuccess(),
                E1ChainSuccess().addToChain(E11ChainSuccess()),
                F1ChainSuccess())
    }
}
