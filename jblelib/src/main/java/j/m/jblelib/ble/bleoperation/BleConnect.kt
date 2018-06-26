package j.m.jblelib.ble.bleoperation

import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.callback.BleCallbackManager
import j.m.jblelib.ble.failmsg.FailMsg
import j.m.jblelib.ble.utils.BleLog


/**
 * Created by 111 on 2018/4/12.
 */

object BleConnect  {
    var isManual = false
    /**
     * 开始连接
     */
    fun connect(mac: String) {
        isManual = false
        if (!BleHelper.bleIsOpen()) {
            BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("Bluetooth is not turned on"))
            return
        }
        if (BleConnectedDevice.connectBleDevices.get(mac) != null) {
            BleLog.d(BleConnect, "This device is connected")
            return
        }
        if (BleConnectedDevice.connectBleDevices.size == 1) {
            for (key in BleConnectedDevice.connectBleDevices.keys) {
                val bleDevice = BleConnectedDevice.connectBleDevices.get(key);
                if (bleDevice != null) {
                    BleLog.d(BleConnect, "Only one device is supported, one device is currently connected-->" + bleDevice.name + ":" + bleDevice.mac)
                    return
                }
            }
        }
        BleLog.d(BleConnect, "startConncet")
        try {
            var remoteDevice = BleHelper.bleManager.adapter.getRemoteDevice(mac)
            remoteDevice.connectGatt(BleHelper.context, false, BleOperationCallback.bluetoothGattCallback)
            BleCallbackManager.callback(BleCallbackManager.START_CONNECT,null)
        }catch (e:IllegalArgumentException){
            BleCallbackManager.callback(BleCallbackManager.CONNECT_FAIL,FailMsg("蓝牙地址无效"))
        }

    }

    /**
     * 开始连接
     */
    fun connect(mac: String,auto:Boolean) {
        isManual = false
        if (!BleHelper.bleIsOpen()) {
            BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("Bluetooth is not turned on"))
            return
        }
        if (BleConnectedDevice.connectBleDevices.get(mac) != null) {
            BleLog.d(BleConnect, "This device is connected")
            return
        }
        if (BleConnectedDevice.connectBleDevices.size == 1) {
            for (key in BleConnectedDevice.connectBleDevices.keys) {
                val bleDevice = BleConnectedDevice.connectBleDevices.get(key);
                if (bleDevice != null) {
                    BleLog.d(BleConnect, "Only one device is supported, one device is currently connected-->" + bleDevice.name + ":" + bleDevice.mac)
                    return
                }
            }
        }
        BleLog.d(BleConnect, "startConncet")

        var remoteDevice = BleHelper.bleManager.adapter.getRemoteDevice(mac)
        remoteDevice.connectGatt(BleHelper.context, auto, BleOperationCallback.bluetoothGattCallback)
        BleCallbackManager.callback(BleCallbackManager.START_CONNECT,null)
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        val firstConnectBleDevice = BleConnectedDevice.getFirstConnectBleDevice()
        if (firstConnectBleDevice == null || firstConnectBleDevice.gatt == null) return
        BleConnectedDevice.getFirstConnectBleDevice()!!.gatt!!.disconnect()
    }


}