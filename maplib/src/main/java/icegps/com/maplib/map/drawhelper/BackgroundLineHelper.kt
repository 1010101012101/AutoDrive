package icegps.com.maplib.map.drawhelper

import android.graphics.Canvas
import android.graphics.Paint
import icegps.com.maplib.map.MapView
import icegps.com.maplib.map.mapdata.MapParameter
import icegps.com.maplib.map.mapdata.MapUiColor

class BackgroundLineHelper(mapView: MapView) {
    private val linePint: Paint
    private val defExceed = 100f
    private var exceed = defExceed
    /**
     * 横线左边起始点为-exceed
     */
    private var hStartX = -exceed
    private var hStopX = 0f
    private var hStartY = 0f
    private var hSotpY = 0f

    /**
     * 竖线上面起始点为-exceed
     */
    private var vStartX = 0f
    private var vStopX = 0f
    private var vStartY = -exceed
    private var vSotpY = 0f
    /**
     * 用于计算line长度拖动判断的条件值
     */
    private var lengthDragTx = 0f
    private var lengthDragTy = 0f
    /**
     * 用于计算line数量拖动判断的条件值
     */
    private var countDragTx = 0f
    private var countDragTy = 0f
    /**
     * 线与线之间的距离
     */
    private var defLineDistance = 50f
    private var lineDistance = defLineDistance

    /**
     * 需要多少条线能填满横竖的一半屏幕
     */
    private var hHalfLineCount = 0
    private var vHalfLineCount = 0
    /**
     * view宽高
     */
    private var mapHeight = 0f
    private var mapWidth = 0f

    /**
     * 实际偏移量
     */
    private var tx = 0f
    private var ty = 0f
    /**
     * 反向缩放值
     */
    private var cancelMultiple = 0f

    private var mapView: MapView
    private var mapParameter: MapParameter

    init {
        linePint = Paint()
        linePint.color = MapUiColor.mapLineColor
        this.mapView = mapView
        mapParameter = mapView.mapParameter
    }

    fun drawLine(canvas: Canvas) {
        tx = mapParameter.sumTx

        ty = mapParameter.sumTy

        cancelMultiple = 1.0f / mapParameter.multiple

        mapHeight = mapParameter.mapHeight

        mapWidth = mapParameter.mapWidth

        hHalfLineCount = (mapHeight / lineDistance + 3).toInt()

        vHalfLineCount = (mapWidth / lineDistance + 3).toInt()



        drawHorizontalLine(canvas)

        drawVerticalLine(canvas)
    }

    /**
     * 画横线
     */
    fun drawHorizontalLine(canvas: Canvas) {
        /**
         * 若偏移距离大于等于一个exceed 则 hStartX重新设置位置
         */
        if (tx - lengthDragTx >= exceed || tx - lengthDragTx <= -exceed) {
            hStartX = -tx - exceed
            lengthDragTx = tx
        }
        /**
         * 横线的右边结束点等于起始位置+屏幕宽度+ exceed*2
         */
        hStopX = hStartX + mapParameter.mapWidth + exceed * 2
        /**
         * 若拖动距离大于一个countDragTy则从新设置基准线的位置
         */
        if (ty - countDragTy >= lineDistance || ty - countDragTy <= -lineDistance) {
            hStartY = -ty - -ty % lineDistance
            countDragTy = ty
        }

        hSotpY = hStartY

        /**
         * 基准线
         */
        drawLine(hStartX, hStartY, hStopX, hSotpY, canvas)

        /**
         * 下半部分
         */
        for (i in 1..hHalfLineCount) {
            val gap = i * lineDistance
            drawLine(
                    hStartX,
                    hStartY + gap,
                    hStopX,
                    hSotpY + gap,
                    canvas)

        }
        /**
         * 上半部分
         */
        for (i in 3 downTo 0) {
            val gap = i * lineDistance
            drawLine(
                    hStartX,
                    hStartY - gap,
                    hStopX,
                    hSotpY - gap,
                    canvas)

        }
    }

    /**
     * 画竖线
     */
    fun drawVerticalLine(canvas: Canvas) {
        /**
         * 若拖动距离大于一个countDragTy则从新设置基准线的位置
         */
        if (tx - countDragTx >= lineDistance || tx - countDragTx <= -lineDistance) {
            vStartX = -tx - -tx % lineDistance
            countDragTx = tx
        }

        vStopX = vStartX
        /**
         * 若偏移距离大于等于一个exceed 则  vStartY 重新设置位置
         */
        if (ty - lengthDragTy >= exceed || ty - lengthDragTy <= -exceed) {
            vStartY = -ty - exceed
            lengthDragTy = ty
        }
        /**
         * 竖线的下面结束点等于起始位置+屏幕高度+ exceed*2
         */
        vSotpY = vStartY + mapParameter.mapHeight + exceed * 2
        /**
         * 基准线
         */
        drawLine(vStartX, vStartY, vStopX, vSotpY, canvas)

        /**
         * 右半部分
         */
        for (i in 1..vHalfLineCount) {
            val gap = i * lineDistance
            drawLine(
                    vStartX + gap,
                    vStartY,
                    vStopX + gap,
                    vSotpY,
                    canvas)
        }
        /**
         * 左半部分
         */
        for (i in 3 downTo 0) {
            val gap = i * lineDistance
            drawLine(
                    vStartX - gap,
                    vStartY,
                    vStopX - gap,
                    vSotpY,
                    canvas)
        }
    }


    private fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float, canvas: Canvas) {
        canvas.drawLine(
                startX * cancelMultiple,
                startY * cancelMultiple,
                stopX * cancelMultiple,
                stopY * cancelMultiple,
                linePint)
    }
}