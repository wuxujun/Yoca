package com.xujun.app.yoca.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by xujunwu on 15/4/3.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment>  list;

    public TabPagerAdapter(FragmentManager fm,List<Fragment> list){
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
