package com.icegps.autodrive.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class WorkHistory implements Serializable {
   private static final long serialVersionUID=1L;
    @Id
    public Long measuredTime;
    public float aPointX;
    public float aPointY;
    public float bPointX;
    public float bPointY;
    @Generated(hash = 277887564)
    public WorkHistory(Long measuredTime, float aPointX, float aPointY,
            float bPointX, float bPointY) {
        this.measuredTime = measuredTime;
        this.aPointX = aPointX;
        this.aPointY = aPointY;
        this.bPointX = bPointX;
        this.bPointY = bPointY;
    }
    @Generated(hash = 1907034409)
    public WorkHistory() {
    }
    public Long getMeasuredTime() {
        return this.measuredTime;
    }
    public void setMeasuredTime(Long measuredTime) {
        this.measuredTime = measuredTime;
    }
    public float getAPointX() {
        return this.aPointX;
    }
    public void setAPointX(float aPointX) {
        this.aPointX = aPointX;
    }
    public float getAPointY() {
        return this.aPointY;
    }
    public void setAPointY(float aPointY) {
        this.aPointY = aPointY;
    }
    public float getBPointX() {
        return this.bPointX;
    }
    public void setBPointX(float bPointX) {
        this.bPointX = bPointX;
    }
    public float getBPointY() {
        return this.bPointY;
    }
    public void setBPointY(float bPointY) {
        this.bPointY = bPointY;
    }


}
