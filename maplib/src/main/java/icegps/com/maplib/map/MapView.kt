package icegps.com.maplib.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import icegps.com.maplib.map.drawhelper.BackgroundLineHelper
import icegps.com.maplib.map.drawhelper.ScaleHelper
import icegps.com.maplib.map.drawhelper.TranslateHelper
import icegps.com.maplib.map.mapdata.MapParameter
import icegps.com.maplib.map.mapdata.MapUiColor
import java.util.concurrent.CopyOnWriteArrayList


class MapView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs) {
    /**
     * 地图view的各项参数
     */
    internal lateinit var mapParameter: MapParameter

    private var scroller: Scroller
    /**
     * 速度检测
     */
    private lateinit var velocityTracker: VelocityTracker
    /**
     * 每次惯性滑动最大范围上下左右-1000~1000
     */
    private var flingRect: Rect
    private var flingLength = 1000

    /**
     * 绘制UI的线程
     */
    private var drawThread: DrawThread
    /**
     * UI绘制请求
     */
    private var REFRESH_UI = 0
    /**
     * 控制UI绘制频率的handler
     */
    private lateinit var drawHandler: Handler
    /**
     * 控制地图惯性滑动线程
     */
    private var flingThread: FlingThread? = null
    /**
     * 绘制背景线的类
     */
    private var backgroundLineHelper: BackgroundLineHelper
    /**
     * 控制地图平移的类
     */
    private lateinit var translateHelper: TranslateHelper
    /**
     * 控制地图缩放的类
     */
    private lateinit var scaleHelper: ScaleHelper

    /**
     * 手势
     */
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector
    /**
     * 是否有双击事件
     */
    private var isConsideredDoubleTap = false
    /**
     * 地图数据具体操作类
     */

    private var copyOnWriteArrayList: CopyOnWriteArrayList<MapCallback>

