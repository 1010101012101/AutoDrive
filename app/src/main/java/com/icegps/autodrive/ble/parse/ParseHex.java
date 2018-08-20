package com.icegps.autodrive.ble.parse;


import com.icegps.jblelib.ble.data.LocationStatus;
import com.icegps.jblelib.ble.data.SatelliteData;
import com.icegps.jblelib.ble.utils.BinaryUtils;
import com.icegps.jblelib.ble.utils.BleLog;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


/**
 * Created by 111 on 2017/12/23.
 */


public class ParseHex implements Parse {
    public static final byte head1 = (byte) 0xB5;
    public static final byte head2 = (byte) 0x62;
    public static final byte locationDataClass = (byte) 0x1C;
    public static final byte locationDataId = (byte) 0xE1;
    public static final byte satelliteDataClass = (byte) 0x1C;
    public static final byte satelliteDataId = (byte) 0xE6;
    private final LocationStatus locationStatus;
    private int dataSumLenght = 0;
    private java.util.ArrayList<SatelliteData> satelliteDatas = new java.util.ArrayList();
    private int pos = 0;
    private static ByteBuffer b;
    private OnHexCallback onHexCallback;

    public ParseHex() {
        locationStatus = new LocationStatus();
        b = getByteBuffer(6);
    }

    public boolean isWorking() {
        return pos != 0;
    }

    public synchronized void parseData(byte bt) {
        if (pos == 0) {
            if (b.get(0) == (byte) 0xB5 && bt == (byte) 0x62) {
                b.put(1, (byte) 0x62);
                pos = 2;
            } else {
                b.put(0, bt);
            }
        } else {
            b.put(pos++, bt);
            if (pos == 6) {
                dataSumLenght = Integer.parseInt(BinaryUtils.bytesToHexString(new byte[]{b.get(5), b.get(4)}), 16) + 8;
                if (dataSumLenght > 256) {
                    pos = 0;
                    BleLog.Companion.d(ParseHex.class, "超长");
                }
                byte[] array = b.array();

                b = getByteBuffer(dataSumLenght);
                try {
                    b.put(array);
                } catch (BufferOverflowException e) {
                    pos = 0;
                }

            }
            if (pos == dataSumLenght) {
                pos = 0;
                boolean checksum = checksum(b, dataSumLenght);
                if (checksum) {
                    if (onHexCallback != null) {
                        onHexCallback.onHex(b.array());
                    }
                    getData();
                }
                b = getByteBuffer(6);
            }
        }
    }


    private void getData() {
        int q = 0;
        // header
        q += 2;
        // class
        byte dataClass = b.get(q);
        q++;
        // ID
        byte dataId = b.get(q);
        q++;
        // lengthIndex
        q += 2;
        if (dataClass == locationDataClass && dataId == locationDataId) {
            getLocationData(q);
        } else if (dataClass == satelliteDataClass && dataId == satelliteDataId) {
            getSatelliteData(q);
        }
    }

    private void getLocationData(int q) {
        //日期 int
        locationStatus.setDate(b.getInt(q));
        q += 4;

        //时间 int
        locationStatus.setMillis(b.getInt(q) / 1000d);
        q += 4;

        //纬度 double
        locationStatus.setLatitude(b.getDouble(q));
        q += 8;

        //经度 double
        locationStatus.setLongitude(b.getDouble(q));
        q += 8;

        // 海拔 double
        locationStatus.setAltitude(b.getDouble(q));
        q += 8;

        //航向角 int
        locationStatus.setCourseAngle(b.getInt(q) / 1000f);
        q += 4;

        //速度 int
        locationStatus.setSpeed(b.getInt(q) / 1000f);
        q += 4;

        //卫星数 int
        locationStatus.setSatelliteSum(b.get(q));
        q += 1;

        //定位状态 byte
        locationStatus.setStatus(b.get(q));
        q += 1;

        //延时 int
        locationStatus.setDelay(b.getFloat(q));
        q += 4;

        //基线长度 int
        locationStatus.setDistance(b.getInt(q) / 1000d);
        q += 4;

        //方位角 int
        locationStatus.setAzimuth(b.getInt(q) / 1000d);
        q += 4;

        //仰角 int
        locationStatus.setElevation(b.getInt(q) / 1000d);


        double v = locationStatus.getAltitude() + locationStatus.getDistance() * Math.sin(locationStatus.getElevation() * (Math.PI / 180.0));

        locationStatus.setAltitude2(v);

        locationStatus.setDispersion(locationStatus.getDistance() * Math.sin(locationStatus.getElevation() * (Math.PI / 180)));
        if (onHexCallback != null) {
            onHexCallback.onLocationData(locationStatus);
        }
    }

    private void getSatelliteData(int q) {
        satelliteDatas.clear();
        byte satelliteType = b.get(q);
        q += 1;
        byte count = b.get(q);
        q += 1;
        for (byte i = 0; i < count; i++) {
            SatelliteData satelliteData = new SatelliteData();
            satelliteData.setSatelliteType(satelliteType);
            satelliteData.setSatelliteNumber(b.get(q));
            q += 1;
            satelliteData.setSatelliteUseSign(b.get(q));
            q += 1;
            satelliteData.setSatelliteElevation(b.get(q));
            q += 1;
            satelliteData.setSatelliteAzimuth(b.getShort(q));
            q += 2;
            satelliteData.setSatelliteSNR(b.get(q));
            q += 1;
            satelliteDatas.add(satelliteData);

        }
        ArrayList<SatelliteData> satellites = new ArrayList<>();

        satellites.addAll(satelliteDatas);
        if (onHexCallback != null) {
            onHexCallback.onSatelliteData(satellites, satelliteType);
        }
    }


    private ByteBuffer getByteBuffer(int dataSumLenght) {
        return ByteBuffer.allocate(dataSumLenght).order(ByteOrder.LITTLE_ENDIAN);
    }

    static boolean checksum(ByteBuffer buff, int len) {
        int cka = 0, ckb = 0;
        int i;
        for (i = 2; i < len - 2; i++) {
            cka += (int) buff.get(i) & 0x000000FF;
            cka = cka & 0x000000FF;
            ckb += cka;
            ckb = ckb & 0x000000FF;
        }
        return cka == ((int) buff.get(len - 2) & 0x000000FF) && ckb == ((int) buff.get(len - 1) & 0x000000FF);
    }

    public void setOnHexCallback(OnHexCallback onHexCallback) {
        this.onHexCallback = onHexCallback;
    }


    public interface OnHexCallback {

        void onHex(byte[] bytes);

        void onLocationData(LocationStatus locationStatus);

        void onSatelliteData(ArrayList<SatelliteData> satellites, byte satelliteType);
    }
}
