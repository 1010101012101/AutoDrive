package com.icegps.jblelib.ble.bleoperation

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.os.Build
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.callback.BleCallbackManager
import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg
import com.icegps.jblelib.ble.utils.BleLog

/**
 * Created by 111 on 2018/4/12.
 */
object BleScan {
    private lateinit var scanCallback: ScanCallback
    private lateinit var leScanCallback: BluetoothAdapter.LeScanCallback

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * 搜索回调
             */
            scanCallback = @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            object : ScanCallback() {
                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    BleCallbackManager.callback(BleCallbackManager.SCAN_FAIL, FailMsg("scan error"))
                }

                override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
                    super.onScanResult(callbackType, result)

                    if (result == null || result.device == null) return
                    val device = result.device

                    val bleDevice = BleDevice(device?.name, device?.address, result.rssi, result.scanRecord.bytes)
                    BleLog.d(BleScan, if (device?.name == null) "null" else device.name, if (device?.address == null) "null" else device.address)
                    BleCallbackManager.callback(BleCallbackManager.SCANR_REQUEST, bleDevice)
                }

            }
        } else {
            /**
             * 搜索回调
             */
            leScanCallback = object : BluetoothAdapter.LeScanCallback {
                override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                    val bleDevice = BleDevice(device?.name, device?.address, rssi, scanRecord)
                    BleLog.d(BleScan, if (device?.name == null) "null" else device.name, if (device?.address == null) "null" else device.address)
                    BleCallbackManager.callback(BleCallbackManager.SCANR_REQUEST, bleDevice)
                }
            }
        }
    }

    /**
     * 开始搜索
     */
    fun startScan() {
        val adapter = BleHelper.bleManager.adapter
        if (!BleHelper.bleIsOpen()) {
            BleCallbackManager.callback(BleCallbackManager.SCAN_FAIL, FailMsg("Bluetooth is not turned on"))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adapter.bluetoothLeScanner.startScan(scanCallback)
        } else {
            stopScan()
            adapter.startLeScan(leScanCallback)
        }
    }


    /**
     * 停止搜索
     */
    fun stopScan() {
        val adapter = BleHelper.bleManager.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (adapter != null && adapter.bluetoothLeScanner != null) {
                adapter.bluetoothLeScanner.stopScan(scanCallback)
            }
        } else {
            if (adapter != null) {
                adapter.stopLeScan(leScanCallback)
            }
        }
    }

}