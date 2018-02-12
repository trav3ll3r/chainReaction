package au.com.beba.chainReaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import org.jetbrains.anko.find
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import au.com.beba.chainreaction.chain.ChainCallback
import java.util.concurrent.atomic.AtomicInteger

const val CHAIN_REACTION_EVENT : String = "au.com.beba.chainReaction.CHAIN_REACTION_EVENT"
const val CHAIN_CLASS : String = "au.com.beba.chainReaction.CHAIN_CLASS"

class VisualiseChainActivity : AppCompatActivity() {

    private val tag: String = VisualiseChainActivity::class.java.simpleName

    private lateinit var canvas: RelativeLayout
    private lateinit var rootAnchorView: View
    private lateinit var topChain: Chain

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.v(tag, "Received broadcast intent [%s]".format(intent))
            val bundle = intent?.extras
            if (bundle != null) {
                val chainTag = bundle[CHAIN_CLASS] as String
                val chain = getChainByTag(chainTag)
                Log.v(tag, "Received broadcast chainTag [%s] for chain [%s]".format(chainTag, chain))
                updateChainView(chain)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualise_chain)

        find<Button>(R.id.btn_show_test).setOnClickListener { visualise() }
        find<Button>(R.id.btn_start_test).setOnClickListener { executeChain() }
        canvas = find(R.id.chain_canvas)
        rootAnchorView = find(R.id.anchor_view)
    }

    private fun visualise() {
        topChain = buildChain()
        drawChain(topChain, null, rootAnchorView)
    }

    private fun executeChain() {
        registerListener()

        topChain.startChain(object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                unregisterListener()
            }
        })
    }

    private fun registerListener() {
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver, IntentFilter(CHAIN_REACTION_EVENT))
    }

    private fun unregisterListener() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
    }

    private var bottomMost: View? = null
    private fun drawChain(chain: Chain, parent: Chain?, anchor: View): View {
        bottomMost = anchor
        var newAnchor = placeChainView(chain, parent, anchor)

        for (chainLink: Chain in chain.getChainLinks()) {
            newAnchor = drawChain(chainLink, chain, newAnchor)
            bottomMost = newAnchor
        }

        return newAnchor
    }

    private fun placeChainView(chain: Chain, parent: Chain?, anchor: View): View {
        val abcChain = chain as AbcChain
        val v = ChainView(this)
        v.id = generateViewId()
        v.update(abcChain)

        var parentView: ChainView? = null
        if (parent != null) {
            parentView = getChainViewByTag(buildChainTag(parent))
        }

        v.layoutParams = RelativeLayout.LayoutParams(abcChain.getSleepTime().toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
        val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
        layoutParams.leftMargin = 2
        layoutParams.topMargin = 2

        place(layoutParams, parentView, anchor)
        v.layoutParams = layoutParams
        v.tag = buildChainTag(chain)

        canvas.addView(v)

        return v
    }

    private fun updateChainView(chain: Chain?) {
        if (chain != null) {
            val chainView = getChainViewByTag(buildChainTag(chain))
            chainView?.update(chain as AbcChain)
        }
    }

    private fun place(lp: RelativeLayout.LayoutParams, parentView: View?, anchorView: View) {
        if (parentView == null) {
            // PLACE AGAINST anchor
            lp.addRule(RelativeLayout.RIGHT_OF, anchorView.id)
        } else {
            // PLACE AGAINST parent AND anchor
            lp.addRule(RelativeLayout.RIGHT_OF, parentView.id)
        }

        lp.addRule(RelativeLayout.BELOW, bottomMost!!.id)
    }

    private fun getChainViewByTag(viewTag: String): ChainView? {
        return canvas.findViewWithTag(viewTag)
    }

    private fun getChainByTag(viewTag: String): Chain? {
        return findInChain(viewTag, topChain)
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
        return AChain(this).addToChain(
                BChain(this),
                CChain(this).addToChain(C1Chain(this), C2Chain(this)),
                DChain(this),
                EChain(this).addToChain(E1Chain(this)),
                FChain(this))
    }

    private val nextGeneratedId = AtomicInteger(1)
    private fun generateViewId(): Int {
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
}
