package au.com.beba.chainReaction.feature

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.RelativeLayout
import au.com.beba.chainreaction.logger.ConsoleLogger

abstract class BaseView
constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
    : ConstraintLayout(context, attrs, defStyle) {

    @Suppress("PrivatePropertyName")
    private val TAG = BaseView::class.java.simpleName

    fun topLeftParent(): BaseView {
        ConsoleLogger.log(TAG, "%s topLeftParent".format(this.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        return this
    }

    fun rightOf(ref: BaseView): BaseView {
        ConsoleLogger.log(TAG, "%s rightOf %s".format(this.tag, ref.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.RIGHT_OF, ref.id)
        return this
    }

    fun below(ref: BaseView): BaseView {
        ConsoleLogger.log(TAG, "%s below %s".format(this.tag, ref.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.BELOW, ref.id)
        return this
    }

    fun alignTopAndBottomWith(ref: BaseView): BaseView {
        ConsoleLogger.log(TAG, "%s alignTopAndBottomWith %s".format(this.tag, ref.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.ALIGN_TOP, ref.id)
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, ref.id)
        return this
    }

    fun asWideAs(ref: BaseView): BaseView {
        ConsoleLogger.log(TAG, "%s asWideAs %s".format(this.tag, ref.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.ALIGN_LEFT, ref.id)
        lp.addRule(RelativeLayout.ALIGN_RIGHT, ref.id)
        return this
    }

    fun above(ref: BaseView): BaseView {
        ConsoleLogger.log(TAG, "%s above %s".format(this.tag, ref.tag))
        val lp: RelativeLayout.LayoutParams = this.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.ABOVE, ref.id)
        return this
    }
}