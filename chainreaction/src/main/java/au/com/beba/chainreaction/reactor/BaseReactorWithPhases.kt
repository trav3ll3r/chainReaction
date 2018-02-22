package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.ExecutionStrategy
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.decision.BaseChainWithPhasesDecision

open class BaseReactorWithPhases(
        override val chainDecision: ChainDecision = BaseChainWithPhasesDecision(),
        override val executionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL
) : Reactor