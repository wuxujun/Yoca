package com.xujun.model;

import com.xujun.sqlite.WeightHisEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/5/15.
 */
public class WeightHisResp extends BaseResp{

    private List<WeightHisEntity> root;

    public List<WeightHisEntity> getRoot() {
        return root;
    }

    public void setRoot(List<WeightHisEntity> root) {
        this.root = root;
    }
}
