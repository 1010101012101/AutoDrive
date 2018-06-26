package com.icegps.autodrive.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.R;
import com.icegps.autodrive.data.WorkWidth;

import java.util.List;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class WorkWidthAdapter extends BaseQuickAdapter<WorkWidth, BaseViewHolder> {

    private Context context;

    public WorkWidthAdapter(int layoutResId, @Nullable List<WorkWidth> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, WorkWidth item) {
        if (item.workName == null) {
            helper.setVisible(R.id.iv_add, true);
        } else {
            helper
                    .setText(R.id.tv_work_width, item.workWidth + "")
                    .setText(R.id.tv_work_name, item.workName)
                    .setText(R.id.tv_wrok_unit, "m")
                    .setVisible(R.id.iv_add, false);
        }
    }
}
