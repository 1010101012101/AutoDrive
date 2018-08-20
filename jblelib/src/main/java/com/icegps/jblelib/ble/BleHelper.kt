package com.icegps.jblelib.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import com.icegps.jblelib.ble.bleoperation.BleConnect
import com.icegps.jblelib.ble.bleoperation.BleConnectedDevice
import com.icegps.jblelib.ble.bleoperation.BleScan
import com.icegps.jblelib.ble.bleoperation.BleWrite
import com.icegps.jblelib.ble.callback.BleCallbackManager
import com.icegps.jblelib.ble.callback.BleStatusCallback
import com.icegps.jblelib.ble.utils.BinaryUtils
import com.icegps.jblelib.ble.utils.BleLog
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore


@SuppressLint("StaticFieldLeak")
/**
 * Created by 111 on 2018/4/11.
 */
object BleHelper {
    lateinit var serviceUUID: String
    lateinit var notifyUUID: String
    lateinit var writeUUID: String
    lateinit var context: Context
    lateinit var bleManager: BluetoothManager
    lateinit var writeQueue: LinkedBlockingQueue<ByteArray>
    lateinit var semaphore: Semaphore
    val WRITE_MAX_LENGTH = 20
    val REQUEST_CODE = 1
    val DEFAULT_WRITE_DELAYED: Long = 10
    var WRITE_DELAYED = DEFAULT_WRITE_DELAYED
    fun init(context: Context, serviceUUID: String, notifyUUID: String, writeUUID: String, isPrintLog: Boolean) {
        BleHelper.serviceUUID = serviceUUID
        BleHelper.notifyUUID = notifyUUID
        BleHelper.writeUUID = writeUUID
        BleHelper.context = context
        BleCallbackManager.init()//保证主线程初始化
        bleManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        writeQueue = LinkedBlockingQueue()
        BleLog.isPrintLog = isPrintLog
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(broadcastReceiver, intentFilter)
        semaphore = Semaphore(1)
        WriteThread(Runnable {}).start()

    }

    fun setWriteDelayed(writeDelayed: Long) {
        WRITE_DELAYED = writeDelayed
    }

    fun disconnect() {
        BleConnect.disconnect()
    }

    fun addWriteRequest(byteArray: ByteArray) {
        write(byteArray)
    }

    fun addWriteRequestASCIIStr(ascii: String) {
        addWriteRequest(ascii.toByteArray())
    }

    fun addWriteRequestHexStr(hex: String) {
        addWriteRequest(BinaryUtils.hexStringToBytes(hex))
    }

    fun isConnect(): Boolean {
        return if (BleConnectedDevice.connectBleDevices.size == 0) false else true
    }

    fun isSupportBle(): Boolean {
        return bleManager.adapter != null
    }

    private fun write(byteArray: ByteArray) {
        var count = byteArray.size / WRITE_MAX_LENGTH
        if (byteArray.size % WRITE_MAX_LENGTH > 0) {
            count++
        }
        var bytes: ByteArray
        for (i in 0 until count) {
            // 不是最后一个数据
            if (i < count - 1) {
                bytes = Arrays.copyOfRange(byteArray, i * WRITE_MAX_LENGTH, (i + 1) * WRITE_MAX_LENGTH)
            } else {
                bytes = Arrays.copyOfRange(byteArray, i * WRITE_MAX_LENGTH, byteArray.size)
            }
            /**
             * @see WriteThread
             */
            writeQueue.put(bytes)
        }
    }

    fun startScan() {
        BleScan.startScan()
    }


    fun stopScan() {
        BleScan.stopScan()
    }


    fun addBleCallback(bleStatusCallback: BleStatusCallback) {
        BleCallbackManager.addBleCallback(bleStatusCallback)
    }

    fun removeBleCallback(bleStatusCallback: BleStatusCallback) {
        BleCallbackManager.removeBleCallback(bleStatusCallback)
    }


    /**
     * 打开蓝牙
     */
    fun openBle(a: Activity, request: Int) {
        val adapter = bleManager.adapter
        if (!bleIsOpen()) {
            var enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            a.startActivityForResult(enableBtIntent, REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    fun bleIsOpen(): Boolean {
        return bleManager.adapter != null && bleManager.adapter.isEnabled()
    }

    fun connect(mac: String) {
        stopScan()
        BleConnect.connect(mac)
    }

    fun connect(mac: String, auto: Boolean) {
        stopScan()
        BleConnect.connect(mac, auto)
    }

    class WriteThread(target: Runnable?) : Thread(target) {
        override fun run() {
            super.run()
            while (true) {
                semaphore.acquire()
                val bytes = writeQueue.take()
                BleWrite.write(bytes)
                SystemClock.sleep(WRITE_DELAYED)
            }
        }
    }


    internal var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        BleLog.d(BleHelper, "蓝牙关闭")
                        BleCallbackManager.callback(BleCallbackManager.OPEN_BLE, null)
                    }
                    BluetoothAdapter.STATE_ON -> {
                        BleLog.d(BleHelper, "蓝牙开启")
                        BleCallbackManager.callback(BleCallbackManager.CLOSE_BLE, null)
                    }
                }
            }
        }
    }

}