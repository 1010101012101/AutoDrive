package com.icegps.autodrive.map2.utils

import com.tencent.bugly.proguard.x
import com.tencent.bugly.proguard.y

class MathUtils {
    companion object {
        /**
         * 计算两点角度
         */
        fun calculateAzimuth(x: Double, y: Double, x1: Double, y1: Double): Double {
            return Math.toDegrees(Math.atan2(x - x1, y - y1))
        }

        fun calculateDictance(x: Double, y: Double, x1: Double, y1: Double): Double {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1))
        }

    }
}