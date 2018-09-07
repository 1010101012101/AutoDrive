package com.icegps.autodrive.map

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.widget.ImageView
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.constant.Cons
import com.icegps.autodrive.constant.Cons.Companion.workWidthPixel
import com.icegps.autodrive.data.WorkHistory
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.map.data.TestData
import com.icegps.autodrive.map.utils.BitmapProviderUtils
import com.icegps.autodrive.map.utils.MathUtils
import com.icegps.autodrive.map.utils.ThreadPool
import com.icegps.autodrive.utils.Init
import com.icegps.jblelib.ble.data.LocationStatus
import com.icegps.mapview.GestureDetectorView
import com.icegps.mapview.MapView
import com.icegps.mapview.utils.ScaleHelper
import java.util.*

class MapUtils(mapview: MapView, activity: Activity, testData: TestData) {

    private var mapview: MapView
    private var activity: Activity
    private var tractors: View? = null
    private var ivMarkerA: View? = null
    private var ivMarkerB: View? = null
    private var bitmapProviderUtils: BitmapProviderUtils
    private var datumLine: DoubleArray
    private var line: FloatArray

    private var isMarkerB = false
    private var isMarkerA = false
    /**
     * 当前车位位置位于第几条线附近(通过AB线计算得出)
     */
    private var centerLinePosition = 0
    /**
     * 工作区域的线始终需要将车包裹在内 因此通过偏移量来设置
     */
    private var workLineEndsOffset = 0.0

    private var centerLinePaint: Paint? = null
    private var centerShadowPaint: Paint? = null
    private var centerShadowPath: Path
    private var scale = 1.0f
    /**
     * 偏移线 -> 正向
     */
    private var forwardOffsetLines: TreeMap<Int, Float>
    /**
     * 偏移线 <- 负向
     */
    private var reverseOffsetLines: TreeMap<Int, Float>
    /**
     * 发送给设备的A点与B点的经纬度
     */
    private var aLat = 0.0
    private var aLon = 0.0
    private var bLat = 0.0
    private var bLon = 0.0

    private var locationStatus: LocationStatus? = null

    private var currentLat = 0.0
    private var currentLon = 0.0
    /**
     * oldx用于与currentmovex 一前一后用于计算车头角度
     */
    private var oldX: Double = 0.0
    private var oldY: Double = 0.0
    private var currentMoveX = 0.0
    private var currentMoveY = 0.0

    private var isStartWork = false
    private var testData: TestData
    private var threadPool: ThreadPool
    private var halfOfTheLine = 2

    init {
        this.mapview = mapview
        this.activity = activity
        this.testData = testData
        forwardOffsetLines = TreeMap()
        reverseOffsetLines = TreeMap()
        scale = mapview.scale
        datumLine = DoubleArray(4)
        line = FloatArray(4)
        threadPool = ThreadPool.getInstance()
        //添加拖拉机图标
        createMarker()
        //设置地图大小
        mapview.setSize(Cons.MAP_WIDTH, Cons.MAP_HEIGHT)
        mapview.tileLength = Cons.TILE_LENGHT
        mapview.bgTileLength = Cons.BG_TILE_LENGHT

        //给mapview设置bitmap提供者
        bitmapProviderUtils = BitmapProviderUtils()
        mapview.bitmapProvider = bitmapProviderUtils
        mapview.addListener(object : GestureDetectorView.ListenerImpl() {
            override fun onScaling(scale: Float) {
                super.onScaling(scale)
                this@MapUtils.scale = scale
                if (isMarkerB)
                    threadPool.executeFixed(Runnable {
                        Init.post(Runnable {
                            drawWorkLine()
                        })
                    })
            }
        })

        centerLinePaint = Paint()
        centerLinePaint!!.setStyle(Paint.Style.STROKE)
        centerLinePaint!!.setColor(Color.RED)
        centerLinePaint!!.setStrokeWidth(3f)

        centerShadowPaint = Paint()
        centerShadowPaint!!.setStyle(Paint.Style.FILL)
        centerShadowPaint!!.setColor(Color.parseColor("#88000000"))
        centerShadowPath = Path()
    }

