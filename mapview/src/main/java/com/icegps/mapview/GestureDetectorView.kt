package com.icegps.mapview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.Interpolator
import android.widget.Scroller
import com.icegps.mapview.utils.ScaleHelper
import java.lang.ref.WeakReference

abstract class GestureDetectorView : ViewGroup {
    private var init = true
    /**
     * 动画时间
     */
    private val DEFAULT_ANIMATION_DURATION = 400L

    private var animationDuration = DEFAULT_ANIMATION_DURATION

    var defaultZoomMultiple = 2.0f
    /**
     * 平移双击缩放手势
     */
    private var gestureDetector: GestureDetector
    private var scaleGestureDetector: ScaleGestureDetector
    /**
     * 触发拖动
     */

    /**
     * valueAnimator
     */
    private var mapAnimator: MapAnimator? = null

    /**
     * 子元素的left top
     */
    private var childLeft: Int = 0
    private var childTop: Int = 0

    /**
     * 最大最小缩放
     */
    private val minScale = 1f
    private val maxScale = 100f

    private var listeners: HashSet<Listener>? = null
    /**
     * 当前缩放
     */
    var scale = 1f
    private var scaleCopy = 1f

    var tx = 0
    var ty = 0
    /**
     * 设置的宽高
     */
    var w = 0
    var h = 0

    private var scroller: Scroller

    private var isDraging = false

    /**
     * 缩放后的宽高
     */
    var scaleWidth = 0
        get() {
            field = ScaleHelper.scale(w.toDouble(), scale)
            return field
        }
    var scaleHeight = 0
        get() {
            field = ScaleHelper.scale(h.toDouble(), scale)
            return field
        }
    /**
     * 平移 双击
     */
    private var simpleOnGestureListener: GestureDetector.SimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (!isDraging) {
                isDraging = true
                onDragBegin()
            } else {
                onDraging()
            }

            tx = tx + distanceX.toInt()
            ty = ty + distanceY.toInt()
            scrollTo(tx, ty)
            invalidate()

