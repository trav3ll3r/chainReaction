package au.com.beba.chainReaction.feature

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.Guideline
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import au.com.beba.chainReaction.R
import au.com.beba.chainReaction.testData.AbcChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

class ChainView : BaseView {

    private val tag = ChainView::class.java.simpleName

    private lateinit var myLayout: ConstraintLayout
    private lateinit var startContentGuideline: Guideline
    private lateinit var endContentGuideline: Guideline

    private lateinit var chainDuration: TextView
    private lateinit var chainName: TextView
    private lateinit var cardView: View
    private lateinit var innerContent: ConstraintLayout
    private lateinit var chainProgress: View

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

        this.chainDuration = find(R.id.execution_duration)
        this.chainName = find(R.id.chain_name)
        this.chainProgress = find(R.id.chain_progress)
        this.cardView = find(R.id.card_view)
        this.innerContent = find(R.id.inner_content)
    }


    fun update(chain: AbcChain) {
        //Log.d(tag, "Update for [%s] with status [%s]".format(chain::class.java.simpleName, chain.getChainStatus()))
        this.chainName.text = chain.chainId
        this.chainDuration.text = chain.getSleepTime().toString()

        chainProgress.backgroundColor = resources.getColor(
                when (chain.getChainStatus()) {
                    ChainCallback.Status.IN_PROGRESS, ChainCallback.Status.QUEUED -> {
                        animateProgress(chain.getSleepTime())
                        R.color.status_in_progress
                    }
                    ChainCallback.Status.SUCCESS -> {
                        R.color.status_success
                    }
                    ChainCallback.Status.ERROR -> R.color.status_error
                    else -> R.color.status_not_started
                }
        )
    }

    private fun animateProgress(max: Long) {
        ConsoleLogger.log(tag, "animateProgress")

        val r = Runnable {
            val animateInside: ConstraintLayout = this.innerContent    //myLayout
            val changeBounds = ChangeBounds()
            changeBounds.duration = max
            changeBounds.interpolator = LinearInterpolator()

            TransitionManager.beginDelayedTransition(animateInside, changeBounds)

            // ANIMATE TO NEW CONSTRAINTS
            val set = ConstraintSet()
            set.clone(animateInside)
            set.connect(R.id.chain_progress, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            set.connect(R.id.chain_progress, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            set.applyTo(animateInside)
        }

        post(r)
    }
}