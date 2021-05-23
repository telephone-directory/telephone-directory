package xyz.nfcv.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import xyz.nfcv.telephone_directory.R
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class Sidebar : View {

    private lateinit var textPaint: TextPaint
    private var selectedColor: Int = Color.BLUE
    private var normalColor: Int = Color.GRAY
    private val items = Header.values()
    private var _selected = 0

    private val listeners = CopyOnWriteArrayList<(Int, Header) -> Unit>()

    fun addListener(listener: (Int, Header) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (Int, Header) -> Unit) {
        listeners.remove(listener)
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
        }

        background = ResourcesCompat.getDrawable(resources, R.drawable.border_capsule_20dp, null)

        val a = context.obtainStyledAttributes(attrs, R.styleable.Sidebar, defStyle, 0)
        selectedColor = a.getColor(R.styleable.Sidebar_selectedColor, selectedColor)
        normalColor = a.getColor(R.styleable.Sidebar_normalColor, normalColor)

        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val width: Int = if (widthMode != MeasureSpec.EXACTLY) {
            20
        } else {
            widthSize
        }

        setMeasuredDimension(width, (width + 1) * items.size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - contentWidth

        textPaint.textSize = contentWidth * 0.5f
        val offset = abs(textPaint.descent() + textPaint.ascent()) + contentWidth * 4 / 5f

        items.forEachIndexed { index: Int, header: Header ->
            when (header) {
                Header.LIKE -> {
                    val like = "♡"
                    textPaint.let {
                        if (_selected == index) {
                            it.color = selectedColor
                        } else {
                            it.color = normalColor
                        }
                    }

                    canvas.drawText(
                        like,
                        paddingLeft + contentWidth / 2f,
                        (contentHeight / items.size) * index + offset,
                        textPaint
                    )
                }
                else -> {
                    textPaint.let {
                        if (_selected == index) {
                            it.color = selectedColor
                        } else {
                            it.color = normalColor
                        }
                    }
                    canvas.drawText(
                        header.value,
                        paddingLeft + contentWidth / 2f,
                        (contentHeight / items.size) * index + offset,
                        textPaint
                    )
                }
            }
//            canvas.drawLine(
//                0f,
//                (contentHeight.toFloat() / items.size) * (index + 1),
//                paddingLeft + contentWidth.toFloat(),
//                (contentHeight.toFloat() / items.size) * (index + 1),
//                textPaint.apply { color = Color.BLACK })
//
//            canvas.drawLine(
//                0f,
//                ((contentHeight.toFloat() / items.size) * (index + 1) - contentWidth / 2f),
//                paddingLeft + contentWidth.toFloat(),
//                ((contentHeight.toFloat() / items.size) * (index + 1) - contentWidth / 2f),
//                textPaint.apply { color = Color.BLACK })
//
//
//            canvas.drawLine(
//                0f,
//                (contentHeight.toFloat() / items.size) * (index + 1) + contentWidth / 2f,
//                paddingLeft + contentWidth.toFloat(),
//                (contentHeight.toFloat() / items.size) * (index + 1) + contentWidth / 2f,
//                textPaint.apply { color = Color.BLACK })

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - contentWidth

        val top = contentWidth / 2f
        val bottom = contentWidth / 2f + contentHeight

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val point = event.x to event.y
                val index = (point.second - top).toInt() / (contentHeight / items.size)
                setSelected(index, feedback = false, ignore = false, callback = true)
            }

            MotionEvent.ACTION_UP -> {
                val point = event.x to event.y
                val index = (point.second - top).toInt() / (contentHeight / items.size)
                setSelected(index, feedback = false, ignore = false, callback = true)
            }

            MotionEvent.ACTION_MOVE -> {
                val point = event.x to event.y
                val index = (point.second - top).toInt() / (contentHeight / items.size)
                setSelected(index, feedback = false, ignore = true, callback = true)
            }
        }

        return true
    }

    fun setSelected(header: Header, feedback: Boolean) {
        val index = items.indexOf(header)
        setSelected(index, feedback = feedback, ignore = true, callback = false)
    }

    private fun setSelected(selected: Int, feedback: Boolean = false, ignore: Boolean = true, callback: Boolean = true) {
        if (ignore && selected == _selected) {
            return
        }

        if (selected < 0) {
            setSelected(0, feedback = false, ignore = true, callback = true)
        } else if (selected > items.lastIndex) {
            setSelected(items.lastIndex, feedback = false, ignore = true, callback = true)
        } else {
            _selected = when {
                selected > items.lastIndex -> items.lastIndex
                selected < 0 -> 0
                else -> selected
            }

            invalidate()

            if (callback) {
                listeners.forEach { it(_selected, items[_selected]) }
            }

            if (feedback) {
                performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
        }

    }
}