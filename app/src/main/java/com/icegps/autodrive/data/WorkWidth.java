package com.icegps.autodrive.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class WorkWidth  {
    public float workWidth;
    public float offset;
    public float distance;
    public String workName;
    @Id
    public Long id;
    @Generated(hash = 200655742)
    public WorkWidth(float workWidth, float offset, float distance, String workName,
            Long id) {
        this.workWidth = workWidth;
        this.offset = offset;
        this.distance = distance;
        this.workName = workName;
        this.id = id;
    }
    @Generated(hash = 489605747)
    public WorkWidth() {
    }
    public float getWorkWidth() {
        return this.workWidth;
    }
    public void setWorkWidth(float workWidth) {
        this.workWidth = workWidth;
    }
    public String getWorkName() {
        return this.workName;
    }
    public void setWorkName(String workName) {
        this.workName = workName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public float getOffset() {
        return this.offset;
    }
    public void setOffset(float offset) {
        this.offset = offset;
    }
    public float getDistance() {
        return this.distance;
    }
    public void setDistance(float distance) {
        this.distance = distance;
    }


}
