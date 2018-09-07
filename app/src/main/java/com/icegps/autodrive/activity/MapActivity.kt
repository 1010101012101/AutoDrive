package com.icegps.autodrive.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.WorkModeAdapter
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.ParseDataManager
import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.constant.Cons
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.map.MapUtils
import com.icegps.autodrive.map.data.TestData
import com.icegps.autodrive.map.utils.ThreadPool
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.StringUtils
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.data.LocationStatus
import com.icegps.mapview.GestureDetectorView
import com.icegps.serialportlib.serialport.SerialPortHelper
import com.tbruyelle.rxpermissions.RxPermissions
import kotlinx.android.synthetic.main.activity_map_view.*
import kotlinx.android.synthetic.main.dialog_new_work.view.*
import kotlinx.android.synthetic.main.pop_work_mode.view.*


class MapActivity : BaseActivity() {
    private var selMode = 0
    private var testData: TestData? = null
    private var autoOrManual = 0
    private var dialog: AlertDialog? = null
    private var workModeContentView: View? = null
    private var popWorkModeView: View? = null
    private var workModeAdapter: WorkModeAdapter? = null
    private var popupWindow: PopupWindow? = null
    private lateinit var mapUtils: MapUtils
    private var workWidths: List<WorkWidth>? = null
    private var measuredTime: Long? = null
    override fun layout(): Int {
        return R.layout.activity_map_view
    }

    override fun init() {
        testData = TestData()
        mapUtils = MapUtils(map_view, activity, testData!!)
        testData()
        initUi()
        initBleCmd()
        initSetOffsetPopupWindow()
        requestPermissions()
    }

    /**
     * 权限请求
     */
    private fun requestPermissions() {
        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({
                    if (!it) {
                        AlertDialog.Builder(activity)
                                .setMessage("禁用读写权限将无法保存作业历史!")
                                .setPositiveButton("确定", null)
                                .show()
                    }

                })
    }

    /**
     * 设置偏移的pop
     */
    fun initSetOffsetPopupWindow() {
        workWidths = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        workModeContentView = View.inflate(this, R.layout.dialog_new_work, null)
        popWorkModeView = View.inflate(this, R.layout.pop_work_mode, null)

        dialog = AlertDialog.Builder(this).create()
        dialog!!.setView(workModeContentView)

        if (workWidths != null) {
            workModeAdapter = WorkModeAdapter(R.layout.item_work_mode, workWidths, this)
            popWorkModeView!!.recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
            popWorkModeView!!.recyclerView.adapter = workModeAdapter
        }

        popupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.setBackgroundDrawable(ColorDrawable())
        popupWindow!!.contentView = popWorkModeView
    }

    /**
     * 设置比例尺的值
     */
    fun setRulerValue(scale: Float) {
        tv_ruler.setText(Math.round(Cons.mapAccuracy / scale * 200f).toString() + "M")
    }

    /**
     * 首次进入发送命令
     */
    private fun initBleCmd() {
        DataManager.writeCmd(Cmds.CONNECT)
        DataManager.writeCmd(Cmds.GETSENSORV, "0", "0")
        DataManager.writeCmd(Cmds.GETCONTROLV, "0", "0")
        DataManager.writeCmd(Cmds.GETGPSDATA, "1", "500")
        DataManager.writeCmd(Cmds.GETWORKV, "1", "500")
    }

    /**
     * 测试数据
     */
    private fun testData() {
        testData!!.getTestData {
            mapUtils.run(locationStatus = it)
        }
    }

    /**
     * 设置监听
     */
    override fun setListener() {
        mapUtils.mapStateCallback = mapStateCallback

        ParseDataManager.addDataCallback(dataCallBackImpl)

        tv_measure.setOnClickListener { startActivity(Intent(activity, MeasureMapActivity::class.java))  }

        iv_wheel.setOnClickListener {
            DataManager.writeCmd(Cmds.AUTO, if (autoOrManual == 1) "0" else "1")
        }
        tv_signal.setOnClickListener { startActivity(Intent(activity, SatelliteSignalActivity::class.java)) }

        tv_setting.setOnClickListener { startActivity(Intent(activity, SystemSettingActivity::class.java)) }

        tv_work_history.setOnClickListener { startActivity(Intent(activity, WorkHistoryListActivity::class.java)) }

        tv_start_or_stop_work.setOnClickListener {
            when (tv_start_or_stop_work.isSelected) {
                false -> {
                    dialog!!.show()
                }
                true -> {
                    ll_ab_point.visibility = View.VISIBLE
                    mapUtils.stopWork(measuredTime!!)
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
            if (Cons.workWidth == 0f) {
                Init.showToast("当前作业宽度为0,请先进入设置界面设置作业宽度")
                dialog!!.dismiss()
                return@setOnClickListener
            }
            dialog!!.dismiss()
            ll_ab_point.visibility = View.VISIBLE
            setIvABEnab(true)
            measuredTime = System.currentTimeMillis()
            mapUtils.startWork(measuredTime!!)
            tv_start_or_stop_work.isSelected = true
            tv_start_or_stop_work.setText(getString(R.string.stop))
            setTvHintStr(getString(R.string.please_set_a))
        }

        workModeAdapter!!.setOnItemClickListener { adapter, view, position ->
            workModeContentView!!.tv_sel_work_mode.setText(workWidths!!.get(position).workName)
            workModeContentView!!.tv_width.setText(workWidths!!.get(position).workWidth.toString())
            popupWindow!!.dismiss()
            Cons.workWidth = workWidths!!.get(position).workWidth
            selMode = position
        }


        iv_set_a_point.setOnClickListener {
            if (!iv_set_a_point.isSelected) {
                if (mapUtils.markerA()) {
                    iv_set_a_point.isSelected = true
                }
            }
        }

        iv_set_b_point.setOnClickListener {
            if (!iv_set_b_point.isSelected) {
                if (iv_set_a_point.isSelected) {
                    iv_set_b_point.isSelected = mapUtils.markerB()
                    ll_ab_point.visibility = View.GONE
                } else {
                    Init.showToast("请先设置A点")
                }
            }
        }


        iv_set_offset.setOnClickListener {
            val et = EditText(activity)
            et.setHintTextColor(Color.GRAY)
            et.setHint("单位CM,以A-B为基准,偏左为负,偏右为正")
            et.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            val builder = AlertDialog.Builder(activity)
            builder.setView(et)
                    .setMessage("设置偏移值")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            try {
                                val offset = et.text.toString().toFloat()
                                mapUtils.setOffset(offset / 10)
                            } catch (e: NumberFormatException) {

                            }

                        }
                    })
                    .show()
        }

