package com.icegps.autodrive.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.ParseDataBean
import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.ble.ParseDataManager
import com.icegps.jblelib.ble.data.LocationStatus

abstract class BaseFragment : Fragment() {
    lateinit var contentView: View
    var locationStatus: LocationStatus? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = childImplView()
        ParseDataManager.addDataCallback(dataCallbackImpl)
        init()
        return contentView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ParseDataManager.removeDataCallback(dataCallbackImpl)
    }

    abstract fun childImplView(): View
    abstract fun init()
    abstract fun refreshUi(parseDataBean: ParseDataBean?, type: String)

    var dataCallbackImpl = object : ParseDataManager.DataCallBackImpl() {
        override fun onComplete(parseDataBean: ParseDataBean?, type: String) {
            refreshUi(parseDataBean, type)
        }

        override fun onLocationData(locationStatus: LocationStatus?) {
            super.onLocationData(locationStatus)
            this@BaseFragment.locationStatus = locationStatus
        }
    }


    fun setValue(type: String, values: String) {
        DataManager.writeCmd(Cmds.SETCONTROLS, type, values)
    }

    fun getValue(type: String) {
        DataManager.writeCmd(Cmds.GETCONTROLS, type)
    }


    fun sendTest(type: String, values: String) {
        DataManager.writeCmd(Cmds.SETWORKS, type, values)
    }
}