package com.icegps.autodrive.ble

/**
 * Created by 111 on 2018/4/27.
 */
class Cmds {
    companion object {
        var who = 0
        val GETSENSORV = "\$ICEGPS,GETSENSORV"
        val GETCONTROLV = "\$ICEGPS,GETCONTROLV"
        val GETSENSORS = "\$ICEGPS,GETSENSORS"
        val SETSENSORS = "\$ICEGPS,SETSENSORS"
        val CONNECT = "\$ICEGPS,CONNECT,1"

        val SETCONTROLS = "\$ICEGPS,SETCONTROLS"

        val GETCONTROLS = "\$ICEGPS,GETCONTROLS"
        val GETGPSDATA = "\$ICEGPS,GETGPSDATA"
        val SETWORKS = "\$ICEGPS,SETWORKS"
        val GETINSTALL = "\$ICEGPS,GETINSTALL"
        val SETINSTALL = "\$ICEGPS,SETINSTALL"
        val GETWORKV = "\$ICEGPS,GETWORKV"
        val GETTHRESHOLD = "\$ICEGPS,GETTHRESHOLD"
        val SETTHRESHOLD = "\$ICEGPS,SETTHRESHOLD"
        val AUTO = "\$ICEGPS,AUTO"
        var SATELLITE = "\$ICEGPS,SATELLITE"
        var GETRADIO = "\$ICEGPS,GETRADIO"
        var SETRADIO = "\$ICEGPS,SETRADIO"


        val SENSORV = "SENSORV"
        val SENSORS = "SENSORS"
        val CONTROLV = "CONTROLV"
        val CONTROLS = "CONTROLS"
        val WORKV = "WORKV"
        val INSTALL = "INSTALL"
        val THRESHOLD = "THRESHOLD"
        val RADIO = "RADIO"
    }

}