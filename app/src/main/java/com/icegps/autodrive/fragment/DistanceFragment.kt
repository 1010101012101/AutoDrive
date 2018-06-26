package com.icegps.autodrive.fragment

import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.ble.ParseDataBean
import com.icegps.autodrive.map.utils.LatLonUtils
import com.icegps.autodrive.utils.Init
import j.m.jblelib.ble.BleHelper
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl
import j.m.jblelib.ble.data.LocationStatus
import kotlinx.android.synthetic.main.fragment_distance_ring.*
import kotlinx.android.synthetic.main.fragment_distance_ring.view.*
import timber.log.Timber


class DistanceFragment : BaseFragment() {
    var locationStatusA: LocationStatus = LocationStatus()
    var locationStatusB: LocationStatus = LocationStatus()
    var angle: Double = 0.0

    override fun refreshUi(parseDataBean: ParseDataBean?, type: String) {
        contentView.et_distance_offset_error.setText(parseDataBean!!.controlStatus.get(4).value.toString())
        contentView.et_course_offset_error.setText(parseDataBean!!.controlStatus.get(5).value.toString())
        if (type.equals(Cmds.CONTROLS)) {
            contentView.et_value2.setText(parseDataBean!!.controlSetValues.get(4).values[1].toString())
            contentView.et_value3.setText(parseDataBean!!.controlSetValues.get(4).values[2].toString())
            contentView.et_value4.setText(parseDataBean!!.controlSetValues.get(4).values[3].toString())
            contentView.et_value5.setText(parseDataBean!!.controlSetValues.get(4).values[4].toString())
        }
        BleHelper.addBleCallback(bleStatusCallBackImpl)
    }

    override fun init() {
        getValue("5")
        contentView.tv_get.setOnClickListener({
            getValue("5")
        })
        contentView.tv_send.setOnClickListener({
            var value2 = et_value2.text.toString()
            var value3 = et_value3.text.toString()
            var value4 = et_value4.text.toString()
            var value5 = et_value5.text.toString()
            if (TextUtils.isEmpty(value2)) value2 = "0"
            if (TextUtils.isEmpty(value3)) value3 = "0"
            if (TextUtils.isEmpty(value4)) value4 = "0"
            if (TextUtils.isEmpty(value5)) value5 = "0"
            setValue("5", "0" + "," + value2 + "," + value3 + "," + value4 + "," + value5)
        })

        contentView.tv_get_a.setOnClickListener({
            if (contentView.tv_test.isChecked) {
                Init.showToast("正在测试中,请不要重复操作")
                return@setOnClickListener
            }
            if (locationStatus != null) {
                contentView.tv_tag_a.isSelected = true
                contentView.tv_tag_a.visibility = View.VISIBLE
                locationStatusA.x = locationStatus!!.x
                locationStatusA.y = locationStatus!!.y
                locationStatusA.latitude = locationStatus!!.latitude
                locationStatusA.longitude = locationStatus!!.longitude
            }
        })
        contentView.tv_get_b.setOnClickListener({
            if (contentView.tv_test.isChecked) {
                Init.showToast("正在测试中,请不要重复操作")
                return@setOnClickListener
            }
            if (!contentView.tv_tag_a.isSelected) {
                Init.showToast("请先记录A点")
                return@setOnClickListener
            }
            if (locationStatus != null) {
                contentView.tv_tag_b.isSelected = true
                contentView.tv_tag_b.visibility = View.VISIBLE
                locationStatusB.x = locationStatus!!.x
                locationStatusB.y = locationStatus!!.y
                locationStatusB.latitude = locationStatus!!.latitude
                locationStatusB.longitude = locationStatus!!.longitude
            }
        })

        contentView.tv_test.setOnCheckedChangeListener({ compoundButton: CompoundButton, b: Boolean ->
            if (locationStatus != null) {
                angle = Math.toDegrees(Math.atan2(
                        locationStatusB.x - locationStatusA.x,
                        locationStatusB.y - locationStatusA.y))

                if (angle < 0.0) angle += 360.0

                if (b) {
                    contentView.tv_test.text = "取消"
                    sendTest("5", "1" + ",1," + locationStatusA.latitude + "," + locationStatusA.longitude + "," + locationStatusB.latitude + "," + locationStatusB.longitude)
                } else {
                    contentView.tv_test.text = "测试"
                    sendTest("0", "0")
                    contentView.tv_tag_a.isSelected = false
                    contentView.tv_tag_b.isSelected = false
                    contentView.tv_tag_a.visibility = View.INVISIBLE
                    contentView.tv_tag_b.visibility = View.INVISIBLE
                }
            }
        })


    }

    override fun childImplView(): View {
        val view = View.inflate(activity, R.layout.fragment_distance_ring, null)
        return view
    }

    var locationStatus: LocationStatus? = null

    var bleStatusCallBackImpl = object : BleStatusCallBackImpl() {
        override fun onLocationData(locationStatus: LocationStatus) {
            super.onLocationData(locationStatus)
            val latLon2Xy = LatLonUtils.latLon2Xy(doubleArrayOf(locationStatus.latitude, locationStatus.longitude, locationStatus.altitude))
            this@DistanceFragment.locationStatus!!.x = latLon2Xy[0]
            this@DistanceFragment.locationStatus!!.y = latLon2Xy[1]
        }
    }


}