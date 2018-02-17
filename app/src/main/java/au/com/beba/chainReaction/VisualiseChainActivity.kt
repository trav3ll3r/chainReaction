package au.com.beba.chainReaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.jetbrains.anko.find
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

const val CHAIN_REACTION_EVENT: String = "au.com.beba.chainReaction.CHAIN_REACTION_EVENT"
const val CHAIN_CLASS: String = "au.com.beba.chainReaction.CHAIN_CLASS"
const val CHAIN_EVENT: String = "au.com.beba.chainReaction.CHAIN_EVENT"

class VisualiseChainActivity : AppCompatActivity() {

    private val tag: String = VisualiseChainActivity::class.java.simpleName

    private lateinit var canvas: RelativeLayout
    private lateinit var rootAnchorView: View
    private lateinit var topChain: Chain

    // INSPECT
    private lateinit var inspectHolder: ViewGroup
    private lateinit var inspectName: TextView
    private lateinit var inspectMainStatus: TextView
    private lateinit var inspectChainStatus: TextView


    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                val chainTag = bundle[CHAIN_CLASS] as String
                val chainEvent = bundle[CHAIN_EVENT] as String
                val chain = getChainByTag(chainTag, topChain)
                Log.v(tag, "Received broadcast [%s]@%s [%s]".format(chainTag, chainEvent, chain?.getChainStatus()))
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

        inspectHolder = find(R.id.inspect_holder)
        inspectName = find(R.id.inspect_chain_name)
        inspectMainStatus = find(R.id.inspect_chain_main_task_status)
        inspectChainStatus = find(R.id.inspect_chain_status)
    }

    override fun onDestroy() {
        unregisterListener()
        super.onDestroy()
    }

    private fun visualise() {
        clearChain()
        topChain = buildChain()
        drawChain(topChain, null, rootAnchorView)
    }

    private fun executeChain() {
        registerListener()

        ConsoleLogger.log("", "begin --> \"topChain\"")

//        val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val chainExecutor: ExecutorService = Executors.newFixedThreadPool(10)
        chainExecutor.submit(topChain.startChain(object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {
                //TODO: DO SOMETHING ONCE ENTIRE CHAIN HAS COMPLETED (SUCCESS / ERROR)
                ConsoleLogger.log("", "done --> \"topChain\"")
            }
        }))
    }

    private var receiverRegistered: AtomicBoolean = AtomicBoolean(false)

    private fun registerListener() {
        if (!receiverRegistered.get()) {
            receiverRegistered.getAndSet(true)
            LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver, IntentFilter(CHAIN_REACTION_EVENT))
        }
    }

    private fun unregisterListener() {
        if (receiverRegistered.get()) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver)
            receiverRegistered.getAndSet(false)
        }
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

    private fun clearChain() {
        canvas.removeAllViews()
    }

    private fun placeChainView(chain: Chain, parent: Chain?, anchor: View): View {
        val abcChain = chain as AbcChain
        val v = ChainView(this)
        v.id = generateViewId()
        v.update(abcChain)

        var parentView: ChainView? = null
        if (parent != null) {
            parentView = getChainViewByTag(canvas, buildChainTag(parent))
        }

//        v.layoutParams = RelativeLayout.LayoutParams(abcChain.getSleepTime().toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
        layoutParams.leftMargin = 2
        layoutParams.topMargin = 2

        place(layoutParams, parentView, anchor)
        v.layoutParams = layoutParams
        v.tag = buildChainTag(chain)

        canvas.addView(v)

        v.setOnClickListener { showChainInspection(v.tag as String) }

        return v
    }

    private fun updateChainView(chain: Chain?) {
        if (chain != null) {
            val chainView = getChainViewByTag(canvas, buildChainTag(chain))
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

    private fun buildChain(): Chain {
//        val serialReactor = ReactorWithBroadcastIml(this)
        val parallelReactor = ReactorWithBroadcastIml(this, Executors.newFixedThreadPool(4))

//        val a = AChain(serialReactor)
        val a = AChain(parallelReactor)
        val b = BChain(parallelReactor)
        b.addToChain(B1Chain(parallelReactor))

        val c1 = C1Chain(parallelReactor)
        c1.defaultSleep = 500
        val c = CChain(parallelReactor).addToChain(c1, C2Chain(parallelReactor))
        val d = DChain(parallelReactor)
        val e = EChain(parallelReactor).addToChain(E1Chain(parallelReactor))
        val f = FChain(parallelReactor)
        return a.addToChain(
                b,
                c,
                d,
                e,
                f
        )
    }

    private fun showChainInspection(chainTag: String) {
        val chain = getChainByTag(chainTag, topChain)
        if (chain != null) {
            inspectHolder.visibility = View.VISIBLE
            inspectName.text = chain::class.java.simpleName
            inspectMainStatus.text = chain.getMainTaskStatus().name
            inspectChainStatus.text = chain.getChainStatus().name
        } else {
            inspectHolder.visibility = View.GONE
        }
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
