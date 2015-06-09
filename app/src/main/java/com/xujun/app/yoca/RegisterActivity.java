package com.xujun.app.yoca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.formedittextvalidator.EmailValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.OrValidator;
import com.andreabaccega.formedittextvalidator.PhoneValidator;
import com.andreabaccega.widget.FormEditText;
import com.xujun.progressbutton.CircularProgressButton;
import com.xujun.util.AppUtil;
import com.xujun.util.JsonUtil;
import com.xujun.util.URLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by xujunwu on 14/12/15.
 */
public class RegisterActivity extends SherlockActivity implements View.OnClickListener{

    public static final String TAG = "RegisterActivity";

    private FormEditText        accountET;
    private FormEditText        passwordET;
    private FormEditText        password2ET;
    private FormEditText        codeET;

    private Button              verfiyBtn;
    private CircularProgressButton registerBtn;

    private Context             mContext;

    private String                  mobile;

    private AppContext              appContext;
    private ProgressDialog          progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        SMSSDK.initSDK(this,"12a614ceb40a","bc5c19f196ac4e9c52e96d3a4fbfda63");
        getActionBar().setTitle(R.string.register);
        getActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        accountET=(FormEditText)findViewById(R.id.etRegAccount);
        accountET.addValidator(new OrValidator(getResources().getString(R.string.login_Mobileoremail_Hit),new PhoneValidator(null),new EmailValidator(null)));
        accountET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                ((TextView) findViewById(R.id.tvRegAccount)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvRegPassword)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView) findViewById(R.id.tvRegPassword2)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView) findViewById(R.id.tvRegCode)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterAccount).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llRegisterPasswd).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterPasswd2).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterCode).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(accountET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(accountET.getWindowToken(), 0);
                }
            }
        });

        passwordET=(FormEditText)findViewById(R.id.etRegPassword);
        passwordET.addValidator(new EmptyValidator(null));
        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvRegPassword)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvRegAccount)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegPassword2)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegCode)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterAccount).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterPasswd).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llRegisterPasswd2).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterCode).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(passwordET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
                }
            }
        });
        password2ET=(FormEditText)findViewById(R.id.etRegPassword2);
        password2ET.addValidator(new EmptyValidator(null));
        password2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvRegPassword2)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvRegAccount)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegPassword)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegCode)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterAccount).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterPasswd2).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llRegisterPasswd).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterCode).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(password2ET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(password2ET.getWindowToken(), 0);
                }
            }
        });

        codeET=(FormEditText)findViewById(R.id.etRegCode);
        codeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvRegCode)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvRegAccount)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegPassword)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvRegPassword2)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterAccount).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterCode).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llRegisterPasswd).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llRegisterPasswd2).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(codeET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(codeET.getWindowToken(), 0);
                }
            }
        });

        codeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val=editable.toString();
                if (val!=null&&val.length()==4){
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(codeET.getWindowToken(), 0);
                }
            }
        });

        registerBtn=(CircularProgressButton)findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(this);
        registerBtn.setEnabled(false);
        verfiyBtn=(Button)findViewById(R.id.btnGetCode);
        verfiyBtn.setOnClickListener(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:{
                register();
                break;
            }
            case R.id.btnGetCode:{
                GetVerifyCode();
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

                    Log.e(TAG,"EVENT_SUBMIT_VERIFICATION_CODE  ..");
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
            SMSSDK.getVerificationCode("86",mobile);
            verfiyBtn.setEnabled(false);
            startTimeTask();
            registerBtn.setEnabled(true);
        }else{
            Toast.makeText(mContext,"手机号格式有误",Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home: {
                Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void register(){
        if (!accountET.testValidity()){
            return;
        }
        if (!passwordET.testValidity()){
            return;
        }

        if (!password2ET.testValidity()){
            return;
        }
        if (!passwordET.getText().toString().equals(password2ET.getText().toString())){
            Toast.makeText(this,getResources().getString(R.string.login_ConfirmPassword_Error),Toast.LENGTH_LONG).show();
            return;
        }
        if (!codeET.testValidity()){
            Toast.makeText(this,getResources().getString(R.string.login_VerCode_Hit),Toast.LENGTH_LONG).show();
            return;
        }
        SMSSDK.submitVerificationCode("86",mobile,codeET.getText().toString());


        Map<String,String> sb=new HashMap<String, String>();
        sb.put("imei",appContext.getIMSI());
        sb.put("username",accountET.getText().toString());
        sb.put("password",passwordET.getText().toString());
        try{
            request(URLs.REGISTER_USER, JsonUtil.toJson(sb));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void request(final String url,final String params){
        progress= AppUtil.showProgress(this,getString(R.string.register_loading));
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){

                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("FragmentType",1);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else if(msg.what==0){
                    registerBtn.setErrorText(msg.obj.toString());
                }else if(msg.what==-1){
                    registerBtn.setErrorText(msg.obj.toString());
                }
                if (progress!=null){
                    progress.dismiss();
                }
            }
        };

        new Thread(){
            public void run(){
                Message msg=new Message();
                try{
                    String resp=appContext.sendRequestData(url, params,null);
                    if (resp!=null){
                        msg.what=1;
                        msg.obj=resp;
                    }else{
                        msg.what=0;
                        msg.obj="注册失败";
                    }
                }catch (AppException e){
                    e.printStackTrace();
                    msg.what=-1;
                    msg.obj=e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private Handler  timeHandler=new Handler();
    private Timer       mTimer;

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

    class TimeTask extends TimerTask{
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
