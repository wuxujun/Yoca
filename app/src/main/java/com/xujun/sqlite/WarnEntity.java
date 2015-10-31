package com.xujun.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xujunwu on 14/12/21.
 */
@DatabaseTable(tableName = "t_warn")
public class WarnEntity implements Serializable{


    @DatabaseField(id=true,canBeNull = false)
    long             wid;

    @DatabaseField
    Integer             type;

    @DatabaseField
    String              value;

    @DatabaseField
    Integer             repeats;

    @DatabaseField
    Integer             week_mon;

    @DatabaseField
    Integer             week_tue;

    @DatabaseField
    Integer             week_wed;

    @DatabaseField
    Integer             week_thu;

    @DatabaseField
    Integer             week_fri;

    @DatabaseField
    Integer             week_sat;

    @DatabaseField
    Integer             week_sun;

    @DatabaseField
    Integer             hours;

    @DatabaseField
    Integer             minutes;

    @DatabaseField
    String              note;

    @DatabaseField
    Integer             status;

    @DatabaseField
    int                 isSync;

    @DatabaseField
    long             addtime;

    @DatabaseField
    long             changetime;


    public long getWId() {
        return wid;
    }

    public void setWId(long wid) {
        this.wid = wid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getRepeats() {
        return repeats;
    }

    public void setRepeats(Integer repeats) {
        this.repeats = repeats;
    }

    public Integer getWeek_mon() {
        return week_mon;
    }

    public void setWeek_mon(Integer week_mon) {
        this.week_mon = week_mon;
    }

    public Integer getWeek_tue() {
        return week_tue;
    }

    public void setWeek_tue(Integer week_tue) {
        this.week_tue = week_tue;
    }

    public Integer getWeek_wed() {
        return week_wed;
    }

    public void setWeek_wed(Integer week_wed) {
        this.week_wed = week_wed;
    }

    public Integer getWeek_thu() {
        return week_thu;
    }

    public void setWeek_thu(Integer week_thu) {
        this.week_thu = week_thu;
    }

    public Integer getWeek_fri() {
        return week_fri;
    }

    public void setWeek_fri(Integer week_fri) {
        this.week_fri = week_fri;
    }

    public Integer getWeek_sat() {
        return week_sat;
    }

    public void setWeek_sat(Integer week_sat) {
        this.week_sat = week_sat;
    }

    public Integer getWeek_sun() {
        return week_sun;
    }

    public void setWeek_sun(Integer week_sun) {
        this.week_sun = week_sun;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
