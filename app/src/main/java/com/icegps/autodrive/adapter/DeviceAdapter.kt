package com.icegps.autodrive.adapter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.icegps.autodrive.R
import j.m.jblelib.ble.data.BleDevice
import kotlinx.android.synthetic.main.item_device.view.*

/**
 * Created by 111 on 2018/4/13.
 */
class DeviceAdapter : BaseAdapter() {
    lateinit var devices: ArrayList<BleDevice>

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflate = View.inflate(parent!!.context, R.layout.item_device, null)
        inflate.tv.text=devices.get(position).mac+"--->"+devices.get(position).name
        return inflate
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return if (devices == null) 0 else devices.size
    }
}