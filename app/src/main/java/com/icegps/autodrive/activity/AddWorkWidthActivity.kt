package com.icegps.autodrive.activity

import android.provider.SyncStateContract.Helpers.insert
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.icegps.autodrive.ListenerImpl.TextWatcherImpl
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.utils.Init
import com.icegps.autodrive.utils.StringUtils
import kotlinx.android.synthetic.main.activity_add_work_width.*
import kotlinx.android.synthetic.main.toobar.*

class AddWorkWidthActivity : BaseActivity(), View.OnFocusChangeListener {

    private var workWidth: WorkWidth? = null
    override fun layout(): Int {
        return R.layout.activity_add_work_width
    }

    override fun init() {
        tv_title.setText("播种农具")
        val intExtra = intent.getLongExtra("id", -1)
        if (intExtra != -1L) {
            workWidth = GreenDaoUtils.daoSession.workWidthDao.load(intExtra)
        }
        if (workWidth != null) {
            et.setText(StringUtils.setAccuracy(workWidth!!.workWidth.toDouble(),2))
            et1.setText(StringUtils.setAccuracy(workWidth!!.offset.toDouble(),2))
            et2.setText(StringUtils.setAccuracy(workWidth!!.distance.toDouble(),2))
            setSel(et)
            setSel(et1)
            setSel(et2)
            tv_tractors_value.setText(et.text.toString())
        }
    }


    override fun setListener() {
        iv_left.setOnClickListener { finish() }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            hintOrShow(checkedId)
        }

        et.addTextChangedListener(object : TextWatcherImpl() {
            var oldS: String = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
                oldS = s.toString()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkIntput(oldS, s.toString(), et)
            }
        })


        et1.addTextChangedListener(object : TextWatcherImpl() {

            var oldS: String = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
                oldS = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkIntput(oldS, s.toString(), et)
            }
        })



        et2.addTextChangedListener(object : TextWatcherImpl() {
            var oldS: String = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
                oldS = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkIntput(oldS, s.toString(), et)
            }
        })


        et.setOnFocusChangeListener(this)
        et1.setOnFocusChangeListener(this)
        et2.setOnFocusChangeListener(this)
    }

    fun setSel(et:EditText){
        et.setSelection(et.length())
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus){
         when(v!!.id){
             et.id->{
                 tv_tractors_value.setText(et.text.toString())
                 iv_tractors.setImageResource(R.mipmap.tractors_width)
             }
             et1.id->{
                 tv_tractors_value.setText(et1.text.toString())
                 iv_tractors.setImageResource(R.mipmap.tractors_left_right_offset)
             }
             et2.id->{
                 tv_tractors_value.setText(et2.text.toString())
                 iv_tractors.setImageResource(R.mipmap.tractors_top_bottom_offset)
             }
         }
        }
    }


    fun checkIntput(oldS: String, s: String, editText: EditText) {
        try {
            val value = s.toFloat()
            if (value > 99.99) {
                Init.showToast("最大值不应超过99.99")
                editText.setText(oldS)
                setSel(et)
            } else {
                tv_tractors_value.setText(value.toString())
                if (workWidth != null) {
                    when (editText.id) {
                        et.id -> {
                            workWidth!!.workWidth = value
                        }
                        et1.id -> {
                            workWidth!!.offset = value
                        }
                        et2.id -> {
                            workWidth!!.distance = value
                        }
                    }
                }
            }
        } catch (e: NumberFormatException) {
            tv_tractors_value.setText(0.00.toString())
            Init.showToast("请输入正确的数值")
        }
    }



    fun hintOrShow(who: Int) {
        ll.visibility = if (who == rb.id) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        super.onPause()
        if (workWidth != null) {
            insert(workWidth!!)
        }
    }

    fun insert(workWidth: WorkWidth) {
        GreenDaoUtils.daoSession.insertOrReplace(workWidth)
    }
}