    /**
     * 通过定位数据运行
     */
    fun run(locationStatus: LocationStatus) {
        if (!isStartWork) return
        this.locationStatus = locationStatus
        currentMoveX = locationStatus.x
        currentMoveY = locationStatus.y
        currentLat = locationStatus.latitude
        currentLon = locationStatus.longitude
        bitmapProviderUtils!!.createBitmapByXy(currentMoveX, currentMoveY, Cons.TILE_LENGHT.toFloat(), Cons.workWidth, Cons.mapAccuracy)
        if (tractors != null) {
            moveTractors(currentMoveX, currentMoveY, tractors!!)
        }
    }

    private fun moveTractors(x: Double, y: Double, tractors: View) {
        activity.runOnUiThread(Runnable {
            //调整拖拉机角度
            if (MathUtils.calculateDictance(x, y, oldX, oldY) > 1) {
                val calculateAzimuth = MathUtils.calculateAngle(oldX, oldY, x, y)
                oldX = x
                oldY = y
                tractors.rotation = calculateAzimuth.toFloat()
            }
            //移动拖拉机
            mapview.moveMarker(x.toInt().toDouble(), y.toInt().toDouble(), tractors)
            //请求mapview重新获取当前屏幕的方块
            mapview.requestGetTile()
            //请求bitmaplayout重绘
            mapview.requestRender()
            //当前拖动值+上拖拉机移动的值
            mapview.dragImmediately(x.toInt(), y.toInt())
            //刷新工作区域x
            refreshWorkArea()
        })
    }


    fun createMarker() {
        tractors = createMarkerIv(R.drawable.tractors)
        ivMarkerA = createMarkerIv(R.mipmap.a_point)
        ivMarkerB = createMarkerIv(R.mipmap.b_point)
        addMarker(0.0, 0.0, tractors!!)
    }


    fun markerA(): Boolean {
        if (locationStatus == null) return false
        addMarker(0)
        return true
    }

    fun markerB(): Boolean {
        if (locationStatus == null) return false
        addMarker(1)
        return true
    }

    /**
     * 添加标记
     */
    private fun addMarker(aorb: Int) {
        if (aorb == 0) {
            addMarker(currentMoveX, currentMoveY, ivMarkerA!!)
            datumLine[0] = currentMoveX
            datumLine[1] = currentMoveY
            aLat = currentLat
            aLon = currentLon
            isMarkerA = true
        } else {
            addMarker(currentMoveX, currentMoveY, ivMarkerB!!)
            datumLine[2] = currentMoveX
            datumLine[3] = currentMoveY
            bLat = currentLat
            bLon = currentLon
            sendAb2Blue()
            drawWorkLine()
            isMarkerB = true
        }
    }


