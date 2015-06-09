package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.xujun.app.yoca.Adapter.TabPagerAdapter;
import com.xujun.app.yoca.fragment.ChartFrgment;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * 图表首页
 * Created by xujunwu on 15/3/25.
 */
public class DefaultActivity extends SherlockFragmentActivity implements ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private static final String TAG = "DefaultActivity";

    private String[]            mTabTitles;

    private ViewPager           mViewPager;
    private List<Fragment>      mFragmentList;
    private TabPagerAdapter     mTabPagerAdapter;

    private int         nTargetType=0;
    private ActionBar mActionBar;

    private int       currentTabPos;

    private int      showType=0; //0 周 1 月  2 年
    private String   beginDay;
    private String   endDay;

    private Context mContext;
    private AppContext              appContext;

    private DatabaseHelper databaseHelper;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }


    private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    private String          strToday=df.format(new Date());

    private AccountEntity localAccountEntity;

    public AccountEntity getLocalAccountEntity(){
        return localAccountEntity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        appContext=(AppContext)getApplication();
        mContext=getApplicationContext();


        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        if (localAccountEntity!=null){
            appContext.setProperty("uid", String.valueOf(localAccountEntity.getId()));
        }
        nTargetType=getIntent().getIntExtra("targetType",0);

        beginDay= DateUtil.getMondayOFWeek();
        endDay=DateUtil.getCurrentWeekday();
        Log.e(TAG,"......"+beginDay+"   "+endDay);

        mTabTitles=getResources().getStringArray(R.array.tab_title);

        mFragmentList=new ArrayList<Fragment>();
        mViewPager=(ViewPager)findViewById(R.id.viewPager);
        mTabPagerAdapter=new TabPagerAdapter(getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mTabPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        mActionBar=getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.app_name));
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i=0;i<mTabTitles.length;i++){
            ActionBar.Tab  tab=mActionBar.newTab();
            tab.setText(mTabTitles[i]);
            tab.setTabListener(this);
            mActionBar.addTab(tab,i);
        }

        for (int i=0;i<mTabTitles.length;i++){
            Fragment fragment=new ChartFrgment();
            ((ChartFrgment)fragment).setAccountEntity(localAccountEntity);
            ((ChartFrgment)fragment).setTargetType(i);
            ((ChartFrgment)fragment).setDataDay(showType,beginDay,endDay);
            mFragmentList.add(fragment);
        }
        mTabPagerAdapter.notifyDataSetChanged();

        if (nTargetType==8||nTargetType==9){
            nTargetType=nTargetType-1;
        }


    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        onPageSelected(nTargetType);
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause(){
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                Log.e(TAG,"onActivityResult =>"+data.getStringExtra("beginDay")+"  "+data.getStringExtra("endDay"));
                beginDay=data.getStringExtra("beginDay");
                endDay=data.getStringExtra("endDay");
                for (int i=0;i<mTabTitles.length;i++){
                    Fragment fragment=mFragmentList.get(i);
                    if (fragment!=null){
                        ((ChartFrgment)fragment).setDataDay(0,beginDay,endDay);
                        ((ChartFrgment)fragment).loadData();
                    }
                }
            }else if(requestCode==2){
                Log.e(TAG,"onActivityResult Month=>"+data.getStringExtra("beginDay")+"  "+data.getStringExtra("endDay"));
                beginDay=data.getStringExtra("beginDay");
                endDay=data.getStringExtra("endDay");
                for (int i=0;i<mTabTitles.length;i++){
                    Fragment fragment=mFragmentList.get(i);
                    if (fragment!=null){
                        ((ChartFrgment)fragment).setDataDay(1,beginDay,endDay);
                        ((ChartFrgment)fragment).loadData();
                    }
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.clear();
        getSupportMenuInflater().inflate(R.menu.chart,menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.itemChartWeek:{
                Intent intent=new Intent(DefaultActivity.this,SelectDialog.class);
                intent.putExtra("dataType",0);
                startActivityForResult(intent,1);
                break;
            }
            case R.id.itemChartMonth:{
                Intent intent=new Intent(DefaultActivity.this,SelectDialog.class);
                intent.putExtra("dataType",1);
                startActivityForResult(intent,2);
                break;
            }
            case R.id.itemChartYear:{

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG,"onPageSelected "+position);
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        currentTabPos=tab.getPosition();
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }


}
