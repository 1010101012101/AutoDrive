package com.icegps.jblelib.ble.data;

/**
 * Created by 111 on 2017/12/23.
 */

public class LocationStatus {
    private long date;//日期
    private double millis;//时间
    private double latitude;//纬度
    private double longitude;//经度
    private double altitude;//海拔
    private double altitude2;//副天线高度
    private float courseAngle;
    private double speed;//速度
    private int satelliteSum;//卫星数
    private int Status;//定位状态
    private float difference;//高差
    private double distance;//基线长度
    private double azimuth; //方位角
    private double elevation;//仰角
    private double x;
    private double y;
    private float delay;
    private int color;
    private double dispersion;


    private double startLatitude;//起始纬度
    private double startLongitude;//起始经度
    private double startAltitude;//起始海拔

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getStartAltitude() {
        return startAltitude;
    }

    public void setStartAltitude(double startAltitude) {
        this.startAltitude = startAltitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDispersion() {
        return dispersion;
    }

    public void setDispersion(double dispersion) {
        this.dispersion = dispersion;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public float getDifference() {
        return difference;
    }

    public void setDifference(float difference) {
        this.difference = difference;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public long getDate() {
        return date;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getMillis() {
        return millis;
    }

    public void setMillis(double millis) {
        this.millis = millis;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude2() {
        return altitude2;
    }

    public LocationStatus setAltitude2(double altitude2) {
        this.altitude2 = altitude2;
        return this;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getSatelliteSum() {
        return satelliteSum;
    }

    public void setSatelliteSum(int satelliteSum) {
        this.satelliteSum = satelliteSum;
    }

    public int getStatus() {
        return Status;
    }

    public float getCourseAngle() {
        return courseAngle;
    }

    public void setCourseAngle(float courseAngle) {
        this.courseAngle = courseAngle;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    @Override
    public String toString() {
        return "LocationStatus{" +
                "date=" + date +
                ", millis=" + millis +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", courseAngle=" + courseAngle +
                ", speedIndex=" + speed +
                ", satelliteSum=" + satelliteSum +
                ", Status=" + Status +
                ", difference=" + difference +
                ", distance=" + distance +
                ", azimuth=" + azimuth +
                ", elevation=" + elevation +
                ", delay=" + delay +
                '}';
    }
}
