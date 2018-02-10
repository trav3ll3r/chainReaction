package au.com.beba.chainreaction.chain

class Reaction(val type: String, val task: (Any?) -> (Any?))