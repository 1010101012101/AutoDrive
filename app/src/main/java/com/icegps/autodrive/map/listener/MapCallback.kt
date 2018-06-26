package com.icegps.autodrive.map.listener

interface MapCallback{
    fun onAzimuth(azimuth: Float)
    fun onAbDistance(distance: Double)
}