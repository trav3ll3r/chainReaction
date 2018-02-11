package au.com.beba.chainReaction

import android.os.Bundle
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
import org.jetbrains.anko.margin
import org.jetbrains.anko.padding
import java.util.concurrent.atomic.AtomicInteger


class VisualiseChainActivity : AppCompatActivity() {

    private val tag: String = VisualiseChainActivity::class.java.simpleName

    private lateinit var canvas: RelativeLayout

    private lateinit var rootAnchorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualise_chain)

        find<Button>(R.id.btn_show_test).setOnClickListener { visualise() }
        find<Button>(R.id.btn_start_test).setOnClickListener { executeChain() }
        canvas = find(R.id.chain_canvas)
        rootAnchorView = find(R.id.anchor_view)
    }

    private fun visualise() {
        val chain = buildChain()
        drawChain(chain, null, rootAnchorView)
    }

    private fun executeChain() {
        val chain = buildChain()
        chain.startChain(object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                //TODO
            }
        })
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
        val v = ChainView(this)
        v.id = generateViewId()
        v.chainDuration = chain::class.java.simpleName
        v.exampleDimension = resources.getDimension(R.dimen.duration_size)
        v.chainBackground = android.R.color.holo_blue_dark

        var parentView: ChainView? = null
        if (parent != null) {
            parentView = getChainViewByTag(getViewTag(parent))
        }

        v.layoutParams = RelativeLayout.LayoutParams(200, 100)
        val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
        layoutParams.leftMargin = 2
        layoutParams.topMargin = 2

        place(layoutParams, parentView, anchor)
        v.layoutParams = layoutParams
        v.tag = getViewTag(chain)

        canvas.addView(v)

        return v
    }

    private fun place(lp: RelativeLayout.LayoutParams, parentView: View?, anchorView: View) {
        Log.v(tag, "ParentView[%s]".format(parentView ?: "X"))
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

    private fun getViewTag(chain: Chain): String {
        return chain::class.java.simpleName
    }

    private fun buildChain(): Chain {
        val a = AChain()
        val b = BChain()
        val c = CChain()
        val c1 = C1Chain()
        val c2 = C2Chain()

        return a.addToChain(b, c.addToChain(c1, c2))
//        return a.addToChain(b, c, c1, c2)
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
