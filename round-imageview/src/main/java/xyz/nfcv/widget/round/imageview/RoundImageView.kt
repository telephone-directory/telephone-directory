package xyz.nfcv.widget.round.imageview


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundImageView : AppCompatImageView {

    private lateinit var paint: Paint
    private lateinit var paint2: Paint
    private var radius = 5
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
            radius = a.getDimensionPixelSize(R.styleable.RoundImageView_radius, radius)
            a.recycle()
        } else {
            val density = context.resources.displayMetrics.density
            radius = (radius * density).toInt()
        }

        paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        paint2 = Paint()
        paint2.xfermode = null
    }

    override fun draw(canvas: Canvas) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(bitmap)
        super.draw(canvas2)
        drawLeftTop(canvas2)
        drawRightTop(canvas2)
        drawLeftDown(canvas2)
        drawRightDown(canvas2)
        canvas.drawBitmap(bitmap, 0f, 0f, paint2)
        bitmap.recycle()
    }

    private fun drawLeftTop(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, radius.toFloat())
        path.lineTo(0f, 0f)
        path.lineTo(radius.toFloat(), 0f)
        path.arcTo(
            RectF(0f, 0f, (radius * 2).toFloat(), (radius * 2).toFloat()),
            -90f,
            -90f
        )
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawLeftDown(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, (height - radius).toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(radius.toFloat(), height.toFloat())
        path.arcTo(
            RectF(
                0f,
                (height - radius * 2).toFloat(),
                (radius * 2).toFloat(),
                height.toFloat()
            ), 90f, 90f
        )
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawRightDown(canvas: Canvas) {
        val path = Path()
        path.moveTo((width - radius).toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), (height - radius).toFloat())
        path.arcTo(
            RectF(
                (width - radius * 2).toFloat(),
                (height - radius * 2).toFloat(),
                width.toFloat(),
                height.toFloat()
            ), 0f, 90f
        )
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawRightTop(canvas: Canvas) {
        val path = Path()
        path.moveTo(width.toFloat(), radius.toFloat())
        path.lineTo(width.toFloat(), 0f)
        path.lineTo((width - radius).toFloat(), 0f)
        path.arcTo(
            RectF(
                (width - radius * 2).toFloat(),
                0f,
                width.toFloat(),
                (radius * 2).toFloat()
            ), -90f, 90f
        )
        path.close()
        canvas.drawPath(path, paint)
    }

}
