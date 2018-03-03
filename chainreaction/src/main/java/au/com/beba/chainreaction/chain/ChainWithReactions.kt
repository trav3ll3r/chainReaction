package au.com.beba.chainreaction.chain

interface ChainWithReactions : Chain {
    fun addReaction(reaction: Reaction)
    val reactions: List<Reaction>

    fun runReactions()
}