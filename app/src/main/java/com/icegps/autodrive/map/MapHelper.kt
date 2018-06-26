package com.icegps.autodrive.map

import android.app.ActivityManager
import android.content.Context
import android.graphics.*
import com.icegps.autodrive.R
import com.icegps.autodrive.map.data.ColorRes
import com.icegps.autodrive.map.data.PointMark
import com.icegps.autodrive.map.listener.MapCallback
import com.icegps.autodrive.map.utils.LatLonUtils
import com.icegps.autodrive.map.utils.TileUtils
import com.icegps.autodrive.utils.DisplayUtils
import icegps.com.maplib.map.Helper
import icegps.com.maplib.map.mapdata.BitmapLruCache
import j.m.jblelib.ble.data.LocationStatus
import java.util.concurrent.CopyOnWriteArrayList


class MapHelper(context: Context) : Helper() {

    private var context: Context
    /**
     * 是否允许开始工作
     */
    private var isWork = false
    /**
     * 拖拉机图标
     */
    private var tractors: Bitmap

    /**
     * 定位数据
     */
    private var locationStatus: LocationStatus? = null
    /**
     * A点至B点线的paint
     */
    private lateinit var distanceLinePaint: Paint
    /**
     * 各种状态监听
     */
    private var copyOnWriteArrayList: CopyOnWriteArrayList<MapCallback>
    /**
     * 拖拉机车头朝向
     */
    private var azimuth = 0f
    /**
     * 用于计算拖拉机车头朝向的XY    (车头朝向的计算方法是本次位置与上次位置所计算出来的角度)
     */
    private var oldX = 0.0
    private var oldY = 0.0
    /**x
     * 车身宽度
     */
    var workWidth = 3f
    /**
     * 一个像素等于多米 默认一个像素等于0.5米
     */
    var mapAccuracy = 0.5f

    var mapAccuracyCm = 0f
        get() {
            return mapAccuracy * 100f
        }
    var workWidthCm = 0f
        get() {
            return workWidth * 100f
        }
    /**
     * A点距B点的距离 若没有设置B点则为A点距当前位置的距离
     */
    var distance = 0.0
        set(value) {
            field = value
            for (mapCallback in copyOnWriteArrayList) {
                mapCallback.onAbDistance(field)
            }
        }

    private var markAbPoint: MarkAbPoint

    init {
        this.context = context
        markAbPoint = MarkAbPoint(this)
        copyOnWriteArrayList = CopyOnWriteArrayList()
        tractors = BitmapFactory.decodeResource(context.resources, R.mipmap.tractors)
        bitmapLruCache = BitmapLruCache(getMemoryCacheSize(context))
        initPaint()
    }

    fun initPaint() {
        distanceLinePaint = Paint()
        distanceLinePaint.strokeWidth = DisplayUtils.dp2px(context, 2f).toFloat()
        distanceLinePaint.color = ColorRes.distanceLineColor
    }

    override fun onDraw0(canvas: Canvas) {

    }

    override fun onDraw1(canvas: Canvas) {
        markAbPoint.draw1(canvas)

    }

    override fun onDraw2(canvas: Canvas) {
        markAbPoint.draw2(canvas)
        drawTractors(canvas)
    }

    /**
     * 工作入口
     */
    fun run(locationStatus: LocationStatus) {
        if (isWork) {
            /**
             * 设置locationstatus中的平面坐标xy
             */
            val latLon2Xy = LatLonUtils.latLon2Xy(doubleArrayOf(locationStatus.latitude, locationStatus.longitude, locationStatus.altitude))
            locationStatus.x = latLon2Xy[0]
            locationStatus.y = latLon2Xy[1]
            locationStatus.difference = latLon2Xy[2].toFloat()

            this.locationStatus = locationStatus
            markAbPoint.run(locationStatus)
            setCurPoint(locationStatus.x, locationStatus.y)
            calculateAzimuth(locationStatus.x, -locationStatus.y)
            createBitmap(locationStatus.x, locationStatus.y, ColorRes.bitmapBgColor)

            requestRefreshUi()
        }
    }


