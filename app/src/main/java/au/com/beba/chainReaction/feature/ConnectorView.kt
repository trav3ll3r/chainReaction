package au.com.beba.chainReaction.feature

import android.content.Context
import android.util.AttributeSet
import au.com.beba.chainReaction.R

class ConnectorView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val type: ConnectorView.Type = Type.SERIAL_CHILD)
    : BaseView(context, attrs, defStyleAttr) {

    enum class Type {
        SERIAL_CHILD,
        SERIAL_SIBLING,
        PARALLEL_PARENT,
        PARALLEL_MIDDLE,
        PARALLEL_LAST
    }

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : this(context, attrs, defStyleAttr, Type.SERIAL_CHILD)

    init {
        inflateView()
    }

    private fun inflateView() {
        inflate(context,
                when (type) {
                    Type.PARALLEL_PARENT -> R.layout.connector_parallel_parent
                    Type.PARALLEL_MIDDLE -> R.layout.connector_parallel_middle
                    Type.PARALLEL_LAST -> R.layout.connector_parallel_last
                    Type.SERIAL_SIBLING -> R.layout.connector_serial_vertical
                    else -> R.layout.connector_serial_horizontal
                },
                this)
    }
}