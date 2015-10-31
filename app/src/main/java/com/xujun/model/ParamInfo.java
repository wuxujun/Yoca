package com.xujun.model;

/**
 * Created by xujunwu on 15/9/19.
 */
public class ParamInfo extends BaseResp{
    private int         id;
    private String      title;
    private String      value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
