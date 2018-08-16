package com.icegps.autodrive.activity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.RadioGroup
import com.icegps.autodrive.R
import com.icegps.autodrive.ble.DataManager
import com.icegps.autodrive.ble.data.Cmds
import com.icegps.autodrive.fragment.InsCourseCalibrationFragment
import com.icegps.autodrive.fragment.InsSteeringAngleZeroCalibrationFragment
import com.icegps.autodrive.fragment.InsThresholdValue
import kotlinx.android.synthetic.main.activity_ins_calibration.*
import kotlinx.android.synthetic.main.toobar.*

class InsCalibrationActivity : BaseActivity() {
    lateinit var fragments: ArrayList<Fragment>

    override fun layout(): Int {
        return R.layout.activity_ins_calibration
    }

    override fun init() {
        fragments = ArrayList()
        fragments.add(InsSteeringAngleZeroCalibrationFragment())
        fragments.add(InsCourseCalibrationFragment())
        fragments.add(InsThresholdValue())
        viewPager.adapter = fragmentPagerAdapter

        tv_title.setText("安装校准")

    }

    override fun setListener() {
        iv_left.setOnClickListener({ finish() })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                DataManager.writeCmd(Cmds.SETWORKS , "0")
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
                }
            }
        })

        radioGroup.setOnCheckedChangeListener({ radioGroup: RadioGroup, i: Int  ->
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

            }
        })
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
