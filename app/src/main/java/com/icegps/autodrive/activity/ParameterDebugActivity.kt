package com.icegps.autodrive.activity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.RadioGroup
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.BleWriteHelper
import com.icegps.autodrive.ble.Cmds
import com.icegps.autodrive.fragment.*
import kotlinx.android.synthetic.main.activity_parameter_debug.*
import kotlinx.android.synthetic.main.toobar.*

class ParameterDebugActivity : BaseActivity() {
    lateinit var fragments: ArrayList<Fragment>

    override fun layout(): Int {
       return R.layout.activity_parameter_debug
    }

    override fun init() {
        fragments = ArrayList()
        fragments.add(LeftElectricityFragment())
        fragments.add(RightElectricityFragment())
        fragments.add(AzimuthFragment())
        fragments.add(CourseFragment())
        fragments.add(DistanceFragment())
        tv_title.setText("参数校准")
        viewPager.adapter = fragmentPagerAdapter
        BleWriteHelper.writeCmd(Cmds.GETCONTROLV , "1" ,"200")

    }

    override fun setListener() {
        iv_left.setOnClickListener({ finish() })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                BleWriteHelper.writeCmd(Cmds.SETWORKS ,"0")
                when (position) {
                    0 -> {
                        rb1.isChecked = true
                    }
                    1 -> {
                        rb2.isChecked = true
                    }
                    2 -> {
                        rb3.isChecked = true
                    }
                    3 -> {
                        rb4.isChecked = true
                    }
                    4 -> {
                        rb5.isChecked = true
                    }
                }
            }
        })

        radioGroup.setOnCheckedChangeListener({ radioGroup: RadioGroup, i: Int ->
            when (i) {
                rb1.id -> {
                    viewPager.setCurrentItem(0)
                }
                rb2.id -> {
                    viewPager.setCurrentItem(1)
                }
                rb3.id -> {
                    viewPager.setCurrentItem(2)
                }
                rb4.id -> {
                    viewPager.setCurrentItem(3)
                }
                rb5.id -> {
                    viewPager.setCurrentItem(4)
                }
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        BleWriteHelper.writeCmd(Cmds.GETCONTROLV , "0" , "0")
        BleWriteHelper.writeCmd(Cmds.SETWORKS ,"0")
    }


    var fragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }
}