            return super.onScroll(e1, e2, distanceX, distanceY)
        }


        override fun onDoubleTap(e: MotionEvent?): Boolean {
            doubleRefershScale(e!!.x.toInt(), e.y.toInt(), scale * defaultZoomMultiple)
            return true
        }
    }


    /**
     * 缩放
     */
    private var onScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = scaleCopy * detector.scaleFactor
            zoom(detector.focusX.toInt(), detector.focusY.toInt(), scale)
            this@GestureDetectorView.scale = checkScale(scale)

            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scaleCopy = scale
            onScaleBegin()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            onScaleEnd()
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        //允许onDraw
        setWillNotDraw(false)
        //
        listeners = HashSet()

        scroller = Scroller(context)

        gestureDetector = GestureDetector(context, simpleOnGestureListener)

        scaleGestureDetector = ScaleGestureDetector(context, onScaleGestureListener)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_UP -> {
                if (isDraging) {
                    isDraging = false
                    onDragEnd()
                }
            }
        }
        return true
    }

    /**
     * 子view的大小由
     * @see setSize 决定
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(scaleWidth, MeasureSpec.EXACTLY)
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(scaleHeight, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        width = View.resolveSize(width, widthMeasureSpec)
        height = View.resolveSize(height, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    /**
     * 设置缩放
     */
    private fun setMapScale(scale: Float) {
        var scale = scale
        scale = checkScale(scale)
        if (this.scale != scale) {
            this.scale = scale
            scaleChange(scale)
        }
    }


    /**
     * 缩放值不应该小于最小限制也不应该大于最大限制
     */
    private fun checkScale(scale: Float): Float {
        if (scale < minScale) {
            return minScale
        }
        if (scale > maxScale) {
            return maxScale
        }
        return scale
    }

    /**
     * 双击放大
     */
    fun doubleRefershScale(x: Int, y: Int, scale: Float) {
        var scale = checkScale(scale)
        if (scale == this.scale || getMapAnimator()!!.isStarted) {
            return
        }
        var scaleScrollX = getOffsetScrollXFromScale(x, scale, this.scale)
        var scaleScrollY = getOffsetScrollYFromScale(y, scale, this.scale)
        getMapAnimator()!!.startAnima(scaleScrollX, scaleScrollY, scale)
    }

    /**
     * 两指手势放大
     */
    private fun zoom(x: Int, y: Int, scale: Float) {
        var scale = checkScale(scale)

        if (scale == this.scaleCopy || getMapAnimator()!!.isStarted) {
            return
        }
        var scaleScrollX = getOffsetScrollXFromScale(x, scale, this.scale)

        var scaleScrollY = getOffsetScrollYFromScale(y, scale, this.scale)

        tx += scaleScrollX

        ty += scaleScrollY

        scrollTo(tx, ty)

        setMapScale(scale)

        onDraging()

        onScaling()

    }

    private fun getOffsetScrollXFromScale(focusX: Int, destinationScale: Float, currentScale: Float): Int {
        val currentPoint = ScaleHelper.scale(scrollX + focusX.toDouble(), destinationScale / currentScale) - (scrollX + focusX)
        return currentPoint
    }

    private fun getOffsetScrollYFromScale(focusY: Int, destinationScale: Float, currentScale: Float): Int {
        val currentPoint = ScaleHelper.scale(scrollY + focusY.toDouble(), destinationScale / currentScale) - (scrollY + focusY)
        return currentPoint
    }

    /**
     * 缩放改变,强制子类刷新
     */
    abstract fun scaleChange(scale: Float)

    /**
     *  布局的时候将屏幕中心移动到子View的0.0
     *
     *  @see MarkerView.pantheOriginTotheCenter
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = width
        val height = height
        if (init) {
            //偏移到子view中心
            tx += -(width / 2 - scaleWidth / 2)
            ty += -(height / 2 - scaleHeight / 2)
            scrollTo(tx, ty)
            init = false
        }
        childLeft = 0
        childTop = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                child.layout(childLeft, childTop, childLeft + scaleWidth, childTop + scaleHeight)
            }

        }

        requestRender()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        requestRender()
    }

    /**
     * 获取动画对象
     */
    private fun getMapAnimator(): MapAnimator? {
        if (mapAnimator == null) {
            mapAnimator = MapAnimator(this)
            mapAnimator!!.setDuration(animationDuration)
        }
        return mapAnimator
    }

    /**
     * 设置地图大小
     */
    open fun setSize(w: Int, h: Int) {
        this.w = w
        this.h = h
    }

    /**
     * 动画
     */
    class MapAnimator(gestureDetectorView: GestureDetectorView) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private var gestureDetectorViewWeakReference: WeakReference<GestureDetectorView>
        private var startState: ScaleOrTranslationState
        private var endState: ScaleOrTranslationState
        private var scaleChange = false
        private var translationChange = false

        init {
            startState = ScaleOrTranslationState()
            endState = ScaleOrTranslationState()
            gestureDetectorViewWeakReference = WeakReference(gestureDetectorView)
            addListener(this)
            setFloatValues(0f, 1f)
            addUpdateListener(this)
            interpolator = FastEaseInInterpolator()
        }

        private var x = 0f
        private var y = 0f

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                var progress = animation!!.animatedValue as Float
                gestureDetectorViewWeakReference.get()!!.setMapScale(animatedFraction)
                if (scaleChange) {
                    var scale = startState.scale + (endState.scale - startState.scale) * progress
                    gestureDetectorView.setMapScale(scale)
                    gestureDetectorView.onScaling()
                }
                if (translationChange) {
                    x = startState.x + (endState.x - startState.x) * progress
                    y = startState.y + (endState.y - startState.y) * progress
                    gestureDetectorView.scrollTo(gestureDetectorView.tx + x.toInt(), gestureDetectorView.ty + y.toInt())
                    gestureDetectorView.onDraging()
                }
            }
        }

        /**
         * 传入xy以及scale开始走动画
         */
        fun startAnima(x: Int, y: Int, scale: Float) {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                scaleChange = scaleAnim(scale)
                translationChange = translationAnima(x, y)
                if (scaleChange || translationChange) {
                    start()
                }
            }
        }

        private fun scaleAnim(scale: Float): Boolean {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                startState.scale = gestureDetectorView.scale
                endState.scale = scale
                return startState.scale != endState.scale
            }
            return false
        }

        private fun translationAnima(x: Int, y: Int): Boolean {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                startState.x = 0
                startState.y = 0
                endState.x = x
                endState.y = y
                return startState.x != endState.x && startState.y != endState.y
            }
            return false
        }

        override fun onAnimationRepeat(animation: android.animation.Animator?) {
        }

        override fun onAnimationEnd(animation: android.animation.Animator?) {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                gestureDetectorView.tx += x.toInt()
                gestureDetectorView.ty += y.toInt()
                if (scaleChange) {
                    gestureDetectorView.onScaleEnd()
                }
                if (translationChange) {
                    gestureDetectorView.onDragEnd()
                }
            }
        }

        override fun onAnimationCancel(animation: android.animation.Animator?) {
        }

        override fun onAnimationStart(animation: android.animation.Animator?) {
            val gestureDetectorView = gestureDetectorViewWeakReference.get()
            if (gestureDetectorView != null) {
                if (scaleChange) {
                    gestureDetectorView.onScaleBegin()
                }
                if (translationChange) {
                    gestureDetectorView.onDragBegin()
                }
            }
        }

        private class ScaleOrTranslationState {
            var x = 0
            var y = 0
            var scale = 0f
        }

        private class FastEaseInInterpolator : Interpolator {
            override fun getInterpolation(input: Float): Float {
                return (1 - Math.pow((1 - input).toDouble(), 8.0)).toFloat()
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
    }

    /**
     * 拖动到指定位置 (动画)
     * @param x 这个值应该是* scale之后的结果
     * @param y 这个值应该是* scale之后的结果
     */
    fun drag(x: Int, y: Int) {
        var x = x - width / 2 + scaleWidth / 2
        var y = y - height / 2 + scaleHeight / 2
        scroller.startScroll(scrollX, scrollY, 0, 0, animationDuration.toInt())
        scroller.setFinalX(x)
        scroller.setFinalY(y)
        invalidate()
        onDragBegin()
    }

    /**
     * 拖动到指定位置(立刻)
     *
     * @param x 这个值应该是* scale之后的结果
     * @param y 这个值应该是* scale之后的结果
     */
    fun dragImmediately(x: Int, y: Int) {
        onDragBegin()
        var x = x - width / 2 + scaleWidth / 2
        var y = y - height / 2 + scaleHeight / 2
        scrollTo(x, y)
        tx = x
        ty = y
        onDragEnd()
        invalidate()
    }


    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            tx = scroller.currX
            ty = scroller.currY
            invalidate()
            if (scroller.isFinished) {
                onDragEnd()
            } else {
                onDraging()
            }
        }
    }

    private fun onScaleBegin() {
        for (listener in listeners!!) {
            listener.onScaleBegin(scale)
        }
    }

    private fun onScaling() {
        for (listener in listeners!!) {
            listener.onScaling(scale)
        }
    }

    private fun onScaleEnd() {
        requestRender()
        for (listener in listeners!!) {
            listener.onScaleEnd(scale)
        }
    }

    private fun onDragBegin() {
        for (listener in listeners!!) {
            listener.onDragBegin(scrollX, scrollY)
        }
    }

    private fun onDraging() {
        for (listener in listeners!!) {
            listener.onDraging(scrollX, scrollY)
        }
    }

    private fun onDragEnd() {
        requestRender()
        for (listener in listeners!!) {
            listener.onDragEnd(scrollX, scrollY)
        }
    }

    abstract fun requestRender()

    interface Listener {
        fun onScaleBegin(scale: Float)
        fun onScaling(scale: Float)
        fun onScaleEnd(scale: Float)
        fun onDragBegin(scrollX: Int, scrollY: Int)
        fun onDraging(scrollX: Int, scrollY: Int)
        fun onDragEnd(scrollX: Int, scrollY: Int)
    }
}