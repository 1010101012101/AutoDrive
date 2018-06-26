package icegps.com.maplib.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import icegps.com.maplib.map.mapdata.BitmapLruCache
import icegps.com.maplib.map.mapdata.MapParameter

abstract class Helper() {

    var mapParameter: MapParameter? = null
    var mapView: MapView? = null
        set(value) {
            field = value
            mapParameter = value!!.mapParameter
        }

    protected var bitmapPaint: Paint? = null
    /**
     * key="left,top"  value="Bitmap"
     */
    protected var bitmapLruCache: BitmapLruCache? = null
        set(value) {
            field = value
            bitmapPaint = Paint()
        }


    internal fun onDraw(canvas: Canvas) {
        for (key in bitmapLruCache!!.keys) {
            val bitmap = bitmapLruCache!!.get(key)
            val leftAndTop = key.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val left = Integer.parseInt(leftAndTop[0])
            val top = Integer.parseInt(leftAndTop[1])
            canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), bitmapPaint!!)
        }
    }

    /**
     * 在此绘制,不接受任何效果
     */
    abstract fun onDraw0(canvas: Canvas)

    /**
     * 在此绘制,会接受平移+缩放的效果
     */
    abstract fun onDraw1(canvas: Canvas)

    /**
     * 在此绘制, 接受平移效果,不接受缩放效果
     */
    abstract fun onDraw2(canvas: Canvas)


    /**
     * 请求刷新绘制界面函数  更改了界面务必调用,否则不会更新
     */
    fun requestRefreshUi() {
        mapView!!.requestRefreshUi()
    }


    fun getCenterX(): Float {
        return mapView!!.centerX
    }

    fun getCenterY(): Float {
        return mapView!!.centerY
    }


}