    var aa = false
    /**
     * 实时刷新5条线
     */
    private fun refreshWorkArea() {
        //车与A点距离
        var AC = MathUtils.calculateDictance(currentMoveX, currentMoveY, datumLine[0], datumLine[1])
        //车与B点距离
        var BC = MathUtils.calculateDictance(currentMoveX, currentMoveY, datumLine[2], datumLine[3])
        //A点与B点距离
        var AB = MathUtils.calculateDictance(datumLine[0], datumLine[1], datumLine[2], datumLine[3])
        if (isMarkerA && !isMarkerB) {
            if (mapStateCallback != null) {
                mapStateCallback!!.onAbDistance(AC)
            }
        }
        if (isMarkerB) {
            //abc组成三角形 ab为底边  三角地形的高度
            var triangleHeight = MathUtils.calculateTriangleHeight(AC, BC, AB)
            //当前位于第几根线 TODO
            var centerLinePosition = Math.round(triangleHeight / Cons.workWidthPixel).toInt()
            //正向直线你
            val right = MathUtils.offsetLine(datumLine, centerLinePosition * Cons.workWidthPixel)
            //负向直线
            val left = MathUtils.offsetLine(datumLine, -centerLinePosition * Cons.workWidthPixel)
            //车与正向直线A点的距离
            val rightDistance = MathUtils.calculateDictance(currentMoveX, currentMoveY, right[0], right[1])
            //车与负向直线A点的距离
            val leftDistance = MathUtils.calculateDictance(currentMoveX, currentMoveY, left[0], left[1])
            //若是车与负向直线A点的距离小于车与正向直线的距离,说明车处于AB直线的左边,为负向直线
            if (leftDistance < rightDistance) {
                centerLinePosition = -centerLinePosition
            }
            //以当前线位置为中心,左右各画两条线
            if (this.centerLinePosition != centerLinePosition) {
                this.centerLinePosition = centerLinePosition
                drawWorkLine()
            }

            //始终保持车在线的中心
            var moveLine = 0.0
            if (AC > BC) {
                //在一条直线  A与车
                if (AC - BC == AB) {
                    moveLine = AC - AB / 2
                } else {
                    var cosa = (Math.pow(AB, 2.0) + Math.pow(AC, 2.0) - Math.pow(BC, 2.0)) / (2 * AB * AC)
                    moveLine = AC * cosa - AB / 2
                }
                aa = true

            } else if (BC > AC) {
                //AB线与车在一条直线B与车
                if (BC - AC == AB) {
                    moveLine = BC - AB / 2
                } else {
                    var cosa = (Math.pow(AB, 2.0) + Math.pow(BC, 2.0) - Math.pow(AC, 2.0)) / (2 * AB * BC)
                    moveLine = -(BC * cosa - AB / 2)
                }
                aa = false
            }

            if (Math.abs(moveLine - this.workLineEndsOffset) > ScaleHelper.unscale(50.0, scale)) {
                this.workLineEndsOffset = moveLine
                drawWorkLine()
            }

            refreshAb(centerLinePosition)
        }
    }

    /**
     * 刷新AB经纬度  AB点应该是距离本车最近的线条
     */
    private fun refreshAb(centerLinePosition: Int) {
        var offset = getOffset(centerLinePosition)
        val offsetLine = MathUtils.offsetLine(datumLine, centerLinePosition * workWidthPixel + offset)
        //将A点XY坐标点转换为经纬度
        val latLon1 = testData.xy2LatLon(offsetLine[0], offsetLine[1])
        //将B点XY坐标点转换为经纬度
        val latLon2 = testData.xy2LatLon(offsetLine[2], offsetLine[3])

        if (latLon1 == null || latLon2 == null) return

        //车与当前所处线路上A点距离
        var AC = MathUtils.calculateDictance(currentMoveX, currentMoveY, offsetLine[0], offsetLine[1])
        //车与当前所处线路上B点距离
        var BC = MathUtils.calculateDictance(currentMoveX, currentMoveY, offsetLine[2], offsetLine[3])

        //如果距离A点较近则按顺序添加
        if (AC < BC) {
            aLat = latLon1[0]
            aLon = latLon1[1]

            bLat = latLon2[0]
            bLon = latLon2[1]
        }
        //如果距离B点较近AB点翻转
        if (BC < AC) {
            aLat = latLon2[0]
            aLon = latLon2[1]

            bLat = latLon1[0]
            bLon = latLon1[1]
        }
    }

    /**
     * 发送AB点
     */
    fun sendAb2Blue() {
        DataManager.writeCmd(
                Cmds.SETWORKS,
                "5",
                "0",
                "0",
                aLat.toString(),
                aLon.toString(),
                bLat.toString(),
                bLon.toString())
    }

