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
 * Created by xujunwu on 15/2/4.
 */
public class AboutFragment extends SherlockFragment {

    private View mContentView;

    private Context mContext;
    private AppContext appContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        getSherlockActivity().getActionBar().setTitle("关于我们");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.layout_about, null);

        ((TextView)mContentView.findViewById(R.id.tvAbountVersion)).setText("Version:"+appContext.getVersionName());
        return mContentView;
    }
}
