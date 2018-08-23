package com.icegps.mapview.level

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.icegps.mapview.data.Tile

class DrawBitmapView : ChildBaseView {
    private var scale = 1f
    private var tiles: HashSet<Tile>? = null
    private var bgTiles: HashSet<Tile>? = null
    constructor(context: Context?) : super(context)


    init {
        tiles = HashSet()
        bgTiles = HashSet()
    }

    /**
     * 设置缩放
     */
    override fun setScale(scale: Float) {
        this.scale = scale
        requestLayout()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bgTiles!!.forEach {
            canvas!!.drawBitmap(it.bitmap, null, RectF(it.left, it.top, it.right, it.bottom), Paint())
        }

        canvas!!.scale(scale, scale)

        tiles!!.forEach {
            canvas!!.drawBitmap(it.bitmap, null, RectF(it.left, it.top, it.right, it.bottom), Paint())
        }
    }

    fun renderTiles(tiles: HashSet<Tile>, bgTiles: HashSet<Tile>) {
        clear(tiles, bgTiles)
        this.tiles!!.addAll(tiles)
        this.bgTiles!!.addAll(bgTiles)
        postInvalidate()
    }

    fun clearTiles() {
        tiles!!.clear()
    }

    private fun clear(tiles: HashSet<Tile>, bgTiles: HashSet<Tile>) {
        val oldTileIterator = this.tiles!!.iterator()
        while (oldTileIterator.hasNext()) {
            val tile = oldTileIterator.next()
            if (!tiles.contains(tile)) {
                oldTileIterator.remove()
            }
        }

        val oldBgTileIterator = this.bgTiles!!.iterator()
        while (oldBgTileIterator.hasNext()) {
            val tile = oldBgTileIterator.next()
            if (!tiles.contains(tile)) {
                oldBgTileIterator.remove()
            }
        }
    }
}