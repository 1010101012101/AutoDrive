package j.m.jblelib.ble.callback

import android.location.Location
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.MainThread
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.data.BleDevice
import j.m.jblelib.ble.data.LocationStatus
import j.m.jblelib.ble.data.SatelliteData
import j.m.jblelib.ble.failmsg.FailMsg
import j.m.jblelib.ble.utils.BleLog
import java.util.concurrent.CopyOnWriteArrayList
import javax.security.auth.callback.CallbackHandler

/**
 * Created by 111 on 2018/4/12.
 */
object BleCallbackManager {
    val SCANR_REQUEST = 1
    val SCAN_FAIL = 2
    val CONNECT_SUCCESS = 3
    val CONNECT_FAIL = 4
    val DISCONNECT = 5
    val NOTIFY_SUCCESS = 6
    val NOTIFY_FAIL = 7
    val WRITE_SUCCESS = 8
    val WRITE_FAIL = 9
    val START_CONNECT = 10
    val OPEN_BLE = 11
    val CLOSE_BLE = 12
    val NOTIFY_DATA = 13
    val HEX = 14
    val ASCII = 15
    val LOCATION_DATA = 16
    val SATELLITE_DATA = 17

    private lateinit var callbackHandler: CallbackHandler
    private lateinit var callbacks: CopyOnWriteArrayList<BleStatusCallback>


    fun init() {
        callbacks = CopyOnWriteArrayList()
        callbackHandler = CallbackHandler()
    }

    fun addBleCallback(bleStatusCallback: BleStatusCallback) {
        if (bleStatusCallback != null && !callbacks.contains(bleStatusCallback)) {
            callbacks.add(bleStatusCallback)
        }
    }

    fun removeBleCallback(bleStatusCallback: BleStatusCallback) {
        if (bleStatusCallback != null && callbacks.contains(bleStatusCallback)) {
            callbacks.remove(bleStatusCallback)
        }
    }

    fun callback(what: Int, vararg any: Any?) {
        val msg = callbackHandler.obtainMessage()
        msg.what = what
        if (any != null) {
            msg.obj = any
        }
        for (any in any) {
            if (any is FailMsg) {
                BleLog.d(this, any.msg)
            }
        }
        callbackHandler.sendMessage(msg)
    }

    class CallbackHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                SCANR_REQUEST -> {
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is BleDevice) {
                                callback.onScanRequest(any)
                            }
                        }
                    }
                }

                SCAN_FAIL -> {
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is FailMsg) {
                                callback.onScanFail(any)
                            }
                        }
                    }
                }

                CONNECT_SUCCESS -> {
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is BleDevice) {
                                callback.onConnectSuccess(any)
                            }
                        }

                    }
                }

                CONNECT_FAIL -> {
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is FailMsg) {
                                callback.onConnectFail(any)
                            }
                        }
                    }
                }

                DISCONNECT -> {
                    BleHelper.semaphore.release()
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is Boolean) {
                                callback.onDisConnect(any)
                            }
                        }
                    }
                }

                NOTIFY_SUCCESS -> {
                    for (callback in callbacks) {
                        callback.onNotifySuccess()
                    }
                }

                NOTIFY_FAIL -> {
                    for (callback in callbacks) {
                        callback.onNotifyFail()
                    }
                }

                WRITE_SUCCESS -> {
                    BleHelper.semaphore.release()
                    for (callback in callbacks) {
                        callback.onWriteSuccess()
                    }
                }

                WRITE_FAIL -> {
                    BleHelper.semaphore.release()
                    for (callback in callbacks) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is FailMsg) {
                                callback.onWriteFail(any)
                            }
                        }
                    }
                }

                START_CONNECT -> {
                    for (callback in callbacks) {
                        callback.onStartConnect()
                    }
                }
                OPEN_BLE -> {
                    for (callback in callbacks) {
                        callback.onOpenBle()
                    }
                }
                CLOSE_BLE -> {
                    for (callback in callbacks) {
                        callback.onCloseBle()
                    }
                }

                NOTIFY_DATA -> {
                    val any = msg.obj as Array<Any>
                    for (any in any) {
                        if (any is ByteArray) {
                            for (callback in callbacks) {
                                callback.onNotifyData(any)
                            }
                        }
                    }
                }

                HEX -> {
                    val any = msg.obj as Array<Any>
                    for (any in any) {
                        if (any is ByteArray) {
                            for (callback in callbacks) {
                                callback.onHex(any)
                            }
                        }
                    }
                }

                ASCII -> {
                    val any = msg.obj as Array<Any>
                    for (any in any) {
                        if (any is String) {
                            for (callback in callbacks) {
                                callback.onAscii(any)
                            }
                        }
                    }
                }

                LOCATION_DATA -> {
                    val any = msg.obj as Array<Any>
                    for (any in any) {
                        if (any is LocationStatus) {
                            for (callback in callbacks) {
                                callback.onLocationData(any)
                            }
                        }
                    }
                }
                SATELLITE_DATA -> {
                    val any = msg.obj as Array<Any>
                    var satellites: ArrayList<SatelliteData>? = null
                    var satelliteType: Byte? = null


                    for (any in any) {
                        if (any is ArrayList<*>) {
                            satellites = any as ArrayList<SatelliteData>
                        }
                        if (any is Byte) {
                            satelliteType = any
                        }
                    }

                    for (callback in callbacks) {
                        callback.onSatelliteData(satellites!!, satelliteType!!)
                    }
                }

            }
        }
    }

}