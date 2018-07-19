package com.icegps.autodrive.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AbsListView
import android.widget.NumberPicker
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import com.tencent.bugly.proguard.s
import kotlinx.android.synthetic.main.activity_radio.*
import kotlinx.android.synthetic.main.activity_radio.view.*
import kotlinx.android.synthetic.main.toobar.*

class RadioActivity : BaseActivity() {
    private var radios: ArrayList<String> = ArrayList()
    private lateinit var s: Array<String?>
    private var currentRadio = 1
    override fun layout(): Int {
        return R.layout.activity_radio
    }


    override fun init() {
        for (i in 1..48) {
            var value = i.toString()
            if (value.length < 2) {
                value = "0" + value
            }
            radios.add(value)
        }
        OnlyBle.addOnParseCompleteCallback(onParseComplete)
        BleWriteHelper.writeCmd(Cmds.GETRADIO)
        s = arrayOfNulls<String>(radios.size)
        radios.toArray(s)
        numberPickerView.setDisplayedValues(s, false)
        numberPickerView.maxValue = radios.size - 1


    }

    override fun setListener() {
        tv_title.setText("数传频道")
        iv_left.setOnClickListener { finish() }
        numberPickerView.setOnScrollListener { view, scrollState ->
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                BleWriteHelper.writeCmd(Cmds.SETRADIO, numberPickerView.contentByCurrValue)
            }
        }

    }

    var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            val current = parseDataBean!!.radio.current
            if (current != currentRadio) {
                currentRadio=current
                numberPickerView.value = current - 1
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OnlyBle.removeParseCompleteCallback(onParseComplete)
    }
}