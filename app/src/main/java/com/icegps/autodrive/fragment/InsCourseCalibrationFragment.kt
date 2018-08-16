package com.icegps.autodrive.fragment

import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import kotlinx.android.synthetic.main.fragment_ins_course_calibration.view.*

class InsCourseCalibrationFragment : BaseFragment() {
    var courseValue = 0f
    override fun childImplView(): View {
        return View.inflate(context, R.layout.fragment_ins_course_calibration, null)
    }

    override fun init() {
        getCourseOffset()
        contentView.tv_get.setOnClickListener({
            getCourseOffset()
        })
        contentView.tv_send.setOnClickListener({
            DataManager.writeCmd(Cmds.SETINSTALL , "2" , contentView.et_course_offset_error.text.toString())
        })
    }

    private fun getCourseOffset() {
        DataManager.writeCmd(Cmds.GETINSTALL , "2")
    }

    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        contentView.et_practical_course.setText(parseDataBean!!.workStatus.carCourse.toString())
        if (courseValue != parseDataBean!!.insTallValue.courseValue) {
            courseValue = parseDataBean!!.insTallValue.courseValue
            contentView.et_course_offset_error.setText(courseValue.toString())
        }

    }


}