package com.icegps.autodrive.activity

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.widget.ImageView
import com.icegps.autodrive.R
import com.icegps.autodrive.R.mipmap.tractors
import com.icegps.autodrive.map.data.TestData
import com.icegps.autodrive.map2.BitmapProviderUtils
import com.icegps.autodrive.map2.utils.MathUtils
import kotlinx.android.synthetic.main.activity_mapview.*
import rx.schedulers.Schedulers.test

class MapViewActivity : BaseActivity() {
    private var tileLength = 200f
    private var workWidth = 3F
    private var mapAccuracy = 0.1f
    private var bitmapProviderUtils: BitmapProviderUtils? = null
    private var oldX: Double = 0.0
    private var oldY: Double = 0.0
    private var tractors: ImageView? = null

    override fun layout(): Int {
        return R.layout.activity_mapview
    }

    override fun init() {
        initView()
        initMapView()
        addTractorsToMapView()
        test()
    }

    private fun initView() {
        tractors = ImageView(activity)
    }

    private fun addTractorsToMapView() {
        tractors!!.setImageResource(R.mipmap.tractors)
        map_view.addMarker(0.0, 0.0, tractors!!)
    }

    private fun initMapView() {

        bitmapProviderUtils = BitmapProviderUtils(tileLength.toFloat(), workWidth, mapAccuracy)
        map_view.setSize(20000, 20000)
        map_view.bitmapProvider = bitmapProviderUtils
    }

    private fun test(){
        TestData().getTestData {
            bitmapProviderUtils!!.createBitmapByXy(it.x, it.y)
            moveTractors(it.x, it.y, tractors!!)
        }
    }


    private fun moveTractors(x: Double, y: Double, tractors: ImageView) {
        runOnUiThread(Runnable {
            //调整拖拉机角度
            if (MathUtils.calculateDictance(x, -y, oldX, oldY) > 1) {
                val calculateAzimuth = MathUtils.calculateAzimuth(x, -y, oldX, oldY)
                oldX = x
                oldY = -y
                tractors.rotation = calculateAzimuth.toFloat()
            }
            //移动拖拉机
            map_view.moveMarker(x, y, tractors)
            //请求mapview重新获取当前屏幕的方块
            map_view.requestGetTile()
            //请求bitmaplayout重绘
            map_view.requestBitmapLayoutRender()
        })
    }


    override fun setListener() {

    }

}