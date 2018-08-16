package com.icegps.autodrive.activity

import android.content.Intent
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.utils.Init
import com.icegps.jblelib.ble.BleHelper
import com.icegps.serialportlib.serialport.SerialPortHelper
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {


    override fun layout(): Int {
        return R.layout.activity_splash
    }

    override fun init() {
        SerialPortHelper.addCallback(object : SerialPortHelper.SerialPortStateCallbackImpl() {
            override fun onConnectSuccess() {
                super.onConnectSuccess()
                startActivity(Intent(activity, MapActivity::class.java))
                DataManager.openNotifyData(DataManager.DataSource.SERIAL_PORT)
                btn_serial_port.isEnabled = true
                finish()
            }

            override fun onConnectFail(msg: String?) {
                super.onConnectFail(msg)
                btn_serial_port.isEnabled = true
                Init.showToast("打开串口失败,请确认设备此是否支持串口")
            }
        })
    }

    override fun setListener() {
        btn_ble.setOnClickListener {
            if (BleHelper.isSupportBle()) {
                btn_ble.isEnabled = false
                startActivity(Intent(activity, BleConnectActivity::class.java))
                DataManager.openNotifyData(DataManager.DataSource.BLE)
                finish()
            } else {
                Init.showToast("此设备不支持蓝牙连接")
            }
        }

        btn_serial_port.setOnClickListener {
            btn_serial_port.isEnabled = false
            SerialPortHelper.openSerialPort(115200)
        }
    }

}