package com.icegps.autodrive.ble

import com.icegps.autodrive.utils.CheckUtils.Companion.addCheckSum
import j.m.jblelib.ble.BleHelper
import timber.log.Timber
import kotlin.experimental.xor

/**
 * Created by 111 on 2018/4/27.
 */
object BleWriteHelper {
    private fun write(cmds: String) {
        val addCheckSum = addCheckSum(cmds)
        Timber.e(addCheckSum)
        BleHelper.addWriteRequest(addCheckSum.toByteArray())
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