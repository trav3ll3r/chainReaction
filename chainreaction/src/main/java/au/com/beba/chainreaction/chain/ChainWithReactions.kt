package au.com.beba.chainreaction.chain

interface ChainWithReactions : Chain {
    fun addReaction(reaction: Reaction)

    fun reactionsPhase(): () -> Any?
}