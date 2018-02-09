package au.com.beba.chainReaction.feature.chain

interface ChainWithPhases : Chain {
    fun addReaction(reaction: Reaction)

    // PHASES
    fun linksPhase()

    fun reactionsPhase()
    fun decisionPhase()
}