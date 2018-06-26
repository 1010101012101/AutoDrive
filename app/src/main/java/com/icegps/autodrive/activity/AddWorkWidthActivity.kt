package com.icegps.autodrive.activity

import android.view.View
import com.icegps.autodrive.ListenerImpl.TextWatcherImpl
import com.icegps.autodrive.R
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import kotlinx.android.synthetic.main.activity_add_work_width.*
import kotlinx.android.synthetic.main.toobar.*

class AddWorkWidthActivity : BaseActivity() {
    override fun layout(): Int {
        return R.layout.activity_add_work_width
    }

    override fun init() {
        tv_title.setText("播种农具")
    }

    override fun setListener() {
        iv_left.setOnClickListener { finish() }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            hintOrShow(checkedId)
        }
        tv_confirm.setOnClickListener {
            et.text.toString().toFloat()
        }
        tv_confirm2.setOnClickListener {
            et2.text.toString().toFloat()
        }
        tv_confirm3.setOnClickListener {
            et3.text.toString().toFloat()
        }


        et.addTextChangedListener(object : TextWatcherImpl() {
            var oldValue = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
                oldValue = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString().toFloat()
                if (value > 1000) {
                    et.setText(oldValue)
//                    tv_tractors_value.setText(oldValue)
                    et.setSelection(et.length())
                } else {
                    tv_tractors_value.setText(s.toString())
                }
            }
        })


        et2.addTextChangedListener(object : TextWatcherImpl() {
            var str = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tv_tractors_value2.setText(s.toString())
            }
        })


        et3.addTextChangedListener(object : TextWatcherImpl() {
            var str = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tv_tractors_value3.setText(s.toString())
            }
        })
    }

    fun hintOrShow(who: Int) {
        ll.visibility = if (who == rb.id) View.VISIBLE else View.GONE
        ll2.visibility = if (who == rb2.id) View.VISIBLE else View.GONE
        ll3.visibility = if (who == rb3.id) View.VISIBLE else View.GONE
    }

    fun insert(workWidth: WorkWidth) {
        GreenDaoUtils.daoSession.insertOrReplace(workWidth)
    }
}