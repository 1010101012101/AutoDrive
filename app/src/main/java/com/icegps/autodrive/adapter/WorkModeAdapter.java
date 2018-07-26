package com.icegps.autodrive.adapter;


import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.R;
import com.icegps.autodrive.data.WorkWidth;

import java.util.List;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class WorkModeAdapter extends BaseQuickAdapter<WorkWidth, BaseViewHolder> {

    private Context context;

    public WorkModeAdapter(int layoutResId, @Nullable List<WorkWidth> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, WorkWidth item) {
        helper.setText(R.id.tv_work_name, item.workName);
    }
}
