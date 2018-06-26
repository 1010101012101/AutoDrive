package com.icegps.autodrive.map

import android.graphics.*
import android.os.SystemClock
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.map.data.ColorRes
import com.icegps.autodrive.map.data.PointMark
import com.icegps.autodrive.map.utils.ThreadPool
import com.icegps.autodrive.utils.DisplayUtils
import com.icegps.autodrive.utils.Init
import j.m.jblelib.ble.BleHelper.context
import j.m.jblelib.ble.data.LocationStatus

class MarkAbPoint(mapHelper: MapHelper) {
    /**
     * AB的偏移距离
     */
    private var offset = 0f
    /**
     * 可能的工作区域
     */
    private var possibleWorkArea: ArrayList<FloatArray>
    private var possibleWorkAreaPaint: Paint
    /**
     * A点至B点线的paint
     */
    private var distanceLinePaint: Paint
    /**
     * Ab点的角度
     */
    private var degree = 0f

    /**
     * Ab的角度距离 摆正需要调整的角度
     */
    private var adjust = 0f
    /**
     * Ab点的位置
     */
    var pointMarkAb: ArrayList<PointMark>
    var finalAPoint: PointMark? = null
    var finalBPoint: PointMark? = null
    var bitmapPaint: Paint

    /**
     * 设置好Ab点之后相对于摆正所需要旋转的角度
     */
    private var rotateDegress = 0f
    /**
     * 旋转动画的时间
     */
    private val rotateAnimaTime = 300
    /**
     *
     */
    private val mapHelper: MapHelper
    /**
     * A点距B点的距离 若没有设置B点则为A点距当前位置的距离
     */
    private var distance = 0.0
        set(value) {
            field = value
            mapHelper.distance = field
        }
    /**
     * 用于避免高亮线以及虚线重复的增删
     */
    private var oldNumberOfIntervals = -1

    private var areaRectPaint: Paint
    var multiple = 1f

    init {
        this.mapHelper = mapHelper

        pointMarkAb = ArrayList()
        possibleWorkArea = ArrayList()
        possibleWorkAreaPaint = Paint()
        possibleWorkAreaPaint.color = Color.RED


        distanceLinePaint = Paint()
        distanceLinePaint.strokeWidth = DisplayUtils.dp2px(context, 2f).toFloat()
        distanceLinePaint.color = ColorRes.distanceLineColor

        bitmapPaint = Paint()

        areaRectPaint = Paint()
        areaRectPaint.color = ColorRes.areaRectColor
    }

    fun draw1(canvas: Canvas) {
        multiple = mapHelper.mapParameter!!.multiple
        canvas.rotate(rotateDegress, 0f, 0f)
    }

    fun draw2(canvas: Canvas) {

        drawPossibleWorkArea(canvas)

        drawAbDistanceLine(canvas)

        drawAbPoint(canvas)
    }


    fun startWork() {
        pointMarkAb.clear()
        possibleWorkArea.clear()

    }

    fun stopWork() {
        oldNumberOfIntervals = -1
        /**
         * 如果AB点都有.说明角度没有放回来   否则...
         */
        if (pointMarkAb.size == 2) {
            smoothRotate(-adjust)
        }
    }

    fun run(locationStatus: LocationStatus) {
        //从设置A点开始一直不停的计算当前与A点的距离 直到设置B点
        if (pointMarkAb.size == 1) {
            distance = distanceCalculate(pointMarkAb.get(0).x, pointMarkAb.get(0).y, locationStatus.x, locationStatus.y)
        }
        /**
         * AB+当前位置组成的三角形  获取三条边长
         */
        if (pointMarkAb.size == 2) {
            val aPoint = pointMarkAb.get(0)
            val bPoint = pointMarkAb.get(1)
            val a = calculateTowPoinsDistance(locationStatus.x, locationStatus.y, aPoint.x, aPoint.y)
            val b = calculateTowPoinsDistance(locationStatus.x, locationStatus.y, bPoint.x, bPoint.y)
            val c = distance

            /**
             * 以AB为底边就算这个三角形的高度  也就是当前位置距离AB线的垂直距离
             */
            val triangleHeight = getTriangleHeight(a, b, c)
            /**x
             * 通过这个距离添加虚线以及高亮线
             */
            addPossibleWorkArea(triangleHeight, locationStatus)
        }
    }

    /**
     * 设置A点
     */
    fun markAPoint(locationStatus: LocationStatus): Boolean {
        if (pointMarkAb.size == 0) {
            return pointAbAddPointMark(locationStatus, PointMark.TYPE_A)

        }
        return false
    }