    var helper: Helper? = null
        set(value) {
            field = value
            field!!.mapView = this
        }
    private var downX = 0f
    private var downY = 0f
    private var upX = 0f
    private var upY = 0f
    private var isInit = true
    /**
     * 屏幕中心点
     */
    internal var centerX = 0f
    internal var centerY = 0f
    /**
     * 缩放手势管理
     */
    private var onScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleHelper.onScale(detector)
            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            //手势缩放开始时 取一次基准值
            scaleHelper.onScaleBegin()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            scaleHelper.onScaleEnd()
        }
    }

    /**
     * 平移 双击手势管理
     */
    private var simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (mapParameter.isIdle) {
                mapParameter.isIdle = false
                translateHelper.setTranslateValue(distanceX, distanceY)
                mapParameter.isIdle = true
            }

            requestRefreshUi()
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            scaleHelper.doubleScale(e!!.x, e.y, 2.0f)
            isConsideredDoubleTap = true
            return true
        }
    }
    /**.
     * surfaceCallback
     */
    private var callback = object : SurfaceHolder.Callback {

        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
            println("surfaceChanged")
        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            println("surfaceDestroyed")
        }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            println("surfaceCreated")
            requestRefreshUi()
        }
    }


    init {
        println("Mapview_init")
        holder.addCallback(callback)

        drawThread = DrawThread()

        scroller = Scroller(context)

        gestureDetector = GestureDetector(context, simpleOnGestureListener)

        scaleGestureDetector = ScaleGestureDetector(context, onScaleGestureListener)

        flingRect = Rect(-flingLength, -flingLength, flingLength, flingLength)

        mapParameter = MapParameter()

        backgroundLineHelper = BackgroundLineHelper(this)

        translateHelper = TranslateHelper(this)

        scaleHelper = ScaleHelper(this)

        copyOnWriteArrayList = CopyOnWriteArrayList()


        drawThread.start()

        /**
         * view加载完毕
         */
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mapParameter.mapHeight = measuredHeight.toFloat()
                mapParameter.mapWidth = measuredWidth.toFloat()
                if (isInit) {
                    /**
                     * 初始化0坐标点偏移到屏幕中心
                     */
                    mapParameter.ty = mapParameter.mapHeight / 2
                    mapParameter.tx = mapParameter.mapWidth / 2
                    isInit = false
                    requestRefreshUi()
                }
                return true
            }
        })
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleGestureDetector!!.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                //取消双击事件标记
                isConsideredDoubleTap = false
                //速度检测
                velocityTracker = VelocityTracker.obtain()
                velocityTracker.addMovement(event)
                //点击时取消惯性滑动
                if (flingThread != null)
                    flingThread!!.cancelFling()

                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                //速度检测
                velocityTracker.addMovement(event)
            }
            MotionEvent.ACTION_UP -> {
                //速度检测
                velocityTracker.addMovement(event)
                velocityTracker.computeCurrentVelocity(1000)

                flingThread = FlingThread()
                flingThread!!.fling()

                upX = event.x
                upY = event.y
                /**
                 * 地图点击事件
                 */
                if (Math.abs(upX - downX) <= 20 && Math.abs(upY - downY) <= 20) {
                    val x = (event.x - mapParameter.tx) * (1.0f / mapParameter.multiple)
                    val y = (event.y - mapParameter.ty) * (1.0f / mapParameter.multiple)
                    requestRefreshUi()
                    for (mapCallback in copyOnWriteArrayList) {
                        mapCallback.onClickMapPosition(x, y)
                    }
                }
            }
        }
        return true
    }

    /**
     * 绘制
     */
    private fun drawUi() {
        val canvas = holder.lockCanvas()
        if (canvas != null)
            try {
                canvas.drawColor(MapUiColor.mapBackgroudColor)
                if (helper != null) {
                    helper!!.onDraw0(canvas)
                }
                //平移
                translateHelper.translate(canvas)

                //缩放
                scaleHelper.scale(canvas)
                //背景线
                backgroundLineHelper.drawLine(canvas)

                centerX = -mapParameter.sumTx + mapParameter.mapWidth / 2

                centerY = -mapParameter.sumTy + mapParameter.mapHeight / 2

                if (helper != null) {
                    helper!!.onDraw1(canvas)
                    helper!!.onDraw(canvas)
                }

                scaleHelper.cancelScale(canvas)

                if (helper != null) {
                    helper!!.onDraw2(canvas)
                }


            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }
    }

    /**
     * 请求刷新绘制界面函数
     */
    internal fun requestRefreshUi() {
        drawHandler.sendEmptyMessage(REFRESH_UI)
    }

    /**
     * 绘制Ui线程
     */
    private inner class DrawThread : Thread() {
        override fun run() {
            super.run()
            Looper.prepare()
            drawHandler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message?) {
                    super.handleMessage(msg)
                    drawUi()
                }
            }
            Looper.loop()
        }
    }

    /**
     * 惯性滑动线程
     */
    private inner class FlingThread : Thread() {
        private var currX = 0f
        private var currY = 0f
        //惯性滑动开始
        fun fling() {
            scroller.fling(
                    0, 0,  //每次都从0点开始计算
                    velocityTracker.xVelocity.toInt(), velocityTracker.yVelocity.toInt(),
                    flingRect.left, flingRect.right,
                    flingRect.top, flingRect.bottom
            )
            start()
        }

        //惯性滑动停止
        fun cancelFling() {
            scroller.forceFinished(true)
            velocityTracker.clear()
            velocityTracker.recycle()
        }

        //惯性滑动具体操作
        override fun run() {
            super.run()
            if (scroller.computeScrollOffset()) {
                translateHelper.setTranslateValue(currX - scroller.currX, currY - scroller.currY)
                currX = scroller.currX.toFloat()
                currY = scroller.currY.toFloat()
                requestRefreshUi()
                postDelayed(this, 5)
            }
        }
    }

    internal fun addMapCallback(mapCallback: MapCallback) {
        copyOnWriteArrayList.add(mapCallback)
    }

    internal fun removeMapCallback(mapCallback: MapCallback) {
        copyOnWriteArrayList.remove(mapCallback)
    }

    internal class MapCallback {
        open fun onClickMapPosition(x: Float, yFloat: Float) {}
    }


}