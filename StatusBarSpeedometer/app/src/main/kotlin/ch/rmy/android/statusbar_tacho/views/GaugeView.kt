package ch.rmy.android.statusbar_tacho.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.compose.ui.graphics.toArgb
import ch.rmy.android.statusbar_tacho.utils.Trigonometry
import kotlin.math.max
import kotlin.math.min

class GaugeView @JvmOverloads constructor(
    context: Context,
    value: Float,
    maxValue: Float,
    markCount: Int,
    theme: GaugeTheme,
) : View(context) {

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

    var value = value
        set(value) {
            val previousValue = field
            val newValue = max(0f, min(maxValue, value))

            animation.cancel()
            animation = ValueAnimator.ofFloat(previousValue, newValue)
            animation.duration = ANIMATION_DURATION
            animation.addUpdateListener { valueAnimator ->
                field = valueAnimator.animatedValue as Float
                invalidate()
            }
            animation.start()
            invalidate()
        }

    var maxValue = maxValue
        set(maxValue) {
            if (field != maxValue) {
                field = maxValue
                reset()
            }
        }

    var markCount = markCount
        set(markCount) {
            if (field != markCount) {
                field = markCount
                reset()
            }
        }

    var theme: GaugeTheme = theme
        set(value) {
            if (field != value) {
                field = value
                applyThemeColors()
                invalidate()
            }
        }

    private fun reset() {
        animation.cancel()
        invalidate()
    }

    init {
        arcPaint.isAntiAlias = true
        arcPaint.style = Paint.Style.STROKE

        numberPaint.isAntiAlias = true
        numberPaint.textAlign = Paint.Align.CENTER

        needlePaint.isAntiAlias = true
        needlePaint.style = Paint.Style.STROKE
        needlePaint.strokeCap = Paint.Cap.ROUND

        bigMarkPaint.isAntiAlias = true
        bigMarkPaint.style = Paint.Style.STROKE

        smallMarkPaint.isAntiAlias = true
        smallMarkPaint.style = Paint.Style.STROKE

        applyThemeColors()
    }

    private fun applyThemeColors() {
        arcPaint.color = theme.arcColor.toArgb()
        numberPaint.color = theme.numberColor.toArgb()
        needlePaint.color = theme.needleColor.toArgb()
        bigMarkPaint.color = theme.bigMarkColor.toArgb()
        smallMarkPaint.color = theme.smallMarkColor.toArgb()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val totalHeight = MeasureSpec.getSize(heightMeasureSpec)

        val inset = arcPaint.strokeWidth

        val availableWidth = totalWidth - 2 * inset
        val availableHeight = totalHeight - 2 * inset

        val openAngle = 360 - ARC_ANGLE
        val aspectRatio = (Trigonometry.cos(openAngle / 2) + 1) / 2

        val width = if (availableWidth * aspectRatio > availableHeight) {
            (availableHeight / aspectRatio)
        } else {
            availableWidth
        }

        val finalWidth: Int
        val finalHeight: Int
        if (availableWidth * aspectRatio > availableHeight) {
            finalWidth = (totalHeight / aspectRatio).toInt()
            finalHeight = totalHeight
        } else {
            finalWidth = totalWidth
            finalHeight = (totalWidth * aspectRatio).toInt()
        }

        radius = width / 2f
        centerX = finalWidth / 2f
        centerY = inset + radius

        arcRect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
        )
        updatePaintSizes()

        setMeasuredDimension(finalWidth, finalHeight)
    }

    private fun updatePaintSizes() {
        arcPaint.strokeWidth = radius * ARC_STROKE_WIDTH_FACTOR
        numberPaint.textSize = radius * NUMBER_SIZE_FACTOR
        needlePaint.strokeWidth = radius * NEEDLE_STROKE_WIDTH_FACTOR
        bigMarkPaint.strokeWidth = radius * BIG_MARK_STROKE_WIDTH_FACTOR
        smallMarkPaint.strokeWidth = radius * SMALL_MARK_STROKE_WIDTH_FACTOR
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw arc
        canvas.drawArc(arcRect, START_ANGLE, ARC_ANGLE, false, arcPaint)

        // Draw marks
        for (i in 0..(markCount - 1) * 2) {
            val progress = i.toFloat() / ((markCount - 1) * 2)
            if (i % 2 == 0) {
                drawLine(canvas, bigMarkPaint, progress, BIG_MARK_START, BIG_MARK_END)
            } else {
                drawLine(canvas, smallMarkPaint, progress, SMALL_MARK_START, SMALL_MARK_END)
            }
        }

        // Draw numbers
        for (i in 0 until markCount) {
            val progress = i.toFloat() / (markCount - 1)
            val value = (progress * maxValue).toInt()
            val angle = getAngle(progress)
            val factorX = Trigonometry.cos(angle)
            val factorY = Trigonometry.sin(angle)

            canvas.drawText(
                value.toString(),
                centerX + factorX * radius * NUMBER_START,
                centerY + factorY * radius * NUMBER_START,
                numberPaint
            )
        }

        // Draw needle
        val progress = value / maxValue
        needlePaint.style = Paint.Style.STROKE
        drawLine(canvas, needlePaint, progress, 0f, NEEDLE_LENGTH)
        needlePaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, NEEDLE_CAP * radius, needlePaint)
    }

    private fun drawLine(canvas: Canvas, paint: Paint, progress: Float, startFactor: Float, endFactor: Float) {
        val angle = getAngle(progress)
        val factorX = Trigonometry.cos(angle)
        val factorY = Trigonometry.sin(angle)
        val start = startFactor * radius
        val end = endFactor * radius
        canvas.drawLine(
            centerX + factorX * start,
            centerY + factorY * start,
            centerX + factorX * end,
            centerY + factorY * end,
            paint,
        )
    }

    private fun getAngle(progress: Float): Float =
        ARC_ANGLE * progress + START_ANGLE

    companion object {

        private const val ANIMATION_DURATION = 700L

        private const val ARC_ANGLE = 360 * 0.6f
        private const val START_ANGLE = 270 - ARC_ANGLE / 2
        private const val NEEDLE_LENGTH = 0.91f
        private const val NEEDLE_CAP = 0.03f
        private const val BIG_MARK_START = 0.88f
        private const val BIG_MARK_END = 0.95f
        private const val SMALL_MARK_START = 0.92f
        private const val SMALL_MARK_END = 0.95f
        private const val NUMBER_START = 0.77f

        private const val NEEDLE_STROKE_WIDTH_FACTOR = 0.029f
        private const val ARC_STROKE_WIDTH_FACTOR = 0.04f
        private const val NUMBER_SIZE_FACTOR = 0.09f
        private const val BIG_MARK_STROKE_WIDTH_FACTOR = 0.01f
        private const val SMALL_MARK_STROKE_WIDTH_FACTOR = 0.005f

    }

}
