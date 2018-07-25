package com.icegps.autodrive.activity

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.widget.EditText
import android.widget.RadioGroup
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import com.icegps.autodrive.utils.Init
import kotlinx.android.synthetic.main.activity_sensor.*
import kotlinx.android.synthetic.main.toobar.*
import java.lang.Float.parseFloat


/**
 * Created by 111 on 2018/4/27.
 */
class SensorActivity : BaseActivity(), RadioGroup.OnCheckedChangeListener {
    var value1 = "0"
    var value2 = "0"
    var checkedId = 0
    var currentSel = 0
    var ad0 = 0
    var ad1 = 0
    var ad2 = 0
    var v1 = 0f
    var v2 = 0f
    var parseDataBean: ParseDataBean? = null

    var k: Float = 0f

    override fun layout(): Int {
        return R.layout.activity_sensor
    }

    override fun init() {
        checkedId = rb1.id
        BleWriteHelper.writeCmd(Cmds.GETSENSORV, "1", "200")
        OnlyBle.addOnParseCompleteCallback(onParseComplete)
        setListener()
        tv_title.setText("传感器校准")

    }

    override fun setListener() {
        iv_left.setOnClickListener({ finish() })
        radioGroup.setOnCheckedChangeListener(this)
        tv_re.setOnClickListener({
            showDialog1()
        })

    }


    fun showDialog1() {
        builderDialog()
                .setTitle("1/4")
                .setMessage("请确认传感器处于0值状态,然后点击下一步")
                .setPositiveButton("下一步", { dialogInterface: DialogInterface, i: Int ->
                    if (parseDataBean != null) {
                        ad0 = parseDataBean!!.twoParameters.get(currentSel).value1
                    }
                    showDialog2()
                }).show()
    }

    fun showDialog2() {
        val editText = EditText(activity)
        builderDialog()

                .setTitle("2/4")
                .setView(editText)
                .setMessage("请输入准确值")
                .setPositiveButton("下一步", { dialogInterface: DialogInterface, i: Int ->
                    val value = editText.text.toString()
                    if (TextUtils.isEmpty(value)) {
                        Init.showToast("值不能为空")
                        showDialog2()
                        return@setPositiveButton
                    }
                    try {
                        //输入值
                        v1 = parseFloat(value)
                    } catch (e: NumberFormatException) {
                        Init.showToast("请输入正确数值")
                        showDialog2()
                        return@setPositiveButton
                    }

                    //此时后台的Ad值就是AD1
                    if (parseDataBean != null) {
                        ad1 = parseDataBean!!.twoParameters.get(currentSel).value1
                    }
                    k = (v1 / (ad1 - ad0))
                    showDialog3()

                }).show()
    }

    fun showDialog3() {
        val editText = EditText(activity)
        builderDialog()
                .setTitle("3/4")
                .setView(editText)
                .setMessage("请再次输入准确值")
                .setPositiveButton("下一步", { dialogInterface: DialogInterface, i: Int ->
                    val value = editText.text.toString()
                    if (TextUtils.isEmpty(value)) {
                        Init.showToast("值不能为空")
                        showDialog2()
                        return@setPositiveButton
                    }
                    try {
                        //输入值
                        v2 = parseFloat(value)
                    } catch (e: NumberFormatException) {
                        Init.showToast("请输入正确数值")
                        showDialog2()
                        return@setPositiveButton
                    }
                    //输入值
                    //此时后台的Ad值就是AD1
                    if (parseDataBean != null) {
                        ad2 = parseDataBean!!.twoParameters.get(currentSel).value1
                    }
                    k += (v2 / (ad2 - ad0))
                    k /= 2.0f

                    BleWriteHelper.writeCmd(Cmds.SETSENSORS, currentSel.toString(), ad0.toString(), k.toString())
                    showDialog4()
                }).show()
    }

    fun showDialog4() {
        builderDialog()
                .setTitle("4/4")
                .setMessage("校准完成")
                .setPositiveButton("完成", { dialogInterface: DialogInterface, i: Int ->
                }).show()
    }

    fun builderDialog(): AlertDialog.Builder {
        return AlertDialog.Builder(activity).setCancelable(false).setNegativeButton("取消", null)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        this.checkedId = checkedId
        when (checkedId) {
            rb1.id -> {
                currentSel = 0
            }
            rb2.id -> {
                currentSel = 1
            }
            rb3.id -> {
                currentSel = 2
            }
            rb4.id -> {
                currentSel = 3
            }
            rb5.id -> {
                currentSel = 4
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BleWriteHelper.writeCmd(Cmds.GETSENSORV, "0", "0")
        OnlyBle.addOnParseCompleteCallback(onParseComplete)
    }

    var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            this@SensorActivity.parseDataBean = parseDataBean
            ref()
        }

    }

    fun ref() {
        when (checkedId) {
            rb1.id -> {
                value1 = "" + parseDataBean!!.twoParameters.get(0).value1
                value2 = "" + parseDataBean!!.twoParameters.get(0).value2 + "°"
            }
            rb2.id -> {
                value1 = "" + parseDataBean!!.twoParameters.get(1).value1
                value2 = "" + parseDataBean!!.twoParameters.get(1).value2 + "℃"
            }
            rb3.id -> {
                value1 = "" + parseDataBean!!.twoParameters.get(2).value1
                value2 = "" + parseDataBean!!.twoParameters.get(2).value2 + "MPa"
            }
            rb4.id -> {
                value1 = "" + parseDataBean!!.twoParameters.get(3).value1
                value2 = "" + parseDataBean!!.twoParameters.get(3).value2 + "mA"
            }
            rb5.id -> {
                value1 = "" + parseDataBean!!.twoParameters.get(4).value1
                value2 = "" + parseDataBean!!.twoParameters.get(4).value2 + "mA"
            }
        }
        ad_value1.setText(value1)
        ad_value2.setText(value2)
    }
}