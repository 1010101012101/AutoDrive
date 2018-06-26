package com.icegps.autodrive.activity

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.map.MapHelper
import com.icegps.autodrive.map.utils.ThreadPool
import com.icegps.autodrive.map.data.TestData
import com.icegps.autodrive.map.listener.MapCallbackImpl
import com.icegps.autodrive.utils.StringUtils
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import j.m.jblelib.ble.data.LocationStatus
import j.m.jblelib.ble.failmsg.FailMsg
import kotlinx.android.synthetic.main.activity_map_view.*
import java.util.*
import android.text.InputType
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.ble.ParseDataBean
import com.icegps.autodrive.utils.Init
import kotlinx.android.synthetic.main.activity_add_work_width.*


class MapActivity : BaseActivity() {
    override fun layout(): Int {
        return R.layout.activity_map_view
    }

    private lateinit var mapHelper: MapHelper
    private var testData: TestData? = null
    private var bleIvAnima: ObjectAnimator? = null
    private var isMarkBPoint = false
    private var mac = ""
    private var autoOrManual = 0


    override fun init() {
        setIvABEnab(false)
        if (BleHelper.isConnect()) setIvBleStatus(1)
        if (intent.extras == null) {
            initBleCmd()
        } else {
            if (intent.extras.getString(ScanBleActivity.MAC) == null) {
                initBleCmd()
            } else {
                mac = intent.extras.getString(ScanBleActivity.MAC)
                BleHelper.connect(mac)
            }
        }

        mapHelper = MapHelper(this)
        map_view.helper = mapHelper

        /**
         * 处理解析数据
         */
        OnlyBle.register()
        testData()
    }

    private fun initBleCmd() {
        BleWriteHelper.writeCmd(Cmds.CONNECT)
        BleWriteHelper.writeCmd(Cmds.GETSENSORV, "0", "0")
        BleWriteHelper.writeCmd(Cmds.GETCONTROLV, "0", "0")
        BleWriteHelper.writeCmd(Cmds.GETGPSDATA, "1", "500")
        BleWriteHelper.writeCmd(Cmds.GETWORKV, "1", "500")
    }

    private fun testData() {
        testData = TestData()
        testData!!.getTestData {
            mapHelper.run(locationStatus = it)
        }
    }

