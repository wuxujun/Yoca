package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xujun.util.StringUtil;

import java.io.Serializable;

/**
 * Created by xujunwu on 15/10/15.
 */
@DatabaseTable(tableName = "t_send_record")
public class SendRecord implements Serializable {
    @DatabaseField(generatedId =true, canBeNull = false)
    Integer             id;
    @DatabaseField
    String              devAddress;

    @DatabaseField
    String              data;

    @DatabaseField
    Integer             status;

    @DatabaseField
    String              addTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDevAddress() {
        return devAddress;
    }

    public void setDevAddress(String devAddress) {
        this.devAddress = devAddress;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
