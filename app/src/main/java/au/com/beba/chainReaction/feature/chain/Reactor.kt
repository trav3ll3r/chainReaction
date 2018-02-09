package au.com.beba.chainReaction.feature.chain

import au.com.beba.chainReaction.feature.reactors.ChainDecision

interface Reactor {
    val chainDecision: ChainDecision
}