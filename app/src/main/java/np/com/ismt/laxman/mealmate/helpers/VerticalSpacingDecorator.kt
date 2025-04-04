package np.com.ismt.laxman.mealmate.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpacingDecorator: SpacingDecorator {
    constructor(
        top: Int,
        start: Int,
        end: Int,
        bottom: Int
    ) : super(top, start, end, bottom)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        val isFirstItem = position == 0
        val isLastItem = position == itemCount - 1
        with(outRect) {
            left = this@VerticalSpacingDecorator.start
            right =  this@VerticalSpacingDecorator.end
            top = if (isFirstItem) 0 else this@VerticalSpacingDecorator.top
            bottom = if (isLastItem) 0 else this@VerticalSpacingDecorator.bottom
        }
    }


    companion object {
        class Builder : SpacingDecorator.Builder() {
            override fun build() = VerticalSpacingDecorator(top, start, end, bottom)
        }
    }
}
