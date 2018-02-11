package au.com.beba.chainReaction.feature

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import au.com.beba.chainReaction.R

/**
 * TODO: document your custom view class.
 */
class ChainView : View {
    private var executionDuration: String? = null // TODO: use a default from R.string...
    private var mExampleColor = Color.RED // TODO: use a default from R.color...
    private var viewBackground = Color.GRAY // TODO: use a default from R.color...
    private var mExampleDimension = 0f // TODO: use a default from R.dimen...
    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    var exampleDrawable: Drawable? = null

    private var textPaint: TextPaint = TextPaint()
    private var mTextWidth: Float = 0.toFloat()
    private var mTextHeight: Float = 0.toFloat()

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    var chainDuration: String?
        get() = executionDuration
        set(executionDuration) {
            this.executionDuration = executionDuration
            invalidateTextPaintAndMeasurements()
        }

    var chainBackground: Int
    get() = viewBackground
    set(value) {
        this.viewBackground = value
        invalidateTextPaintAndMeasurements()
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    var exampleColor: Int
        get() = mExampleColor
        set(exampleColor) {
            mExampleColor = exampleColor
            invalidateTextPaintAndMeasurements()
        }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    var exampleDimension: Float
        get() = mExampleDimension
        set(exampleDimension) {
            mExampleDimension = exampleDimension
            invalidateTextPaintAndMeasurements()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.ChainView, defStyle, 0)

        executionDuration = a.getString(R.styleable.ChainView_executionDuration)
        mExampleColor = a.getColor(R.styleable.ChainView_exampleColor, mExampleColor)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(R.styleable.ChainView_exampleDimension, mExampleDimension)

        if (a.hasValue(R.styleable.ChainView_exampleDrawable)) {
            exampleDrawable = a.getDrawable(R.styleable.ChainView_exampleDrawable)
            exampleDrawable!!.callback = this
        }

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint()
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.textAlign = Paint.Align.LEFT

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.textSize = mExampleDimension
        textPaint.color = mExampleColor
        mTextWidth = textPaint.measureText(executionDuration ?: "")
//        setBackgroundColor(chainBackground ?: resources.getColor(chainBackground))

        val fontMetrics = textPaint.fontMetrics
        mTextHeight = fontMetrics.bottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

//        // Draw the example drawable on top of the text.
//        if (exampleDrawable != null) {
//            exampleDrawable!!.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight)
//            exampleDrawable!!.draw(canvas)
//        }

        // Draw the text.
        canvas.drawText(executionDuration!!,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                textPaint)

        setBackgroundColor(resources.getColor(chainBackground))
    }
}
