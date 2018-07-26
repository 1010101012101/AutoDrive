package com.icegps.mapview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.icegps.mapview.data.Tile
import com.icegps.mapview.level.BgLineLayout
import com.icegps.mapview.level.BitmapLayout
import com.icegps.mapview.utils.BitmapProvider
import com.icegps.mapview.level.MarkerLayout

class MapView : GestureDetectorView {
    private var markerLayout: MarkerLayout
    private var bitmapLayout: BitmapLayout
    private var bgLineLayout: BgLineLayout
    private var tiles: HashSet<Tile> = HashSet()
    private var viewport: Rect
    private var stateSnapshot: StateSnapshot? = null
    private var bitmap1: Bitmap
    private var bitmap2: Bitmap
    var bitmapProvider: BitmapProvider? = null
    var tileLength = 200
    var scaleTileLength = tileLength.toFloat()
        get() {
            return field * scale
        }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {

        bitmap1 = Bitmap.createBitmap(tileLength, tileLength, Bitmap.Config.ARGB_4444)
        bitmap2 = Bitmap.createBitmap(tileLength, tileLength, Bitmap.Config.ARGB_4444)
        Canvas(bitmap1).drawColor(Color.BLUE)
        Canvas(bitmap2).drawColor(Color.BLACK)

        viewport = Rect()

        markerLayout = MarkerLayout(context)
        bitmapLayout = BitmapLayout(context)
        bgLineLayout = BgLineLayout(context)


        addLevelView()
    }

    private fun addLevelView() {
        addView(bgLineLayout)
        addView(bitmapLayout)
        addView(markerLayout)
    }

    override fun scaleChange(scale: Float) {
        markerLayout.setScale(scale)
        bitmapLayout.setScale(scale)
    }

    /**
     * 添加一个标记 以view的形式
     */
    fun addMarker(x: Double, y: Double, view: View) {
        markerLayout.addMarker(x, y, view)
    }

    /**
     * 缩小
     */
    fun zoomOut() {
        zoom(scale / defaultZoomMultiple)
    }

    /**
     * 放大
     */
    fun zoomIn() {
        zoom(scale * defaultZoomMultiple)
    }

    /**
     * @param multiple 缩小多少倍
     */
    fun zoomOut(multiple: Float) {
        zoom(scale / multiple)
    }

    /**
     * @param multiple 放大多少倍
     */
    fun zoomIn(multiple: Float) {
        zoom(scale * multiple)
    }


    private fun zoom(multiple: Float) {
        doubleRefershScale(width / 2, height / 2, multiple)
    }


    override fun requestRender() {
        computeViewport()
        renderTiles()
    }

    /**
     * 移动标记的位置
     */
    fun moveMarker(x: Double, y: Double, view: View) {
        markerLayout.moveMarker(x, y, view)
    }

    /**
     * 强制重绘Bitmap
     */
    fun requestBitmapLayoutRender() {
        bitmapLayout.invalidate()
    }


    /**
     * 计算当前屏幕的偏移量
     */
    private fun computeViewport() {
        var left = scrollX - scaleWidth / 2
        var right = left + width
        var top = scrollY - scaleHeight / 2
        var bottom = top + height
        viewport.set(left, top, right, bottom)
        if (computeCurrentState()) {
            deleteOldAddNewTiles()
        }
    }

    /**
     * 强制更新当前偏移量
     */
    private fun forcedComputeViewport() {
        var left = scrollX - scaleWidth / 2
        var right = left + width
        var top = scrollY - scaleHeight / 2
        var bottom = top + height
        viewport.set(left, top, right, bottom)
        deleteOldAddNewTiles()
    }

    /**
     * 刷新方块
     */
    private fun renderTiles() {
        bitmapLayout.renderTiles(tiles)
    }


    /**
     * 计算当前屏幕所需要的方块
     */
    private fun computeCurrentState(): Boolean {

        val rowStart = Math.floor((viewport.top.toDouble() / scaleTileLength)).toInt()
        val rowEnd = Math.ceil((viewport.bottom.toDouble() / scaleTileLength)).toInt()
        val columnStart = Math.floor((viewport.left.toDouble() / scaleTileLength)).toInt()
        val columnEnd = Math.ceil((viewport.right.toDouble() / scaleTileLength)).toInt()
        val stateSnapshot = StateSnapshot(rowStart, rowEnd, columnStart, columnEnd)
        val sameState = stateSnapshot.equals(this.stateSnapshot)


        this.stateSnapshot = stateSnapshot
        return !sameState
    }

    /**
     * 删除旧的方块添加新的方块
     */
    private fun deleteOldAddNewTiles() {
        tiles.clear()
        requestGetTile()
    }

    /**
     * 触发一次方块获取
     */
    fun requestGetTile() {
        if (stateSnapshot != null)
            for (column in stateSnapshot!!.columnStart..stateSnapshot!!.columnEnd) {
                for (row in stateSnapshot!!.rowStart..stateSnapshot!!.rowEnd) {
                    var top = row * tileLength.toFloat()
                    var bottom = top + tileLength.toFloat()
                    var left = column * tileLength.toFloat()
                    var right = left + tileLength.toFloat()
                    if (bitmapProvider != null) {
                        val tile = Tile(row, column, left, top, right, bottom)
                        val bitmap = bitmapProvider!!.getBitmap(tile)
                        if (bitmap != null) {
                            tile.bitmap = bitmap
                            tiles.add(tile)
                        }
                    }

//                if (column % 2 == 0) {
//                    if (row % 2 == 0) {
//                        tiles.add(Tile(row, column, left, top, right, bottom, bitmap1))
//
//                    } else {
//                        tiles.add(Tile(row, column, left, top, right, bottom, bitmap2))
//
//                    }
//                } else {
//                    if (row % 2 == 0) {
//                        tiles.add(Tile(row, column, left, top, right, bottom, bitmap2))
//
//                    } else {
//                        tiles.add(Tile(row, column, left, top, right, bottom, bitmap1))
//
//                    }
//                }
                }
            }

    }

    class StateSnapshot {
        var rowStart: Int = 0
        var rowEnd: Int = 0
        var columnStart: Int = 0
        var columnEnd: Int = 0

        constructor(rowStart: Int, rowEnd: Int, columnStart: Int, columnEnd: Int) {
            this.rowStart = rowStart
            this.rowEnd = rowEnd
            this.columnStart = columnStart
            this.columnEnd = columnEnd
        }

        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            val stateSnapshot = other as StateSnapshot
            return stateSnapshot.rowStart == rowStart &&
                    stateSnapshot.rowEnd == rowEnd &&
                    stateSnapshot.columnStart == columnStart &&
                    stateSnapshot.columnEnd == columnEnd
        }
    }

}