    /**
     * 设置B点
     */
    fun markBPoint(locationStatus: LocationStatus): Boolean {
        if (pointMarkAb.size == 1) {
            if (pointAbAddPointMark(locationStatus, PointMark.TYPE_B)) {
                sendAb(0f)

                // Ab角度摆正算法
                degree = calculateAbPointDegree(
                        pointMarkAb.get(1).x,
                        pointMarkAb.get(1).y,
                        pointMarkAb.get(0).x,
                        pointMarkAb.get(0).y)

                //角度大于180向右旋转  小于180向左边旋转
                if (degree > 180) {
                    adjust = (360 - degree)
                } else {
                    adjust = -(360 - (360 - degree))
                }
                smoothRotate(adjust)
                return true
            }
        }
        return false
    }


    /**
     * 取消A点  (由于A点没有了, B点也会同时取消)
     */
    fun unMarkAPoint() {
        if (pointMarkAb.size == 2) {
            //保证取消A或者取消B只还原一次角度
            smoothRotate(-adjust)
        }
        pointMarkAb.clear()
        possibleWorkArea.clear()

    }


    /**
     * 取消B点
     */
    fun unMarkBPoint() {
        if (pointMarkAb.size == 2) {
            pointMarkAb.removeAt(1)
            //取消B点的时候将角度归位
            smoothRotate(-adjust)
            possibleWorkArea.clear()
            oldNumberOfIntervals = -1
        }
    }


    /**
     * Ab点反转
     */
    fun inversionAb() {
        pointMarkAb.add(pointMarkAb.removeAt(1))
        pointMarkAb.get(0).type = pointMarkAb.get(0).type + pointMarkAb.get(1).type
        pointMarkAb.get(1).type = pointMarkAb.get(0).type - pointMarkAb.get(1).type
        pointMarkAb.get(0).type = pointMarkAb.get(0).type - pointMarkAb.get(1).type
        mapHelper.requestRefreshUi()
        sendAb(offset)
    }


    fun setAbLineOffset(offsetDistance: Float) {
        if (pointMarkAb.size != 2) {
            Init.showToast(context.getString(R.string.please_set_ab_point))
            return
        }
        oldNumberOfIntervals=-1
        this.offset = offsetDistance

        sendAb(offsetDistance)

        val aPoint = pointMarkAb.get(0)
        val bPoint = pointMarkAb.get(1)

        val offsetLine = offsetLine(floatArrayOf(aPoint.x.toFloat(), aPoint.y.toFloat(), bPoint.x.toFloat(), bPoint.y.toFloat()), actualDistance2ViewDistance(offsetDistance))
        aPoint.x = offsetLine[0].toDouble()
        aPoint.y = offsetLine[1].toDouble()
        bPoint.x = offsetLine[2].toDouble()
        bPoint.y = offsetLine[3].toDouble()

        mapHelper.requestRefreshUi()
    }

    /**
     * 求拖拉机位置与AB线 组成的三角形的高度  AB线为底边线  (如果已经设置了AB点的话)
     */
    fun getTriangleHeight(a: Double, b: Double, c: Double): Double {

        var s = (a + b + c) / 2.0

        s = Math.sqrt(s * (s - a) * (s - b) * (s - c))

        var h = s / (1.0f / 2.0f * c)

        return h
    }

