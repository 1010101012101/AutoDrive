package com.icegps.mapview.level

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.icegps.mapview.data.Tile

class DrawBitmapView : ChildBaseView {
    private var scale = 1f
    private var tiles: HashSet<Tile>? = null
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : super(context)


    init {
        this.tiles = HashSet()
    }

    /**
     * 设置缩放
     */
    override fun setScale(scale: Float) {
        this.scale = scale
        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.scale(scale, scale)
        tiles!!.forEach {
            canvas!!.drawBitmap(it.bitmap, null, RectF(it.left, it.top, it.right, it.bottom), Paint())
        }
    }

    fun renderTiles(tiles: HashSet<Tile>) {
        clear(tiles)
        this.tiles!!.addAll(tiles)
        invalidate()
    }

    fun clearTiles() {
        tiles!!.clear()
    }

    private fun clear(tiles: HashSet<Tile>) {
        val oldTileIterator = this.tiles!!.iterator()
        while (oldTileIterator.hasNext()) {
            val tile = oldTileIterator.next()
            if (!tiles.contains(tile)) {
                oldTileIterator.remove()
            }
        }
    }
}