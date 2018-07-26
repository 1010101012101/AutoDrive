package com.icegps.mapview.level

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import com.icegps.mapview.data.MapColor
import com.icegps.mapview.utils.ScaleHelper

class MarkerLayout(context: Context?) : ViewGroup(context) {
    private var scale = 1f

    init {
        setWillNotDraw(false)
        //允许子控件超出父控件范围
        clipChildren = true
    }

    /**
     * 设置缩放
     */
    fun setScale(scale: Float) {
        this.scale = scale
        requestLayout()
    }

    /**
     * 添加标记
     */
    fun addMarker(x: Double, y: Double, view: View) {
        val defLayoutParams = getDefLayoutParams()
        defLayoutParams.x = x
        defLayoutParams.y = y
        addView(view, defLayoutParams)
    }

    /**
     * 随着缩放变动元素的位置  但是元素本身的大小是恒定的
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            val layoutParams = childAt.layoutParams as LayoutParams
            val scaleX = ScaleHelper.scale(layoutParams.x, scale)
            val scaleY = ScaleHelper.scale(layoutParams.y, scale)

            //addMarker的xy 始终是与元素的中心点相对应的
            layoutParams.left = scaleX - childAt.measuredWidth / 2
            layoutParams.top = scaleY - childAt.measuredHeight / 2
            layoutParams.right = layoutParams.left + childAt.measuredWidth
            layoutParams.bottom = layoutParams.top + childAt.measuredHeight
        }

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(availableWidth, availableHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        pantheOriginTotheCenter()
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            if (childAt.visibility != GONE) {
                val layoutParams = childAt.layoutParams as LayoutParams
                childAt.layout(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom)
            }
        }
    }

    fun moveMarker(x: Double, y: Double, view: View) {
        val layoutParams = view.layoutParams as LayoutParams
        layoutParams.x = x
        layoutParams.y = y
        moveMarker(view, layoutParams)
    }

    fun moveMarker(view: View, params: LayoutParams) {
        if (indexOfChild(view) > -1) {
            view.layoutParams = params
            requestLayout()
        }
    }

    /**
     * 将(0.0)坐标点移动到中心
     */
    private fun pantheOriginTotheCenter() {
        scrollTo(-measuredWidth / 2, -measuredHeight / 2)
    }


    private fun getDefLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    open class LayoutParams : ViewGroup.LayoutParams {
        var x = 0.0
        var y = 0.0
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        constructor(width: Int, height: Int) : super(width, height)
    }
}