    /**
     * 当前位置距离哪条线比较近(AB点直线开始,以车宽为间隔存在无数条平行线)   添加这条线 与左右两条线
     */
    private fun addPossibleWorkArea(triangleHeight: Double, locationStatus: LocationStatus) {
        if (pointMarkAb.size != 2) return


        /**
         * numberOfIntervals是目前位于第几个间隔  oldNumberOfIntervals的作用就是避免重复的    清空和添加
         */
        var numberOfIntervals = 0
//
        if (triangleHeight * (mapHelper.mapAccuracyCm) % (mapHelper.workWidthCm) > (mapHelper.workWidthCm) / 2) {

            numberOfIntervals = (triangleHeight * mapHelper.mapAccuracyCm / (mapHelper.workWidthCm)).toInt() + 1

        } else {
            numberOfIntervals = (triangleHeight * mapHelper.mapAccuracyCm / (mapHelper.workWidthCm)).toInt()

        }
        val axy_bxy1 = offsetLine(
                floatArrayOf(
                        pointMarkAb.get(0).x.toFloat(),
                        pointMarkAb.get(0).y.toFloat(),
                        pointMarkAb.get(1).x.toFloat(),
                        pointMarkAb.get(1).y.toFloat()),
                actualDistance2ViewDistance(mapHelper.workWidthCm * numberOfIntervals))

        val axy_bxy2 = offsetLine(
                floatArrayOf(
                        pointMarkAb.get(0).x.toFloat(),
                        pointMarkAb.get(0).y.toFloat(),
                        pointMarkAb.get(1).x.toFloat(),
                        pointMarkAb.get(1).y.toFloat()),
                actualDistance2ViewDistance(mapHelper.workWidthCm * -numberOfIntervals))

        val calculateTowPoinsDistance1 = calculateTowPoinsDistance(locationStatus.x, locationStatus.y, axy_bxy1[0].toDouble(), axy_bxy1[1].toDouble())
        val calculateTowPoinsDistance2 = calculateTowPoinsDistance(locationStatus.x, locationStatus.y, axy_bxy2[0].toDouble(), axy_bxy2[1].toDouble())

        if (calculateTowPoinsDistance1 > calculateTowPoinsDistance2) {
            numberOfIntervals = -numberOfIntervals
        }

        if (oldNumberOfIntervals != numberOfIntervals || oldNumberOfIntervals == -1) {

            possibleWorkArea.clear()

            oldNumberOfIntervals = numberOfIntervals
            /**
             *   左右各两条线再加上自身
             */
            for (i in numberOfIntervals - 2..numberOfIntervals + 2) {
                val axy_bxy = offsetLine(
                        floatArrayOf(
                                pointMarkAb.get(0).x.toFloat(),
                                pointMarkAb.get(0).y.toFloat(),
                                pointMarkAb.get(1).x.toFloat(),
                                pointMarkAb.get(1).y.toFloat()),
                        actualDistance2ViewDistance(mapHelper.workWidthCm * i)
                )

                var line = floatArrayOf(
                        axy_bxy[0],
                        axy_bxy[1],
                        axy_bxy[2],
                        axy_bxy[3]
                )
                line = lengthenB(line)
                possibleWorkArea.add(line)
            }
        }
    }


    /**
     * 延长 B 端线
     */
    private fun lengthenB(line: FloatArray): FloatArray {
        var startX = line[0]
        var startY = line[1]
        var stopX = line[2]
        var stopY = line[3]

        val angle1 = Math.PI / 2 - (Math.PI / 2f - Math.atan2(startY - stopY.toDouble(), startX - stopX.toDouble()))
        var x1 = 500 * Math.cos(angle1)
        var y1 = 500 * Math.sin(angle1)
        line[0] += x1.toFloat()
        line[1] += y1.toFloat()


        val angle2 = Math.PI / 2 - (Math.PI / 2f - Math.atan2(stopY - startY.toDouble(), stopX - startX.toDouble()))
        var x2 = 500 * Math.cos(angle2)
        var y2 = 500 * Math.sin(angle2)
        line[2] += x2.toFloat()
        line[3] += y2.toFloat()

        return line
    }


    /**
     * 当前精度是 米/ 像素   设备只接受厘米, 所以在偏移AB点的时候也需改成厘米移动
     */
    fun actualDistance2ViewDistance(distance: Float): Float {
        return distance / (mapHelper.mapAccuracy * 100f)  //厘米级别
    }

    /**
     * 以AB直线为基准   偏移offset之后返回一条新直线
     * */
    private fun offsetLine(line: FloatArray, offset: Float): FloatArray {

        var atan2 = Math.atan2((line[2].toDouble() - line[0].toDouble()), -(line[3].toDouble() - line[1].toDouble()))

        var axy_bxy = FloatArray(4)

        axy_bxy[0] = line[0] + (offset * Math.cos(atan2).toFloat())
        axy_bxy[1] = line[1] + (offset * Math.sin(atan2).toFloat())
        axy_bxy[2] = line[2] + (offset * Math.cos(atan2).toFloat())
        axy_bxy[3] = line[3] + (offset * Math.sin(atan2).toFloat())

        return axy_bxy
    }

