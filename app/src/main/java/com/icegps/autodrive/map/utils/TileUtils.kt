package com.icegps.autodrive.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

object TileUtils {
    var tileLength: Int = 200
    /**
     * 通过y计算当前方块的top
     *
     * @param y
     * @return
     */
    fun getTop(y: Double): Double {
        var bitmapY = y
        bitmapY = Math.floor(bitmapY / tileLength)
        return bitmapY
    }

    /**
     * 根据X计算当前方块的left
     *
     * @param x
     * @return
     */
    fun getLeft(x: Double): Double {
        var bitmapX = x
        bitmapX = Math.floor(bitmapX / tileLength)
        return bitmapX
    }

    /**
     * 根据Y计算当前所处方块的Y
     *
     * @param xOrY
     * @return
     */
    fun getPixelLocation(xOrY: Double): Int {
        var xOrY = xOrY
        val remainder = xOrY % tileLength
        if (remainder >= 0) {
            xOrY = remainder
        } else {
            xOrY = tileLength - Math.abs(remainder)
        }
        return Math.floor(xOrY).toInt()
    }


    /**
     * 初始化一张透明图片
     *
     * @return
     */
    fun getEmptyBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(tileLength, tileLength, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#00000000"))
        return bitmap
    }
}