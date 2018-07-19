package com.icegps.autodrive.activity

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.SystemSettingAdapter
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.Sp
import kotlinx.android.synthetic.main.activity_system_setting.*
import kotlinx.android.synthetic.main.toobar.*

class SystemSettingActivity : BaseActivity() {
    private var settingMenu = ArrayList<String>()
    private lateinit var systemSettingAdapter: SystemSettingAdapter
    override fun layout(): Int {
        return R.layout.activity_system_setting
    }

    override fun init() {
        tv_title.setText(getString(R.string.system_setting))
        settingMenu.add(getString(R.string.radio))
        settingMenu.add(getString(R.string.work_width))
        settingMenu.add(getString(R.string.signal_resource))
        settingMenu.add(getString(R.string.clear_data))
        settingMenu.add(getString(R.string.about_this_device))
        settingMenu.add(getString(R.string.factory_calibration))
        settingMenu.add(getString(R.string.control_parameter_calibration))
        settingMenu.add(getString(R.string.ins_calibration))

        systemSettingAdapter = SystemSettingAdapter(R.layout.item_system_setting, settingMenu, activity)
        val gridLayoutManager = GridLayoutManager(activity, 4)
        recyclerView.layoutManager = gridLayoutManager as LinearLayoutManager
        recyclerView.adapter = systemSettingAdapter
    }

    override fun setListener() {
        iv_left.setOnClickListener { finish() }
        systemSettingAdapter.setOnItemClickListener { adapter, view, position ->
            val get = settingMenu.get(position)
            when (get) {
                "数传频道" -> {
                    startActivity(Intent(activity, RadioActivity::class.java))
                }
                "作业宽度" -> {
                    startActivity(Intent(activity, WorkWidthActivity::class.java))
                }
                "清除数据" -> {
                    Sp.getInstance().putString(ScanBleActivity.MAC, "")
                    Init.showToast("清除成功,下次进入应用将手动选择设备连接")
                }
                "出厂校准" -> {
                    startActivity(Intent(activity, SensorActivity::class.java))
                }
                "控制参数校准" -> {
                    startActivity(Intent(activity, ParameterDebugActivity::class.java))
                }
                "安装校准" -> {
                    startActivity(Intent(activity, InsCalibrationActivity::class.java))
                }
            }
        }

    }
}