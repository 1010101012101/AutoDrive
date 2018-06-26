package com.icegps.autodrive.adapter;


import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.App;
import com.icegps.autodrive.R;
import com.icegps.autodrive.utils.Init;

import java.util.List;

import j.m.jblelib.ble.data.SatelliteData;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class SatelliteInfoAdatper extends BaseQuickAdapter<SatelliteData, BaseViewHolder> {

    public SatelliteInfoAdatper(int layoutResId, @Nullable List<SatelliteData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SatelliteData satelliteData) {
        if (satelliteData == null) return;
        if (satelliteData.getSatelliteSNR() > 50) satelliteData.setSatelliteSNR((byte) 50);
        helper
                .setProgress(R.id.pb, satelliteData.getSatelliteSNR())
                .setText(R.id.tv_top, String.valueOf(satelliteData.getSatelliteSNR()))
                .setText(R.id.tv_bottom, String.valueOf(satelliteData.getSatelliteNumber()));
        ProgressBar pb = helper.getView(R.id.pb);

        switch (satelliteData.getSatelliteUseSign()) {
            case 0:
                pb.setProgressDrawable(Init.context.getResources().getDrawable(R.drawable.pb_bg_red));
                break;
            case 1:
                pb.setProgressDrawable(Init.context.getResources().getDrawable(R.drawable.pb_bg_green));
                break;
        }


    }
}
