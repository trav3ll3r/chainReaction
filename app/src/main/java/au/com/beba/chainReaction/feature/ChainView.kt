package au.com.beba.chainReaction.feature

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.TextView
import au.com.beba.chainReaction.R
import au.com.beba.chainReaction.testData.AbcChain
import au.com.beba.chainreaction.chain.ChainCallback
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

class ChainView : ConstraintLayout {

    private lateinit var chainDuration: TextView
    private lateinit var chainName: TextView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.chain_view, this)

        this.chainDuration = find(R.id.execution_duration)
        this.chainName = find(R.id.chain_name)
    }

    fun update(chain: AbcChain) {
        this.chainName.text = chain::class.java.simpleName
        this.chainDuration.text = chain.getSleepTime().toString()
        backgroundColor = resources.getColor(
                when (chain.getChainStatus()) {
                    ChainCallback.Status.IN_PROGRESS -> R.color.status_in_progress
                    ChainCallback.Status.SUCCESS -> R.color.status_success
                    ChainCallback.Status.ERROR -> R.color.status_error
                    else -> R.color.status_not_started
                }
        )

        invalidate()
        requestLayout()
    }
}