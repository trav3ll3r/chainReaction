package au.com.beba.chainreaction.chain

class Reaction(val type: String, val task: (Chain, Reaction) -> Unit, var skip: Boolean = false)