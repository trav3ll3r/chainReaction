package au.com.beba.chainReaction

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import au.com.beba.chainReaction.feature.BaseView
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainReaction.feature.ConnectorView
import au.com.beba.chainReaction.testData.AbcChain
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy
import java.util.concurrent.atomic.AtomicInteger

fun getChainViewByTag(canvas: ViewGroup, viewTag: String): BaseView? {
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

fun findChildBefore(parent: Chain?, beforeSibling: Chain): Chain? {
    if (parent != null) {
        var previousSibling: Chain? = null
        for (currentSibling in parent.getChainLinks()) {
            if (currentSibling == beforeSibling) {
                return previousSibling
            }
            previousSibling = currentSibling
        }
    }
    return null
}

fun findChildParent(child: Chain, haystack: Chain): Chain? {
    var result: Chain? = null

    if (haystack.getChainLinks().contains(child)) {
        result = haystack
    } else {
        val children = haystack.getChainLinks().size
        if (children > 0) {
            (0 until children).forEach {
                val interim = findChildParent(child, haystack.getChainLinks()[it])
                if (interim != null) {
                    result = interim
                    return@forEach
                }
            }
        }
    }
    return result
}

fun determineConnectorType(currentChain: Chain, parentChain: Chain): ConnectorView.Type {
    return if (parentChain.getChainLinks().size == 1) {
        ConnectorView.Type.ONLY_CHILD
    } else {
        when {
            isFirstChild(currentChain, parentChain) -> ConnectorView.Type.FIRST_CHILD
            isLastChild(currentChain, parentChain) -> ConnectorView.Type.LAST_CHILD
            else -> ConnectorView.Type.MIDDLE_CHILD
        }
    }
}

fun isFirstChild(currentChain: Chain, parentChain: Chain?): Boolean {
    return currentChain == parentChain?.getChainLinks()?.firstOrNull()
}

private fun isLastChild(currentChain: Chain, parentChain: Chain): Boolean {
    val links = parentChain.getChainLinks()
    if (links.isNotEmpty()) {
        val lastLink = links[links.size - 1]
        return currentChain == lastLink
    }
    return false
}

fun buildChainTag(chain: Chain): String {
    return chain::class.java.simpleName
}

fun buildChainConnectorTag(chain: Chain): String {
    return "%s-%s".format(chain::class.java.simpleName, "connector")
}

private val nextGeneratedId = AtomicInteger(1)
fun generateViewId(): Int {
    while (true) {
        val result = nextGeneratedId.get()
        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
        var newValue = result + 1
        if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
        if (nextGeneratedId.compareAndSet(result, newValue)) {
            return result
        }
    }
}

fun buildChainView(context: Context, chain: AbcChain): ChainView {
    val view = ChainView(context, null)
    view.id = generateViewId()
    val pixelsPerSecond = context.resources.getDimensionPixelSize(R.dimen.dips_per_second)
    val width = (chain.getSleepTime().toDouble() / 1000 * pixelsPerSecond).toInt()
    view.layoutParams = RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT)
    view.tag = buildChainTag(chain)
    return view
}

fun connectorFactory(context: Context, chain: Chain?, connectorType: ConnectorView.Type, executionStrategy: ExecutionStrategy): ConnectorView {
    val view = ConnectorView(context, null, 0, connectorType, executionStrategy)
    view.id = generateViewId()
    view.layoutParams = RelativeLayout.LayoutParams(
            context.resources.getDimensionPixelSize(R.dimen.connector_width),
            context.resources.getDimensionPixelSize(R.dimen.chain_height))
    if (chain != null) {
        view.tag = when (chain) { null -> ""
            else -> buildChainConnectorTag(chain)
        }
    }
    return view
}
