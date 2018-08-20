package com.icegps.jblelib.ble.bleoperation

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.callback.BleCallbackManager
import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg
import com.icegps.jblelib.ble.utils.BleLog
import java.util.*

/**
 * Created by 111 on 2018/4/19.
 */
object BleOperationCallback {
    /**
     * 连接回调
     */
    internal val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    BleLog.d(BleConnect,
                            "onConnectSuccess",
                            if (gatt!!.device.name == null) "null" else gatt.device.name,
                            if (gatt.device.address == null) "null" else gatt.device.address)
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            val bleDevice = BleDevice(gatt.device.name, gatt.device.address)
                            bleDevice.gatt = gatt;
                            BleConnectedDevice.connectBleDevices.put(bleDevice.mac!!, bleDevice)
                            BleCallbackManager.callback(BleCallbackManager.CONNECT_SUCCESS, bleDevice)
                            gatt.discoverServices()
                        }
                    }, 1000)

                }

                BluetoothGatt.STATE_DISCONNECTED -> {
                    //如果设备不存在map中就是连接失败  如果存在就是断开连接
                    if (BleConnectedDevice.connectBleDevices.get(gatt!!.device.address) == null) {
                        BleCallbackManager.callback(BleCallbackManager.CONNECT_FAIL, FailMsg("连接失败"))
                        BleLog.d(BleConnect,
                                "onConnectFail",
                                if (gatt.device.name == null) "null" else gatt.device.name,
                                if (gatt.device.address == null) "null" else gatt.device.address)
                    } else {
                        BleCallbackManager.callback(BleCallbackManager.DISCONNECT, BleConnect.isManual)
                        BleConnectedDevice.connectBleDevices.remove(gatt.device.address)
                        BleLog.d(BleConnect,
                                "disconnect",
                                if (gatt.device.name == null) "null" else gatt.device.name,
                                if (gatt.device.address == null) "null" else gatt.device.address)
                    }
                    gatt!!.close()
                }
            }
        }

        /**
         * 开启通知回调
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    BleLog.d(BleConnect, "openNotifySuccess")
                    BleNotify.enabledNotify(true)
                    BleCallbackManager.callback(BleCallbackManager.NOTIFY_SUCCESS, null)

                }

                BluetoothGatt.GATT_FAILURE -> {
                    BleLog.d(BleConnect, "openNotifyFail")
                    BleCallbackManager.callback(BleCallbackManager.NOTIFY_FAIL, null)
                }
            }

        }

        /**
         * 通知数据
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic!!.uuid.toString().equals(BleHelper.notifyUUID.toLowerCase())) {
                val value = characteristic!!.value
                BleCallbackManager.callback(BleCallbackManager.NOTIFY_DATA, value)
//                BleLog.d(BleOperationCallback, String(value))

            }
        }

        /**
         * 写操作回调
         */
        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    BleLog.d(BleConnect, "onWriteSuccess")
                    BleCallbackManager.callback(BleCallbackManager.WRITE_SUCCESS, null)
                }
                BluetoothGatt.GATT_FAILURE -> {
                    BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("onCharacteristicWrite gatt failure"))
                }
            }
        }
    }
}