package com.icegps.jblelib.ble.bleoperation

import com.icegps.jblelib.ble.data.BleDevice
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by 111 on 2018/4/17.
 */
object BleConnectedDevice {

    var connectBleDevices = ConcurrentHashMap<String, BleDevice>()
        @Synchronized
        get() {
            return field
        }

    fun getFirstConnectBleDevice(): BleDevice? {
        for (key in connectBleDevices.keys) {
            return connectBleDevices.get(key)
        }
        return null
    }


}