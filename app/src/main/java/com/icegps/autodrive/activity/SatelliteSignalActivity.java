package com.icegps.autodrive.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.icegps.autodrive.R;
import com.icegps.autodrive.adapter.SatelliteInfoAdatper;
import com.icegps.autodrive.ble.BleWriteHelper;
import com.icegps.autodrive.ble.Cmds;
import com.icegps.autodrive.view.SatelliteInfoView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import j.m.jblelib.ble.BleHelper;
import j.m.jblelib.ble.BleStatusCallBackImpl.BleStatusCallBackImpl;
import j.m.jblelib.ble.bleoperation.BleWrite;
import j.m.jblelib.ble.data.SatelliteData;

/**
 * Created by 111 on 2018/3/16.
 */

public class SatelliteSignalActivity extends BaseActivity {
    private ArrayList<SatelliteData> gpsData = new ArrayList<>();
    private ArrayList<SatelliteData> bdData = new ArrayList<>();
    private ArrayList<SatelliteData> gloData = new ArrayList<>();
    private SatelliteInfoAdatper gpsAdapter;
    private SatelliteInfoAdatper bdAdapter;
    private SatelliteInfoAdatper gloAdapter;
    private RecyclerView rvGps;
    private RecyclerView rvBd;
    private RecyclerView rvGlo;
    private SatelliteInfoView satelliteInfoView;

    @Override
    public int layout() {
        return R.layout.activity_satellite_signal;
    }

    private void findView() {
        rvGps = findViewById(R.id.rv_gps);
        rvBd = findViewById(R.id.rv_bd);
        rvGlo = findViewById(R.id.rv_glo);
        satelliteInfoView = findViewById(R.id.satelliteInfoView);
    }

    public void init() {

        findView();


        BleWriteHelper.INSTANCE.writeCmd(Cmds.Companion.getSATELLITE(), "1");

        gpsAdapter = new SatelliteInfoAdatper(R.layout.item_satellite_info, gpsData);
        bdAdapter = new SatelliteInfoAdatper(R.layout.item_satellite_info, bdData);
        gloAdapter = new SatelliteInfoAdatper(R.layout.item_satellite_info, gloData);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvGps.setLayoutManager(llm);

        llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvBd.setLayoutManager(llm);

        llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvGlo.setLayoutManager(llm);

        rvGps.setAdapter(gpsAdapter);
        rvBd.setAdapter(bdAdapter);
        rvGlo.setAdapter(gloAdapter);
        findViewById(R.id.iv_left).setOnClickListener(v -> {
            finish();
        });

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("卫星信号");

    }




    @Override
    public void setListener() {
        BleHelper.INSTANCE.addBleCallback(bleStatusCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleHelper.INSTANCE.removeBleCallback(bleStatusCallBack);
        BleWriteHelper.INSTANCE.writeCmd(Cmds.Companion.getSATELLITE(), "0");
    }

    BleStatusCallBackImpl bleStatusCallBack = new BleStatusCallBackImpl() {
        @Override
        public void onSatelliteData(@NotNull ArrayList<SatelliteData> satellites, byte satelliteType) {
            super.onSatelliteData(satellites, satelliteType);
            switch (satelliteType) {
                case 0:
                    gpsData.clear();
                    gpsData.addAll(satellites);
                    gpsAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    bdData.clear();
                    bdData.addAll(satellites);
                    bdAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    gloData.clear();
                    gloData.addAll(satellites);
                    gloAdapter.notifyDataSetChanged();
                    break;
            }
            satelliteInfoView.drawSatellite(gpsData, bdData, gloData);
        }
    };
}
