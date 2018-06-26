package com.icegps.autodrive.adapter;


import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.App;
import com.icegps.autodrive.R;
import com.icegps.autodrive.ble.BleWriteHelper;
import com.icegps.autodrive.ble.Cmds;

import java.util.List;

import j.m.jblelib.ble.data.SatelliteData;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class RadioAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public static int selItem = 1;

    /**
     * @param layoutResId
     * @param data
     */
    public RadioAdapter(int layoutResId, @Nullable List<Integer> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Integer item) {
        helper.setText(R.id.tv_name, "信道" + item)
                .setChecked(R.id.ckeckBox, false)
                .setChecked(R.id.ckeckBox, selItem == item);
        CheckBox ck = helper.getView(R.id.ckeckBox);
        ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selItem = helper.getPosition() + 1;
                BleWriteHelper.INSTANCE.writeCmd(Cmds.Companion.getSETRADIO() , RadioAdapter.selItem+"");
                notifyDataSetChanged();
            }
        });
    }
}
