package com.icegps.autodrive.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.utils.DisplayUtils

/**
 * Created by jmj on 2018/5/2.
 */
class ThermometerSensorView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var thermometerContentPaint: Paint
    var thermometerBottomCirclePaint: Paint
    var thermometerMarkLinePaint: Paint
    var thermometerMarkTextPaint: Paint
    var temperatureValue = 20f
    var thermometerSumHeight = 0f
    val dp5: Float
    val dp10: Float
    val dp15: Float
    val dp20: Float
    val dp1: Float
    var radius: Float
    var sp14: Float
    var gap = 0f

    init {

        sp14 = DisplayUtils.sp2px(context, 16f).toFloat()
        dp5 = DisplayUtils.dp2px(context, 5f).toFloat()
        dp1 = DisplayUtils.dp2px(context, 1f).toFloat()
        dp10 = DisplayUtils.dp2px(context, 10f).toFloat()
        dp15 = DisplayUtils.dp2px(context, 15f).toFloat()
        dp20 = DisplayUtils.dp2px(context, 20f).toFloat()

        thermometerContentPaint = Paint()
        thermometerContentPaint.strokeCap = Paint.Cap.ROUND

        thermometerBottomCirclePaint = Paint()
        thermometerBottomCirclePaint.isAntiAlias = true


        thermometerMarkLinePaint = Paint()
        thermometerMarkLinePaint.color = Color.parseColor("#737274")
        thermometerMarkLinePaint.strokeWidth = dp1

        thermometerMarkTextPaint = Paint()
        thermometerMarkTextPaint.color = Color.parseColor("#737274")
        thermometerMarkTextPaint.textSize = sp14

        thermometerMarkTextPaint.textAlign = Paint.Align.CENTER
        radius = dp15

    }

    var width = 0f
    var height = 0f
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        width = getWidth().toFloat()
        height = getHeight().toFloat()

        drawMark(canvas!!)

        for (i in 3 downTo 1) {
            when (i) {
                3 -> {
                    thermometerContentPaint.color = ContextCompat.getColor(context,R.color.colorAccent)
                    thermometerBottomCirclePaint.color = ContextCompat.getColor(context,R.color.colorAccent)
                }
                2 -> {
                    thermometerContentPaint.color = Color.WHITE
                    thermometerBottomCirclePaint.color = Color.WHITE
                }
                1 -> {
                    thermometerContentPaint.color = Color.parseColor("#FF6364")
                    thermometerBottomCirclePaint.color = Color.parseColor("#FF6364")
                }
            }

            thermometerContentPaint.strokeWidth = dp5 * i

            radius = dp5 * i

            drawLine(canvas!!, i)

            drawCircle(canvas!!)
        }
    }

    fun refreshTemperature(temperatureValue: Float) {
        this.temperatureValue = temperatureValue
        invalidate()
    }

    /**
     * 绘制刻度
     */
    fun drawMark(canvas: Canvas) {

        for (i in 0 until 6) {

            thermometerSumHeight = height - dp10 * 3

            gap = thermometerSumHeight / 6

            var startX = width / 2 - dp15 / 2 - dp5

            var startY = dp10 + gap * i

            var stopX = width / 2 - dp15 - dp5

            var stopY = startY

            canvas!!.drawLine(startX, startY, stopX, stopY, thermometerMarkLinePaint)

            canvas.drawText((100 - i * 20).toString(), stopX - dp10 * 2, startY + sp14 / 2, thermometerMarkTextPaint)
        }

    }

    /**
     * 绘制温度计
     */
    fun drawLine(canvas: Canvas, case: Int) {

        var startX = width / 2
        var startY = dp10
        if (case == 1) {
            startY = dp10 + (thermometerSumHeight-gap - (temperatureValue * ((thermometerSumHeight-gap) / 100f)))
        }
        var stopX = startX

        var stopY = height - dp15

        canvas!!.drawLine(startX, startY, stopX, stopY, thermometerContentPaint)
    }

    /**
     * 绘制温度计下面的圆
     */
    fun drawCircle(canvas: Canvas) {
        var cx = width / 2
        var cy = height - dp15

        canvas!!.drawCircle(cx, cy, radius, thermometerBottomCirclePaint)

    }

}