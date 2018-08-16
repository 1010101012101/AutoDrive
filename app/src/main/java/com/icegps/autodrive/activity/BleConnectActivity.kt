package com.icegps.autodrive.activity

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.DeviceAdapter
import com.icegps.autodrive.utils.Init
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg
import com.tbruyelle.rxpermissions.RxPermissions
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import kotlinx.android.synthetic.main.activity_scan_ble.*


class BleConnectActivity : BaseActivity() {
    var devices = ArrayList<BleDevice>()
    lateinit var deviceAdapter: DeviceAdapter


    override fun onDestroy() {
        super.onDestroy()
        BleHelper.removeBleCallback(bleStatusCallbackImpl)
    }

    override fun layout(): Int {
        return R.layout.activity_scan_ble
    }

    override fun init() {
        RxPermissions(this).request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe({
                    if (it) {
                        BleHelper.addBleCallback(bleStatusCallbackImpl)
                        Thread(Runnable { BleHelper.startScan() }).start()
                    } else {
                        val builder = AlertDialog.Builder(activity)
                        val create = builder.setMessage("检测到权限未允许,无法正常工作,点击确定退出应用")
                                .setPositiveButton("确定", null).create()
                        create.show()
                        create.setOnDismissListener {
                            finish()
                        }
                    }
                })
        deviceAdapter = DeviceAdapter()
        deviceAdapter.devices = devices
        listView.adapter = deviceAdapter

    }

    override fun setListener() {
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                BleHelper.connect(devices.get(position).mac!!)
            }
        })
    }

    var bleStatusCallbackImpl = object : BleStatusCallBackImpl() {
        override fun onScanRequest(bleDevice: BleDevice) {
            super.onScanRequest(bleDevice)
            for (device in devices) {
                if (device.mac.equals(bleDevice.mac)) {
                    return
                }
            }
            devices.add(bleDevice)
            deviceAdapter.notifyDataSetChanged()
        }

        override fun onNotifySuccess() {
            super.onNotifySuccess()
            startActivity(Intent(activity, MapActivity::class.java))
            finish()
        }

        override fun onConnectFail(failMsg: FailMsg) {
            super.onConnectFail(failMsg)
            Init.showToast("连接失败")
        }
    }

}
