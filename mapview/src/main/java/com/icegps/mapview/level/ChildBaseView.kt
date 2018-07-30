package com.icegps.mapview.level

import android.content.Context
import android.text.method.Touch.scrollTo
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

open abstract class ChildBaseView:View{

    constructor(context: Context?) : super(context)
    init {
        setWillNotDraw(false)
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        pantheOriginTotheCenter()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(availableWidth, availableHeight)
    }

    /**
     * 将(0.0)坐标点移动到中心
     */
    private fun pantheOriginTotheCenter() {
        scrollTo(-measuredWidth / 2, -measuredHeight / 2)
    }


   abstract fun setScale(scale: Float)
}