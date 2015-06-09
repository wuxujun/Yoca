package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 15/4/23.
 */

@DatabaseTable(tableName = "t_config")
public class ConfigEntity implements Serializable{

    @DatabaseField(generatedId =true, canBeNull = false)
    Integer             id;

    @DatabaseField(defaultValue="0",canBeNull = true)
    Integer             type;

    @DatabaseField
    Integer             week;

    @DatabaseField
    String              title;

    @DatabaseField
    String              beginDay;

    @DatabaseField
    String              endDay;

    @DatabaseField(index = true)
    Integer             status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeginDay() {
        return beginDay;
    }

    public void setBeginDay(String beginDay) {
        this.beginDay = beginDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }
}
