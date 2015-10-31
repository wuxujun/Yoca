package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 14/12/18.
 */
@DatabaseTable(tableName = "t_account")
public class AccountEntity implements Serializable{

    @DatabaseField(id=true,canBeNull = false)
    long             id;

    // 0家庭成员  1 主帐号  2 增加
    @DatabaseField(defaultValue="0",canBeNull = true)
    Integer             type;
    @DatabaseField
    String              userNick;

    @DatabaseField(defaultValue ="0",canBeNull = true)
    Integer             accountType;

    @DatabaseField(defaultValue="0",canBeNull = true)
    Integer             sex;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              birthday;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    Integer             height;

    @DatabaseField(defaultValue ="0",canBeNull = true)
    Integer             age;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              avatar;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    Integer             targetType;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              targetWeight;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              targetFat;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              doneTime;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              weight;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              fat;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              subFat;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              visFat;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              water;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              bmr;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              bodyAge;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              muscle;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              bone;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              bmi;

    @DatabaseField(defaultValue="0",canBeNull = true)
    String              protein;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    String              remark;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    Integer             status;

    @DatabaseField(defaultValue="0",canBeNull = true)
    int                 isSync;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    long             createtime;

    @DatabaseField(defaultValue = "0",canBeNull = true)
    long             changetime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeshan(Integer height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
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

    public String getBmr() {
        return bmr;
    }

    public void setBmr(String bmr) {
        this.bmr = bmr;
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

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getTargetFat() {
        return targetFat;
    }

    public void setTargetFat(String targetFat) {
        this.targetFat = targetFat;
    }
}
