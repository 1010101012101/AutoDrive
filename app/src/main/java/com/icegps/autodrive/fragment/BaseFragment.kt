package com.icegps.autodrive.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.OnlyBle
import com.icegps.autodrive.ble.ParseDataBean

abstract class BaseFragment : Fragment() {
    lateinit var contentView: View
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = childImplView()
        OnlyBle.addOnParseCompleteCallback(onParseComplete)
        init()
        return contentView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OnlyBle.removeParseCompleteCallback(onParseComplete)
    }

    abstract fun childImplView(): View
    abstract fun init()
    abstract fun refreshUi(parseDataBean: ParseDataBean?, type: String)

    var onParseComplete = object : OnlyBle.OnParseComplete {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            refreshUi(parseDataBean, type)
        }
    }

    fun setValue(type: String, values: String) {
        BleWriteHelper.writeCmd(Cmds.SETCONTROLS ,type ,values)
    }

    fun getValue(type: String) {
        BleWriteHelper.writeCmd(Cmds.GETCONTROLS , type)
    }


    fun sendTest(type: String, values: String) {
        BleWriteHelper.writeCmd(Cmds.SETWORKS ,type,values)
    }
}