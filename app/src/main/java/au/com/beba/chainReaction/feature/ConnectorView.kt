package au.com.beba.chainReaction.feature

import android.content.Context
import android.util.AttributeSet
import au.com.beba.chainReaction.R

class ConnectorView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val type: ConnectorView.Type = Type.SERIAL)
    : BaseView(context, attrs, defStyleAttr) {

    enum class Type {
        SERIAL,
        PARALLEL_PARENT,
        PARALLEL_MIDDLE,
        PARALLEL_LAST
    }

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : this(context, attrs, defStyleAttr, Type.SERIAL)

    init {
        inflateView()
    }

    private fun inflateView() {
        inflate(context,
                when (type) {
                    Type.PARALLEL_PARENT -> R.layout.connector_parallel_parent
                    Type.PARALLEL_MIDDLE -> R.layout.connector_parallel_middle
                    Type.PARALLEL_LAST -> R.layout.connector_parallel_last
                    else -> R.layout.connector_serial
                },
                this)
    }
}