        iv_wheel.setOnLongClickListener {
            map_view.dragTo(0, 0)
            return@setOnLongClickListener true
        }

    }

    /**
     * 初始化ui
     */
    fun initUi() {
        setRulerValue(map_view.scale)
        setIvABEnab(false)
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
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_gray)
                else
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_a_b_black))

        iv_set_b_point.setBackground(
                if (enab)
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_gray)
                else
                    ContextCompat.getDrawable(activity, R.drawable.sel_btn_a_b_black))

        if (!enab) {
            iv_set_a_point.isSelected = false
            iv_set_b_point.isSelected = false
        }
    }

    /**
     * 设置提示
     */
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
        ParseDataManager.removeDataCallback(dataCallBackImpl)
        BleHelper.disconnect()
        map_view.removeListener(mapListenerImpl)
        SerialPortHelper.closeSerialPort()
        if (testData != null)
            testData!!.startOrStop(true)
    }

    override fun onResume() {
        super.onResume()
        workWidths = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        workModeAdapter!!.notifyDataSetChanged()
        workModeContentView?.tv_width?.setText(workWidths!!.get(selMode)!!.workWidth.toString())
        map_view.addListener(mapListenerImpl)
    }

    override fun onPause() {
        super.onPause()
        DataManager.writeCmd(Cmds.AUTO, "0")
    }


    /**
     * @see MapHelper.addCallback
     */
    private var mapStateCallback = object : MapUtils.MapStateCallback {

        override fun onAbDistance(distance: Double) {
            runOnUiThread {
                setTvHintStr("当前距离A点的直线距离为" + StringUtils.setAccuracy(distance / 10, 2) + "米")
            }
        }
    }

    private var dataCallBackImpl = object : ParseDataManager.DataCallBackImpl() {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            super.onComplete(parseDataBean, type)
            autoOrManual(parseDataBean!!.workStatus.workMode)
            if (autoOrManual != parseDataBean!!.workStatus.workMode) {
                autoOrManual = parseDataBean!!.workStatus.workMode
                iv_wheel.drawable.setLevel(autoOrManual)
            }
            if (autoOrManual == 1) {
                mapUtils.sendAb2Blue()
            }
            val offset = parseDataBean.workStatus.distanceOffset
            if (offset != null) {
                tv_offset.setText(offset.toInt().toString())
                setOffsetIv(offset)
            }
        }

        override fun onLocationData(locationStatus: LocationStatus?) {
            super.onLocationData(locationStatus)
            when (locationStatus!!.status) {
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
                DataManager.writeCmd(Cmds.AUTO, "0")
            }
            mapUtils.run(locationStatus)
        }
    }

    /**
     * 根据偏移值来设置偏移的图片
     */
    private fun setOffsetIv(offset: Float) {
        iv_left_offset_green.visibility = if (offset < 0 && offset >= -2) View.VISIBLE else View.INVISIBLE
        iv_left_offset_yellow.visibility = if (offset < -2 && offset >= -4) View.VISIBLE else View.INVISIBLE
        iv_left_offset_red.visibility = if (offset < -4) View.VISIBLE else View.INVISIBLE

        iv_right_offset_green.visibility = if (offset > 0 && offset <= 2) View.VISIBLE else View.INVISIBLE
        iv_right_offset_yellow.visibility = if (offset > 2 && offset <= 4) View.VISIBLE else View.INVISIBLE
        iv_right_offset_red.visibility = if (offset > 4) View.VISIBLE else View.INVISIBLE
    }


    override fun onBackPressed() {
        val alert = AlertDialog.Builder(activity)
        alert.setMessage("退出应用?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                    }
                }).show()
    }

    private var oldScale = 0f
    private var mapListenerImpl = object : GestureDetectorView.ListenerImpl() {
        override fun onScaling(scale: Float) {
            super.onScaling(scale)
            if (oldScale != scale) {
                setRulerValue(scale)
                oldScale = scale
            }
        }
    }

}