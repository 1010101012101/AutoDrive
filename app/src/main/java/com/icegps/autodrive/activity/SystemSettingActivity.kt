package com.icegps.autodrive.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.SystemSettingAdapter
import com.tencent.bugly.proguard.ac
import kotlinx.android.synthetic.main.activity_system_setting.*
import kotlinx.android.synthetic.main.toobar.*

class SystemSettingActivity : BaseActivity() {
    private var settingMenu = ArrayList<String>()
    private lateinit var systemSettingAdapter:SystemSettingAdapter
    override fun layout():Int{
        return R.layout.activity_system_setting
    }

    override fun init(){
        tv_title.setText(getString(R.string.system_setting))
        settingMenu.add(getString(R.string.work_width))
        settingMenu.add(getString(R.string.signal_resource))
        settingMenu.add(getString(R.string.clear_data))
        settingMenu.add(getString(R.string.about_this_device))
        settingMenu.add(getString(R.string.work_parameter))

        systemSettingAdapter  = SystemSettingAdapter(R.layout.item_system_setting, settingMenu, activity)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager=gridLayoutManager
        recyclerView.adapter=systemSettingAdapter
    }

    override fun setListener(){
        iv_left.setOnClickListener { finish() }
        systemSettingAdapter.setOnItemClickListener { adapter, view, position ->
            val get = settingMenu.get(position)
            when(get){
                "工作参数"->{
                    startActivity(Intent(activity,WorkSettingActivity::class.java))
                }
                "作业宽度"->{
                    startActivity(Intent(activity,WorkWidthActivity::class.java))
                }
            }
        }

    }
}