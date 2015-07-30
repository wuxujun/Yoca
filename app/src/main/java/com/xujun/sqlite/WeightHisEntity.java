package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 14/12/26.
 */
@DatabaseTable(tableName = "t_weight_his")
public class WeightHisEntity implements Serializable{

    @DatabaseField(generatedId =true, canBeNull = false)
    Integer             wid;

    @DatabaseField(index = true)
    Integer             aid;

    @DatabaseField(index = true)
    String              pickTime;

    @DatabaseField
    Double              weight; //体重

    @DatabaseField
    Double              fat;  //人体脂肪率

    @DatabaseField
    Double              subFat;//皮下脂肪率

    @DatabaseField
    Double              visFat;//内脏脂肪等级

    @DatabaseField
    Double              water;//人体水分占体重百分比

    @DatabaseField
    Double              BMR;// 新陈代谢 千卡路里/kg/天）

    @DatabaseField
    Integer              bodyAge; //身体年龄

    @DatabaseField
    Double              muscle;//肌肉含量kg

    @DatabaseField
    Double              bone;//骨含量kg

    @DatabaseField
    Double              bmi;//BMI

    @DatabaseField(defaultValue="0")
    Double              protein;//蛋白质

    @DatabaseField(defaultValue="0")
    Integer             syncid;

    @DatabaseField(index = true)
    int                 isSync;
    @DatabaseField
    long             addtime;

    @DatabaseField
    long             changetime;

    @DatabaseField
    String              bust;

    @DatabaseField
    String              waistline;

    @DatabaseField
    String              hips;

    @DatabaseField
    String              avatar;

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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getSubFat() {
        return subFat;
    }

    public void setSubFat(Double subFat) {
        this.subFat = subFat;
    }

    public Double getVisFat() {
        return visFat;
    }

    public void setVisFat(Double visFat) {
        this.visFat = visFat;
    }

    public Double getWater() {
        return water;
    }

    public void setWater(Double water) {
        this.water = water;
    }

    public Double getBMR() {
        return BMR;
    }

    public void setBMR(Double BMR) {
        this.BMR = BMR;
    }

    public Integer getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(Integer bodyAge) {
        this.bodyAge = bodyAge;
    }

    public Double getMuscle() {
        return muscle;
    }

    public void setMuscle(Double muscle) {
        this.muscle = muscle;
    }

    public Double getBone() {
        return bone;
    }

    public void setBone(Double bone) {
        this.bone = bone;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
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

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Integer getSyncid() {
        return syncid;
    }

    public void setSyncid(Integer syncid) {
        this.syncid = syncid;
    }

    public String getBust() {
        return bust;
    }

    public void setBust(String bust) {
        this.bust = bust;
    }

    public String getWaistline() {
        return waistline;
    }

    public void setWaistline(String waistline) {
        this.waistline = waistline;
    }

    public String getHips() {
        return hips;
    }

    public void setHips(String hips) {
        this.hips = hips;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
