package com.icegps.autodrive.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class WorkWidth {
    public float workWidth;
    public String workName;
    @Id
    public Long id;
    @Generated(hash = 81465602)
    public WorkWidth(float workWidth, String workName, Long id) {
        this.workWidth = workWidth;
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


}
