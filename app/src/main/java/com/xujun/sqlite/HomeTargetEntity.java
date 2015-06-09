package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 15/5/14.
 */

@DatabaseTable(tableName = "t_home_target")
public class HomeTargetEntity implements Serializable {

    @DatabaseField(generatedId =true,canBeNull = false)
    private Integer id;

    @DatabaseField(index = true)
    private Integer aid;

    @DatabaseField(index = true)
    private Integer type;

    @DatabaseField
    private String title;

    @DatabaseField
    private String valueTitle;

    @DatabaseField(defaultValue="0",canBeNull = true)
    private Integer valueStatus;

    @DatabaseField
    private Integer  sex;

    @DatabaseField
    private Integer  height;

    @DatabaseField
    private Integer age;

    @DatabaseField
    private String weight;

    @DatabaseField
    private String value;

    @DatabaseField
    private String unit;

    @DatabaseField
    private Integer progres;

    @DatabaseField(defaultValue="0",canBeNull = true)
    private Integer isShow;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
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

    public String getValueTitle() {
        return valueTitle;
    }

    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getProgres() {
        return progres;
    }

    public void setProgres(Integer progres) {
        this.progres = progres;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Integer getValueStatus() {
        return valueStatus;
    }

    public void setValueStatus(Integer valueStatus) {
        this.valueStatus = valueStatus;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
