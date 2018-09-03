package com.icegps.autodrive.activity

import com.icegps.autodrive.R
import com.icegps.autodrive.constant.Cons
import kotlinx.android.synthetic.main.activity_measure.*

class MeasureMapActivity:BaseActivity(){
    override fun layout(): Int {
        return R.layout.activity_measure
    }

    override fun init() {
        map_view.setSize(Cons.MAP_WIDTH,Cons.MAP_WIDTH)
    }

    override fun setListener() {

    }

}