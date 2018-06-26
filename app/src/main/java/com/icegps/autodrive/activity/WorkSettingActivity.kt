package com.icegps.autodrive.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText
import com.icegps.autodrive.R
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.Sp
import kotlinx.android.synthetic.main.activity_work_setting.*
import kotlinx.android.synthetic.main.toobar.*

class WorkSettingActivity : BaseActivity() {
    override fun layout(): Int {
        return R.layout.activity_work_setting
    }

    override fun init() {
        tv_title.setText("工作参数")


    }

    override fun setListener() {
        iv_left.setOnClickListener({ finish() })
        tv_sensor_calibration.setOnClickListener({
            val et = EditText(activity)
            val builder = AlertDialog.Builder(activity)
            builder.setView(et)
                    .setMessage("请输入密码")
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val password = et.text.toString()
                            if (TextUtils.isEmpty(password)) {
                                Init.showToast("请输入密码")
                            }
                            if (password.equals("ZDJS2018")) {
                            } else {
                                Init.showToast("密码错误,请重新输入")
                            }

                        }
                    })
                    .setNegativeButton("取消", null)
//                    .show()
            startActivity(Intent(activity, SensorActivity::class.java))

        })

        tv_parameter_calibration.setOnClickListener({
            val et = EditText(activity)
            val builder = AlertDialog.Builder(activity)
            builder.setView(et)
                    .setMessage("请输入密码")
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val password = et.text.toString()
                            if (TextUtils.isEmpty(password)) {
                                Init.showToast("请输入密码")
                            }
                            if (password.equals("ZDJS2018")) {
                            } else {
                                Init.showToast("密码错误,请重新输入")
                            }

                        }
                    })
                    .setNegativeButton("取消", null)
//                    .show()
            startActivity(Intent(activity, ParameterDebugActivity::class.java))

        })

        tv_ins_calibration.setOnClickListener({ startActivity(Intent(activity, InsCalibrationActivity::class.java)) })
        tv_radio.setOnClickListener({ startActivity(Intent(activity, RadioActivity::class.java)) })
        tv_clear_ble.setOnClickListener {
            Sp.getInstance().putString(ScanBleActivity.MAC, "")
            Init.showToast("清除成功,下次进入应用将手动选择设备连接")
        }
    }
}