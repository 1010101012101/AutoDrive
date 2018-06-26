package j.m.jblelib.ble.data;

/**
 * Created by 111 on 2018/1/22.
 */

public class SatelliteData {
    private byte satelliteNumber;//卫星号  bottom

    private byte satelliteUseSign;//卫星使用标志  bg  1绿0红

    private byte satelliteElevation;//卫星仰角

    private short satelliteAzimuth;//卫星方位角

    private byte satelliteSNR;//卫星信噪比    value  <=50

    private byte satelliteType;//卫星类型


    public byte getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(byte satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public byte getSatelliteUseSign() {
        return satelliteUseSign;
    }

    public void setSatelliteUseSign(byte satelliteUseSign) {
        this.satelliteUseSign = satelliteUseSign;
    }

    public byte getSatelliteElevation() {
        return satelliteElevation;
    }

    public void setSatelliteElevation(byte satelliteElevation) {
        this.satelliteElevation = satelliteElevation;
    }

    public short getSatelliteAzimuth() {
        return satelliteAzimuth;
    }

    public void setSatelliteAzimuth(short satelliteAzimuth) {
        this.satelliteAzimuth = satelliteAzimuth;
    }

    public byte getSatelliteSNR() {
        return satelliteSNR;
    }

    public void setSatelliteSNR(byte satelliteSNR) {
        this.satelliteSNR = satelliteSNR;
    }

    public byte getSatelliteType() {
        return satelliteType;
    }

    public void setSatelliteType(byte satelliteType) {
        this.satelliteType = satelliteType;
    }

    @Override
    public String toString() {
        return "SatelliteData{" +
                "satelliteNumber=" + satelliteNumber +
                ", satelliteUseSign=" + satelliteUseSign +
                ", satelliteElevation=" + satelliteElevation +
                ", satelliteAzimuth=" + satelliteAzimuth +
                ", satelliteSNR=" + satelliteSNR +
                '}';
    }
}
