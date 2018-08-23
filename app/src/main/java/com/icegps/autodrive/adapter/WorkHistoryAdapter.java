package com.icegps.autodrive.adapter;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.R;
import com.icegps.autodrive.data.WorkHistory;
import com.icegps.autodrive.utils.DateFormat;

import java.util.List;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class WorkHistoryAdapter extends BaseQuickAdapter<WorkHistory, BaseViewHolder> {


    public WorkHistoryAdapter(int layoutResId, @Nullable List<WorkHistory> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, WorkHistory item) {
        helper.setText(R.id.tv_time, DateFormat.time2Date(item.measuredTime));
    }
}
