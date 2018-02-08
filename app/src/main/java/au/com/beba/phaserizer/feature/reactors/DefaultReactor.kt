package au.com.beba.phaserizer.feature.reactors

interface Reactor {
    val chainDecision: ChainDecision
}

class DefaultReactor(override val chainDecision: ChainDecision = IdleChainDecision()) : Reactor

