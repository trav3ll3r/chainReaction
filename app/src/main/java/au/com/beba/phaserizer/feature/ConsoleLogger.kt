package au.com.beba.phaserizer.feature

import java.text.SimpleDateFormat
import java.util.*

class ConsoleLogger {
    companion object {
        private val dateFormatter = SimpleDateFormat("dd-MM-YY HH:mm:ss.SS", Locale.ENGLISH)
        fun log(message: String) {
            println("%s %s".format(dateFormatter.format(Date()), message))
        }
    }
}