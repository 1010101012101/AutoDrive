package com.icegps.mapview.level

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class DrawBackgroundLineView : ChildBaseView {
    private var lineGap = 50
    private var scale = 1f
    private var paint: Paint

    constructor(context: Context?) : super(context)

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
    override fun setScale(scale: Float) {
        this.scale = scale
        requestLayout()
    }
}