package j.m.jblelib.ble.BleStatusCallBackImpl

import j.m.jblelib.ble.callback.BleStatusCallback
import j.m.jblelib.ble.data.BleDevice
import j.m.jblelib.ble.data.LocationStatus
import j.m.jblelib.ble.failmsg.FailMsg
import j.m.jblelib.ble.data.SatelliteData

/**
 * Created by 111 on 2018/4/16.
 */
open class BleStatusCallBackImpl : BleStatusCallback {
    override fun onSatelliteData(satellites: ArrayList<SatelliteData>, satelliteType: Byte) {

    }

    override fun onLocationData(locationStatus: LocationStatus) {

    }

    override fun onHex(bytes: ByteArray?) {
    }

    override fun onAscii(data: String?) {
    }

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