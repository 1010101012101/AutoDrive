package com.icegps.autodrive.fragment

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.TextureView
import android.view.View
import android.widget.CompoundButton
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import kotlinx.android.synthetic.main.fragment_left_electricity.*
import kotlinx.android.synthetic.main.fragment_left_electricity.view.*
import java.lang.Float.parseFloat


class LeftElectricityFragment : BaseFragment() {
    var isTest = false
    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        contentView.et_electricity_practical.setText(OnlyBle.parseDataBean!!.controlStatus.get(0).value.toString())
        if (isTest) {
            differenceValueCalculate()
        }
        if (type.equals(Cmds.CONTROLS)) {
            et_value1.setText(parseDataBean!!.controlSetValues.get(0).values[0].toString())
            et_value2.setText(parseDataBean!!.controlSetValues.get(0).values[1].toString())
            et_value3.setText(parseDataBean!!.controlSetValues.get(0).values[2].toString())
        }
    }

    override fun init() {
        getValue("1")
        contentView.et_target.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isTest) {
                    differenceValueCalculate()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        contentView.tv_send.setOnClickListener({
            var maxLimit = contentView.et_value1.text.toString()
            var controlScotoma = contentView.et_value2.text.toString()
            var controlGain = contentView.et_value3.text.toString()
            if (TextUtils.isEmpty(maxLimit)) maxLimit = "0"
            if (TextUtils.isEmpty(controlScotoma)) controlScotoma = "0"
            if (TextUtils.isEmpty(controlGain)) controlGain = "0"
            setValue("1", maxLimit + "," + controlScotoma + "," + controlGain + ",0,0")
        })

        contentView.tv_get.setOnClickListener({
            getValue("1")
        })
        contentView.tv_test.setOnCheckedChangeListener({ compoundButton: CompoundButton, b: Boolean ->
            if (b) {
                test("1")
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
        sendTest(switch, "1" + "," + target)
        differenceValueCalculate()
    }

    override fun childImplView(): View {
        val view = View.inflate(activity, R.layout.fragment_left_electricity, null)
        return view
    }

    fun differenceValueCalculate() {
        val value1 = contentView.et_target.text.toString()
        val value2 = contentView.et_electricity_practical.text.toString()
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            val setValue = parseFloat(value1)
            val practicalValue = parseFloat(value2)
            contentView.et_electricity_offset_error.setText((practicalValue-setValue).toString())
        }
    }

}