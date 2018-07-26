package com.icegps.mapview.utils

import android.graphics.Bitmap

interface BitmapProvider {
    fun getBitmap(x: com.icegps.mapview.data.Tile): Bitmap?
}