package com.xujun.model;

/**
 * Created by xujunwu on 15/8/9.
 */
public class ThirdLoginResp extends BaseResp{

    private int isExist;
    private Member      user;

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
}
