package xyz.nfcv.telephone_directory.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ContactorHeaderDecoration(private val adapter: ContactorRecyclerAdapter) :
    RecyclerView.ItemDecoration() {
    private val paint = Paint()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (adapter.data.first(position)) {
            outRect.top = 64
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.paddingRight
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (i == 0) {
                var top: Int = parent.paddingTop
                var bottom: Int = top + 64
                if (adapter.data.last(i) && bottom > child.bottom) {
                    bottom = child.bottom
                    top = bottom - 64
                }
                drawHeader(
                    left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), c,
                    adapter.data.group(position)?.header?.value ?: "?"
                )
            } else if (adapter.data.first(i)) {
                val top = child.top - 64
                val bottom = top + 64
                drawHeader(
                    left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), c,
                    adapter.data.group(position)?.header?.value ?: "?"
                )
            }

        }
    }

    private fun drawHeader(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        c: Canvas,
        header: String
    ) {
        paint.textSize = 36F
        paint.color = Color.GRAY

        val rect = Rect()
        paint.getTextBounds(header, 0, header.length, rect)

        val textHeight = rect.height()
        val textX = left + 48
        val textY: Float = top + (36 + textHeight) / 2
        c.drawText(header, textX, textY, paint)
    }

}