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
import au.com.beba.chainReaction.feature.BaseView
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainReaction.feature.ConnectorView
import au.com.beba.chainReaction.testData.AbcChain
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ExecutionStrategy
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.jetbrains.anko.find
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

const val CHAIN_REACTION_EVENT: String = "au.com.beba.chainReaction.CHAIN_REACTION_EVENT"
const val CHAIN_CLASS: String = "au.com.beba.chainReaction.CHAIN_CLASS"
const val CHAIN_EVENT: String = "au.com.beba.chainReaction.CHAIN_EVENT"

abstract class BaseVisualChainActivity : AppCompatActivity() {

    protected open val tag: String = BaseVisualChainActivity::class.java.simpleName

    private lateinit var canvas: RelativeLayout
    private var topChain: Chain? = null

    // INSPECT
    private lateinit var inspectHolder: ViewGroup
    private lateinit var inspectName: TextView
    private lateinit var inspectMainStatus: TextView
    private lateinit var inspectChainStatus: TextView

    private var bottomMost: BaseView? = null

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                val chainTag = bundle[CHAIN_CLASS] as String
                val chainEvent = bundle[CHAIN_EVENT] as String
                val chain = getChainByTag(chainTag, topChain!!)
                Log.v(tag, "Received broadcast [%s]@%s [%s]".format(chainTag, chainEvent, chain?.getChainStatus()))
                updateChainView(chain)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualise_chain)

        find<Button>(R.id.btn_show_test).setOnClickListener { visualise() }
        find<Button>(R.id.btn_clear).setOnClickListener { clearChain() }
        find<Button>(R.id.btn_start_test).setOnClickListener { executeChain() }
        canvas = find(R.id.chain_canvas)

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

        drawChain(topChain!!, null)
    }

    private fun executeChain() {
        if (topChain != null) {
            registerListener()

            //val executionStrategy: ExecutorService = Executors.newSingleThreadExecutor()
            val chainExecutor: ExecutorService = Executors.newFixedThreadPool(10)

            ConsoleLogger.log("", "begin --> \"topChain\"")

            topChain!!.setParentCallback(object : ChainCallback<Chain> {
                override fun onDone(completedChain: Chain) {
                    //DO SOMETHING ONCE ENTIRE CHAIN-REACTION HAS COMPLETED (SUCCESS / ERROR)
                    ConsoleLogger.log("", "done --> \"topChain\"")
                }
            })

            chainExecutor.submit(topChain)
        }
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

    private fun drawChain(chain: Chain, parent: Chain?): BaseView? {
        ConsoleLogger.log("drawChain", "chain=%s".format(chain::class.java.simpleName))
//        placeChainView(chain, parent)
//        var newAnchor: BaseView? = placeChainView(chain, parent)
        var newAnchor: BaseView? = placeChainView(chain, parent)

        for (chainLink: Chain in chain.getChainLinks()) {
            newAnchor = drawChain(chainLink, chain/*, getBottomReference()*/)
//            newAnchor = drawChain(chainLink, chain, newAnchor)
//            drawChain(chainLink, chain, newAnchor)
//            bottomMost = newAnchor
        }

        return newAnchor
    }

    private fun placeChainView(chain: Chain, parent: Chain?): BaseView {

        // CONFIGURE ChainView
        val abcChain = chain as AbcChain
        val view = buildChainView(this, abcChain)
        view.setOnClickListener { showChainInspection(view.tag as String) }
        view.update(abcChain)

        // FIND reference view
        var referenceView: BaseView? = null
        if (parent != null) {
            referenceView = findReferenceView(parent)
        }

        // PLACE IN CANVAS
        ConsoleLogger.log("place", "view %s".format(view.tag))

        place(view, chain, referenceView, parent)

        return view
    }

    /**
     * @return new BottomMost
     */
    private fun place(currentChainView: ChainView, currentChain: Chain, ref: BaseView?, parentChain: Chain?) {

        val executionStrategy = parentChain?.reactor?.executionStrategy ?: ExecutionStrategy.SERIAL

        val firstChild = parentChain?.getChainLinks()?.firstOrNull()

        // PLACE ChainView
        if (ref == null) {
            currentChainView.topLeftParent()
        } else {
            when (executionStrategy) {
                ExecutionStrategy.SERIAL -> {
                    if (firstChild == currentChain) {
//                        canvas.addView(currentChainView)
                        currentChainView.rightOf(ref).asHighAs(ref)
                    } else {
//                        canvas.addView(currentChainView)
                        currentChainView.below(getBottomReference()!!).rightOf(ref)
                    }

                }
                ExecutionStrategy.PARALLEL -> {
//                    canvas.addView(currentChainView)
                    currentChainView.below(getBottomReference()!!).rightOf(ref)
                }
            }
        }
        canvas.addView(currentChainView)
        setBottomReference(currentChainView)

        // BUILD AND PLACE POST-Connector
        val connectorView = buildConnector(currentChainView, currentChain)
        if (connectorView != null) {
            // ADD Connector
            connectorView.rightOf(currentChainView).asHighAs(currentChainView)
            canvas.addView(connectorView)
            setBottomReference(connectorView)
        }

        // BUILD AND PLACE PRE-Connector
        val preConnectorView = buildChainPreConnector(this, currentChain, parentChain)
        if (preConnectorView != null) {
            // ADD Pre-Connector
            preConnectorView.leftOf(currentChainView).asHighAs(currentChainView)
            canvas.addView(preConnectorView)
        }
    }

    protected abstract fun buildChain(): Chain

    private fun showChainInspection(chainTag: String) {
        val chain = getChainByTag(chainTag, topChain!!)
        if (chain != null) {
            inspectHolder.visibility = View.VISIBLE
            inspectName.text = chain::class.java.simpleName
            inspectMainStatus.text = chain.getMainTaskStatus().name
            inspectChainStatus.text = chain.getChainStatus().name
        } else {
            inspectHolder.visibility = View.GONE
        }
    }

    private fun updateChainView(chain: Chain?) {
        if (chain is AbcChain) {
            val baseView = getChainViewByTag(canvas, buildChainTag(chain))
            if (baseView is ChainView) {
                baseView.update(chain)
            }
        }
    }

    private fun buildConnector(currentChainView: ChainView, currentChain: Chain): ConnectorView? {
        ConsoleLogger.log("buildConnector", "for %s".format(currentChainView.tag))
        return buildChainConnector(this, currentChain)
    }

    private fun findReferenceView(chain: Chain): BaseView? {
        var result: BaseView? = getChainViewByTag(canvas, buildChainConnectorTag(chain))
        if (result == null) {
            result = getChainViewByTag(canvas, buildChainTag(chain))
        }
        //ConsoleLogger.log("place", "findReferenceView: %s".format(result?.tag))
        return result
    }

    private fun clearChain() {
        setBottomReference(null)
        canvas.removeAllViews()
    }

    private fun setBottomReference(r: BaseView?) {
        ConsoleLogger.log("bottomMost", "set to %s".format(r?.tag))
        bottomMost = r
    }

    private fun getBottomReference(): BaseView? {
        return bottomMost
    }
}
