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
import com.xujun.app.yoca.fragment.HistoryFragment;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.DateUtil;
import com.xujun.util.UIHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图表首页
 * Created by xujunwu on 15/3/25.
 */
public class HistoryActivity extends SherlockFragmentActivity implements ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private static final String TAG = "HistoryActivity";

    private String[]            mTabTitles;

    private ViewPager           mViewPager;
    private List<Fragment>      mFragmentList;
    private TabPagerAdapter     mTabPagerAdapter;

    private int         nTargetType=0;
    private ActionBar mActionBar;
    private boolean     isEdit=false;

    private int       currentTabPos;

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
        nTargetType=getIntent().getIntExtra("targetType",0);

        if (localAccountEntity!=null) {
            Log.e(TAG, "onCreate() "+localAccountEntity.getId()+"......");
            appContext.setProperty("uid",String.valueOf(localAccountEntity.getId()));
        }
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
            Tab  tab=mActionBar.newTab();
            tab.setText(mTabTitles[i]);
            tab.setTabListener(this);
            mActionBar.addTab(tab,i);
        }

        for (int i=0;i<mTabTitles.length;i++){
            Fragment fragment=new HistoryFragment();
            ((HistoryFragment)fragment).setLocalAccountEntity(localAccountEntity);
            ((HistoryFragment)fragment).setTargetType(i);
            mFragmentList.add(fragment);
        }
        mTabPagerAdapter.notifyDataSetChanged();

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
    public boolean onCreateOptionsMenu(Menu menu){
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.clear();
        if (isEdit){
            getSupportMenuInflater().inflate(R.menu.main_done, menu);
        }else {
            getSupportMenuInflater().inflate(R.menu.main, menu);
        }
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
            case R.id.item_main_edit:{
                isEdit=true;
                for (int i=0;i<mTabTitles.length;i++){
                    Fragment fragment=mFragmentList.get(i);
                    if (fragment!=null){
                        ((HistoryFragment)fragment).setEdit(isEdit);
                    }
                }
                UIHelper.refreshActionBarMenu(this);
                break;
            }
            case R.id.item_main_done:{
                isEdit=false;
                for (int i=0;i<mTabTitles.length;i++){
                    Fragment fragment=mFragmentList.get(i);
                    if (fragment!=null){
                        ((HistoryFragment)fragment).setEdit(isEdit);
                    }
                }
                UIHelper.refreshActionBarMenu(this);
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
