package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.decision.PassThroughChainDecision
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PassThroughReactor(
        override val chainDecision: ChainDecision = PassThroughChainDecision(),
        override val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()
) : Reactor