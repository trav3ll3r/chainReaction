package au.com.beba.chainReaction.feature

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import au.com.beba.chainReaction.R
import au.com.beba.chainReaction.testData.AbcChain
import au.com.beba.chainreaction.chain.ChainCallback
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

class ChainView : BaseView {

    private val tag = ChainView::class.java.simpleName

    private lateinit var myLayout: ConstraintLayout
    private lateinit var startContentGuideline: Guideline
    private lateinit var endContentGuideline: Guideline

    private lateinit var chainName: TextView
    private lateinit var cardView: View
    private lateinit var innerContent: ConstraintLayout
    private lateinit var progress: ChainViewProgress
    private lateinit var chainFinalStatus: View

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

    init {
        inflateView()
    }

    private fun inflateView() {
        Log.d(tag, "init for [objId=%s]".format(this))
        inflate(context, R.layout.chain_view, this)

        this.myLayout = find(R.id.constraint_layout)
        startContentGuideline = find(R.id.start_content)
        endContentGuideline = find(R.id.end_content)

        this.chainName = find(R.id.chain_name)
        this.chainFinalStatus = find(R.id.chain_final_status)
        this.cardView = find(R.id.card_view)
        this.innerContent = find(R.id.inner_content)

        progress = ChainViewProgress()
        progress.bindView(this)
    }

    fun updateProgress(currentStep: String) {
        progress.update(currentStep)
    }

    fun update(chain: AbcChain) {
        //Log.d(tag, "Update for [%s] with status [%s]".format(chain::class.java.simpleName, chain.getChainStatus()))
        this.chainName.text = chain.chainId

        chainFinalStatus.backgroundColor = resources.getColor(
                when (chain.getChainStatus()) {
                    ChainCallback.Status.QUEUED,
                    ChainCallback.Status.IN_PROGRESS -> R.color.status_in_progress
                    ChainCallback.Status.SUCCESS -> R.color.status_success
                    ChainCallback.Status.ERROR -> R.color.status_error
                    else -> R.color.status_not_started
                }
        )
    }
}