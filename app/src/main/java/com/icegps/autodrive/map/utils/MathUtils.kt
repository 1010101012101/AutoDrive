package com.icegps.autodrive.map.utils

class MathUtils {
    companion object {
        /**
         * 公倍数
         */
        @Synchronized
        fun commonMultiple(a: Int, b: Int): Int {
            return a * b / commonDivisor(a, b)
        }

        /**
         * 公约数
         */
        fun commonDivisor(x: Int, y: Int)   //递归的辗转相减法
                : Int {
            if (x == y) return x
            return if (x > y) commonDivisor(x - y, y) else commonDivisor(x, y - x)
        }
    }
}