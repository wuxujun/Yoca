package com.xujun.model;

import java.util.List;

/**
 * Created by xujunwu on 15/6/9.
 */
public class InfoResp extends BaseResp{

    private List<ArticleInfo> root;

    public List<ArticleInfo> getRoot() {
        return root;
    }

    public void setRoot(List<ArticleInfo> root) {
        this.root = root;
    }
}
