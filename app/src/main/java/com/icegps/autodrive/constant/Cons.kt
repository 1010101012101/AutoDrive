package com.icegps.autodrive.constant

class Cons{
    companion object {
        var TILE_LENGHT=200
        var BG_TILE_LENGHT=400
        var MAP_WIDTH=200000
        var MAP_HEIGHT=200000

        var workWidth = 3F
        var mapAccuracy = 0.1f
        var workWidthPixel = 0f
            get() {
                field = workWidth / mapAccuracy
                return field
            }
    }
}