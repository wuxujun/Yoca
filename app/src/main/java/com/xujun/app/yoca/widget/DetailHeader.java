package com.xujun.app.yoca.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xujun.app.yoca.R;

import org.w3c.dom.Text;

/**
 * Created by xujunwu on 15/4/13.
 */
public class DetailHeader extends LinearLayout{

    private Context mContext;
    private View            mContentView;
    public TextView        weightTextView;
    public TextView        topTextView;

    public DetailHeader(Context context){
        super(context,null);
        init(context);
    }

    public void init(Context context){
        mContext=context;
        mContentView= LayoutInflater.from(mContext).inflate(R.layout.activity_detail_header,null);
        weightTextView=(TextView)mContentView.findViewById(R.id.tvWeight);
        topTextView=(TextView)mContentView.findViewById(R.id.tvTopWeight);

        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        addView(mContentView,lp);
    }

}
