package icegps.com.maplib.map.drawhelper

import android.graphics.Canvas
import icegps.com.maplib.map.MapView
import icegps.com.maplib.map.mapdata.MapParameter

class TranslateHelper(mapView: MapView) {
    private var mapView: MapView
    private var mapParameter: MapParameter

    init {
        this.mapView = mapView
        mapParameter = mapView.mapParameter
    }

    fun translate(canvas: Canvas) {
        canvas.translate(mapParameter.sumTx, mapParameter.sumTy)
    }

    fun setTranslateValue(distanceX: Float, distanceY: Float) {
        mapParameter.tx -= distanceX
        mapParameter.ty -= distanceY
    }
}