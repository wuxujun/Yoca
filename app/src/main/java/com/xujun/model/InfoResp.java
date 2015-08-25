package com.xujun.model;

import com.xujun.sqlite.InfoEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/6/9.
 */
public class InfoResp extends BaseResp{

    private String          groupName;
    private List<InfoEntity> infos;

    public List<InfoEntity> getInfos() {
        return infos;
    }

    public void setInfos(List<InfoEntity> infos) {
        this.infos = infos;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
