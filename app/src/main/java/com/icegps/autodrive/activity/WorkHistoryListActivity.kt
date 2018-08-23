package com.icegps.autodrive.activity

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.icegps.autodrive.R
import com.icegps.autodrive.adapter.WorkHistoryAdapter
import com.icegps.autodrive.data.WorkHistory
import com.icegps.autodrive.gen.GreenDaoUtils
import com.icegps.autodrive.map.utils.FileUtils
import kotlinx.android.synthetic.main.activity_work_history_list.*
import kotlinx.android.synthetic.main.toobar.*

class WorkHistoryListActivity : BaseActivity() {
    companion object {
        var WORK_HISTORY = "WORK_HISTORY"
    }

    private var workHistoryAdapter: WorkHistoryAdapter? = null
    private var workHistorys: ArrayList<WorkHistory>? = null
    override fun layout(): Int {
        return R.layout.activity_work_history_list
    }

    override fun init() {
        workHistorys = ArrayList()
        initUi()
    }

    private fun initUi() {

        tv_title.setText("作业历史")
        iv_left.setImageResource(R.mipmap.back)
        workHistorys!!.addAll(GreenDaoUtils.daoSession.workHistoryDao.loadAll())
        if (workHistorys!!.size > 0) {
            workHistoryAdapter = WorkHistoryAdapter(R.layout.item_measure, workHistorys)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = workHistoryAdapter
            tv_no_data_hint.visibility = View.GONE
        }
    }

    override fun setListener() {
        iv_left.setOnClickListener { finish() }
        workHistoryAdapter?.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(activity, WorkHistoryMapActivity::class.java)
            intent.putExtra(WORK_HISTORY, workHistorys!!.get(position))
            startActivity(intent)
        }
        workHistoryAdapter?.setOnItemLongClickListener { adapter, view, position ->
            AlertDialog.Builder(activity).setMessage("是否删除这条历史?")
                    .setNegativeButton("不删除", null)
                    .setPositiveButton("删除", { dialogInterface: DialogInterface, i: Int ->
                        FileUtils.setDir(workHistorys!!.get(position).measuredTime).delete()
                        GreenDaoUtils.daoSession.workHistoryDao.delete(workHistorys!!.get(position))
                        workHistorys!!.removeAt(position)
                        workHistoryAdapter!!.notifyDataSetChanged()
                    })
                    .show()
            return@setOnItemLongClickListener true
        }
    }

}