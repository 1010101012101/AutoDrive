package com.icegps.jblelib.ble.data

import android.bluetooth.BluetoothGatt

/**
 * Created by 111 on 2018/4/11.
 */
class BleDevice : Comparable<BleDevice> {

    override fun compareTo(other: BleDevice): Int {
        return if (other?.mac!!.equals(mac)) 0 else 1
    }

    constructor(name: String?, mac: String?) {
        this.name = name
        this.mac = mac
    }

    constructor(name: String?, mac: String?, rssi: Int, scanRecord: ByteArray?) {
        this.name = name
        this.mac = mac
        this.rssi = rssi
        this.scanRecord = scanRecord
    }


    var name: String?
    var mac: String?
    var rssi: Int = 100
    var gatt: BluetoothGatt? = null
    var scanRecord: ByteArray? = null

}