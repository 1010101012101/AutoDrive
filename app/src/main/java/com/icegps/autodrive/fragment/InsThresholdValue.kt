package com.icegps.autodrive.fragment

import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import kotlinx.android.synthetic.main.fragment_ins_threshold_value.view.*

class InsThresholdValue : BaseFragment() {
    var MPa = 0
    var temperatureAlert = 0
    override fun childImplView(): View {
        return View.inflate(context, R.layout.fragment_ins_threshold_value, null)
    }

    override fun init() {
        getMPa()
        getTemperatureAlert()

        contentView.tv_temperature_get.setOnClickListener({
            getTemperatureAlert()
        })
        contentView.tv_mpa_get.setOnClickListener({
            getMPa()
        })

        contentView.tv_mpa_send.setOnClickListener({
            DataManager.writeCmd(Cmds.SETTHRESHOLD ,"1" , contentView.et_mpa_alert.text.toString())
        })
        contentView.tv_temperature_send.setOnClickListener({
            DataManager.writeCmd(Cmds.SETTHRESHOLD , "2", contentView.et_temperature_alert.text.toString())
        })
    }

    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        if (MPa != parseDataBean!!.threshold.MPa) {
            MPa = parseDataBean!!.threshold.MPa
            contentView.et_mpa_alert.setText(MPa.toString())
        }

        if (temperatureAlert != parseDataBean!!.threshold.temperatureAlert) {
            temperatureAlert = parseDataBean!!.threshold.temperatureAlert
            contentView.et_temperature_alert.setText(temperatureAlert.toString())
        }
    }

    fun getMPa() {
        DataManager.writeCmd(Cmds.GETTHRESHOLD ,"1")
    }

    fun getTemperatureAlert() {
        DataManager.writeCmd(Cmds.GETTHRESHOLD , "2")
    }

}