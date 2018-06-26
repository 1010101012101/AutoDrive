package com.icegps.autodrive.map.data;


import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;

import com.icegps.autodrive.map.utils.ThreadPool;
import com.icegps.autodrive.map.utils.LatLonUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import j.m.jblelib.ble.data.LocationStatus;

/**
 * //地图数据管理类
 * Created by 111 on 2018/2/26.
 */

public class TestData {
    private double[] bPos = new double[3];
    private double[] bECEF = new double[3];
    private double[] rPos = new double[3];
    private double[] rECEF = new double[3];
    private double[] vECEF = new double[3];
    private double[] enu = new double[3];
    private String assetsFilePath = "assets/data.txt";
    private boolean isFirstPoint = true;
    private boolean testThreAdIsStart;
    public static int dataGapTime = 10;
    private boolean startOrStop = false;

    public void getTestData(final OnTestDataListener onTestDataListener) {
        if (testThreAdIsStart) return;
        testThreAdIsStart = true;
        ThreadPool.getInstance().executeFixed(new Runnable() {
            @Override
            public void run() {
                InputStream is = getClass().getClassLoader().getResourceAsStream(assetsFilePath);
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    String s;
                    while (!TextUtils.isEmpty((s = bufferedReader.readLine())) && startOrStop == false) {
                        String[] data = s.split(",");
                        if (isFirstPoint) {
                            bPos[0] = LatLonUtils.dmm2deg(Double.parseDouble(data[2])) * LatLonUtils.D2R;
                            bPos[1] = LatLonUtils.dmm2deg(Double.parseDouble(data[4])) * LatLonUtils.D2R;
                            bPos[2] = Double.parseDouble(data[9]) + Double.parseDouble(data[11]);
                            bECEF = LatLonUtils.pos2ecef(bPos);
                            isFirstPoint = false;
                        }
                        rPos[0] = LatLonUtils.dmm2deg(Double.parseDouble(data[2])) * LatLonUtils.D2R;
                        rPos[1] = LatLonUtils.dmm2deg(Double.parseDouble(data[4])) * LatLonUtils.D2R;
                        rPos[2] = Double.parseDouble(data[9]) + Double.parseDouble(data[11]);
                        rECEF = LatLonUtils.pos2ecef(rPos);

                        vECEF[0] = rECEF[0] - bECEF[0];
                        vECEF[1] = rECEF[1] - bECEF[1];
                        vECEF[2] = rECEF[2] - bECEF[2];

                        enu = LatLonUtils.ecef2enu(bPos, vECEF);

                        double x = (enu[0] * 2);
                        double y = -(enu[1] * 2);
                        float dispersion = (float) enu[2];
                        if (dispersion == 0) dispersion = 0.001f;
                        LocationStatus locationStatus = new LocationStatus();
                        locationStatus.setX(x);
                        locationStatus.setY(y);
                        locationStatus.setLatitude(LatLonUtils.dmm2deg(Double.parseDouble(data[2])));
                        locationStatus.setLongitude(LatLonUtils.dmm2deg(Double.parseDouble(data[4])));
                        locationStatus.setDifference(dispersion);
                        locationStatus.setAltitude(rPos[2]);
                        locationStatus.setColor(Color.parseColor("#770000FF"));
                        if (onTestDataListener != null) {
                            onTestDataListener.onData(locationStatus);
                        }
                        SystemClock.sleep(dataGapTime);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startOrStop(boolean startOrStop) {
        this.startOrStop = startOrStop;

    }

    public interface OnTestDataListener {
        void onData(LocationStatus locationStatus);
    }

}
