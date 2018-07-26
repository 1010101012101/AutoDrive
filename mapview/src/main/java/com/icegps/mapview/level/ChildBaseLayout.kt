package com.icegps.mapview.level

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

open class ChildBaseLayout:ViewGroup{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
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
}