package com.icegps.autodrive.fragment

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import kotlinx.android.synthetic.main.fragment_azimuth.*
import kotlinx.android.synthetic.main.fragment_azimuth.view.*
import java.lang.Float.parseFloat


class AzimuthFragment : BaseFragment() {
    var isTest = false
    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        contentView.et_practical_azimuth.setText(OnlyBle.parseDataBean!!.controlStatus.get(2).value.toString())
        if (isTest) {
            differenceValueCalculate()
        }
        if (type.equals(Cmds.CONTROLS)) {
            contentView.et_value1.setText(parseDataBean!!.controlSetValues.get(2).values[0].toString())
            contentView.et_value2.setText(parseDataBean!!.controlSetValues.get(2).values[1].toString())
            contentView.et_value3.setText(parseDataBean!!.controlSetValues.get(2).values[2].toString())
            contentView.et_value4.setText(parseDataBean!!.controlSetValues.get(2).values[3].toString())
            contentView.et_value5.setText(parseDataBean!!.controlSetValues.get(2).values[4].toString())
        }
    }

    override fun init() {
        getValue("3")
        contentView.tv_get.setOnClickListener({
            getValue("3")
        })
        contentView.tv_send.setOnClickListener({
            var value1 = et_value1.text.toString()
            var value2 = et_value2.text.toString()
            var value3 = et_value3.text.toString()
            var value4 = et_value4.text.toString()
            var value5 = et_value5.text.toString()
            if (TextUtils.isEmpty(value1)) value1 = "0"
            if (TextUtils.isEmpty(value2)) value2 = "0"
            if (TextUtils.isEmpty(value3)) value3 = "0"
            if (TextUtils.isEmpty(value4)) value4 = "0"
            if (TextUtils.isEmpty(value5)) value5 = "0"
            setValue("3", value1 + "," + value2 + "," + value3 + "," + value4 + "," + value5)
        })

        contentView.et_target.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (isTest) {
                    differenceValueCalculate()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        contentView.tv_test.setOnCheckedChangeListener({ compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                test("3")
                contentView.tv_test.text="取消"
            } else {
                test("0")
                contentView.tv_test.text="测试"
            }
        })
    }


    fun test(switch: String) {
        var target = et_target.text.toString()
        if (TextUtils.isEmpty(target)) target = "0"
        isTest = true
        sendTest(switch,   "1," + target)
        differenceValueCalculate()
    }


    override fun childImplView(): View {
        val view = View.inflate(activity, R.layout.fragment_azimuth, null)
        return view
    }

    fun differenceValueCalculate() {
        val value1 = contentView.et_target.text.toString()
        val value2 = contentView.et_practical_azimuth.text.toString()
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            val setValue = parseFloat(value1)
            val practicalValue = parseFloat(value2)
            contentView.et_practical_azimuth_offset_error.setText((practicalValue-setValue).toString())
        }
    }


}