    /**
     * 绘制五条表示工作区域的直线
     */
    private fun drawWorkLine() {
        mapview.clearPath()
        for (i in centerLinePosition - halfOfTheLine..centerLinePosition + halfOfTheLine) {
            //平移线条
            var lines = MathUtils.offsetLine(datumLine, workWidthPixel * i)
            //左右单边延长的长度
            val max = Math.min(mapview.height, mapview.width) / 4 * 3

            //延长线条
            lines = MathUtils.lengthenLine(lines, ScaleHelper.unscale(max.toDouble(), scale))

            //垂直移动线条
            lines = MathUtils.moveLine(lines, workLineEndsOffset)

            var offset = getOffset(i)

            //偏移线条
            lines = MathUtils.offsetLine(lines, offset)

            val linePath = MathUtils.doubleArray2Path(lines)

            mapview.drawPath(linePath, if (i == centerLinePosition) centerLinePaint else null)

            //添加阴影部分
            if (i == centerLinePosition) {
                centerShadowPath.reset()
                val offsetLine1 = MathUtils.offsetLine(lines, -workWidthPixel / 2)
                val offsetLine2 = MathUtils.offsetLine(lines, workWidthPixel / 2)
                centerShadowPath.moveTo(offsetLine1[0].toFloat(), offsetLine1[1].toFloat())
                centerShadowPath.lineTo(offsetLine1[2].toFloat(), offsetLine1[3].toFloat())
                centerShadowPath.lineTo(offsetLine2[2].toFloat(), offsetLine2[3].toFloat())
                centerShadowPath.lineTo(offsetLine2[0].toFloat(), offsetLine2[1].toFloat())
                centerShadowPath.close()
                mapview.drawPath(centerShadowPath, centerShadowPaint)
            }
        }
    }

    /**
     * 获取当前位置的线条的偏移量
     */
    private fun getOffset(linePosition: Int): Float {
        var offset = 0f
        if (linePosition > 0) {
            for (forwardOffsetLine in forwardOffsetLines) {
                if (forwardOffsetLine.key <= linePosition) {
                    offset += forwardOffsetLine.value
                }
            }
        } else if (linePosition < 0) {
            for (reverseOffsetLine in reverseOffsetLines) {
                if (reverseOffsetLine.key >= linePosition) {
                    offset += reverseOffsetLine.value
                }
            }
        }
        return offset
    }

    /**
     * 设置A-B基准线的偏移量
     */
    fun setOffset(offset: Float) {
        drawWorkLine()
        if (centerLinePosition < 0) {
            reverseOffsetLines.put(centerLinePosition, offset)
        } else if (centerLinePosition > 0) {
            forwardOffsetLines.put(centerLinePosition, offset)
        } else {
            datumLine = MathUtils.offsetLine(datumLine, offset)
            mapview.moveMarker(datumLine[0], datumLine[1], ivMarkerA!!)
            mapview.moveMarker(datumLine[2], datumLine[3], ivMarkerB!!)
        }
        drawWorkLine()
    }

    /**
     * 添加标记
     */
    private fun addMarker(x: Double, y: Double, view: View) {
        mapview.addMarker(x, y, view)
        tractors!!.bringToFront()
    }

    /**
     * 创建作为标记的Imageview
     */
    private fun createMarkerIv(res: Int): View {
        var resIv = ImageView(activity)
        resIv.setImageResource(res)
        return resIv
    }

    /**
     * 停止工作
     */
    fun stopWork(measuredTime: Long) {
        isStartWork = false
        bitmapProviderUtils.saveBitmap()
        val workHistory = WorkHistory(measuredTime, datumLine[0].toFloat(), datumLine[1].toFloat(), datumLine[2].toFloat(), datumLine[3].toFloat())
        GreenDaoUtils.daoSession.workHistoryDao.insertOrReplace(workHistory)
    }

    /**
     * 开始工作
     */
    fun startWork(measuredTime: Long) {
        isStartWork = true
        isMarkerA = false
        isMarkerB = false
        bitmapProviderUtils.measuredTime = measuredTime
        bitmapProviderUtils.clearBitmapTileCache()
        mapview.removeMarker(ivMarkerA!!)
        mapview.removeMarker(ivMarkerB!!)
        mapview.clearPath()
        mapview.clearTile()
        mapview.requestRender()
    }

    var mapStateCallback: MapStateCallback? = null

    interface MapStateCallback {
        fun onAbDistance(distance: Double)
    }
}