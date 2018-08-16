package com.icegps.autodrive.activity;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.icegps.autodrive.R;
import com.icegps.autodrive.adapter.SatelliteInfoAdatper;
import com.icegps.autodrive.ble.DataManager;
import com.icegps.autodrive.ble.data.Cmds;
import com.icegps.autodrive.ble.ParseDataManager;
import com.icegps.autodrive.view.SatelliteInfoView;
import com.icegps.jblelib.ble.data.SatelliteData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

        DataManager.INSTANCE.writeCmd(Cmds.Companion.getSATELLITE(), "1");

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

        ParseDataManager.INSTANCE.addDataCallback(dataCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseDataManager.INSTANCE.removeDataCallback(dataCallBack);
        DataManager.INSTANCE.writeCmd(Cmds.Companion.getSATELLITE(), "0");
    }

    ParseDataManager.DataCallBackImpl dataCallBack = new ParseDataManager.DataCallBackImpl() {
        @Override
        public void onSatelliteData(@NotNull ArrayList<SatelliteData> satellites, @NotNull  Byte satelliteType) {
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
