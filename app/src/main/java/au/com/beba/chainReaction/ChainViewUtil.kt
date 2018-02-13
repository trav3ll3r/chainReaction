package au.com.beba.chainReaction

import android.view.ViewGroup
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainreaction.chain.Chain

fun getChainViewByTag(canvas: ViewGroup, viewTag: String): ChainView? {
    return canvas.findViewWithTag(viewTag)
}

fun getChainByTag(viewTag: String, topChain: Chain): Chain? {
    return findInChain(viewTag, topChain)
}

fun findInChain(needle: String, chain: Chain): Chain? {
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

fun buildChainTag(chain: Chain): String {
    return chain::class.java.simpleName
}
