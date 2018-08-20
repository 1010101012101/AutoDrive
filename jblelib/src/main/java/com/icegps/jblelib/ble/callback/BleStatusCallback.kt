package com.icegps.jblelib.ble.callback

import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg

/**
 * Created by 111 on 2018/4/11.
 */
interface BleStatusCallback {
    fun onScanRequest(bleDevice: BleDevice)
    fun onScanFail(failMsg: FailMsg)
    fun onConnectSuccess(bleDevice: BleDevice)
    fun onConnectFail(failMsg: FailMsg)
    fun onDisConnect(isManualDis: Boolean)
    fun onNotifySuccess()
    fun onNotifyFail()
    fun onNotifyData(byteArray: ByteArray)
    fun onWriteSuccess()
    fun onWriteFail(failMsg: FailMsg)
    fun onStartConnect();
    fun onOpenBle()
    fun onCloseBle()
}