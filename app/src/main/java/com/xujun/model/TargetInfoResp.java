package com.xujun.model;

import com.xujun.sqlite.ConfigEntity;
import com.xujun.sqlite.TargetEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/4/20.
 */
public class TargetInfoResp extends BaseResp{

    private List<TargetEntity> targetList;
    private List<ConfigEntity> configs;

    public List<ConfigEntity> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigEntity> configs) {
        this.configs = configs;
    }

    public List<TargetEntity> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<TargetEntity> targetList) {
        this.targetList = targetList;
    }
}
