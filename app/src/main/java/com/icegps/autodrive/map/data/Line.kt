package com.icegps.autodrive.map.data

class Line {
    var startX = 0f
    var startY = 0f
    var stopX = 0f
    var stopY = 0f

    constructor(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        this.startX = startX
        this.startY = startY
        this.stopX = stopX
        this.stopY = stopY
    }
}