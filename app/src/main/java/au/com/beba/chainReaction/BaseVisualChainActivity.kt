package au.com.beba.chainReaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import au.com.beba.chainReaction.feature.BaseView
import au.com.beba.chainReaction.feature.ChainView
import au.com.beba.chainReaction.feature.ConnectorView
import au.com.beba.chainReaction.feature.mockAppView.RequestValueListener
import au.com.beba.chainReaction.feature.mockAppView.SelectLetterFragment
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
const val CHAIN_REQUEST_ACTION: String = "au.com.beba.chainReaction.CHAIN_REQUEST_ACTION"
const val CHAIN_TAG: String = "au.com.beba.chainReaction.CHAIN_TAG"
const val CHAIN_EVENT: String = "au.com.beba.chainReaction.CHAIN_EVENT"
const val CHAIN_REQUEST: String = "au.com.beba.chainReaction.CHAIN_REQUEST"

abstract class BaseVisualChainActivity : AppCompatActivity(), RequestValueListener {

    protected open val tag: String = BaseVisualChainActivity::class.java.simpleName

    private lateinit var canvas: RelativeLayout
    private var topChain: Chain? = null

    // INSPECT
    private lateinit var inspectHolder: ViewGroup
    private lateinit var inspectName: TextView
    private lateinit var inspectMainStatus: TextView
    private lateinit var inspectChainStatus: TextView

    // APP UI
    private lateinit var appUiContainer: FrameLayout

