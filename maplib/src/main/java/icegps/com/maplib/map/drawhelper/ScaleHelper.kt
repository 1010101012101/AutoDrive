package icegps.com.maplib.map.drawhelper

import android.graphics.Canvas
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.view.ScaleGestureDetector
import icegps.com.maplib.map.MapView
import icegps.com.maplib.map.mapdata.MapParameter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScaleHelper(mapView: MapView) {
    private var mapView: MapView
    private var mapParameter: MapParameter
    val newCachedThreadPool: ExecutorService

    init {
        this.mapView = mapView
        mapParameter = mapView.mapParameter
        newCachedThreadPool = Executors.newCachedThreadPool()
    }

    fun scale(canvas: Canvas) {
        canvas.scale(mapParameter.multiple, mapParameter.multiple)
    }

    fun cancelScale(canvas: Canvas) {
        canvas.scale(1.0f / mapParameter.multiple, 1.0f / mapParameter.multiple)
    }

    fun doubleScale(x: Float, y: Float, multiple: Float) {
        if (mapParameter.isIdle) {
            mapParameter.isIdle = false
            var multiple = multiple
            var finalMultiple = mapParameter.multiple * multiple
            if (finalMultiple <= mapParameter.minMultiple) {
                finalMultiple = mapParameter.minMultiple
                multiple = finalMultiple / mapParameter.multiple
            } else if (finalMultiple > mapParameter.maxMultiple) {
                finalMultiple = mapParameter.maxMultiple
                multiple = finalMultiple / mapParameter.multiple
            }
            //双击放大
            val px = x - mapParameter.tx
            val py = y - mapParameter.ty
            val sumPx = px * multiple - px
            val sumPy = py * multiple - py

            val sum = finalMultiple - mapParameter.multiple

            newCachedThreadPool.execute(object : Runnable {
                var count = 100
                override fun run() {
                    for (i in 1..count) {
                        mapParameter.tx -= sumPx / count
                        mapParameter.ty -= sumPy / count
                        mapParameter.multiple += sum / count
                        mapView.requestRefreshUi()
                        SystemClock.sleep(2)
                    }
                    mapParameter.isIdle = true
                }

            })
        }
    }


    internal var focusX: Float = 0.toFloat()
    internal var focusY: Float = 0.toFloat()
    internal var tx: Float = 0.toFloat()
    internal var ty: Float = 0.toFloat()
    internal var multiple: Float = 0.toFloat()

    fun onScale(detector: ScaleGestureDetector) {
        //手势缩放
        if (mapParameter.isIdle) {
            mapParameter.isIdle = false
            if (focusX == 0f && focusY == 0f) {
                focusX = detector.focusX
                focusY = detector.focusY
            }
            var scaleFactor = detector.scaleFactor
            var multiple = this.multiple * scaleFactor

            val px = focusX - tx
            val py = focusY - ty

            if (multiple <= mapParameter.minMultiple) {
                multiple = mapParameter.minMultiple
                scaleFactor = multiple / this.multiple
            } else if (multiple >= mapParameter.maxMultiple) {
                multiple = mapParameter.maxMultiple
                scaleFactor = multiple / this.multiple
            }

            val tx = this.tx - (px * scaleFactor - px)

            val ty = this.ty - (py * scaleFactor - py)

            mapParameter.tx = tx
            mapParameter.ty = ty
            mapParameter.multiple = multiple
            mapView.requestRefreshUi()
            mapParameter.isIdle = true
        }

    }

    fun onScaleBegin() {
        //手势缩放开始时 取一次基准值
        tx = mapParameter.tx
        ty = mapParameter.ty
        multiple = mapParameter.multiple
    }

    fun onScaleEnd() {
        focusX = 0f
        focusY = 0f
    }

}