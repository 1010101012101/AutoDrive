package com.icegps.autodrive.activity

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.WorkModeAdapter
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.map.MapUtils
import com.icegps.autodrive.map.data.TestData
import com.icegps.autodrive.map.utils.ThreadPool
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.StringUtils
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import j.m.jblelib.ble.data.LocationStatus
import j.m.jblelib.ble.failmsg.FailMsg
import kotlinx.android.synthetic.main.activity_map_view.*
import kotlinx.android.synthetic.main.dialog_new_work.view.*
import kotlinx.android.synthetic.main.pop_work_mode.view.*
import java.util.*


class MapActivity : BaseActivity() {
    private var selMode = 0
    private var testData: TestData? = null
    private var bleIvAnima: ObjectAnimator? = null
    private var isMarkBPoint = false
    private var mac = ""
    private var autoOrManual = 0

    private var dialog: AlertDialog? = null
    private var workModeContentView: View? = null
    private var popWorkModeView: View? = null
    private var workModeAdapter: WorkModeAdapter? = null
    private var popupWindow: PopupWindow? = null
    private var width = 3f
    private lateinit var mapUtils: MapUtils

    override fun layout(): Int {
        return R.layout.activity_map_view
    }

    override fun init() {
        testData= TestData()
        mapUtils = MapUtils(map_view, activity, testData!!)
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


        /**
         * 处理解析数据
         */
        OnlyBle.register()
        testData()
        workWidths = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        workModeContentView = View.inflate(this, R.layout.dialog_new_work, null)
        popWorkModeView = View.inflate(this, R.layout.pop_work_mode, null)

        dialog = AlertDialog.Builder(this).create()
        dialog!!.setView(workModeContentView)

        if (workWidths != null) {
            workModeAdapter = WorkModeAdapter(R.layout.item_work_mode, workWidths, this)
            popWorkModeView!!.recyclerView.layoutManager = LinearLayoutManager(this)
            popWorkModeView!!.recyclerView.adapter = workModeAdapter
        }

        popupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.setBackgroundDrawable(ColorDrawable())
        popupWindow!!.contentView = popWorkModeView

    }

    private fun initBleCmd() {
        BleWriteHelper.writeCmd(Cmds.CONNECT)
        BleWriteHelper.writeCmd(Cmds.GETSENSORV, "0", "0")
        BleWriteHelper.writeCmd(Cmds.GETCONTROLV, "0", "0")
        BleWriteHelper.writeCmd(Cmds.GETGPSDATA, "1", "500")
        BleWriteHelper.writeCmd(Cmds.GETWORKV, "1", "500")
    }

    private fun testData() {
        testData!!.getTestData {
            mapUtils.run(locationStatus = it)
        }
    }

