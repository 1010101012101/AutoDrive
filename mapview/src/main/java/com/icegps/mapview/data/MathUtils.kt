package com.icegps.mapview.data

import android.graphics.Path
import java.util.*

class MathUtils {
    companion object {
        /**
         * 计算两点角度
         */
        fun calculateAngle(x: Double, y: Double, x1: Double, y1: Double): Double {
            var degrees = Math.toDegrees(Math.atan2(y - y1, x - x1))
            degrees = (degrees + 270) % 360
            return degrees
        }

        fun calculateRadian(x: Double, y: Double, x1: Double, y1: Double): Double {
            return Math.atan2(y - y1, x - x1)
        }

        fun calculateDictance(x: Double, y: Double, x1: Double, y1: Double): Double {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1))
        }

        /**
         * 平移直线
         * */
         fun offsetLine(line: FloatArray, offset: Float): FloatArray {
            var atan2 = Math.atan2((line[2].toDouble() - line[0].toDouble()), -(line[3].toDouble() - line[1].toDouble()))
            var axy_bxy = FloatArray(4)
            axy_bxy[0] = line[0] + (offset * Math.cos(atan2).toFloat())
            axy_bxy[1] = line[1] + (offset * Math.sin(atan2).toFloat())
            axy_bxy[2] = line[2] + (offset * Math.cos(atan2).toFloat())
            axy_bxy[3] = line[3] + (offset * Math.sin(atan2).toFloat())
            return axy_bxy
        }

        fun  floatArray2Path(floatArray: FloatArray): Path {
            var path= Path()
            path.moveTo(floatArray[0],floatArray[1])
            path.lineTo(floatArray[2],floatArray[3])
            return path
        }

        /**
         * 延长直线
         */
        fun lengthenLine(line: FloatArray, lengthen: Float): FloatArray {
            var line = Arrays.copyOfRange(line, 0, line.size)

            var startX = line[0]
            var startY = line[1]
            var stopX = line[2]
            var stopY = line[3]

            val angle1 = Math.PI / 2 - (Math.PI / 2f - Math.atan2(startY - stopY.toDouble(), startX - stopX.toDouble()))
            var x1 = lengthen * Math.cos(angle1)
            var y1 = lengthen * Math.sin(angle1)
            line[0] -= x1.toFloat()
            line[1] -= y1.toFloat()


            val angle2 = Math.PI / 2 - (Math.PI / 2f - Math.atan2(stopY - startY.toDouble(), stopX - startX.toDouble()))
            var x2 = lengthen * Math.cos(angle2)
            var y2 = lengthen * Math.sin(angle2)
            line[2] += x2.toFloat()
            line[3] += y2.toFloat()

            return line
        }
    }
}