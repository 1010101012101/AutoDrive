package com.icegps.autodrive.activity

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import com.icegps.autodrive.R
import com.icegps.autodrive.R.id.*
import com.icegps.autodrive.adapter.WorkWidthAdapter
import com.icegps.autodrive.data.WorkWidth
import com.icegps.autodrive.gen.GreenDaoUtils
import kotlinx.android.synthetic.main.activity_work_width.*
import kotlinx.android.synthetic.main.toobar.*

class WorkWidthActivity : BaseActivity() {
    private var workWidths: ArrayList<WorkWidth> = ArrayList()
    private lateinit var workWidthAdapter: WorkWidthAdapter
    override fun layout(): Int {
        return R.layout.activity_work_width
    }

    override fun init() {
        tv_title.setText(resources.getString(R.string.work_width))
        workWidthAdapter=WorkWidthAdapter(R.layout.item_work_width,workWidths,activity)
        val gridLayoutManager = GridLayoutManager(activity, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = workWidthAdapter
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
    override fun setListener() {
        iv_left.setOnClickListener { finish() }
        workWidthAdapter.setOnItemClickListener { adapter, view, position ->
            if (position==workWidths.size-1){
             startActivity(Intent(activity,AddWorkWidthActivity::class.java))
            }
        }
    }


    fun refresh(){
        val loadAll = GreenDaoUtils.daoSession.workWidthDao.loadAll()
        workWidths.clear()
        if (loadAll == null||loadAll.size==0) {
            workWidths.add(WorkWidth(0f,"播种",0))
            workWidths.add(WorkWidth(0f,"打药",0))
            workWidths.add(WorkWidth(0f,"施肥",0))
            workWidths.add(WorkWidth(0f,"开沟",0))
            workWidths.add(WorkWidth(0f,"犁地",0))
        }else{
            workWidths.addAll(loadAll)
        }
        workWidthAdapter.notifyDataSetChanged()

        workWidths.add(WorkWidth())



    }
}