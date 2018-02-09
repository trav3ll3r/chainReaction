package au.com.beba.chainReaction.feature.logger

import java.text.SimpleDateFormat
import java.util.*

class ConsoleLogger {
    companion object {
        private val dateFormatter = SimpleDateFormat("dd-MM-YY HH:mm:ss.SS", Locale.ENGLISH)

        fun log(message: String) {
            log(".", message)
        }

        fun log(tag: String, message: String) {
//            println("%s |%s| %s".format(dateFormatter.format(Date()), tag, message))
            println("%s | %s".format(tag, message))
        }
    }
}