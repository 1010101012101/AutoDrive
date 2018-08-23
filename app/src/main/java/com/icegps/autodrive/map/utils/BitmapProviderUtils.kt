package com.icegps.autodrive.map.utils

import android.graphics.*
import android.util.LruCache
import com.icegps.autodrive.utils.Init
import com.icegps.mapview.data.Tile
import com.icegps.mapview.utils.BitmapProvider
import timber.log.Timber
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BitmapProviderUtils() : BitmapProvider {
    private var bitmapLruCache: BitmapLruCache
    private var paint: Paint
    private var textPaint: Paint
    private var canvas: Canvas
    private var bgBitmap: Bitmap? = null
    var measuredTime: Long = 0
    private var newSingleThreadExecutor: ExecutorService

    init {
        newSingleThreadExecutor = Executors.newSingleThreadExecutor()
        paint = Paint()
        textPaint = Paint()
        canvas = Canvas()
        paint.color = Color.parseColor("#E9F055")
        paint.isFilterBitmap = true
        textPaint.textSize = 16f
        textPaint.isAntiAlias = true
        textPaint.color = Color.BLACK
        bitmapLruCache = BitmapLruCache(Init.getMemoryCacheSize())
    }

    /**
     * 受缩放影响
     */
    override fun getBitmap(tile: Tile): Bitmap? {
        val bitmapName = StringBuffer().append(tile.column).append("_").append(tile.row).toString()
        var bitmap = bitmapLruCache.get(bitmapName)
        if (bitmap == null && measuredTime != 0L) {
            newSingleThreadExecutor.execute(Runnable {
                bitmap = FileUtils.setDir(measuredTime!!).getBitmapBySdCard(bitmapName)
                if (bitmap != null) {
                    bitmapLruCache.add(bitmapName, bitmap)
                    onLoadSDBitmap?.onCompleted()
                }
            })
        }
        return bitmap
    }

    /**
     * 不受缩放影响
     */
    override fun getNotAffectedByScaleBitmap(tile: Tile): Bitmap? {
        return getBgBitmap(tile.tileLenght)
    }

    /**
     * 拖拉机行走时需要在屏幕上绘制轨迹,目前地图的布局是采用bitmap分块实现,轨迹的展现是在bitmap上绘制实现的
     *
     * 在这种情况下又分有几个概念
     *
     * 1,车宽
     *
     * 2,一个像素点代表多少的实际宽度
     *
     * 假设拖拉机走到的位置刚好又在当前bitmap的边缘,那么此时在一个bitmap的方块中就不能够将轨迹完整的展现,所
     *
     * 以就需要将当前方块绘制不完的伸到另外一个方块上.
     *
     * 在这个方法中使用的绘制轨迹方法是Canvas(bitmap).drawRect(Rectf(left,top,right,bottom)) , 因此步骤为
     *
     * 1,计算当前车宽占用了几个方块
     *
     * 2,计算每个方块中的Rectf(left,top,right,bottom)的数值
     *
     * 3,调用canvas.drawRect将每一个rectF都绘制到bitmap上去.
     */
    fun createBitmapByXy(x: Double, y: Double, tileLength: Float, workWidth: Float, mapAccuracy: Float) {
        //车宽的一般所需要的像素
        val workWidthHalfPixel = (workWidth / mapAccuracy) / 2
        //当前的x减掉掉车宽的一半 计算出车宽左边缘是哪个bitmap方块
        var startTileLeft = TileUtils.getLeft(x - workWidthHalfPixel).toInt()
        //右边
        var endTileLeft = TileUtils.getLeft(x + workWidthHalfPixel).toInt()
        //上边
        var startTileTop = TileUtils.getTop(y - workWidthHalfPixel).toInt()
        //下边
        var endTileTop = TileUtils.getTop(y + workWidthHalfPixel).toInt()

        //临时变量用于控制do while语句,保证自增不会影响原数据
        var tempStartTileLeft = startTileLeft
        //用于判断do while执行的次数
        var leftAndRihtCount = 0

        do {
            //用于判断do while执行的次数
            var topAndBottomCount = 0

            //临时变量用于控制do while语句,保证自增不会影响原数据
            var tempStartTileTop = startTileTop
            //bitmap绘制矩形的关键
            var rectF = RectF()

            //-----------------------------------------------------Rect的 left rihgt 判断处理----------------------------------------------------------------------------//
            //如果是第一个方块,且是唯一的一个方块
            //则这个方块的rectf 的left与right都应该使用TileUtils.getPixelLocation方法获取
            if (leftAndRihtCount == 0 && tempStartTileLeft == endTileLeft) {
                rectF.left = TileUtils.getPixelLocation(x - workWidthHalfPixel).toFloat()
                rectF.right = TileUtils.getPixelLocation(x + workWidthHalfPixel).toFloat()
            }

            //如果是第一个方块,且后面还有方块,
            // 则这个方块rect的left应该使用 TileUtils.getPixelLocation, 而right则可直接取最大值. 因为在这里车宽已经超过一个方块,说明后面还有rect衔接
            else if (leftAndRihtCount == 0 && tempStartTileLeft != endTileLeft) {
                rectF.left = TileUtils.getPixelLocation(x - workWidthHalfPixel).toFloat()
                rectF.right = tileLength
            }

            //如果是最后一个方
            // 则这个rect的left可直接取0 ,而right应该使用TileUtils.getPixelLocation来获取
            else if (leftAndRihtCount != 0 && tempStartTileLeft == endTileLeft) {
                rectF.left = 0f
                rectF.right = TileUtils.getPixelLocation(x + workWidthHalfPixel).toFloat()
            }

            //如果这是中间的方块,
            //因为左右都有衔接,所以,left=0 right取最大值
            else {
                rectF.left = 0f
                rectF.right = tileLength
            }
            //-----------------------------------------------------Rect的 left rihgt 判断处理----------------------------------------------------------------------------//

            do {

                //-----------------------------------------------------Rect的 top bottom 判断处理----------------------------------------------------------------------------//
                //rectf 的top 和 bottom  思路同上
                if (topAndBottomCount == 0 && tempStartTileTop == endTileTop) {

                    rectF.top = TileUtils.getPixelLocation(y - workWidthHalfPixel).toFloat()
                    rectF.bottom = TileUtils.getPixelLocation(y + workWidthHalfPixel).toFloat()

                } else if (topAndBottomCount == 0 && tempStartTileTop != endTileTop) {

                    rectF.top = TileUtils.getPixelLocation(y - workWidthHalfPixel).toFloat()
                    rectF.bottom = tileLength

                } else if (topAndBottomCount != 0 && tempStartTileTop == endTileTop) {

                    rectF.top = 0f
                    rectF.bottom = TileUtils.getPixelLocation(y + workWidthHalfPixel).toFloat()

                } else {

                    rectF.top = 0f
                    rectF.bottom = tileLength
                }
                //-----------------------------------------------------Rect的 top bottom 判断处理----------------------------------------------------------------------------//

                //组装这个bitmap方块的名称
                val tileName = StringBuffer().append(tempStartTileLeft).append("_").append(tempStartTileTop).toString()
                //通过名称以及需要在这个bitmap上绘制矩形的rectf来处理
                createBitmapToCache(tileName, rectF)

                topAndBottomCount++
                tempStartTileTop++

            } while (tempStartTileTop <= endTileTop)

            leftAndRihtCount++
            tempStartTileLeft++

        } while (tempStartTileLeft <= endTileLeft)

    }

    /**
     * 清空cache
     */
    fun clearBitmapTileCache() {
        for (key in bitmapLruCache.getKeys()) {
            bitmapLruCache.remove(key)
        }
        bitmapLruCache.getKeys().clear()
    }

    /**
     * 创建bitmap并存入cache
     */
    private fun createBitmapToCache(tileName: String, rectF: RectF) {
        var bitmap = bitmapLruCache.get(tileName)
        if (bitmap == null) {
            bitmap = TileUtils.getEmptyBitmap()
            bitmapLruCache.add(tileName, bitmap)
            bitmapLruCache.getKeys().add(tileName)
        }
        canvas.setBitmap(bitmap)
        canvas.drawRect(rectF, paint)
    }


    /**
     * 初始化背景图片
     */
    fun getBgBitmap(tileLength: Int): Bitmap? {
        if (bgBitmap == null) {
            bgBitmap = Bitmap.createBitmap(tileLength, tileLength, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bgBitmap)
            canvas.drawColor(Color.parseColor("#59A522"))
            val paint = Paint()
            paint.strokeWidth = 20f
            paint.color = Color.parseColor("#22000000")
            var count = 4
            val gap = tileLength / count
            for (i in 0 until count) {
                for (j in 0 until count) {
                    if ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1)) {
                        canvas.drawRect(
                                gap * i.toFloat(),
                                gap * j.toFloat(),
                                gap * i.toFloat() + gap,
                                gap * j.toFloat() + gap,
                                paint
                        )
                    }
                }
            }
        }
        return bgBitmap
    }

    /**
     * 保存图片
     */
    fun saveBitmap() {
        ThreadPool.getInstance().executeFixed(Runnable {
            for (key in bitmapLruCache.getKeys()) {
                FileUtils.setDir(measuredTime!!).saveBitmap2SdCard(key, bitmapLruCache.get(key))
            }
            Timber.e("保存完毕")
        })
    }

    var onLoadSDBitmap: OnLoadSDBitmap? = null

    interface OnLoadSDBitmap {
        fun onCompleted()
    }

    /**
     * bitmap方块的缓存器
     */
    inner class BitmapLruCache : LruCache<String, Bitmap> {
        constructor(maxSize: Int) : super(maxSize)

        private val keys: HashSet<String>

        init {
            keys = HashSet()
        }

        fun getKeys(): HashSet<String> {
            return keys
        }

        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }

        fun add(key: String, bitmap: Bitmap) {
            put(key, bitmap)
            keys.add(key)
        }

        override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
            super.entryRemoved(evicted, key, oldValue, newValue)
            if (evicted) {
                FileUtils.setDir(measuredTime!!).saveBitmap2SdCard(key!!, oldValue!!)
            }
        }
    }
}