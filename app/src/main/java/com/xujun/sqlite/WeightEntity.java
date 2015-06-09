package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 14/12/26.
 */
@DatabaseTable(tableName = "t_weight")
public class WeightEntity implements Serializable{

    @DatabaseField(generatedId =true, canBeNull = false)
    Integer             wid;

    @DatabaseField(index = true)
    Integer             aid;

    @DatabaseField(index = true)
    String              pickTime;

    @DatabaseField
    String              weight; //体重

    @DatabaseField
    String              fat;  //人体脂肪率

    @DatabaseField
    String              subFat;//皮下脂肪率

    @DatabaseField
    String              visFat;//内脏脂肪等级

    @DatabaseField
    String              water;//人体水分占体重百分比

    @DatabaseField
    String              BMR;// 新陈代谢 千卡路里/kg/天）

    @DatabaseField
    String              bodyAge; //身体年龄

    @DatabaseField
    String              muscle;//肌肉含量kg

    @DatabaseField
    String              bone;//骨含量kg

    @DatabaseField
    String              bmi;

    @DatabaseField(defaultValue="0")
    String              protein;

    @DatabaseField
    Integer             syncid;

    @DatabaseField(index = true)
    int                 isSync;

    @DatabaseField
    long             addtime;

    @DatabaseField
    long             changetime;

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
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

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public Integer getSyncid() {
        return syncid;
    }

    public void setSyncid(Integer syncid) {
        this.syncid = syncid;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }
}
