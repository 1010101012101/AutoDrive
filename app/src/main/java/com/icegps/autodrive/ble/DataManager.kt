package com.icegps.autodrive.ble

import com.icegps.autodrive.utils.CheckUtils.Companion.addCheckSum
import com.icegps.jblelib.ble.BleHelper
import com.icegps.serialportlib.serialport.SerialPortHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import timber.log.Timber

/**
 * Created by 111 on 2018/4/27.
 */
object DataManager {

    private var dataSource: DataSource? = null
        set(value) {
            field = value

        }

    enum class DataSource {
        BLE, SERIAL_PORT, WIFI
    }

    init {
    }

    fun openNotifyData(dataSource: DataSource) {
        this.dataSource = dataSource
        when (dataSource) {
            DataSource.BLE -> {
                //接收蓝牙的数据放进去解析
                BleHelper.addBleCallback(object : BleStatusCallBackImpl() {
                    override fun onNotifyData(byteArray: ByteArray) {
                        super.onNotifyData(byteArray)
                        ParseDataManager.parseData(byteArray)
                    }
                })
            }
            DataSource.SERIAL_PORT -> {
                //接受串口的数据放进去解析
                SerialPortHelper.addCallback(object : SerialPortHelper.SerialPortStateCallbackImpl() {
                    override fun onNotifyData(byteArray: ByteArray) {
                        super.onNotifyData(byteArray)
                        ParseDataManager.parseData(byteArray)
                    }
                })

            }
        }
    }


    private fun write(cmds: String) {
        val addCheckSum = addCheckSum(cmds)
        Timber.e(addCheckSum)
        when (dataSource) {
            DataSource.BLE -> {
                BleHelper.addWriteRequest(addCheckSum.toByteArray())
            }
            DataSource.SERIAL_PORT -> {
                SerialPortHelper.sendSerialPort(addCheckSum.toByteArray())
            }
        }
    }

    fun writeCmd(type: String, vararg values: String) {
        var cmd = StringBuilder().append(type)
        for (value in values) {
            cmd.append(",")
            cmd.append(value)
        }
        write(cmd.toString())
    }
}