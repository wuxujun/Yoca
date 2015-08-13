package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.formedittextvalidator.EmailValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.OrValidator;
import com.andreabaccega.formedittextvalidator.PhoneValidator;
import com.andreabaccega.widget.FormEditText;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by xujunwu on 14/12/25.
 */
public class ForgotPwdActivity extends SherlockActivity implements View.OnClickListener{

    public static final String TAG = "ForgotPwdActivity";

    private Context mContext;
    private AppContext      appContext;


    private Button verfiyBtn;
    private FormEditText accountET;
    private FormEditText codeET;
    private FormEditText newPasswordET;
    private FormEditText newPassword2ET;


    String mobile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgot_pwd);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();
        SMSSDK.initSDK(this,AppConfig.SMS_APPKEY,AppConfig.SMS_APPKSECRET);

        accountET=(FormEditText)findViewById(R.id.etForgotAccount);
        accountET.addValidator(new OrValidator(getResources().getString(R.string.login_Mobileoremail_Hit), new PhoneValidator(null), new EmailValidator(null)));
        accountET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView) findViewById(R.id.tvForgotAccount)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvForgotCode)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llForgotAccount).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llForgotCode).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(accountET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(accountET.getWindowToken(), 0);
                }
            }
        });
        codeET=(FormEditText)findViewById(R.id.etForgotCode);
        codeET.addValidator(new EmptyValidator(null));
        codeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView) findViewById(R.id.tvForgotCode)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvForgotAccount)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llForgotCode).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llForgotAccount).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(codeET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(codeET.getWindowToken(), 0);
                }

            }
        });

        verfiyBtn=(Button)findViewById(R.id.btnGetCode);
        verfiyBtn.setOnClickListener(this);

        newPasswordET=(FormEditText)findViewById(R.id.etForgotPassword);
        newPasswordET.addValidator(new EmptyValidator(null));

        newPassword2ET=(FormEditText)findViewById(R.id.etForgotPassword2);
        newPassword2ET.addValidator(new EmptyValidator(null));

        findViewById(R.id.btnNext).setEnabled(false);
        findViewById(R.id.btnNext).setOnClickListener(this);
        findViewById(R.id.btnDone).setOnClickListener(this);
        getActionBar().setTitle(getResources().getString(R.string.forgot_password));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNext:{
                if (!accountET.testValidity()){
                    return;
                }
                if (!codeET.testValidity()){
                    return;
                }
                SMSSDK.submitVerificationCode("86",mobile,codeET.getText().toString());

                findViewById(R.id.llForgotPwd).setVisibility(View.GONE);
                findViewById(R.id.llForgotPwd2).setVisibility(View.VISIBLE);
                break;
            }
            case R.id.btnGetCode:{
                GetVerifyCode();
            }
                break;
            case R.id.btnDone:{
                if (!newPasswordET.testValidity()){
                    return;
                }
                if (!newPassword2ET.testValidity()){
                    return;
                }

                Intent intent=new Intent(ForgotPwdActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    EventHandler eh=new EventHandler(){

        @Override
        public void afterEvent(int event,int result,Object data){
            if (result==SMSSDK.RESULT_COMPLETE){
                //回调成功
                if (event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    //提交验证码成功
                    Log.e(TAG, "EVENT_SUBMIT_VERIFICATION_CODE  ..");
                }else if(event==SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    Log.e(TAG,"EVENT_GET_VERIFICATION_CODE  ..");
                }else if(event==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                    Log.e(TAG,data.toString());
                }
            }else{
                Log.e(TAG,"error..");
                ((Throwable)data).printStackTrace();
            }
        }
    };

    private void GetVerifyCode(){
        if (!accountET.testValidity()){
            return;
        }

        mobile=accountET.getText().toString();
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m=p.matcher(mobile);
        if (m.matches()){
            SMSSDK.getVerificationCode("86", mobile);
            verfiyBtn.setEnabled(false);
            startTimeTask();
            findViewById(R.id.btnNext).setEnabled(true);
        }else{
            Toast.makeText(mContext, "手机号格式有误", Toast.LENGTH_LONG).show();
        }
    }

    private Handler timeHandler=new Handler();
    private Timer mTimer;

    private TimeTask   mTimeTask;
    private int        timeCount;
    private void startTimeTask(){
        timeCount=0;
        stopTimeTask();
        if (mTimer==null){
            mTimer=new Timer();
        }
        if (mTimeTask==null){
            mTimeTask=new TimeTask();

        }
        if (mTimer!=null&&mTimeTask!=null){
            mTimer.schedule(mTimeTask,0,1000);
        }
        verfiyBtn.setEnabled(false);
    }

    private void stopTimeTask(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
        if (mTimeTask!=null){
            mTimeTask.cancel();
            mTimeTask=null;
        }
        verfiyBtn.setEnabled(false);
    }

    class TimeTask extends TimerTask {
        public void run(){
            if (timeCount>=60){
                refreshBtnTextEnabled("点击获取");
            }else{
                timeCount++;
                refreshBtnText((60-timeCount)+"秒");
            }
        }
    };

    private void refreshBtnText(final String value){
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                verfiyBtn.setText(value);
                verfiyBtn.setTextColor(getResources().getColor(R.color.btn_color_selected));
            }
        });
    }

    private void refreshBtnTextEnabled(final String value){
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                verfiyBtn.setText(value);
                verfiyBtn.setTextColor(getResources().getColor(R.color.btn_color));
                stopTimeTask();
                verfiyBtn.setEnabled(true);
            }
        });
    }

}
