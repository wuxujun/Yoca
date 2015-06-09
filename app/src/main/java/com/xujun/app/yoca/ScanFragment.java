package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.xujun.sqlite.AccountEntity;

/**
 * Created by xujunwu on 15/1/20.
 */
public class ScanFragment  extends SherlockFragment implements View.OnClickListener{

    public static final String TAG = "ScanFragment";


    private View        mContentView;
    private ImageView   mScanLine;

    private Context                 mContext;
    private AppContext              appContext;


    private boolean                 bVisitor=false;

    private AccountEntity           localAccountEngity=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        mContentView = inflater.inflate(R.layout.layout_scan, null);
        mScanLine = (ImageView) mContentView.findViewById(R.id.ivScan);
        mScanLine.setVisibility(View.GONE);
        mContentView.findViewById(R.id.btnStart).setOnClickListener(this);

        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate()");
        mContext=getActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        getSherlockActivity().getActionBar().setDisplayUseLogoEnabled(false);
        getSherlockActivity().getActionBar().setTitle(getResources().getString(R.string.main_scan_device));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateStatus(String msg,int type){
        ((TextView)mContentView.findViewById(R.id.tvStatus)).setText(msg);
        if (type==0){
            scane();
            mContentView.findViewById(R.id.btnStart).setVisibility(View.INVISIBLE);
        }else{
            mScanLine.setVisibility(View.GONE);
            mScanLine.clearAnimation();
            mContentView.findViewById(R.id.btnStart).setVisibility(View.VISIBLE);
        }
    }

    public void loadVisitor(AccountEntity accountEntity){
        bVisitor=true;
        if (accountEntity!=null){
            localAccountEngity=accountEntity;
        }
    }

    public void scane(){
        mScanLine.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation=new TranslateAnimation(TranslateAnimation.ABSOLUTE,0f,TranslateAnimation.ABSOLUTE,0f,
                TranslateAnimation.RELATIVE_TO_PARENT,0f,TranslateAnimation.RELATIVE_TO_PARENT,0.85f);
        mAnimation.setDuration(2000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mScanLine.setAnimation(mAnimation);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStart:{
                broadcaseStartWeigh();
                break;
            }
        }
    }


    private void broadcaseStartWeigh(){
        Log.e(TAG,"broadcaseStartWeigh");
        Intent intent=new Intent(AppConfig.ACTION_START_WEIGH);
        intent.putExtra(AppConfig.EXTRA_DATA_HEIGHT,170);
        intent.putExtra(AppConfig.EXTRA_DATA_AGE,35);
        intent.putExtra(AppConfig.EXTRA_DATA_SEX,0);
        getSherlockActivity().sendBroadcast(intent);
    }
}
