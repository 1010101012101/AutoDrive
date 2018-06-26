package com.icegps.autodrive.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.icegps.autodrive.utils.DisplayUtils

/**
 * Created by jmj on 2018/4/28.
 */
class AngleSensorView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var excirclePaint: Paint
    var pointPaint: Paint
    var circlePaint: Paint
    var rectF: RectF
    val dp15: Float
    val dp5: Float
    var cx = 0f
    var cy = 0f
    var radius = 0f
    var angle = 0f

    init {
        dp15 = DisplayUtils.dp2px(context, 15f).toFloat()
        dp5 = DisplayUtils.dp2px(context, 5f).toFloat()

        excirclePaint = Paint()
        excirclePaint.color = Color.parseColor("#548AC8")
        excirclePaint.strokeWidth = DisplayUtils.dp2px(context, 15f).toFloat()
        excirclePaint.isAntiAlias = true
        excirclePaint.style = Paint.Style.STROKE

        pointPaint = Paint()
        pointPaint.color = Color.parseColor("#FF6364")
        pointPaint.strokeWidth = DisplayUtils.dp2px(context, 5f).toFloat()
        pointPaint.strokeCap = Paint.Cap.ROUND

        circlePaint = Paint()
        circlePaint.color = Color.parseColor("#5B5C6E")
        circlePaint.isAntiAlias = true

        rectF = RectF()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        init()
        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)
        //从270度开始画130度
        canvas!!.drawArc(rectF, 270f - 65f, 130f, false, excirclePaint)
        //指针角度设置
        canvas.rotate(angle, cx, cy)
        //指针
        canvas.drawLine(cx, dp5, cx, cy + dp5 * 2, pointPaint)
        //黑点
        canvas.drawCircle(cx, cy, dp5, circlePaint)
    }

    fun refreshAngle(angle: Float) {
        this.angle = angle
        invalidate()
    }

    fun init() {
        //长宽取最大的 保证扇形是圆的
        val max = Math.max(width.toFloat(), height.toFloat())
        cx = max / 2
        cy = max / 2
        radius = max / 2 - dp15
    }
}