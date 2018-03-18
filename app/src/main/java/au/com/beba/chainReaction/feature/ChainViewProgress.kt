package au.com.beba.chainReaction.feature

import android.view.View
import android.widget.TextView
import au.com.beba.chainReaction.R
import au.com.beba.chainreaction.stepSwitcher.BaseChainStepSwitcher
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

class ChainViewProgress {
    private lateinit var preMainStep: View
    private lateinit var mainStep: View
    private lateinit var postMainStep: View
    private lateinit var linksStep: View
    private lateinit var reactionsStep: View
    private lateinit var decisionStep: View
    private lateinit var finishStep: View

    fun bindView(chainView: ChainView) {
        preMainStep = chainView.find(R.id.chain_step_premain)
        mainStep = chainView.find(R.id.chain_step_main)
        postMainStep = chainView.find(R.id.chain_step_postmain)
        linksStep = chainView.find(R.id.chain_step_links)
        reactionsStep = chainView.find(R.id.chain_step_reactions)
        decisionStep = chainView.find(R.id.chain_step_decision)
        finishStep = chainView.find(R.id.chain_step_finish)
    }

    fun update(currentStep: String) {
        markAsVisited(
                when (currentStep) {
                    BaseChainStepSwitcher.STEP_PRE_MAIN -> preMainStep
                    BaseChainStepSwitcher.STEP_MAIN -> mainStep
                    BaseChainStepSwitcher.STEP_POST_MAIN -> postMainStep
                    BaseChainStepSwitcher.STEP_LINKS -> linksStep
                    BaseChainStepSwitcher.STEP_REACTIONS -> reactionsStep
                    BaseChainStepSwitcher.STEP_DECISION -> decisionStep
                    BaseChainStepSwitcher.STEP_FINISH -> finishStep
                    else -> null
                }
        )
    }

    private fun markAsVisited(view: View?) {
        if (view != null) {
            view.backgroundColor = view.resources.getColor(R.color.step_visited)
            if (view is TextView) {
                val count = Integer.parseInt(view.text.toString())
                view.text = count.inc().toString()

            }
        }
    }
}
