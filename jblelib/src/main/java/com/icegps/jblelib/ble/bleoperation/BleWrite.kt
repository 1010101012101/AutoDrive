package com.icegps.jblelib.ble.bleoperation

import android.bluetooth.BluetoothGattCharacteristic
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.callback.BleCallbackManager
import com.icegps.jblelib.ble.failmsg.FailMsg
import java.util.*

/**
 * Created by 111 on 2018/4/16.
 */
object BleWrite {
    /**
     * 写入数据
     */
    fun write(byteArray: ByteArray) {
        synchronized(this) {
            val firstConnectBleDevice = BleConnectedDevice.getFirstConnectBleDevice()

            if (!BleHelper.bleIsOpen()) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("Bluetooth is not turned on"))
                return
            }

            if (firstConnectBleDevice == null || firstConnectBleDevice.gatt == null) {
                    BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("Device is not connected"))
                return
            }

            if (byteArray == null) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("data can't null"))
                return
            }

            val service = firstConnectBleDevice.gatt!!.getService(UUID.fromString(BleHelper.serviceUUID))

            if (service == null) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("getService error"))
                return
            }
            val characteristic = service!!.getCharacteristic(UUID.fromString(BleHelper.writeUUID))

            if (characteristic == null || characteristic.properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg(BleHelper.writeUUID + "-->this characteristic not support write!"))
                return
            }

            val value = characteristic.setValue(byteArray)
            if (!value) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("setValue error"))
                return
            }

            if (!firstConnectBleDevice.gatt!!.writeCharacteristic(characteristic)) {
                BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("writeCharacteristic error"))
                return
            }
        }
    }
}