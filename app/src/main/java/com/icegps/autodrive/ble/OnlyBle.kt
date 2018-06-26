package com.icegps.autodrive.ble

import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import j.m.jblelib.ble.parse.ParseManager
import j.m.jblelib.ble.threadpool.ThreadPool
import timber.log.Timber
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by 111 on 2018/4/28.  解析蓝牙数据的单例类
 */
object OnlyBle {
    var parseDataBean: ParseDataBean
    var strData = ""
    private var type = ""

    init {
        parseDataBean = ParseDataBean()
    }

    fun register() {
        BleHelper.addBleCallback(bleStatusCallBackImpl)
    }

    fun unregister() {
        BleHelper.removeBleCallback(bleStatusCallBackImpl)
    }

    private var bleStatusCallBackImpl = object : BleStatusCallBackImpl() {
        override fun onAscii(cmds: String?) {
            super.onAscii(cmds)
            Timber.e(cmds)
            strData = cmds!!
            try {
                val cmds = splitAndRemoveAss(cmds!!)
                if (cmds!!.contains(Cmds.SENSORV)) {
                    type = Cmds.SENSORV
                    val twoParameters = parseDataBean.twoParameters
                    twoParameters.get(0).setValue(parseInt(cmds[2]), parseFloat(cmds[3]))
                    twoParameters.get(1).setValue(parseInt(cmds[4]), parseFloat(cmds[5]))
                    twoParameters.get(2).setValue(parseInt(cmds[6]), parseFloat(cmds[7]))
                    twoParameters.get(3).setValue(parseInt(cmds[8]), parseFloat(cmds[9]))
                    twoParameters.get(4).setValue(parseInt(cmds[10]), parseFloat(cmds[11]))
                } else if (cmds!!.contains(Cmds.CONTROLV)) {
                    type = Cmds.CONTROLV
                    val controlStatus = parseDataBean.controlStatus
                    controlStatus.get(0).value = parseFloat(cmds[2])
                    controlStatus.get(1).value = parseFloat(cmds[3])
                    controlStatus.get(2).value = parseFloat(cmds[4])
                    controlStatus.get(3).value = parseFloat(cmds[5])
                    controlStatus.get(4).value = parseFloat(cmds[6])
                    controlStatus.get(5).value = parseFloat(cmds[7])
                } else if (cmds!!.contains(Cmds.CONTROLS)) {
                    type = Cmds.CONTROLS
                    val controlSetValues = parseDataBean.controlSetValues
                    var controlSetValue: ParseDataBean.ControlSetValue? = null
                    when (cmds[2]) {
                        "1" -> {
                            controlSetValue = controlSetValues.get(0)

                        }
                        "2" -> {
                            controlSetValue = controlSetValues.get(1)

                        }
                        "3" -> {
                            controlSetValue = controlSetValues.get(2)

                        }
                        "4" -> {
                            controlSetValue = controlSetValues.get(3)

                        }
                        "5" -> {
                            controlSetValue = controlSetValues.get(4)

                        }

                    }
                    controlSetValue!!.setValue(
                            parseFloat(cmds[3]),
                            parseFloat(cmds[4]),
                            parseFloat(cmds[5]),
                            parseFloat(cmds[6]),
                            parseFloat(cmds[7]))
                } else if (cmds!!.contains(Cmds.WORKV)) {
                    parseDataBean.workStatus.type = cmds[2].toInt()
                    parseDataBean.workStatus.workMode = cmds[3].toInt()
                    parseDataBean.workStatus.wheelAngle = cmds[4].toFloat()
                    parseDataBean.workStatus.carCourse = cmds[5].toFloat()
                    parseDataBean.workStatus.distanceOffset = cmds[6].toFloat()
                    parseDataBean.workStatus.courseOffset = cmds[7].toFloat()
                    parseDataBean.workStatus.oilCylinderTemperature = cmds[8].toFloat()
                    parseDataBean.workStatus.oilCylinderMpa = cmds[9].toFloat()
                } else if (cmds!!.contains((Cmds.INSTALL))) {
                    if (cmds[2].equals("1")) {
                        parseDataBean.insTallValue.angleValue = cmds[3].toFloat()
                    } else if (cmds[2].equals("2")) {
                        parseDataBean.insTallValue.courseValue = cmds[3].toFloat()
                    }
                } else if (cmds!!.contains(Cmds.THRESHOLD)) {
                    if (cmds[2].equals("1")){
                        parseDataBean.threshold.MPa=cmds[3].toInt()
                    } else if (cmds[2].equals("2")) {
                        parseDataBean.threshold.temperatureAlert=cmds[3].toInt()
                    }
                } else if (cmds!!.contains(Cmds.RADIO)){
                    parseDataBean.radio.max=cmds[3].toInt()
                    parseDataBean.radio.min=cmds[4].toInt()
                    parseDataBean.radio.current=cmds[5].toInt()
                }

                if (copyOnWriteArrayList != null) {
                    for (onAsciiData in copyOnWriteArrayList) {
                        onAsciiData.onComplete(parseDataBean, type)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        override fun onNotifyData(byteArray: ByteArray) {
            super.onNotifyData(byteArray)
            ThreadPool.excuteSingle(Runnable { ParseManager.parseData(byteArray) })

        }

        override fun onHex(bytes: ByteArray?) {
            super.onHex(bytes)
//            Timber.e(BinaryUtils.bytesToHexString(bytes))
        }

    }

    private fun splitAndRemoveAss(cmds: String): Array<String> {
        val split = cmds.split("\\*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return split[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }


    var copyOnWriteArrayList = CopyOnWriteArrayList<OnParseComplete>()

    fun addOnParseCompleteCallback(onParseComplete: OnParseComplete) {
        copyOnWriteArrayList.add(onParseComplete)
    }

    fun removeParseCompleteCallback(onParseComplete: OnParseComplete) {
        copyOnWriteArrayList.remove(onParseComplete)
    }

    interface OnParseComplete {
        fun onComplete(parseDataBean: ParseDataBean?, type: String)
    }
}

