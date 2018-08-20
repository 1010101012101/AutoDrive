package com.icegps.autodrive.ble

import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.parse.ParseAscii
import com.icegps.autodrive.ble.parse.ParseHex
import com.icegps.jblelib.ble.data.LocationStatus
import com.icegps.jblelib.ble.data.SatelliteData
import java.lang.Float
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by 111 on 2018/4/26.
 *
 */
 object ParseDataManager {
    private var hex: ParseHex
    private var ascii: ParseAscii
    private var dataCallbacks: CopyOnWriteArrayList<DataCallback>
    private var strData = ""
    private var type = ""
    var parseDataBean: ParseDataBean

    init {
        parseDataBean = ParseDataBean()
        dataCallbacks = CopyOnWriteArrayList()
        hex = ParseHex()
        ascii = ParseAscii()
        ascii.setOnAsciiCallback(object : ParseAscii.OnAsciiCallback {
            override fun onAscii(data: String?) {
                parseAscii(data!!)
                for (dataCallback in dataCallbacks) {
                    dataCallback.onAscii(data)
                }
            }

        })

        hex.setOnHexCallback(object : ParseHex.OnHexCallback {

            override fun onHex(bytes: ByteArray?) {
                for (dataCallback in dataCallbacks) {
                    dataCallback.onHex(bytes)
                }
            }

            override fun onLocationData(locationStatus: LocationStatus?) {
                for (dataCallback in dataCallbacks) {
                    dataCallback.onLocationData(locationStatus)
                }
            }

            override fun onSatelliteData(satellites: ArrayList<SatelliteData>?, satelliteType: Byte) {
                for (dataCallback in dataCallbacks) {
                    dataCallback.onSatelliteData(satellites, satelliteType)
                }
            }

        })
    }

    fun parseData(byteArray: ByteArray) {
        byteArray.forEach {
            if (hex.isWorking()) {
                hex.parseData(it)
            } else if (ascii.isWorking()) {
                ascii.parseData(it)
            } else {
                hex.parseData(it)
                ascii.parseData(it)
            }
        }
    }

    fun addDataCallback(dataCallback: DataCallback) {
        dataCallbacks.add(dataCallback)
    }

    fun removeDataCallback(dataCallback: DataCallback) {
        dataCallbacks.remove(dataCallback)
    }

    private fun splitAndRemoveAss(cmds: String): Array<String> {
        val split = cmds.split("\\*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return split[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    interface DataCallback {
        fun onAscii(data: String?)
        fun onHex(bytes: ByteArray?)
        fun onLocationData(locationStatus: LocationStatus?)
        fun onSatelliteData(satellites: ArrayList<SatelliteData>?, satelliteType: Byte?)
        fun onComplete(parseDataBean: ParseDataBean?, type: String)
    }

    open class DataCallBackImpl : DataCallback {
        override fun onAscii(data: String?) {
        }

        override fun onHex(bytes: ByteArray?) {
        }

        override fun onLocationData(locationStatus: LocationStatus?) {
        }

        override fun onSatelliteData(satellites: ArrayList<SatelliteData>?, satelliteType: Byte?) {
        }

        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
        }

    }

    private fun parseAscii(cmds: String) {
        strData = cmds!!
        try {
            val cmds = splitAndRemoveAss(cmds!!)
            if (cmds!!.contains(Cmds.SENSORV)) {
                type = Cmds.SENSORV
                val twoParameters = parseDataBean.twoParameters
                twoParameters.get(0).setValue(Integer.parseInt(cmds[2]), Float.parseFloat(cmds[3]))
                twoParameters.get(1).setValue(Integer.parseInt(cmds[4]), Float.parseFloat(cmds[5]))
                twoParameters.get(2).setValue(Integer.parseInt(cmds[6]), Float.parseFloat(cmds[7]))
                twoParameters.get(3).setValue(Integer.parseInt(cmds[8]), Float.parseFloat(cmds[9]))
                twoParameters.get(4).setValue(Integer.parseInt(cmds[10]), Float.parseFloat(cmds[11]))
            } else if (cmds!!.contains(Cmds.CONTROLV)) {
                type = Cmds.CONTROLV
                val controlStatus = parseDataBean.controlStatus
                controlStatus.get(0).value = Float.parseFloat(cmds[2])
                controlStatus.get(1).value = Float.parseFloat(cmds[3])
                controlStatus.get(2).value = Float.parseFloat(cmds[4])
                controlStatus.get(3).value = Float.parseFloat(cmds[5])
                controlStatus.get(4).value = Float.parseFloat(cmds[6])
                controlStatus.get(5).value = Float.parseFloat(cmds[7])
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
                        Float.parseFloat(cmds[3]),
                        Float.parseFloat(cmds[4]),
                        Float.parseFloat(cmds[5]),
                        Float.parseFloat(cmds[6]),
                        Float.parseFloat(cmds[7]))
            } else if (cmds!!.contains(Cmds.WORKV)) {
                type = Cmds.WORKV
                parseDataBean.workStatus.type = cmds[2].toInt()
                parseDataBean.workStatus.workMode = cmds[3].toInt()
                parseDataBean.workStatus.wheelAngle = cmds[4].toFloat()
                parseDataBean.workStatus.carCourse = cmds[5].toFloat()
                parseDataBean.workStatus.distanceOffset = cmds[6].toFloat()
                parseDataBean.workStatus.courseOffset = cmds[7].toFloat()
                parseDataBean.workStatus.oilCylinderTemperature = cmds[8].toFloat()
                parseDataBean.workStatus.oilCylinderMpa = cmds[9].toFloat()
            } else if (cmds!!.contains((Cmds.INSTALL))) {
                type = Cmds.INSTALL
                if (cmds[2].equals("1")) {
                    parseDataBean.insTallValue.angleValue = cmds[3].toFloat()
                } else if (cmds[2].equals("2")) {
                    parseDataBean.insTallValue.courseValue = cmds[3].toFloat()
                }
            } else if (cmds!!.contains(Cmds.THRESHOLD)) {
                type = Cmds.THRESHOLD
                if (cmds[2].equals("1")) {
                    parseDataBean.threshold.MPa = cmds[3].toInt()
                } else if (cmds[2].equals("2")) {
                    parseDataBean.threshold.temperatureAlert = cmds[3].toInt()
                }
            } else if (cmds!!.contains(Cmds.RADIO)) {
                type = Cmds.RADIO
                parseDataBean.radio.max = cmds[3].toInt()
                parseDataBean.radio.min = cmds[4].toInt()
                parseDataBean.radio.current = cmds[5].toInt()
            }
            for (dataCallback in dataCallbacks) {
                dataCallback.onComplete(parseDataBean, type)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}