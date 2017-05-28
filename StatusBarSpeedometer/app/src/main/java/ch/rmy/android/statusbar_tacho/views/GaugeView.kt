package ch.rmy.android.statusbar_tacho.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import ch.rmy.android.statusbar_tacho.R
import ch.rmy.android.statusbar_tacho.utils.Trigo

class GaugeView : View {

    private val arcPaint = Paint()
    private val needlePaint = Paint()
    private val bigMarkPaint = Paint()
    private val smallMarkPaint = Paint()
    private val numberPaint = Paint()

    private val arcRect = RectF()
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f

    private var animation: ValueAnimator = ValueAnimator.ofFloat(0F, 0F)

    var value = 0f
        set(value) {
            val previousValue = field
            val newValue = Math.max(0f, Math.min(MAX_VALUE, value))

            animation.cancel();
            animation = ValueAnimator.ofFloat(previousValue, newValue)
            animation.duration = ANIMATION_DURATION;
            animation.addUpdateListener { valueAnimator ->
                field = valueAnimator!!.animatedValue as Float;
                invalidate();
            };
            animation.start();
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        arcPaint.strokeWidth = context.resources.getDimension(R.dimen.gauge_arc_stroke)
        arcPaint.color = getColor(R.color.gauge_arc_stroke)
        arcPaint.isAntiAlias = true
        arcPaint.style = Paint.Style.STROKE

        numberPaint.color = getColor(R.color.gauge_numbers)
        numberPaint.isAntiAlias = true
        numberPaint.textSize = getDimension(R.dimen.gauge_number_size)
        numberPaint.textAlign = Paint.Align.CENTER

        needlePaint.strokeWidth = getDimension(R.dimen.gauge_needle_stroke)
        needlePaint.color = getColor(R.color.gauge_needle)
        needlePaint.isAntiAlias = true
        needlePaint.style = Paint.Style.STROKE
        needlePaint.strokeCap = Paint.Cap.ROUND

        bigMarkPaint.strokeWidth = getDimension(R.dimen.gauge_big_mark_stroke)
        bigMarkPaint.color = getColor(R.color.gauge_big_mark)
        bigMarkPaint.isAntiAlias = true
        bigMarkPaint.style = Paint.Style.STROKE

        smallMarkPaint.strokeWidth = getDimension(R.dimen.gauge_small_mark_stroke)
        smallMarkPaint.color = getColor(R.color.gauge_small_mark)
        smallMarkPaint.isAntiAlias = true
        smallMarkPaint.style = Paint.Style.STROKE
    }

    private fun getDimension(dimenRes: Int): Float {
        return context.resources.getDimension(dimenRes)
    }

    private fun getColor(colorRes: Int): Int {
        return context.resources.getColor(colorRes)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val totalHeight = MeasureSpec.getSize(heightMeasureSpec)

        val inset = arcPaint.strokeWidth

        val availableWidth = totalWidth - 2 * inset
        val availableHeight = totalHeight - 2 * inset

        val openAngle = 360 - ARC_ANGLE
        val aspectRatio = (Trigo.cos((openAngle / 2)) + 1) / 2

        val width: Float;

        if (availableWidth * aspectRatio > availableHeight) {
            // Use full height
            width = (availableHeight / aspectRatio)
        } else {
            // Use full width
            width = availableWidth
        }

        radius = width / 2f
        centerX = totalWidth / 2f
        centerY = inset + radius

        arcRect.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        )

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw arc
        canvas.drawArc(arcRect, START_ANGLE, ARC_ANGLE, false, arcPaint)

        // Draw marks
        for (i in 0..(MARK_COUNT - 1) * 2) {
            val progress = i.toFloat() / ((MARK_COUNT - 1) * 2)
            if (i % 2 == 0) {
                drawLine(canvas, bigMarkPaint, progress, BIG_MARK_START, BIG_MARK_END)
            } else {
                drawLine(canvas, smallMarkPaint, progress, SMALL_MARK_START, SMALL_MARK_END)
            }
        }

        // Draw numbers
        for (i in 0..(MARK_COUNT - 1)) {
            val progress = i.toFloat() / (MARK_COUNT - 1)
            val value = (progress * MAX_VALUE).toInt()
            val angle = getAngle(progress)
            val factorX = Trigo.cos(angle)
            val factorY = Trigo.sin(angle)

            canvas.drawText(
                    value.toString(),
                    centerX + factorX * radius * NUMBER_START,
                    centerY + factorY * radius * NUMBER_START,
                    numberPaint
            )
        }

        // Draw needle
        val progress = value / MAX_VALUE
        needlePaint.style = Paint.Style.STROKE
        drawLine(canvas, needlePaint, progress, 0f, NEEDLE_LENGTH)
        needlePaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, NEEDLE_CAP * radius, needlePaint)
    }

    private fun drawLine(canvas: Canvas, paint: Paint, progress: Float, startFactor: Float, endFactor: Float) {
        val angle = getAngle(progress)
        val factorX = Trigo.cos(angle)
        val factorY = Trigo.sin(angle)
        val start = startFactor * radius
        val end = endFactor * radius
        canvas.drawLine(
                centerX + factorX * start,
                centerY + factorY * start,
                centerX + factorX * end,
                centerY + factorY * end,
                paint
        )
    }

    private fun getAngle(progress: Float): Float {
        return ARC_ANGLE * progress + START_ANGLE
    }

    companion object {

        private val ANIMATION_DURATION = 1000L
        private val ARC_ANGLE = 360 * 0.6f
        private val START_ANGLE = 270 - ARC_ANGLE / 2
        private val NEEDLE_LENGTH = 0.91f
        private val NEEDLE_CAP = 0.03f
        private val MAX_VALUE = 180f
        private val MARK_COUNT = 10
        private val BIG_MARK_START = 0.88f
        private val BIG_MARK_END = 0.95f
        private val SMALL_MARK_START = 0.92f
        private val SMALL_MARK_END = 0.95f
        private val NUMBER_START = 0.77f

    }

}
