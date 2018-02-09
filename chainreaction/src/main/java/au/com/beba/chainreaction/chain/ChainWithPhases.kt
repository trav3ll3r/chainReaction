package au.com.beba.chainreaction.chain

interface ChainWithPhases : Chain {
    fun addReaction(reaction: Reaction)

    // PHASES
    fun linksPhase()

    fun reactionsPhase()
    fun decisionPhase()
}