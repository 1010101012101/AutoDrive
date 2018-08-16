package com.icegps.autodrive.activity

import android.widget.NumberPicker
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.ble.ParseDataManager
import kotlinx.android.synthetic.main.activity_radio.*
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
        ParseDataManager.addDataCallback(dataCallbackImpl)
        DataManager.writeCmd(Cmds.GETRADIO)
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
                DataManager.writeCmd(Cmds.SETRADIO, numberPickerView.contentByCurrValue)
            }
        }

    }

    var dataCallbackImpl = object : ParseDataManager.DataCallBackImpl() {

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
        ParseDataManager.removeDataCallback(dataCallbackImpl)
    }
}