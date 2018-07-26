package com.icegps.mapview.utils

import android.util.Log

class LogUtils {
    companion object {
        fun e(any: Any, content: String) {
            Log.e(any::javaClass.name, content)
        }
    }
}