package com.icegps.autodrive.map.data

import java.io.Serializable

class PointMark : Serializable {
    companion object {
        var TYPE_A = 0
        var TYPE_B = 1
    }

    var type = -1
    var x = 0.0
        get() = field + pointAbXoffset
    var y = 0.0
        get() = field + pointAbYoffset
    var lat = 0.0
    var lon = 0.0


    var pointAbXoffset = 0.0
    var pointAbYoffset = 0.0

    constructor(type: Int, x: Double, y: Double, lat: Double, lon: Double) {
        this.type = type
        this.x = x
        this.y = y
        this.lat = lat
        this.lon = lon
    }

    constructor()

    override fun toString(): String {
        return "PointMark(type=$type, lat=$lat, lon=$lon, pointAbXoffset=$pointAbXoffset, pointAbYoffset=$pointAbYoffset)"
    }


}