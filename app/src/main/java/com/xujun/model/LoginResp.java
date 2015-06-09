package com.xujun.model;

import com.xujun.sqlite.AccountEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/2/11.
 */
public class LoginResp extends BaseResp{


    private Member          data;
    private List<AccountEntity>        members;

    public Member getData() {
        return data;
    }

    public void setData(Member data) {
        this.data = data;
    }

    public List<AccountEntity> getMembers() {
        return members;
    }

    public void setMembers(List<AccountEntity> members) {
        this.members = members;
    }
}