    /**
     * 绘制拖拉机图标
     */
    private fun drawTractors(canvas: Canvas) {
        var tractorsLeft = 0f
        var tractorsTop = 0f
        if (locationStatus != null) {
            tractorsLeft = (locationStatus!!.x.toFloat() * mapParameter!!.multiple) - tractors.width / 2
            tractorsTop = (locationStatus!!.y.toFloat() * mapParameter!!.multiple) - tractors.height / 2
        }
        canvas.rotate(azimuth, tractorsLeft + tractors.width / 2, tractorsTop + tractors.height / 2)
        canvas.drawBitmap(tractors, tractorsLeft, tractorsTop, bitmapPaint)


    }


    /**
     * 禁止工作
     */
    fun stopWork() {
        isWork = false
        LatLonUtils.isFirstPoint = true //重新计算基准坐标点
        markAbPoint.stopWork()
    }

    /**
     * 允许工作
     */
    fun startWork() {
        //清空缓存中的bitmap
        for (key in bitmapLruCache!!.keys) {
            bitmapLruCache!!.remove(key)
        }
        bitmapLruCache!!.keys.clear()
        mapParameter!!.recover()
        markAbPoint.startWork()
        isWork = true
        azimuth = 0f
        requestRefreshUi()
    }

    /**
     * 设置A点
     */
    fun markAPoint(): Boolean {
        return markAbPoint.markAPoint(locationStatus!!)
    }

    /**
     * 设置B点
     */
    fun markBPoint(): Boolean {
        return markAbPoint.markBPoint(locationStatus!!)
    }

    /**
     * 取消A点  (由于A点没有了, B点也会同时取消)
     */
    fun unMarkAPoint() {
        markAbPoint.unMarkAPoint()

    }

    /**
     * 取消B点
     */
    fun unMarkBPoint() {
        markAbPoint.unMarkBPoint()
    }


    /**
     * Ab点反转
     */
    fun inversionAb() {
        markAbPoint.inversionAb()
    }

    /**
     * @param viewOffsetDistance  界面线的偏移距离  (相对距离)
     *
     * @param cmdOffsetDistance   发送给设备的绝对偏移距离  TODO (绝对距离 待定)
     */
    fun setAbLineOffset(offsetDistance: Float) {
        markAbPoint.setAbLineOffset(offsetDistance)
    }

    /**
     * 移动位置点
     */
    private fun setCurPoint(x: Double, y: Double) {
        mapParameter!!.curX = x.toFloat()
        mapParameter!!.curY = y.toFloat()
    }


    /**
     * 计算车头角度
     */
    private fun calculateAzimuth(x: Double, y: Double) {
        if (Math.sqrt((x - oldX) * (x - oldX) + (y - oldY) * (y - oldY)) > 0.1) {
            azimuth = Math.toDegrees(Math.atan2(x - oldX, y - oldY)).toFloat()
            oldX = x
            oldY = y
        }
    }


    /**
     * 获取可用内大小
     */
    private fun getMemoryCacheSize(context: Context): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val largeMemoryClass = activityManager.largeMemoryClass
        return largeMemoryClass * 1024 * 1024 / 8
    }

    /**
     *
     * 创建BITMAP并存入
     * @see  Helper.bitmapLruCache
     *
     */
    private fun createBitmap(x: Double, y: Double, color: Int) {
        for (i in 0 until (workWidth / mapAccuracy).toInt()) {
            for (j in 0 until (workWidth / mapAccuracy).toInt()) {
                val bitmapLeft = TileUtils.getLeft(x - workWidth + i).toInt()
                val bitmapTop = TileUtils.getTop(y - workWidth + j).toInt()
                val pixelX = TileUtils.getX(x - workWidth + i)
                val pixelY = TileUtils.getY(y - workWidth + j)
                val name = StringBuilder().append(bitmapLeft).append(",").append(bitmapTop).toString()
                var bitmap = bitmapLruCache!!.get(name)
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(TileUtils.getEmptyBitmap(), 0, 0, TileUtils.tileLength, TileUtils.tileLength)
                }
                if (bitmap != null) {
                    bitmap.setPixel(pixelX, pixelY, color)
                    bitmapLruCache!!.add(name, bitmap)
                }
            }
        }
    }

    fun addCallback(mapCallback: MapCallback) {
        copyOnWriteArrayList.add(mapCallback)
    }

    fun removeCallback(mapCallback: MapCallback) {
        copyOnWriteArrayList.remove(mapCallback)
    }


}