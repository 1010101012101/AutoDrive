package com.icegps.autodrive.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.icegps.autodrive.constant.Cons

object TileUtils {
    /**
     * 通过y计算当前方块的top
     *
     * @param y
     * @return
     */
    fun getTop(y: Double): Double {
        var bitmapY = y
        bitmapY = Math.floor(bitmapY / Cons.TILE_LENGHT)
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
        bitmapX = Math.floor(bitmapX / Cons.TILE_LENGHT)
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
        val remainder = xOrY % Cons.TILE_LENGHT
        if (remainder >= 0) {
            xOrY = remainder
        } else {
            xOrY = Cons.TILE_LENGHT - Math.abs(remainder)
        }
        return Math.floor(xOrY).toInt()
    }


    /**
     * 初始化一张透明图片
     *
     * @return
     */
    fun getEmptyBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(Cons.TILE_LENGHT, Cons.TILE_LENGHT, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#00000000"))
        return bitmap
    }
}