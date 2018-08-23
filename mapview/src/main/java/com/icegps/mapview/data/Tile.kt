package com.icegps.mapview.data

import android.graphics.Bitmap

class Tile {


    enum class State {
        UNASSIGNED,
        PENDING_DECODE,
        DECODE
    }

    var state = State.UNASSIGNED

    var tileLenght = 0
    /**
     * 第几行
     */
    var row = 0
    /**
     * 第几列
     */
    var column = 0
    /**
     * bitmap绘制范围
     */
    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f

    /**
     * tilebitmap
     */
    var bitmap: Bitmap? = null
        set(value) {
            if (value != null) {
                field = value
                state = State.DECODE
            }
        }

    constructor(row: Int, column: Int, left: Float, top: Float, right: Float, bottom: Float, tileLenght: Int) {
        //竖向
        this.row = row
        //横向
        this.column = column
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        this.tileLenght = tileLenght
    }


    override fun hashCode(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        val tile = other as Tile
        return tile.row == row && tile.column == column
    }

    override fun toString(): String {
        return "Tile(row=$row, column=$column, left=$left, top=$top, right=$right, bottom=$bottom, bitmap=$bitmap)"
    }


}