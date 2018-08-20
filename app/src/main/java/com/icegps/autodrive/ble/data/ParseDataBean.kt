package com.icegps.autodrive.ble.data

import java.util.*

/**
 * Created by 111 on 2018/4/28.
 */
class ParseDataBean {
    var twoParameters: ArrayList<TwoParameter>
    var controlStatus: ArrayList<ControlStatus>
    var controlSetValues: ArrayList<ControlSetValue>
    var workStatus: WorkStatus
    var insTallValue: InsTallValue
    var threshold: Threshold
    var radio: Radio

    init {
        radio = Radio()
        threshold = Threshold()
        insTallValue = InsTallValue()
        workStatus = WorkStatus()
        twoParameters = ArrayList()
        controlStatus = ArrayList()
        controlSetValues = ArrayList()
        for (i in 0 until 5) {
            twoParameters.add(TwoParameter())
            controlStatus.add(ControlStatus())
            controlSetValues.add(ControlSetValue())
        }
        controlStatus.add(ControlStatus())
    }

    class TwoParameter {
        var value1 = 0
        var value2: Float = 0f

        fun setValue(value1: Int, value2: Float) {
            this.value1 = value1
            this.value2 = value2
        }

        override fun toString(): String {
            return "TwoParameter(value1=$value1, value2=$value2)"
        }
    }


    class ControlStatus {
        var value = 0f
    }

    class ControlSetValue {
        var values: FloatArray = FloatArray(5)

        fun setValue(vararg values: Float) {
            this.values = values
        }

        override fun toString(): String {
            return "ControlSetValue(values=${Arrays.toString(values)})"
        }
    }

    class WorkStatus() {
        var type = 0
        var workMode = 0
        var wheelAngle = 0f
        var carCourse = 0f   //航向
        var distanceOffset = 0f
        var courseOffset = 0f
        var oilCylinderTemperature = 0f
        var oilCylinderMpa = 0f
        override fun toString(): String {
            return "WorkStatus(type=$type, workMode=$workMode, wheelAngle=$wheelAngle, carCourse=$carCourse, distanceOffset=$distanceOffset, courseOffset=$courseOffset, oilCylinderTemperature=$oilCylinderTemperature, oilCylinderMpa=$oilCylinderMpa)"
        }
    }

    class InsTallValue {
        var angleValue = 0f
        var courseValue = 0f
    }

    class Threshold {
        var MPa = 0
        var temperatureAlert = 0
    }

    class Radio {
        var max = 0
        var current = 0
        var min = 0
    }

}