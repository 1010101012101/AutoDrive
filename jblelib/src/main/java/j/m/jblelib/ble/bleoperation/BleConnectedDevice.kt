package j.m.jblelib.ble.bleoperation

import j.m.jblelib.ble.data.BleDevice
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