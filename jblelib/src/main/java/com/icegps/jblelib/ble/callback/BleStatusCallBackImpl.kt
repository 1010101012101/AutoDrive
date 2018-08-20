package j.m.jblelib.ble.BleStatusCallBackImpl

import com.icegps.jblelib.ble.callback.BleStatusCallback
import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg

/**
 * Created by 111 on 2018/4/16.
 */
open class BleStatusCallBackImpl : BleStatusCallback {


    override fun onScanRequest(bleDevice: BleDevice) {
    }

    override fun onScanFail(failMsg: FailMsg) {
    }

    override fun onConnectSuccess(bleDevice: BleDevice) {
    }

    override fun onConnectFail(failMsg: FailMsg) {
    }

    override fun onDisConnect(isManualDis: Boolean) {
    }

    override fun onNotifySuccess() {
    }

    override fun onNotifyFail() {
    }

    override fun onNotifyData(byteArray: ByteArray) {
    }

    override fun onWriteSuccess() {
    }

    override fun onWriteFail(failMsg: FailMsg) {
    }

    override fun onStartConnect() {
    }

    override fun onOpenBle() {
    }

    override fun onCloseBle() {
    }

}