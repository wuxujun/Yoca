package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.update.UmengUpdateAgent;
import com.xujun.widget.ToggleButton;

/**
 * 设置
 * Created by xujunwu on 15/6/9.
 */
public class SettingActivity extends SherlockActivity implements View.OnClickListener{
    public static final String TAG = "SettingActivity";

    private Context mContext;
    private AppContext      appContext;

    private ToggleButton mPrePassTB;
    private ToggleButton            mAutoLoginTB;

    private String mAutoLogin="0";
    private String mUserLock="0";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        mAutoLogin=AppConfig.getAppConfig(mContext).get(AppConfig.USER_AUTO_LOGIN);

        mPrePassTB=(ToggleButton)findViewById(R.id.tbPrePwd);
        mPrePassTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    Intent intent=new Intent(SettingActivity.this,LockSetupActivity.class);
                    startActivity(intent);
                }else{
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_LOCK_TYPE,"0");
                    Intent intent=new Intent(SettingActivity.this,LockActivity.class);
                    startActivity(intent);
                }
            }
        });

        mAutoLoginTB=(ToggleButton)findViewById(R.id.tbAutoLogin);

        mAutoLoginTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_AUTO_LOGIN,"1");
                }else{
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_AUTO_LOGIN,"0");
                }
            }
        });

        if (mAutoLogin!=null&&mAutoLogin.equals("1")){
            mAutoLoginTB.setToggleOn();
        }

        ((TextView)findViewById(R.id.tvVersion)).setText("当前版本:" + appContext.getVersionName());


        findViewById(R.id.llWarn).setOnClickListener(this);
        findViewById(R.id.llGroup).setOnClickListener(this);
//        findViewById(R.id.llPrvPassword).setOnClickListener(this);
        findViewById(R.id.llVersion).setOnClickListener(this);
        findViewById(R.id.llAbout).setOnClickListener(this);
        findViewById(R.id.tvLogout).setOnClickListener(this);


        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.llWarn:{
                Intent intent=new Intent(SettingActivity.this,WarnActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.llGroup:{

                break;
            }
            case R.id.llPrvPassword:{

                break;
            }
            case R.id.tbAutoLogin:{

            }
            case R.id.llVersion:{
                UmengUpdateAgent.setDefault();
                UmengUpdateAgent.forceUpdate(this);
                break;
            }
            case R.id.llAbout:{
                Intent intent=new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tvLogout:{
                appContext.setProperty("login_flag","0");
                Intent intent=new Intent(SettingActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserLock=AppConfig.getAppConfig(mContext).get(AppConfig.USER_LOCK_PASS);
        if (mUserLock!=null&&mUserLock.equals("1")){
            mPrePassTB.setToggleOn();
        }else{
            mPrePassTB.setToggleOff();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int keyCode=event.getKeyCode();
        if (event.getAction()==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
