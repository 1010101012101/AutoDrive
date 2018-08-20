package com.icegps.jblelib.ble.bleoperation

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.callback.BleCallbackManager
import com.icegps.jblelib.ble.failmsg.FailMsg
import java.util.*

/**
 * Created by 111 on 2018/4/16.
 */
object BleNotify {
    private val NOTIFY_INHERENT_UUID = "00002902-0000-1000-8000-00805f9b34fb"// 固定UUID

    fun enabledNotify(boolean: Boolean) {
        val firstConnectBleDevice = BleConnectedDevice.getFirstConnectBleDevice()

        if (!BleHelper.bleIsOpen()) {
            BleCallbackManager.callback(BleCallbackManager.NOTIFY_FAIL, FailMsg("Bluetooth is not turned on"))
            return

        }

        if (firstConnectBleDevice == null || firstConnectBleDevice.gatt == null) {
            BleCallbackManager.callback(BleCallbackManager.NOTIFY_FAIL, FailMsg("Device is not connected"))
        }

        val service = firstConnectBleDevice!!.gatt!!.getService(UUID.fromString(BleHelper.serviceUUID))

        if (service == null) {
            BleCallbackManager.callback(BleCallbackManager.WRITE_FAIL, FailMsg("getService error"))
            return
        }

        val characteristic = service.getCharacteristic(UUID.fromString(BleHelper.notifyUUID))

        if (characteristic != null && characteristic.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY == 0) {
            BleCallbackManager.callback(BleCallbackManager.NOTIFY_FAIL, FailMsg(BleHelper.notifyUUID + "-->this characteristic not support notify!"))
            return
        }

        firstConnectBleDevice.gatt!!.setCharacteristicNotification(characteristic, boolean)
        val descriptor = characteristic.getDescriptor(UUID.fromString(NOTIFY_INHERENT_UUID))
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        firstConnectBleDevice.gatt!!.writeDescriptor(descriptor)
    }
}