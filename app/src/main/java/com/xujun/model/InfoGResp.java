package com.xujun.model;

import java.util.List;

/**
 * Created by xujunwu on 15/8/6.
 */
public class InfoGResp extends BaseResp{
    private List<InfoResp> root;

    public List<InfoResp> getRoot() {
        return root;
    }

    public void setRoot(List<InfoResp> root) {
        this.root = root;
    }
}