    private var bottomMost: BaseView? = null

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                val chainTag = bundle[CHAIN_TAG] as String
                val chainEvent = bundle[CHAIN_EVENT] as String
                val chain = getChainByTag(chainTag, topChain!!)
                Log.v(tag, "Received broadcast [%s]@%s [%s]".format(chainTag, chainEvent, chain?.getChainStatus()))
                updateChainView(chain)
            }
        }
    }

    private val chainRequestsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                val chainTag = bundle[CHAIN_TAG] as String
                val chainRequest = bundle[CHAIN_REQUEST] as String
                Log.v(tag, "Broadcast: [%s] requires [%s]".format(chainTag, chainRequest))
                showUiForChainRequest(chainTag, chainRequest)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualise_chain)

        find<Button>(R.id.btn_show_test).setOnClickListener { visualise() }
        find<Button>(R.id.btn_reset_all).setOnClickListener { resetAll() }
        find<Button>(R.id.btn_start_test).setOnClickListener { executeChain() }
        canvas = find(R.id.chain_canvas)

        inspectHolder = find(R.id.inspect_holder)
        inspectName = find(R.id.inspect_chain_name)
        inspectMainStatus = find(R.id.inspect_chain_main_task_status)
        inspectChainStatus = find(R.id.inspect_chain_status)

        appUiContainer = find(R.id.app_ui_container)
    }

    override fun onDestroy() {
        unregisterListener()
        super.onDestroy()
    }

    private fun visualise() {
        resetAll()
        topChain = buildChain()

        drawChain(topChain!!, null)
    }

    private fun executeChain() {
        if (topChain == null) {
            topChain = buildChain()
        }

        registerListener()

        val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()

        ConsoleLogger.log("start", "topChain")
        topChain!!.setParentCallback(object : ChainCallback<Chain> {
            override fun onDone(completedChain: Chain) {
                //DO SOMETHING ONCE ENTIRE CHAIN-REACTION HAS COMPLETED (SUCCESS / ERROR)
                ConsoleLogger.log("onDone", "topChain")
            }
        })
        chainExecutor.submit(topChain)
    }

    private var receiversRegistered: AtomicBoolean = AtomicBoolean(false)

    private fun registerListener() {
        if (!receiversRegistered.get()) {
            receiversRegistered.getAndSet(true)

            with(LocalBroadcastManager.getInstance(this), {
                registerReceiver(localBroadcastReceiver, IntentFilter(CHAIN_REACTION_EVENT))
                registerReceiver(chainRequestsReceiver, IntentFilter(CHAIN_REQUEST_ACTION))
            })
        }
    }

    private fun unregisterListener() {
        if (receiversRegistered.get()) {
            with(LocalBroadcastManager.getInstance(this)) {
                unregisterReceiver(localBroadcastReceiver)
                unregisterReceiver(chainRequestsReceiver)
            }
            receiversRegistered.getAndSet(false)
        }
    }

    private fun drawChain(chain: Chain, parent: Chain?): BaseView? {
        ConsoleLogger.log("drawChain", "chain=%s".format(chain::class.java.simpleName))
        var newAnchor: BaseView? = placeChainView(chain, parent)

        for (chainLink: Chain in chain.getChainLinks()) {
            newAnchor = drawChain(chainLink, chain)
        }

        return newAnchor
    }

    private fun placeChainView(chain: Chain, parent: Chain?): ChainView {
        // CONFIGURE ChainView
        val abcChain = chain as AbcChain
        val view = buildChainView(this, abcChain)
        view.setOnClickListener { showChainInspection(view.tag as String) }
        view.update(abcChain)

        // FIND reference view
        var referenceView: BaseView? = null
        if (parent != null) {
            referenceView = findReferenceChainView(parent)
        }

        // PLACE IN CANVAS
        place(view, chain, referenceView, parent)

        return view
    }

    private fun place(currentChainView: ChainView, currentChain: Chain, ref: BaseView?, parentChain: Chain?) {
        ConsoleLogger.log("place", "view %s".format(currentChainView.tag))

        var refView = ref

        if (parentChain != null) {
            refView = buildAndPlacePreConnector(currentChain, refView, parentChain)
            buildAndPlaceConnectorPatch(currentChain, refView, parentChain)
        }

        buildAndPlaceChainView(currentChainView, currentChain, refView, parentChain)
    }

    private fun buildAndPlacePreConnector(currentChain: Chain, ref: BaseView?, parentChain: Chain): BaseView {
        val connectorType = determineConnectorType(currentChain, parentChain)
        val connectorView = connectorFactory(this, currentChain, connectorType, parentChain.reactor.executionStrategy)
        when (connectorType) {
            ConnectorView.Type.LAST_CHILD,
            ConnectorView.Type.MIDDLE_CHILD -> connectorView.rightOf(ref!!).below(getBottomReference()!!)
            else -> connectorView.rightOf(ref!!).alignTopAndBottomWith(ref)
        }

        canvas.addView(connectorView)
        return connectorView
    }

    private fun buildAndPlaceConnectorPatch(currentChain: Chain, currentConnector: BaseView, parentChain: Chain) {
        val prevSibling = findChildBefore(parentChain, currentChain)
        if (prevSibling != null) {
            // FIND ConnectorView FOR prevSibling
            val prevSiblingConnector = findReferenceConnectorView(prevSibling)
            if (prevSiblingConnector != null) {
                // BUILD ConnectorView BETWEEN prevSiblingConnector AND currentConnector (VERTICAL_PATCH)
                val connectorPatch = connectorFactory(this, null, ConnectorView.Type.VERTICAL_PATCH, ExecutionStrategy.SERIAL)
                connectorPatch
                        .below(prevSiblingConnector)
                        .above(currentConnector)
                        .asWideAs(prevSiblingConnector)
                canvas.addView(connectorPatch)
            }
        }
    }

    private fun buildAndPlaceChainView(currentChainView: ChainView, currentChain: Chain, refView: BaseView?, parentChain: Chain?) {
        // PLACE ChainView
        if (refView == null) {
            currentChainView.topLeftParent()
        } else {
            val isFirstChild = isFirstChild(currentChain, parentChain)
            if (isFirstChild) {
                currentChainView.rightOf(refView).alignTopAndBottomWith(refView)
            } else {
                currentChainView.below(getBottomReference()!!).rightOf(refView)
            }
        }
        canvas.addView(currentChainView)
        setBottomReference(currentChainView)
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
            if (baseView == null) {
                val parent = findChildParent(chain, topChain!!)
                val newBaseView: ChainView = placeChainView(chain, parent)
                newBaseView.update(chain)
            } else {
                if (baseView is ChainView) {
                    baseView.update(chain)
                }
            }
        }
    }

    private fun findReferenceChainView(chain: Chain): BaseView? {
        return getChainViewByTag(canvas, buildChainTag(chain))
    }

    private fun findReferenceConnectorView(chain: Chain): BaseView? {
        return getChainViewByTag(canvas, buildChainConnectorTag(chain))
    }

    private fun resetAll() {
        topChain = null
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

    // APP UI METHODS
    private fun showUiForChainRequest(chainTag: String, chainRequest: String) {
        when (chainRequest) {
            "LETTER" -> loadFragment(SelectLetterFragment.newInstance(chainTag, chainRequest))
            else -> null
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val t = supportFragmentManager.beginTransaction()
        t.replace(appUiContainer.id, fragment)
        t.commit()
        appUiContainer.visibility = View.VISIBLE

    }

    override fun onValueSelected(chainTag: String, request: String, value: Any?) {
        // FIND CHAIN
        val chain: ChainWithRequest? = getChainByTag(chainTag, topChain!!) as ChainWithRequest?
        chain?.acceptExternalValue(request, value)
    }
}
