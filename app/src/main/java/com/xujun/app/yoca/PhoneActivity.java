package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.formedittextvalidator.EmailValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.OrValidator;
import com.andreabaccega.formedittextvalidator.PhoneValidator;
import com.andreabaccega.widget.FormEditText;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.message.UmengRegistrar;
import com.xujun.util.AppUtil;
import com.xujun.util.JsonUtil;
import com.xujun.util.URLs;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by xujunwu on 15/8/8.
 */
public class PhoneActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = "PhoneActivity";


    private Button verfiyBtn;
    private FormEditText mobileET;
    private FormEditText codeET;

    String      mobile;

    private int  uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);


        SMSSDK.initSDK(this, AppConfig.SMS_APPKEY, AppConfig.SMS_APPKSECRET);

        uid=getIntent().getIntExtra("uid", 0);

        mHeadTitle.setText(getText(R.string.login_Mobile_Code));
        mHeadIcon.setVisibility(View.INVISIBLE);

        mHeadButton.setText(getText(R.string.btn_skip));
        mHeadButton.setOnClickListener(this);

        verfiyBtn=(Button)findViewById(R.id.btnGetCode);
        verfiyBtn.setOnClickListener(this);

        mobileET=(FormEditText)findViewById(R.id.etForgotAccount);
        mobileET.addValidator(new OrValidator(getResources().getString(R.string.login_Mobileoremail_Hit), new PhoneValidator(null), new EmailValidator(null)));
        mobileET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView) findViewById(R.id.tvForgotAccount)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvForgotCode)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llForgotAccount).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llForgotCode).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mobileET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mobileET.getWindowToken(), 0);
                }
            }
        });

        mobileET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = editable.toString();
                if (val != null && val.length() == 11) {
                    verfiyBtn.setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mobileET.getWindowToken(), 0);
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

        codeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = editable.toString();
                if (val != null && val.length() == 4) {
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(codeET.getWindowToken(), 0);
                }
            }
        });


        findViewById(R.id.btnSubmit).setEnabled(false);
        findViewById(R.id.btnSubmit).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit: {
                if (!mobileET.testValidity()) {
                    return;
                }
                if (!codeET.testValidity()) {
                    return;
                }
                SMSSDK.submitVerificationCode("86", mobile, codeET.getText().toString());
                progress= AppUtil.showProgress(this, getString(R.string.login_loading));
                Map<String,String> sb=new HashMap<String, String>();
                sb.put("imei",appContext.getIMSI());
                sb.put("umengToken", UmengRegistrar.getRegistrationId(mContext));
                sb.put("uid",""+uid);
                sb.put("mobile", mobile);
                try{
                    sendLogin(JsonUtil.toJson(sb));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            }
            case R.id.btnGetCode: {
                GetVerifyCode();
                break;
            }
            case R.id.btnHeadEdit:{
                Intent intent=new Intent(PhoneActivity.this,TabActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("FragmentType",0);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            }
            default:
                break;
        }
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

    private void parserResp(String resp){
        try{
            Log.e(TAG,resp);
            Intent intent=new Intent(PhoneActivity.this,TabActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("FragmentType", 0);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendLogin(final String params){
        Log.e(TAG,params);
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }else if(msg.what==0){
                    Toast.makeText(mContext,"绑定手机号失败",Toast.LENGTH_LONG).show();
                }else if(msg.what==-1){
                    Toast.makeText(mContext,"请求失败",Toast.LENGTH_LONG).show();
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
                    String resp=appContext.sendRequestData(URLs.THIRD_MOBILE_HTTP, params,null);
                    if (resp!=null){
                        msg.what=1;
                        msg.obj=resp;
                    }else{
                        msg.what=0;
                        msg.obj="绑定手机号失败";
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
        if (!mobileET.testValidity()){
            return;
        }

        mobile=mobileET.getText().toString();
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m=p.matcher(mobile);
        if (m.matches()){
            SMSSDK.getVerificationCode("86", mobile);
            verfiyBtn.setEnabled(false);
            startTimeTask();
            findViewById(R.id.btnSubmit).setEnabled(true);
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
                verfiyBtn.setTextColor(getResources().getColor(R.color.btn_color));
            }
        });
    }

    private void refreshBtnTextEnabled(final String value){
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                verfiyBtn.setText(value);
                verfiyBtn.setTextColor(getResources().getColor(R.color.white));
                stopTimeTask();
                verfiyBtn.setEnabled(true);
            }
        });
    }
}
