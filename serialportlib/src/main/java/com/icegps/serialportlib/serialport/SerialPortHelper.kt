package com.icegps.serialportlib.serialport

import android.util.Log
import android_serialport_api.SerialPort
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


object SerialPortHelper {

    private val TAG = "serial_port_log"
    private val path = "/dev/ttyS1"
    private var serialPortStatus = false //是否打开串口标志
    private var threadStatus: Boolean = false //线程状态，为了安全终止线程

    private var serialPort: SerialPort? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    //这是写了一监听器来监听接收数据
    var onDataReceiveListener: SerialPortStateCallback? = null
    private var serialPortStateCallbacks: CopyOnWriteArrayList<SerialPortStateCallback>

    init {
        serialPortStateCallbacks = CopyOnWriteArrayList()
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    fun openSerialPort(baudrate: Int): SerialPort? {
        try {
            serialPort = SerialPort(File(path), baudrate, 0)
            this.serialPortStatus = true
            threadStatus = false //线程状态

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort!!.inputStream
            outputStream = serialPort!!.outputStream

            ReadThread().start() //开始线程监控是否有数据要接收
            Log.d(TAG, "openSerialPort: 打开串口")
            for (serialPortStateCallback in serialPortStateCallbacks) {
                serialPortStateCallback.onConnectSuccess()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString())
            for (serialPortStateCallback in serialPortStateCallbacks) {
                    serialPortStateCallback.onConnectFail(e.message)
            }
            return serialPort
        }

        return serialPort
    }

    /**
     * 关闭串口
     */
    fun closeSerialPort() {
        try {
            inputStream!!.close()
            outputStream!!.close()
            this.serialPortStatus = false
            this.threadStatus = true //线程状态
            serialPort!!.close()

            Log.d(TAG, "closeSerialPort: 关闭串口成功")
            for (serialPortStateCallback in serialPortStateCallbacks) {
                serialPortStateCallback.onDisconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "closeSerialPort: 关闭串口异常：" + e.toString())
            for (serialPortStateCallback in serialPortStateCallbacks) {
                serialPortStateCallback.onDisconnectFail(e.message)
            }
            return
        }

    }

    /**
     * 发送串口指令（字符串）
     *
     * @param data String数据指令
     */
    fun sendSerialPort(data: String) {
        try {
            val sendData = data.toByteArray() //string转byte[]
            if (sendData.size > 0) {
                outputStream!!.write(sendData)
                outputStream!!.write('\n'.toInt())
                //outputStream.write('\r'+'\n');
                outputStream!!.flush()
                Log.d(TAG, "sendSerialPort: 串口数据发送成功")
                for (serialPortStateCallback in serialPortStateCallbacks) {
                    serialPortStateCallback.onWriteSuccess()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString())
            for (serialPortStateCallback in serialPortStateCallbacks) {
                serialPortStateCallback.onWriteFail(e.message)
            }
        }

    }


    /**
     * 发送串口指令（字符串）
     *
     * @param data String数据指令
     */
    fun sendSerialPort(bytes: ByteArray) {
        try {
            if (bytes.size > 0) {
                outputStream!!.write(bytes)
                outputStream!!.write('\r'.toInt() + '\n'.toInt())
                //outputStream.write('\r'+'\n');
                outputStream!!.flush()
                Log.d(TAG, "sendSerialPort: 串口数据发送成功")
                for (serialPortStateCallback in serialPortStateCallbacks) {
                    serialPortStateCallback.onWriteSuccess()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString())
            for (serialPortStateCallback in serialPortStateCallbacks) {
                serialPortStateCallback.onWriteFail(e.message)
            }
        }

    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread : Thread() {
        override fun run() {
            super.run()
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus) {
                val bytes = ByteArray(1024)
                val size: Int //读取数据的大小
                try {
                    size = inputStream!!.read(bytes)
                    if (size > 0) {
                        Log.d(TAG, ": notify_data：" + BinaryUtils.bytesToHexString(Arrays.copyOfRange(bytes, 0, size)))
                        Log.d(TAG, ": datalength：" + size.toString())
                        if (onDataReceiveListener != null) {
                            onDataReceiveListener!!.onNotifyData(bytes)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "serialport_notify: 数据读取异常：" + e.toString())
                }

            }

        }
    }


    fun addCallback(serialPortStateCallback: SerialPortStateCallback) {
        serialPortStateCallbacks.add(serialPortStateCallback)
    }

    fun removeCallback(serialPortStateCallback: SerialPortStateCallback) {
        serialPortStateCallbacks.remove(serialPortStateCallback)
    }

    interface SerialPortStateCallback {
        fun onNotifyData(byteArray: ByteArray)
        fun onConnectSuccess()
        fun onConnectFail(msg: String?)
        fun onDisconnect()
        fun onDisconnectFail(msg: String?)
        fun onWriteSuccess()
        fun onWriteFail(msg: String?)
    }

    open class SerialPortStateCallbackImpl : SerialPortStateCallback {
        override fun onNotifyData(byteArray: ByteArray) {
        }

        override fun onConnectSuccess() {
        }

        override fun onConnectFail(msg: String?) {
        }

        override fun onDisconnect() {
        }

        override fun onDisconnectFail(msg: String?) {
        }

        override fun onWriteSuccess() {
        }

        override fun onWriteFail(msg: String?) {
        }

    }


}