    override fun setListener() {
        iv_wheel.setOnClickListener {
            BleWriteHelper.writeCmd(Cmds.SETWORKS, "1", if (autoOrManual == 1) "0" else "1")
        }

        OnlyBle.addOnParseCompleteCallback(onParseComplete)

        BleHelper.addBleCallback(bleStatusCallBackImpl)

        mapHelper.addCallback(mapCallbackImpl)

        tv_view_change.setOnClickListener {
            var et = EditText(this)
            et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val builder = AlertDialog.Builder(this)
            builder.setMessage("设置农具宽度")
                    .setView(et)
                    .setPositiveButton("确定", { dialogInterface: DialogInterface, i: Int ->
                        val workWidth = et.text.toString().toFloat()
                        mapHelper.workWidth = workWidth
                        Init.showToast("农具宽度设置成功!")
                    })
                    .setNegativeButton("取消", { dialogInterface: DialogInterface, i: Int -> })
                    .show()
        }

        tv_signal.setOnClickListener { startActivity(Intent(activity, SatelliteSignalActivity::class.java)) }

        tv_setting.setOnClickListener { startActivity(Intent(activity, SystemSettingActivity::class.java)) }
        tv_work.setOnClickListener {

        }

        tv_start_or_stop_work.setOnClickListener {
            when (tv_start_or_stop_work.isSelected) {
                false -> {   //开始工作
                    ll_ab_point.visibility = View.VISIBLE
                    setIvABEnab(true)
                    mapHelper.startWork()
                    tv_start_or_stop_work.isSelected = true
                    tv_start_or_stop_work.setText(getString(R.string.stop))
                    setTvHintStr(getString(R.string.please_set_a))
                }
                true -> {  //停止工作
                    ll_ab_point.visibility = View.VISIBLE
                    isMarkBPoint = false
                    mapHelper.stopWork()
                    setIvABEnab(false)
                    tv_start_or_stop_work.isSelected = false
                    tv_start_or_stop_work.setText(getString(R.string.new_work))
                    setTvHintStr(getString(R.string.new_work_set_ab))
                }
            }
        }

        iv_set_a_point.setOnClickListener {
            markAPoint()
        }

        iv_set_b_point.setOnClickListener {
            markBPoint()
            ll_ab_point.visibility = View.GONE
        }

        iv_set_a_point.setOnLongClickListener {
            unMarkAPoint()
            true
        }

        iv_set_b_point.setOnLongClickListener {
            unMarkBPoint()
            true
        }

        iv_set_offset.setOnClickListener {
            val et = EditText(activity)
            et.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            val builder = AlertDialog.Builder(activity)
            builder.setView(et)
                    .setMessage("设置偏移值")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            mapHelper.setAbLineOffset(et.text.toString().toFloat())
                        }
                    })
                    .show()
        }
        iv_set_offset.setOnLongClickListener {
            mapHelper.inversionAb()
            return@setOnLongClickListener true
        }

    }

    /**
     * 设置蓝牙图标的状态
     */
    private fun setIvBleStatus(status: Int) {
        iv_ble_status.drawable.setLevel(status)
    }

    /**
     * 设置AB button 是否可以点击
     */
    private fun setIvABEnab(enab: Boolean) {
        iv_set_a_point.isEnabled = enab
        iv_set_a_point.isEnabled = enab
        iv_set_b_point.isEnabled = enab

        iv_set_a_point.setBackground(
                if (enab)
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_green)
                else
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_a_b_black))

        iv_set_b_point.setBackground(
                if (enab)
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_green)
                else
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_a_b_black))

        if (!enab) {
            iv_set_a_point.isSelected = false
            iv_set_b_point.isSelected = false
        }
    }


    /**
     * 设置A点
     */
    private fun markAPoint() {
        if (!iv_set_a_point.isSelected)
            changeTvASel(mapHelper.markAPoint())

    }

    /**
     * 设置B点
     */
    private fun markBPoint() {
        if (!iv_set_b_point.isSelected) {
            isMarkBPoint = mapHelper.markBPoint()
            changeTvBSel(isMarkBPoint)
            if (isMarkBPoint) {
                setTvHintStr("A点距B点的直线距离距离为" + StringUtils.setAccuracy(mapHelper.distance / 2, 2) + "米")
            }
        }
    }

    /**
     * 取消A点
     */
    private fun unMarkAPoint() {
        changeTvASel(false)
        changeTvBSel(false)
        isMarkBPoint = false
        mapHelper.unMarkAPoint()
    }

    /**
     * 取消B点
     */
    private fun unMarkBPoint() {
        changeTvBSel(false)
        isMarkBPoint = false
        mapHelper.unMarkBPoint()
    }

    /**
     * 改变A button 的 Selected
     */
    private fun changeTvASel(sel: Boolean) {
        iv_set_a_point.isSelected = sel

    }

    /**
     * 改变B button 的 Selected
     */
    private fun changeTvBSel(sel: Boolean) {
        iv_set_b_point.isSelected = sel

    }


    /**
     * 蓝牙连接中的闪烁动画
     */
    private fun startBleIvAnima() {
        if (bleIvAnima == null) {
            bleIvAnima = ObjectAnimator.ofFloat(iv_ble_status, "Alpha", 1.0f, 0f)
            bleIvAnima!!.repeatCount = ObjectAnimator.RESTART
            bleIvAnima!!.repeatCount = ObjectAnimator.INFINITE
            bleIvAnima!!.duration = 500
        }
        bleIvAnima!!.start()
    }

    private fun setTvHintStr(hint: String) {
        tv_hint.setText(hint)
    }

    /**
     *  若是手动 隐藏偏移显示AB   反之则相反
     */
    private fun autoOrManual(autoOrManual: Int) {
        when (autoOrManual) {
            0 -> {
                iv_set_offset.visibility = View.GONE
            }
            1 -> {
                iv_set_offset.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (testData != null)
            testData!!.startOrStop(true)
        BleHelper.removeBleCallback(bleStatusCallBackImpl)
        BleHelper.disconnect()
        OnlyBle.unregister()
        OnlyBle.removeParseCompleteCallback(onParseComplete)
        mapHelper.removeCallback(mapCallbackImpl)
    }


    /**
     * 蓝牙数据监听
     */
    private var bleStatusCallBackImpl = object : BleStatusCallBackImpl() {
        override fun onStartConnect() {
            super.onStartConnect()
            startBleIvAnima()
        }

        override fun onNotifySuccess() {
            super.onNotifySuccess()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    initBleCmd()
                }

            }, 1000)
            setIvBleStatus(1)
            Init.showToast(getString(R.string.ble_connect_success))
            if (bleIvAnima != null) {
                bleIvAnima!!.cancel()
            }
            iv_ble_status.alpha = 1.0f
        }

        override fun onDisConnect(isManualDis: Boolean) {
            super.onDisConnect(isManualDis)
            setIvBleStatus(0)
            BleHelper.connect(mac)
        }

        override fun onConnectFail(failMsg: FailMsg) {
            super.onConnectFail(failMsg)
            setIvBleStatus(0)
            BleHelper.connect(mac)
        }

        override fun onLocationData(locationStatus: LocationStatus) {
            super.onLocationData(locationStatus)
            when (locationStatus.status) {
                0 -> {
                    //未定
                    tv_location_status.setText("未定")
                    iv_location_status.drawable.setLevel(0)
                }
                1 -> {
                    //单点
                    tv_location_status.setText("单点")
                    iv_location_status.drawable.setLevel(1)
                }
                4 -> {
                    //固定
                    tv_location_status.setText("固定")
                    iv_location_status.drawable.setLevel(4)
                }
                else -> {
                    //浮动
                    tv_location_status.setText("浮动")
                    iv_location_status.drawable.setLevel(5)
                }
            }
            tv_delay.setText(locationStatus.delay.toString())
            tv_satellite_sum.setText(locationStatus.satelliteSum.toString())
            var speed = locationStatus.speed
            val d = speed / 1000 * 3600
            tv_speed.setText(StringUtils.setAccuracy(d, 1))
            ThreadPool.getInstance().executeFixed(Runnable {
                locationStatus.color = Color.parseColor("#770000FF")
            })

            mapHelper.run(locationStatus)
        }
    }
    /**
     * @see MapHelper.addCallback
     */
    private var mapCallbackImpl = object : MapCallbackImpl() {

        override fun onAbDistance(distance: Double) {
            runOnUiThread {
                if (!isMarkBPoint) {
                    setTvHintStr("当前距离A点的直线距离为" + StringUtils.setAccuracy(distance / 2, 2) + "米")
                }
            }
        }
    }


    private var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            autoOrManual(parseDataBean!!.workStatus.workMode)
            if (autoOrManual != parseDataBean!!.workStatus.workMode) {
                autoOrManual = parseDataBean!!.workStatus.workMode
                iv_wheel.drawable.setLevel(autoOrManual)
            }
        }

    }


}