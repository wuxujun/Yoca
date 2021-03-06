package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 14/12/26.
 */
@DatabaseTable(tableName = "t_health")
public class HealthEntity implements Serializable{

    @DatabaseField(id=true,canBeNull = false)
    long             hid;

    @DatabaseField(index = true)
    long             aid;

    @DatabaseField(defaultValue = "0")
    int                 dataType;  //1 周  2 月  3 年

    @DatabaseField(index = true)
    String              pickTime;

    @DatabaseField
    String              weight; //体重  1

    @DatabaseField
    String              fat;  //人体脂肪率 3

    @DatabaseField
    String              subFat;//皮下脂肪率 4

    @DatabaseField
    String              visFat;//内脏脂肪等级 5

    @DatabaseField
    String              water;//人体水分占体重百分比  7

    @DatabaseField
    String              BMR;// 新陈代谢 千卡路里/kg/天）6

    @DatabaseField
    String              bodyAge; //身体年龄 11

    @DatabaseField
    String              muscle;//肌肉含量kg 8

    @DatabaseField
    String              bone;//骨含量kg 9

    @DatabaseField
    String              bmi; // 2

    @DatabaseField(defaultValue="0")
    String              protein; //10 蛋白质量

    @DatabaseField(defaultValue = "0")
    String              sholai;


    public long getHid() {
        return hid;
    }

    public void setHid(long hid) {
        this.hid = hid;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getPickTime() {
        return pickTime;
    }

    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSubFat() {
        return subFat;
    }

    public void setSubFat(String subFat) {
        this.subFat = subFat;
    }

    public String getVisFat() {
        return visFat;
    }

    public void setVisFat(String visFat) {
        this.visFat = visFat;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getBMR() {
        return BMR;
    }

    public void setBMR(String BMR) {
        this.BMR = BMR;
    }

    public String getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(String bodyAge) {
        this.bodyAge = bodyAge;
    }

    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }

    public String getBone() {
        return bone;
    }

    public void setBone(String bone) {
        this.bone = bone;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getSholai() {
        return sholai;
    }

    public void setSholai(String sholai) {
        this.sholai = sholai;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
