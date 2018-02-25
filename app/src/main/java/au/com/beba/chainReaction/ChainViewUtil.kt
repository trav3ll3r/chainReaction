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

fun buildChainTag(chain: Chain): String {
    return chain::class.java.simpleName
}

fun buildChainConnectorTag(chain: Chain, prefix: String = "post"): String {
    return "%s:%s%s".format(chain::class.java.simpleName, prefix, "connector")
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

fun buildChainConnector(context: Context, chain: Chain): ConnectorView? {
    var view: ConnectorView? = null
    var connectorType: ConnectorView.Type

    if (chain.getChainLinks().isNotEmpty()) {
        connectorType = ConnectorView.Type.SERIAL
        if (chain.reactor.executionStrategy == ExecutionStrategy.PARALLEL) {
            connectorType = ConnectorView.Type.PARALLEL_PARENT
        }

        view = ConnectorView(context, null, 0, connectorType)
        view.id = generateViewId()
        view.layoutParams = RelativeLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.connector_width), RelativeLayout.LayoutParams.WRAP_CONTENT)
        view.tag = buildChainConnectorTag(chain)
    }

    return view
}

fun buildChainPreConnector(context: Context, chain: Chain, parentChain: Chain?): ConnectorView? {
    var view: ConnectorView? = null
    var connectorType: ConnectorView.Type

    if (parentChain != null) {
        if (parentChain.reactor.executionStrategy == ExecutionStrategy.PARALLEL) {

            val links = parentChain.getChainLinks()
            val lastChild = links[links.lastIndex] == chain

            connectorType = ConnectorView.Type.PARALLEL_MIDDLE
            if (lastChild) {
                connectorType = ConnectorView.Type.PARALLEL_LAST
            }

            view = ConnectorView(context, null, 0, connectorType)
            view.id = generateViewId()
            view.layoutParams = RelativeLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.connector_width), RelativeLayout.LayoutParams.WRAP_CONTENT)
            view.tag = buildChainConnectorTag(chain, "pre")
        }
    }

    return view
}
