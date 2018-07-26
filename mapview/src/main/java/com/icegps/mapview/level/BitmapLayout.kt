package com.icegps.mapview.level

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ViewGroup
import com.icegps.mapview.data.MapColor
import com.icegps.mapview.data.Tile

class BitmapLayout : ChildBaseLayout {
    private var scale = 1f
    private var tiles: HashSet<Tile>? = null
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    init {
        this.tiles = HashSet()
    }

    /**
     * 设置缩放
     */
    fun setScale(scale: Float) {
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