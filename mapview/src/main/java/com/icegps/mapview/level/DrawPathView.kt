package com.icegps.mapview.level

import android.content.Context
import android.graphics.*
import java.util.concurrent.CopyOnWriteArrayList

class DrawPathView : ChildBaseView {
    private var scale = 1f
    private var mapPaths: CopyOnWriteArrayList<MapPath>
    private var defaultPaint: Paint
    private var DEFAULT_COLOR = Color.RED
    private var pathMatrix: Matrix
    private var matrixPath: Path

    constructor(context: Context?) : super(context)

    init {
        mapPaths = CopyOnWriteArrayList()
        pathMatrix = Matrix()
        matrixPath = Path()
        defaultPaint = Paint()
        defaultPaint.setStyle(Paint.Style.STROKE)
        defaultPaint.setColor(DEFAULT_COLOR)
        defaultPaint.strokeWidth = 1f
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val iterator = mapPaths.iterator()
        while (iterator.hasNext()){
            val mapPath = iterator.next()
            matrixPath.set(mapPath.path)
            matrixPath.transform(pathMatrix)
            canvas!!.drawPath(matrixPath, mapPath.paint)
        }

    }

    fun addPath(path: Path, paint: Paint?): MapPath {
        val mapPath = MapPath(path, if (paint == null) defaultPaint else paint)
        return addPath(mapPath)
    }

    fun addPath(mapPath: MapPath): MapPath {
        mapPaths.add(mapPath)
        invalidate()
        return mapPath
    }

    override fun setScale(scale: Float) {
        this.scale = scale
        pathMatrix.setScale(scale, scale)
        invalidate()
    }

    fun clearAllPath() {
        mapPaths.clear()
        postInvalidate()
    }


    class MapPath {
        var path: Path
        var paint: Paint

        constructor(path: Path, paint: Paint) {
            this.path = path
            this.paint = paint
        }
    }
}