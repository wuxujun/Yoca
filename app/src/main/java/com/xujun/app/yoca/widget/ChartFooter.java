package com.xujun.app.yoca.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.LockActivity;
import com.xujun.app.yoca.LockSetupActivity;
import com.xujun.app.yoca.R;
import com.xujun.charting.charts.Chart;
import com.xujun.util.StringUtil;
import com.xujun.widget.ToggleButton;

/**
 * Created by xujunwu on 7/17/15.
 */
public class ChartFooter extends LinearLayout implements View.OnClickListener{

    private ChartController   chartController;

    private Context mContext;
    private View mContentView;

    private ToggleButton    toggleButton;

    public ChartFooter(Context context){
        super(context, null);
        init(context);
    }

    public void init(Context context){
        mContext=context;
        mContentView= LayoutInflater.from(mContext).inflate(R.layout.chart_d_footer,null);

        mContentView.findViewById(R.id.llHistoryData).setOnClickListener(this);

        toggleButton=(ToggleButton)mContentView.findViewById(R.id.tbTargetShow);
        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    getChartController().onTargetShow(true);
                } else {
                    getChartController().onTargetShow(false);
                }
            }
        });
        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        addView(mContentView, lp);
    }


    public ChartController  getChartController(){
        return chartController;
    }

    public void setChartController(ChartController  controller){
        this.chartController=controller;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llHistoryData:{
                getChartController().onViewHistoryClicked();
                break;
            }
        }
    }

    public void setUnit(String unit){
        if (!unit.equals("0")) {
            ((TextView)mContentView.findViewById(R.id.tvUnit)).setText(unit);
        }
    }

    public void setRemark(String remark){
        ((TextView)mContentView.findViewById(R.id.tvRemark)).setText(remark);

    }

    public void setIsShow(int show){
        if (show==0){
            toggleButton.setToggleOff();
        }else{
            toggleButton.setToggleOn();
        }
    }
}
