package au.com.beba.chainreaction.stepSwitcher

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainStepSwitcher
import au.com.beba.chainreaction.logger.ConsoleLogger

class BaseChainStepSwitcher : ChainStepSwitcher {

    @Suppress("PrivatePropertyName")
    private val TAG = BaseChainStepSwitcher::class.java.simpleName

    companion object {
        const val STEP_PRE_MAIN = "STEP_PRE_MAIN"
        const val STEP_MAIN = "STEP_MAIN"
        const val STEP_POST_MAIN = "STEP_POST_MAIN"
        const val STEP_LINKS = "STEP_LINKS"
        const val STEP_REACTIONS = "STEP_REACTIONS"
        const val STEP_DECISION = "STEP_DECISION"
        const val STEP_FINISH = "STEP_FINISH"
    }

    override fun switchNext(links: List<Chain>, fromStep: String?): String? {
        return when (fromStep) {
            null -> firstStep()
            STEP_PRE_MAIN,
            STEP_MAIN,
            STEP_LINKS,
            STEP_REACTIONS -> nextInSequence(fromStep)
            STEP_POST_MAIN ->
                when (links.size) {
                    0 -> STEP_REACTIONS
                    else -> STEP_LINKS
                }
            STEP_DECISION -> afterDecision(links)
            else -> null
        }
    }

    private val steps: List<String> =
        listOf(
                STEP_PRE_MAIN, STEP_MAIN, STEP_POST_MAIN,
                STEP_LINKS, STEP_REACTIONS,
                STEP_DECISION, STEP_FINISH
        )


    private fun firstStep(): String {
        return STEP_PRE_MAIN
    }

    private fun nextInSequence(fromStep: String): String? {
        val fromIndex = steps.indexOfFirst { it == fromStep }
        val toIndex = fromIndex.inc()
        if (toIndex < steps.size) {
            return steps[toIndex]
        }
        return null
    }

    private fun afterDecision(links: List<Chain>): String? {
        ConsoleLogger.log(TAG, "decisionPhase")
        val incompleteLinks = links.count { it.getChainStatus() !in listOf(ChainCallback.Status.SUCCESS, ChainCallback.Status.ERROR) }
        ConsoleLogger.log(TAG, "decisionPhase links[%s] incomplete[%s]".format(links.size, incompleteLinks))

        if (incompleteLinks == 0) {
            ConsoleLogger.log(TAG, "received all results, decision done")
            return STEP_FINISH
        } else {
            val notStartedLinks = links.count { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED) }
            if (notStartedLinks == 0) {
                // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
                ConsoleLogger.log(TAG, "waiting for all results, decision pending")
            } else {
                // RUN LINKS PHASE
                return STEP_LINKS
            }
        }
        return null
    }
}