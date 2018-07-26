package com.icegps.mapview.level

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.icegps.mapview.data.MapColor

class BgLineLayout : ChildBaseLayout {
    private var lineGap = 50
    private var scale = 1f
    private var paint: Paint

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        paint = Paint()
        paint.color = Color.GRAY
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    /**
     * 设置缩放
     */
    fun setScale(scale: Float) {
        this.scale = scale
        requestLayout()
    }
}