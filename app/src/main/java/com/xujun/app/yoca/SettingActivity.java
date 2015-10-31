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
import com.xujun.util.StringUtil;
import com.xujun.widget.ToggleButton;

/**
 * 设置
 * Created by xujunwu on 15/6/9.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{
    public static final String TAG = "SettingActivity";

    private ToggleButton mPrePassTB;
    private ToggleButton            mAutoLoginTB;
    private ToggleButton            mShowTargetTB;

    private String mAutoLogin="0";
    private String mUserLock="0";
    private String mShowTarget="0";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);

        mShowTargetTB=(ToggleButton)findViewById(R.id.tbShowTarget);
        mShowTargetTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_SHOW_TARGET, "1");
                } else {
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_SHOW_TARGET, "0");
                }
            }
        });

        mShowTarget=appContext.getProperty(AppConfig.USER_SHOW_TARGET);
        if (!StringUtil.isEmpty(mShowTarget) &&mShowTarget.equals("1")){
            mShowTargetTB.setToggleOn();
        }
        mAutoLogin=AppConfig.getAppConfig(mContext).get(AppConfig.USER_AUTO_LOGIN);

        mPrePassTB=(ToggleButton)findViewById(R.id.tbPrePwd);
        mPrePassTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    Intent intent = new Intent(SettingActivity.this, LockSetupActivity.class);
                    startActivity(intent);
                } else {
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_LOCK_TYPE, "0");
                    Intent intent = new Intent(SettingActivity.this, LockActivity.class);
                    startActivity(intent);
                }
            }
        });

        mUserLock=appContext.getProperty(AppConfig.USER_LOCK_TYPE);


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
        mAutoLogin=appContext.getProperty(AppConfig.USER_AUTO_LOGIN);

        if (!StringUtil.isEmpty(mAutoLogin)&&mAutoLogin.equals("1")){
            mAutoLoginTB.setToggleOn();
        }

        ((TextView)findViewById(R.id.tvVersion)).setText("当前版本:" + appContext.getVersionName());


        findViewById(R.id.llWarn).setOnClickListener(this);
        findViewById(R.id.llGroup).setOnClickListener(this);
//        findViewById(R.id.llPrvPassword).setOnClickListener(this);
        findViewById(R.id.llVersion).setOnClickListener(this);
        findViewById(R.id.llAbout).setOnClickListener(this);
        findViewById(R.id.tvLogout).setOnClickListener(this);
        findViewById(R.id.llRecord).setOnClickListener(this);


        mHeadTitle.setText(getResources().getString(R.string.btn_setting));
        mHeadButton.setVisibility(View.INVISIBLE);
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ibHeadBack:{
                finish();
                break;
            }
            case R.id.llWarn:{
                Intent intent=new Intent(SettingActivity.this,WarnActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.llGroup:{
                Intent intent=new Intent(SettingActivity.this,DeviceActivity.class);
                startActivity(intent);
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
            case R.id.llRecord:{
                Intent intent=new Intent(SettingActivity.this,RecordActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tvLogout:{
                appContext.setProperty(AppConfig.CONF_LOGIN_FLAG,"0");
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


}
