package j.m.jblelib.ble.callback

import j.m.jblelib.ble.data.BleDevice
import j.m.jblelib.ble.data.LocationStatus
import j.m.jblelib.ble.data.SatelliteData
import j.m.jblelib.ble.failmsg.FailMsg

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
    fun onHex(bytes: ByteArray?)
    fun onAscii(data: String?)
    fun onLocationData(locationStatus: LocationStatus)
    fun onSatelliteData(satellites: ArrayList<SatelliteData>, satelliteType: Byte)
}