package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.decision.BaseChainWithPhasesDecision
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class BaseReactorWithPhases(
        override val chainDecision: ChainDecision = BaseChainWithPhasesDecision(),
        override val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()
)
    : Reactor