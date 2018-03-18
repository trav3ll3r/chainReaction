package au.com.beba.chainreaction.chain

interface ChainStepSwitcher {
    fun switchNext(links: List<Chain>, fromStep: String?): String?
}