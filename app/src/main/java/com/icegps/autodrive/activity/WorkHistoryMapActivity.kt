package com.icegps.autodrive.activity

import android.graphics.Path
import android.view.View
import android.widget.ImageView
import com.icegps.autodrive.R
import com.icegps.autodrive.constant.Cons
import com.icegps.autodrive.data.WorkHistory
import com.icegps.autodrive.map.utils.BitmapProviderUtils
import kotlinx.android.synthetic.main.activity_work_history_map.*

class WorkHistoryMapActivity : BaseActivity() {
    override fun layout(): Int {
        return R.layout.activity_work_history_map
    }

    override fun init() {
        val workHistory = intent.getSerializableExtra(WorkHistoryListActivity.WORK_HISTORY) as WorkHistory
        map_view.setSize(Cons.MAP_WIDTH, Cons.MAP_HEIGHT)
        val bitmapProviderUtils = BitmapProviderUtils()
        bitmapProviderUtils.measuredTime = workHistory.measuredTime
        map_view.bitmapProvider = bitmapProviderUtils
        map_view.addMarker(workHistory.aPointX.toDouble(), workHistory.aPointY.toDouble(), createMarkerIv(R.mipmap.a_point))
        map_view.addMarker(workHistory.bPointX.toDouble(), workHistory.bPointY.toDouble(), createMarkerIv(R.mipmap.b_point))
        var path = Path()
        path.moveTo(workHistory.aPointX, workHistory.aPointY)
        path.lineTo(workHistory.bPointX, workHistory.bPointY)
        map_view.drawPath(path, null)

        bitmapProviderUtils.onLoadSDBitmap = object : BitmapProviderUtils.OnLoadSDBitmap {
            override fun onCompleted() {
                runOnUiThread {
                    map_view.requestGetTile()
                    map_view.render()
                }
            }
        }
    }

    override fun setListener() {
    }


    private fun createMarkerIv(res: Int): View {
        var resIv = ImageView(activity)
        resIv.setImageResource(res)
        return resIv
    }
}