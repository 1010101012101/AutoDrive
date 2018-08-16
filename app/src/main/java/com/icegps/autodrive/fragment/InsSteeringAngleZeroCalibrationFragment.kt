package com.icegps.autodrive.fragment

import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import kotlinx.android.synthetic.main.fragment_ins_steering_angle_zero_calibration.view.*

class InsSteeringAngleZeroCalibrationFragment : BaseFragment() {
    var angleValue = 0f
    override fun childImplView(): View {
        return View.inflate(context, R.layout.fragment_ins_steering_angle_zero_calibration, null)
    }

    override fun init() {
        getCenterValue()
        contentView.tv_get.setOnClickListener({
            getCenterValue()

        })
        contentView.tv_send.setOnClickListener({
            DataManager.writeCmd(Cmds.SETINSTALL,"1",contentView.et_center_azimuth.text.toString())
        })

    }

    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        contentView.et_practical_azimuth.setText(parseDataBean!!.workStatus.wheelAngle.toString())
        if (angleValue != parseDataBean.insTallValue.angleValue) {
            angleValue = parseDataBean.insTallValue.angleValue
            contentView.et_center_azimuth.setText(angleValue.toString())

        }
    }

    fun getCenterValue() {
        DataManager.writeCmd(Cmds.GETINSTALL , "1")

    }

}