    /**
     * 绘制可能的工作区域的线段
     */
    private fun drawPossibleWorkArea(canvas: Canvas) {
        var path = Path()
        for (i in 0 until possibleWorkArea.size) {
            var line = possibleWorkArea.get(i)
            if (i == 2) {
                //如果为中间线,,高亮
                possibleWorkAreaPaint.strokeWidth = DisplayUtils.dp2px(context, 2f).toFloat()
                possibleWorkAreaPaint.setPathEffect(DashPathEffect(floatArrayOf(0f, 0f), 0f))
            } else {
                possibleWorkAreaPaint.strokeWidth = DisplayUtils.dp2px(context, 1f).toFloat()
                possibleWorkAreaPaint.setPathEffect(DashPathEffect(floatArrayOf(3f, 3f), 0f))
            }

            if (i == 1) {
                val line = offsetLine(line, actualDistance2ViewDistance(mapHelper.workWidthCm / 2))
                path.moveTo(line[0] * multiple, line[1] * multiple)
                path.lineTo(line[2] * multiple, line[3] * multiple)
            }
            if (i == 3) {
                val line = offsetLine(line, actualDistance2ViewDistance(-mapHelper.workWidthCm / 2))
                path.lineTo(line[2] * multiple, line[3] * multiple)
                path.lineTo(line[0] * multiple, line[1] * multiple)
            }

            canvas.drawLine(
                    line[0] * multiple,
                    line[1] * multiple,
                    line[2] * multiple,
                    line[3] * multiple,
                    possibleWorkAreaPaint
            )
        }
        canvas.drawPath(path, areaRectPaint)
    }


    /**
     * A点到B点的指示线
     */
    private fun drawAbDistanceLine(canvas: Canvas) {
        if (pointMarkAb.size == 2) {
            canvas.drawLine(
                    (pointMarkAb.get(0).x * multiple).toFloat(),
                    (pointMarkAb.get(0).y * multiple).toFloat(),
                    (pointMarkAb.get(1).x * multiple).toFloat(),
                    (pointMarkAb.get(1).y * multiple).toFloat(),
                    distanceLinePaint)
        }
    }

    /**
     * 绘制Ab点
     */
    private fun drawAbPoint(canvas: Canvas) {
        for (pointMark in pointMarkAb) {
            var markBitmap: Bitmap? = null
            when (pointMark.type) {
                PointMark.TYPE_A -> {
                    markBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.a_point)
                }
                PointMark.TYPE_B -> {
                    markBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.b_point)

                }
            }
            val left = pointMark.x * multiple - markBitmap!!.width / 2
            val top = pointMark.y * multiple - markBitmap!!.height / 2
            canvas.drawBitmap(markBitmap, left.toFloat(), top.toFloat(), bitmapPaint)

        }
    }


    /**
     * 添加A点或B点
     */
    private fun pointAbAddPointMark(locationStatus: LocationStatus, type: Int): Boolean {
        if (locationStatus != null) {
            val pointMark = PointMark(
                    type,
                    locationStatus!!.x,
                    locationStatus!!.y,
                    locationStatus!!.latitude,
                    locationStatus!!.longitude)
            pointMarkAb.add(pointMark)
            return true
        } else {
            Init.showToast(context.getString(R.string.no_loction_data))
            return false
        }
    }


    /**
     * 发送Ab点给设备 TODO
     */
    private fun sendAb(offset: Float) {
        this.offset = offset
        if (pointMarkAb.size == 2) {
            BleWriteHelper.writeCmd(Cmds.SETWORKS,
                    "5",
                    "0",
                    offset.toString(),
                    pointMarkAb.get(0).lat.toString(),
                    pointMarkAb.get(0).lon.toString(),
                    pointMarkAb.get(1).lat.toString(),
                    pointMarkAb.get(1).lon.toString())
        }
    }

    /**
     * 平滑旋转
     */
    private fun smoothRotate(adjust: Float) {
        ThreadPool.getInstance().executeSingle(Runnable {
            for (i in 0 until rotateAnimaTime) {
                rotateDegress += adjust / rotateAnimaTime
                SystemClock.sleep(1)
                mapHelper.requestRefreshUi()
            }
        })
    }

    /**
     *计算 A b 两点的角度
     */
    private fun calculateAbPointDegree(x: Double, y: Double, x1: Double, y1: Double): Float {
        if (x == x1 && y == y1) return 0f
        var degree = Math.toDegrees(Math.atan2((x - x1), -(y - y1))).toFloat()
        if (degree < 0.0) degree += 360f
        return degree
    }


    /**
     * 计算A-B 距离
     */
    private fun distanceCalculate(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        var distance = calculateTowPoinsDistance(x1, y1, x2, y2)
        return distance
    }

    /**
     * 计算两点距离
     */
    private fun calculateTowPoinsDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return Math.sqrt(Math.pow((x1 - x2), 2.0) + Math.pow((y1 - y2), 2.0))
    }
}