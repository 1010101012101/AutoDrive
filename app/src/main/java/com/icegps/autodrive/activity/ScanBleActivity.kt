package com.icegps.autodrive.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.DeviceAdapter
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.Sp
import com.tbruyelle.rxpermissions.RxPermissions
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import j.m.jblelib.ble.data.BleDevice
import j.m.jblelib.ble.failmsg.FailMsg
import kotlinx.android.synthetic.main.activity_scan_ble.*


class ScanBleActivity : BaseActivity() {
    var devices = ArrayList<BleDevice>()
    var mac = ""
    lateinit var deviceAdapter: DeviceAdapter

    companion object {
        val MAC = "MAC"

    }

    override fun onDestroy() {
        super.onDestroy()
        BleHelper.removeBleCallback(bleStatusCallbackImpl)
    }

    override fun layout(): Int {
        return R.layout.activity_scan_ble
    }
    override fun init() {
        val mac = Sp.getInstance().getString(MAC)
        if (!TextUtils.isEmpty(mac)) {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra(MAC, mac)
            startActivity(intent)
            finish()
            return
        }

        RxPermissions(this).request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe({
                    if (it) {
                        if (TextUtils.isEmpty(mac)) {
                            BleHelper.addBleCallback(bleStatusCallbackImpl)
                            Thread(Runnable { BleHelper.startScan() }).start()
                        }
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
                this@ScanBleActivity.mac = devices.get(position).mac!!
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
            Sp.getInstance().putString(MAC, mac)
            finish()
        }

        override fun onConnectFail(failMsg: FailMsg) {
            super.onConnectFail(failMsg)
            Init.showToast("连接失败")
        }
    }

}
