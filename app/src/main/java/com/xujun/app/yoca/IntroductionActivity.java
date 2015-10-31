package com.xujun.app.yoca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xujun.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 15/9/7.
 */
public class IntroductionActivity extends Activity{

    @ViewInject(R.id.viewpager)
    private ViewPager           viewPager;


    private ViewGroup           mImagePoints;

    private List<View>          pageViews;
    private ImageView[]         mImageViews;
    private ImageView           imageView;


    private AppContext          appContext;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_introduction);
        appContext=(AppContext)getApplication();

        LayoutInflater  inflater=getLayoutInflater();
        pageViews=new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.viewpager_page0,null));
        pageViews.add(inflater.inflate(R.layout.viewpager_page1,null));
        pageViews.add(inflater.inflate(R.layout.viewpager_page2,null));
        pageViews.add(inflater.inflate(R.layout.viewpager_page3, null));

        mImageViews=new ImageView[pageViews.size()];

        mImagePoints=(ViewGroup)findViewById(R.id.page_indicator);
        for (int i=0;i<pageViews.size();i++){
            imageView=new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            mImageViews[i]=imageView;
            if(i==0){
                mImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                mImageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            mImagePoints.addView(mImageViews[i]);
        }

        ViewUtils.inject(this);
        viewPager.setAdapter(new ItemAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());


    }

    private View.OnClickListener    mBtnClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnStart: {
                    UIHelper.openHome(IntroductionActivity.this);
                    appContext.setProperty(AppConfig.CONF_FIRST_START,"1");
                    finish();
                    break;
                }
            }
        }
    };

    class ItemAdapter extends PagerAdapter{
        @Override
        public void destroyItem(View v, int position, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager)v).removeView(pageViews.get(position));

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        //获取当前窗体界面数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pageViews.size();
        }

        //初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));

            // 测试页卡3内的按钮事件
            if (position == 3) {
                Button btn = (Button) v.findViewById(R.id.btnStart);
                btn.setOnClickListener(mBtnClickListener);
            }

            return pageViews.get(position);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }



        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<mImageViews.length;i++){
                mImageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
                //不是当前选中的page，其小圆点设置为未选中的状态
                if(position !=i){
                    mImageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
    }
}
