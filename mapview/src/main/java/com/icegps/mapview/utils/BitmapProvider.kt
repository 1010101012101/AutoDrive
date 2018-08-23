package com.icegps.mapview.utils

import android.graphics.Bitmap

interface BitmapProvider {
    /**
     * 受缩放影响的bitmap
     */
    fun getBitmap(x: com.icegps.mapview.data.Tile): Bitmap?

    /**
     * 不受缩放影响的bitmap
     */
    fun getNotAffectedByScaleBitmap(tile: com.icegps.mapview.data.Tile): Bitmap?

}