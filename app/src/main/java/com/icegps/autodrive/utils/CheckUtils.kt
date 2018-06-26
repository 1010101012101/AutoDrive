package com.icegps.autodrive.utils

import kotlin.experimental.xor

/**
 * Created by jmj on 2018/5/2.
 */
class CheckUtils{
    companion object {
        //计算校验值
        fun addCheckSum(s: String): String {
            var sum: Byte = 0
            for (i in 1 until s.length) {
                sum = sum xor s[i].toByte()
            }
            return s + "*" + Integer.toHexString(sum.toInt()) + "\r\n"
        }
    }
}