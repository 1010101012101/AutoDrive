package com.icegps.autodrive.utils

class StringUtils{
    companion object {
        fun setAccuracy(value: Double, endAccuracy: Int): String {
            return String.format("%." + endAccuracy + "f", value)
        }
    }
}