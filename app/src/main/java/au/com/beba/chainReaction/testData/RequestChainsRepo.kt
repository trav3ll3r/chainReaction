package au.com.beba.chainReaction.testData

import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.reactor.PassThroughReactor

abstract class LetterRequestChain(override val reactor: Reactor)
    : AbcChain(reactor) {

    override fun preMainTask() {
        requestExternalValue("LETTER")
        super.preMainTask()
    }

    override fun postMainTask() {
        taskResult = requestExternalValue("LETTER") as String
        super.postMainTask()
    }
}

class AnyLetterChain(override val reactor: Reactor = PassThroughReactor()) : LetterRequestChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = AnyLetterChain::class.java.simpleName
    override var taskResult = "[A..Z]"
}
