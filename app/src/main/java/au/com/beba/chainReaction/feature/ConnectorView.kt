package au.com.beba.chainReaction.feature

import android.content.Context
import android.util.AttributeSet
import au.com.beba.chainReaction.R
import au.com.beba.chainreaction.chain.ExecutionStrategy

class ConnectorView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
                    private val childType: ConnectorView.Type = Type.ONLY_CHILD,
                    private val executionStrategy: ExecutionStrategy)
    : BaseView(context, attrs, defStyleAttr) {

    enum class Type {
        ONLY_CHILD,
        FIRST_CHILD,
        MIDDLE_CHILD,
        LAST_CHILD,
        VERTICAL_PATCH
    }

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : this(context, attrs, defStyleAttr, Type.ONLY_CHILD, ExecutionStrategy.SERIAL)

    init {
        inflateView()
    }

    private fun inflateView() {
        inflate(context,
                when (childType) {
                    Type.ONLY_CHILD -> R.layout.connector_only_child
                    Type.LAST_CHILD -> R.layout.connector_last_child
                    Type.FIRST_CHILD ->
                        when (executionStrategy) {
                            ExecutionStrategy.SERIAL -> R.layout.connector_first_serial
                            ExecutionStrategy.PARALLEL -> R.layout.connector_first_parallel
                        }
                    Type.MIDDLE_CHILD ->
                        when (executionStrategy) {
                            ExecutionStrategy.SERIAL -> R.layout.connector_middle_serial
                            ExecutionStrategy.PARALLEL -> R.layout.connector_middle_parallel
                        }
                    Type.VERTICAL_PATCH -> R.layout.connector_patch_vertical
                },
                this)
    }
}