    override fun setListener() {
        iv_wheel.setOnClickListener {
            BleWriteHelper.writeCmd(Cmds.AUTO, if (autoOrManual == 1) "0" else "1")
        }

        OnlyBle.addOnParseCompleteCallback(onParseComplete)

        BleHelper.addBleCallback(bleStatusCallBackImpl)


        tv_signal.setOnClickListener { startActivity(Intent(activity, SatelliteSignalActivity::class.java)) }

        tv_setting.setOnClickListener { startActivity(Intent(activity, SystemSettingActivity::class.java)) }

        tv_work.setOnClickListener {}

        tv_start_or_stop_work.setOnClickListener {
            when (tv_start_or_stop_work.isSelected) {
                false -> {
                    dialog!!.show()
                }
                true -> {
                    ll_ab_point.visibility = View.VISIBLE
                    isMarkBPoint = false
                    mapUtils.stopWork()
                    setIvABEnab(false)
                    tv_start_or_stop_work.isSelected = false
                    tv_start_or_stop_work.setText(getString(R.string.new_work))
                    setTvHintStr(getString(R.string.new_work_set_ab))
                }
            }
        }
        workModeContentView!!.tv_sel_work_mode.setOnClickListener {
            popupWindow!!.showAsDropDown(workModeContentView!!.tv_sel_work_mode)
        }
        workModeContentView!!.iv_sel_work_mode.setOnClickListener {
            popupWindow!!.showAsDropDown(workModeContentView!!.tv_sel_work_mode)
        }

        workModeContentView!!.tv_cancel.setOnClickListener {
            dialog!!.dismiss()
        }
        workModeContentView!!.tv_confirm.setOnClickListener {
            if (width == 0f) {
                Init.showToast("当前作业宽度为0,请先进入设置界面设置作业宽度")
                dialog!!.dismiss()
                return@setOnClickListener
            }
            dialog!!.dismiss()
            ll_ab_point.visibility = View.VISIBLE
            setIvABEnab(true)
            mapUtils.startWork()
            tv_start_or_stop_work.isSelected = true
            tv_start_or_stop_work.setText(getString(R.string.stop))
            setTvHintStr(getString(R.string.please_set_a))

        }

        workModeAdapter!!.setOnItemClickListener { adapter, view, position ->
            workModeContentView!!.tv_sel_work_mode.setText(workWidths!!.get(position).workName)
            workModeContentView!!.tv_width.setText(workWidths!!.get(position).workWidth.toString())
            popupWindow!!.dismiss()
            width = workWidths!!.get(position).workWidth
            selMode = position
        }


        iv_set_a_point.setOnClickListener {
            if (!iv_set_a_point.isSelected){
                mapUtils.markerA()
                iv_set_a_point.isSelected=true
            }
        }

        iv_set_b_point.setOnClickListener {
            if (!iv_set_b_point.isSelected){
                mapUtils.markerB()
                iv_set_b_point.isSelected=true
            }
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
                            //TODO 设置偏移
                        }
                    })
                    .show()
        }


    }

    private var workWidths: List<WorkWidth>? = null
    override fun onResume() {
        super.onResume()
        workWidths = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        workModeAdapter!!.notifyDataSetChanged()
        workModeContentView?.tv_width?.setText(workWidths!!.get(selMode)!!.workWidth.toString())
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
    }

    override fun onPause() {
        super.onPause()
        BleWriteHelper.writeCmd(Cmds.AUTO, "0")
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
            if (bleIvAnima != null) {
                bleIvAnima!!.cancel()
            }
        }

        override fun onConnectFail(failMsg: FailMsg) {
            super.onConnectFail(failMsg)
            setIvBleStatus(0)
            BleHelper.connect(mac)
            if (bleIvAnima != null) {
                bleIvAnima!!.cancel()
            }
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

            if (d > 10) {
                BleWriteHelper.writeCmd(Cmds.AUTO, "0")
            }
            mapUtils.run(locationStatus)
        }
    }
//    /**
//     * @see MapHelper.addCallback
//     */
//    private var mapCallbackImpl = object : MapCallbackImpl() {
//
//        override fun onAbDistance(distance: Double) {
//            runOnUiThread {
//                if (!isMarkBPoint) {
//                    setTvHintStr("当前距离A点的直线距离为" + StringUtils.setAccuracy(distance / 2, 2) + "米")
//                }
//            }
//        }
//    }

    private var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            autoOrManual(parseDataBean!!.workStatus.workMode)
            if (autoOrManual != parseDataBean!!.workStatus.workMode) {
                autoOrManual = parseDataBean!!.workStatus.workMode
                iv_wheel.drawable.setLevel(autoOrManual)
            }
            val offset = parseDataBean.workStatus.distanceOffset
            if (offset != null) {
                tv_offset.setText(offset.toInt().toString())
                setOffsetIv(offset)
            }
        }
    }


    private fun setOffsetIv(offset: Float) {
        iv_left_offset_green.visibility = if (offset < 0 && offset >= -2) View.VISIBLE else View.INVISIBLE
        iv_left_offset_yellow.visibility = if (offset < -2 && offset >= -4) View.VISIBLE else View.INVISIBLE
        iv_left_offset_red.visibility = if (offset < -4) View.VISIBLE else View.INVISIBLE

        iv_right_offset_green.visibility = if (offset > 0 && offset <= 2) View.VISIBLE else View.INVISIBLE
        iv_right_offset_yellow.visibility = if (offset > 2 && offset <= 4) View.VISIBLE else View.INVISIBLE
        iv_right_offset_red.visibility = if (offset > 4) View.VISIBLE else View.INVISIBLE
    }


}