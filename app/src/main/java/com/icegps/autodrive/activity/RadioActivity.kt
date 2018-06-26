package com.icegps.autodrive.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.adapter.RadioAdapter
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean
import kotlinx.android.synthetic.main.activity_radio.*
import kotlinx.android.synthetic.main.toobar.*

class RadioActivity : BaseActivity() {
    private var radios: ArrayList<Int> = ArrayList()
    private lateinit var radioAdapter: RadioAdapter
    override fun layout(): Int {
        return R.layout.activity_radio
    }


    override fun init() {
        for (i in 1..8) {
            radios.add(i)
        }
        var llm = LinearLayoutManager(activity)
        recyclerView.setLayoutManager(llm)
        radioAdapter = RadioAdapter(R.layout.item_radio, radios)
        recyclerView.adapter = radioAdapter
        OnlyBle.addOnParseCompleteCallback(onParseComplete)
        BleWriteHelper.writeCmd(Cmds.GETRADIO)

    }

    override fun setListener() {
        radioAdapter.setOnItemClickListener({ baseQuickAdapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, i: Int ->
            RadioAdapter.selItem = i + 1
            BleWriteHelper.writeCmd(Cmds.SETRADIO , RadioAdapter.selItem.toString())
            radioAdapter.notifyDataSetChanged()
        })
        tv_title.setText("数传频道")
        iv_left.setOnClickListener { finish() }
    }

    var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            if (RadioAdapter.selItem != parseDataBean!!.radio.current) {
                RadioAdapter.selItem = parseDataBean!!.radio.current
                radioAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OnlyBle.removeParseCompleteCallback(onParseComplete)
    }
}