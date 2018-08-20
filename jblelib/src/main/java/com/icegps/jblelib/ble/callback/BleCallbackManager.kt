package com.icegps.jblelib.ble.callback

import android.os.Handler
import android.os.Message
import com.icegps.jblelib.ble.BleHelper
import com.icegps.jblelib.ble.data.BleDevice
import com.icegps.jblelib.ble.failmsg.FailMsg
import com.icegps.jblelib.ble.utils.BleLog
import java.util.concurrent.CopyOnWriteArrayList

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

    private lateinit var callbackHandler: CallbackHandler
    private lateinit var callbackBles: CopyOnWriteArrayList<BleStatusCallback>


    fun init() {
        callbackBles = CopyOnWriteArrayList()
        callbackHandler = CallbackHandler()
    }

    fun addBleCallback(bleStatusCallback: BleStatusCallback) {
        if (bleStatusCallback != null && !callbackBles.contains(bleStatusCallback)) {
            callbackBles.add(bleStatusCallback)
        }
    }

    fun removeBleCallback(bleStatusCallback: BleStatusCallback) {
        if (bleStatusCallback != null && callbackBles.contains(bleStatusCallback)) {
            callbackBles.remove(bleStatusCallback)
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
                    for (callback in callbackBles) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is BleDevice) {
                                callback.onScanRequest(any)
                            }
                        }
                    }
                }

                SCAN_FAIL -> {
                    for (callback in callbackBles) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is FailMsg) {
                                callback.onScanFail(any)
                            }
                        }
                    }
                }

                CONNECT_SUCCESS -> {
                    for (callback in callbackBles) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is BleDevice) {
                                callback.onConnectSuccess(any)
                            }
                        }

                    }
                }

                CONNECT_FAIL -> {
                    for (callback in callbackBles) {
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
                    for (callback in callbackBles) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is Boolean) {
                                callback.onDisConnect(any)
                            }
                        }
                    }
                }

                NOTIFY_SUCCESS -> {
                    for (callback in callbackBles) {
                        callback.onNotifySuccess()
                    }
                }

                NOTIFY_FAIL -> {
                    for (callback in callbackBles) {
                        callback.onNotifyFail()
                    }
                }

                WRITE_SUCCESS -> {
                    BleHelper.semaphore.release()
                    for (callback in callbackBles) {
                        callback.onWriteSuccess()
                    }
                }

                WRITE_FAIL -> {
                    BleHelper.semaphore.release()
                    for (callback in callbackBles) {
                        val any = msg.obj as Array<Any>
                        for (any in any) {
                            if (any is FailMsg) {
                                callback.onWriteFail(any)
                            }
                        }
                    }
                }

                START_CONNECT -> {
                    for (callback in callbackBles) {
                        callback.onStartConnect()
                    }
                }
                OPEN_BLE -> {
                    for (callback in callbackBles) {
                        callback.onOpenBle()
                    }
                }
                CLOSE_BLE -> {
                    for (callback in callbackBles) {
                        callback.onCloseBle()
                    }
                }

                NOTIFY_DATA -> {
                    val any = msg.obj as Array<Any>
                    for (any in any) {
                        if (any is ByteArray) {
                            for (callback in callbackBles) {
                                callback.onNotifyData(any)
                            }
                        }
                    }
                }

            }
        }
    }

}