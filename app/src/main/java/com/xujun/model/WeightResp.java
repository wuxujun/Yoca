package com.xujun.model;

import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/5/15.
 */
public class WeightResp extends BaseResp {

    private List<WeightEntity> root;

    public List<WeightEntity> getRoot() {
        return root;
    }

    public void setRoot(List<WeightEntity> root) {
        this.root = root;
    }
}
