package com.icegps.autodrive.fragment

import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.data.ParseDataBean

class SpeedFragment: BaseFragment() {
    override fun childImplView(): View {
        return View.inflate(context,R.layout.fragment_speed,null)
    }

    override fun init() {

    }

    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {

    }

}