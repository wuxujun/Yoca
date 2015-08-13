package com.xujun.model;

import java.util.List;

/**
 * Created by xujunwu on 15/6/9.
 */
public class InfoResp extends BaseResp{

    private String          groupName;
    private List<ArticleInfo> infos;

    public List<ArticleInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<ArticleInfo> infos) {
        this.infos = infos;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
