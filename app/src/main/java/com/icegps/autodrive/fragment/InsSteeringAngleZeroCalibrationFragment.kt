package com.icegps.autodrive.fragment

import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.ParseDataBean
import kotlinx.android.synthetic.main.fragment_ins_steering_angle_zero_calibration.*
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
            BleWriteHelper.writeCmd(Cmds.SETINSTALL,"1",contentView.et_center_azimuth.text.toString())
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
        BleWriteHelper.writeCmd(Cmds.GETINSTALL , "1")

    }

}