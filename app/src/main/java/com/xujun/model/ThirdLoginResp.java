package com.xujun.model;

import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by xujunwu on 15/8/9.
 */
public class ThirdLoginResp extends BaseResp{

    private int isExist;
    private Member      user;

    private List<MemberInfo> memberInfos;

    public int getIsExist() {
        return isExist;
    }

    public void setIsExist(int isExist) {
        this.isExist = isExist;
    }

    public Member getUser() {
        return user;
    }


    public void setUser(Member user) {
        this.user = user;
    }

    public List<MemberInfo> getMemberInfos() {
        return memberInfos;
    }

    public void setMemberInfos(List<MemberInfo> memberInfos) {
        this.memberInfos = memberInfos;
    }
}
