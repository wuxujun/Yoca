package com.xujun.app.yoca.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xujun.app.yoca.R;
import com.xujun.util.StringUtil;
import com.xujun.widget.RunningTextView;

/**
 * Created by xujunwu on 15/5/14.
 */
public class ContentHeader extends LinearLayout implements View.OnClickListener{

    private ContentController   contentController;


    private Context         mContext;
    private View            mContentView;

    private LinearLayout    mTargetView;

    public TextView         targetValue;
    public TextView         targetDay;
    public TextView         currentDate;
    public ImageButton      sharedButton;

    public ImageView        scanLine;

    public TextView         weightTextView;

    private boolean         isScan=false;

    private TranslateAnimation  mAnimation;


    public ContentHeader(Context context){
        super(context,null);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.layout_content, null);

        mContentView.findViewById(R.id.llTargetSet).setOnClickListener(this);
        mContentView.findViewById(R.id.ibLeft).setOnClickListener(this);
        mContentView.findViewById(R.id.ibRight).setOnClickListener(this);
        mContentView.findViewById(R.id.flDetail).setOnClickListener(this);

        mContentView.findViewById(R.id.ibMainShared).setOnClickListener(this);
        mContentView.findViewById(R.id.llTarget).setOnClickListener(this);
        sharedButton=(ImageButton)mContentView.findViewById(R.id.ibMainShared);
        weightTextView=(TextView)mContentView.findViewById(R.id.tvWeightValue);
        scanLine=(ImageView)mContentView.findViewById(R.id.ivScan);
        scanLine.setVisibility(GONE);
        mTargetView=(LinearLayout)mContentView.findViewById(R.id.llTarget);

        targetValue=(TextView)mContentView.findViewById(R.id.tvTargetValueTotal);
        targetDay=(TextView)mContentView.findViewById(R.id.tvTargetDayNum);
        currentDate=(TextView)mContentView.findViewById(R.id.tvDataDay);

        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        addView(mContentView,lp);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llTargetSet:{
                getContentController().onViewTargetClicked();
                break;
            }
            case R.id.ibRight:{
                getContentController().onViewRightClicked();
                break;
            }
            case R.id.ibLeft:{
                getContentController().onViewLeftClicked();
                break;
            }
            case R.id.flDetail:{
                getContentController().onViewDetailClicked();
                break;
            }
            case R.id.ibMainShared:{
//                getContentController().onViewSharedClicked();
                getContentController().onViewDetailClicked();
                break;
            }
            case R.id.llTarget:{
                getContentController().onViewTargetClicked();
                break;
            }
        }

    }

    public ContentController getContentController() {
        return contentController;
    }

    public void setContentController(ContentController contentController) {
        this.contentController = contentController;
    }


    public void startEffect() {
        mContentView.findViewById(R.id.ivMainFooter).setVisibility(View.GONE);
        weightTextView.setVisibility(View.INVISIBLE);
        scanLine.setVisibility(View.VISIBLE);
        if (!isScan) {
            if (mAnimation==null){
                mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.85f);
                mAnimation.setDuration(2000);
                mAnimation.setRepeatCount(-1);
                mAnimation.setRepeatMode(Animation.REVERSE);
                mAnimation.setInterpolator(new LinearInterpolator());
            }
            scanLine.setAnimation(mAnimation);
            isScan=true;
        }
    }

    public void stopEffect(String val){
        weightTextView.setVisibility(VISIBLE);
        weightTextView.setText(val);
        if (StringUtil.toDouble(val)>99.0){
            weightTextView.setTextSize(46);
        }else{
            weightTextView.setTextSize(66);
        }
        scanLine.setVisibility(GONE);
        if (mAnimation!=null){
            mAnimation.cancel();
            scanLine.clearAnimation();
        }
        mAnimation=null;
        isScan=false;
    }

    public void stopEffect(int type){
        if (type==-1) {
            if (weightTextView.getVisibility()==View.INVISIBLE) {
                mContentView.findViewById(R.id.ivMainFooter).setVisibility(VISIBLE);
            }
        }
        scanLine.setVisibility(GONE);
        if (mAnimation!=null){
            mAnimation.cancel();
            scanLine.clearAnimation();
        }
        mAnimation=null;
        isScan=false;
    }

    public void setStatus(String val){
        ((TextView)mContentView.findViewById(R.id.tvHeaderStatus)).setText(val);
    }

    public void isShowContent(boolean flag)
    {
        mContentView.findViewById(R.id.ivMainFooter).setVisibility(flag?View.GONE:View.VISIBLE);
        mContentView.findViewById(R.id.tvMainHeadDesc).setVisibility(flag?View.VISIBLE:View.GONE);
        weightTextView.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        mContentView.findViewById(R.id.ivInfo).setVisibility(flag?View.VISIBLE:View.GONE);
        mContentView.findViewById(R.id.ibMainShared).setVisibility(flag?View.VISIBLE:View.INVISIBLE);
        mContentView.findViewById(R.id.tvHeaderStatus).setVisibility(flag?View.VISIBLE:View.GONE);
        mContentView.findViewById(R.id.tvHeaderUnit).setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    public void setWeightValue(String value)
    {
        scanLine.setVisibility(View.GONE);
        isShowContent(true);
        stopEffect(value);
    }

    public LinearLayout getTargetView(){
        return mTargetView;
    }

    public void setWeekValue(int weeks,double weight,double targetWeight)
    {
        double val=(weight-targetWeight)/(weeks+1);
        ((TextView)mContentView.findViewById(R.id.tvWeekValue)).setText(StringUtil.doubleToStringOne(val));
    }

    public void setTargetViewStatus(boolean show){
        if (show) {
            mContentView.findViewById(R.id.llTargetSet).setVisibility(VISIBLE);
            mContentView.findViewById(R.id.llTargetContent).setVisibility(GONE);
        }else{
            mContentView.findViewById(R.id.llTargetContent).setVisibility(VISIBLE);
            mContentView.findViewById(R.id.llTargetSet).setVisibility(GONE);
        }
    }
}
