package com.icegps.autodrive.fragment

import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.fragment_course.view.*
import java.lang.Float

class CourseFragment : BaseFragment() {
    var isTest = false
    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        if (isTest) {
            differenceValueCalculate()
        }
        contentView.et_practical_course.setText(parseDataBean!!.controlStatus.get(3).value.toString())
        contentView.et_practical_course_offset_error.setText(parseDataBean!!.controlStatus.get(5).value.toString())
        if (type.equals(Cmds.CONTROLS)) {
            contentView.et_value2.setText(parseDataBean!!.controlSetValues.get(3).values[1].toString())
            contentView.et_value3.setText(parseDataBean!!.controlSetValues.get(3).values[2].toString())
            contentView.et_value4.setText(parseDataBean!!.controlSetValues.get(3).values[3].toString())
            contentView.et_value5.setText(parseDataBean!!.controlSetValues.get(3).values[4].toString())
        }
    }

    override fun init() {
        getValue("4")
        contentView.tv_get.setOnClickListener({
            getValue("4")
        })
        contentView.tv_send.setOnClickListener({
            var value2 = et_value2.text.toString()
            var value3 = et_value3.text.toString()
            var value4 = et_value4.text.toString()
            var value5 = et_value5.text.toString()
            if (TextUtils.isEmpty(value2)) value2 = "0"
            if (TextUtils.isEmpty(value3)) value3 = "0"
            if (TextUtils.isEmpty(value4)) value4 = "0"
            if (TextUtils.isEmpty(value5)) value5 = "0"
            setValue("4", "0" + "," + value2 + "," + value3 + "," + value4 + "," + value5)
        })

        contentView.tv_test.setOnCheckedChangeListener({ compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                test("4")
                contentView.tv_test.text = "取消"
            } else {
                test("0")
                contentView.tv_test.text = "测试"
            }
        })
    }

    fun test(switch: String) {
        var target = et_target.text.toString()
        if (TextUtils.isEmpty(target)) target = "0"
        isTest = true
        sendTest(switch, "1" + "," + target)
        differenceValueCalculate()
    }


    override fun childImplView(): View {
        val view = View.inflate(activity, R.layout.fragment_course, null)
        return view
    }


    fun differenceValueCalculate() {
        val value1 = contentView.et_target.text.toString()
        val value2 = contentView.et_practical_course.text.toString()
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            val setValue = Float.parseFloat(value1)
            val practicalValue = Float.parseFloat(value2)

        }
    }


}