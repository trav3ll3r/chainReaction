package au.com.beba.chainReaction.executors

typealias TaskId = String

enum class ExecutionStrategy {
    SERIAL,
    PARALLEL
}

class Logger {
    fun log(level: Int, message: String) {
        val levelIndent = StringBuilder()
        (0 until level).forEach {
            levelIndent.append("|\t")
        }

        System.out.println("%s%s".format(levelIndent, message))
    }
}