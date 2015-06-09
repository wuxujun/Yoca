package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;

/**
 * Created by xujunwu on 15/4/6.
 */
public class MyFragment  extends SherlockFragment {

    private View mContentView;

    private Context mContext;
    private AppContext appContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.layout_about, null);

        ((TextView)mContentView.findViewById(R.id.tvAbountVersion)).setText("Version:"+appContext.getVersionName());
        return mContentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getSherlockActivity().getActionBar().setTitle("我");
        getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getSherlockActivity().getActionBar().setDisplayShowHomeEnabled(false);
    }

}
