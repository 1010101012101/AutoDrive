package com.icegps.mapview

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.icegps.mapview.data.Tile
import com.icegps.mapview.level.DrawBitmapView
import com.icegps.mapview.level.DrawPathView
import com.icegps.mapview.level.MarkerLayout
import com.icegps.mapview.utils.BitmapProvider

class MapView : GestureDetectorView {
    private var markerLayout: MarkerLayout
    private var drawBitmapView: DrawBitmapView
    private var drawPathView: DrawPathView
    private var tiles: HashSet<Tile> = HashSet()
    private var bgTiles: HashSet<Tile> = HashSet()
    private var viewport: Rect
    private var stateSnapshot: StateSnapshot? = null
    private var bgStateSnapshot: StateSnapshot? = null
    var bitmapProvider: BitmapProvider? = null
    var tileLength = 200
    var bgTileLength = 400
    var scaleTileLength = tileLength.toFloat()
        get() {
            return field * scale
        }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        viewport = Rect()
        drawBitmapView = DrawBitmapView(context)
        drawPathView = DrawPathView(context)
        markerLayout = MarkerLayout(context)
        scaleChange(scale)
        addLevelView()
    }


    private fun addLevelView() {
        addView(drawBitmapView)
        addView(drawPathView)
        addView(markerLayout)
    }

    override fun scaleChange(scale: Float) {
        drawBitmapView.setScale(scale)
        drawPathView.setScale(scale)
        markerLayout.setScale(scale)
    }

    fun setRotate(rotate: Float, x: Float, y: Float) {
        drawBitmapView.rotation = rotate
        drawBitmapView.rotation = rotate
        drawPathView.rotation = rotate
        markerLayout.rotation = rotate
    }

    /**
     * 添加一个标记 以view的形式
     */
    fun addMarker(x: Double, y: Double, view: View) {
        markerLayout.addMarker(x, y, view)
    }

    fun removeMarker(view: View) {
        markerLayout.removeView(view)
    }

    fun removeAllMarkers() {
        markerLayout.removeAllViews()
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


    override fun render() {
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
    fun requestRender() {
        drawBitmapView.invalidate()
        drawPathView.invalidate()
    }

    fun drawPath(path: Path, paint: Paint?): DrawPathView.MapPath {
        return drawPathView.addPath(path, paint)
    }


    fun clearPath() {
        drawPathView.clearAllPath()
    }

    fun clearTile() {
        drawBitmapView.clearTiles()
        tiles.clear()
    }

    fun drawPath(mapPath: DrawPathView.MapPath): DrawPathView.MapPath {
        return drawPathView.addPath(mapPath)
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
        if (computeCurrentState() || computeCurrentState1()) {
            deleteOldAddNewTiles()
        }
    }


    /**
     * 刷新方块
     */
    private fun renderTiles() {
        drawBitmapView.renderTiles(tiles, bgTiles)
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
     * 计算当前屏幕所需要的背景方块
     */
    private fun computeCurrentState1(): Boolean {
        val rowStart = Math.floor((viewport.top.toDouble() / bgTileLength)).toInt()
        val rowEnd = Math.ceil((viewport.bottom.toDouble() / bgTileLength)).toInt()
        val columnStart = Math.floor((viewport.left.toDouble() / bgTileLength)).toInt()
        val columnEnd = Math.ceil((viewport.right.toDouble() / bgTileLength)).toInt()
        val stateSnapshot = StateSnapshot(rowStart, rowEnd, columnStart, columnEnd)
        val sameState = stateSnapshot.equals(this.bgStateSnapshot)
        this.bgStateSnapshot = stateSnapshot
        return !sameState
    }


    /**
     * 删除旧的方块添加新的方块
     */
    private fun deleteOldAddNewTiles() {
        tiles.clear()
        bgTiles.clear()
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
                        val tile = Tile(row, column, left, top, right, bottom, tileLength)
                        val bitmap = bitmapProvider!!.getBitmap(tile)
                        if (bitmap != null) {
                            tile.bitmap = bitmap
                            tiles.add(tile)
                        }
                    }

                }
            }

        if (bgStateSnapshot != null)
            for (column in bgStateSnapshot!!.columnStart..bgStateSnapshot!!.columnEnd) {
                for (row in bgStateSnapshot!!.rowStart..bgStateSnapshot!!.rowEnd) {
                    var top = row * bgTileLength.toFloat()
                    var bottom = top + bgTileLength.toFloat()
                    var left = column * bgTileLength.toFloat()
                    var right = left + bgTileLength.toFloat()
                    if (bitmapProvider != null) {
                        val tile = Tile(row, column, left, top, right, bottom, bgTileLength)
                        val bitmap = bitmapProvider!!.getNotAffectedByScaleBitmap(tile)
                        if (bitmap != null) {
                            tile.bitmap = bitmap
                            bgTiles.add(tile)
                        }
                    }
                }
            }

    }

    fun onDestroy() {
        tiles.clear()
        removeAllViews()
    }

    fun onResume() {
        setWillNotDraw(false)
        computeViewport()
        renderTiles()
    }

    fun onPause() {
        setWillNotDraw(true)
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