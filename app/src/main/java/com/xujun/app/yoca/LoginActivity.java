package com.xujun.app.yoca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.andreabaccega.formedittextvalidator.EmailValidator;
import com.andreabaccega.formedittextvalidator.OrValidator;
import com.andreabaccega.formedittextvalidator.PhoneValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.widget.FormEditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.message.UmengRegistrar;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.xujun.model.LoginResp;
import com.xujun.progressbutton.CircularProgressButton;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.TargetEntity;
import com.xujun.util.AppUtil;
import com.xujun.util.JsonUtil;
import com.xujun.util.URLs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 14/12/15.
 */
public class LoginActivity extends SherlockActivity implements View.OnClickListener{

    public static final String TAG = "LoginActivity";

    private FormEditText        accountET;
    private FormEditText        passwordET;
    private CircularProgressButton   loginBtn;

    private Context             mContext;
    private ProgressDialog      progress;


    private AppContext              appContext;
    private DatabaseHelper databaseHelper;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        getActionBar().setTitle(R.string.login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        loginBtn=(CircularProgressButton)findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(this);
        findViewById(R.id.tvRegister).setOnClickListener(this);
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);

        accountET=(FormEditText)findViewById(R.id.etLoginAccount);
        if (AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT)!=null) {
           accountET.setText(AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT));
        }
        accountET.addValidator(new OrValidator(getResources().getString(R.string.login_Mobileoremail_Hit), new PhoneValidator(null), new EmailValidator(null)));
        accountET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                ((TextView) findViewById(R.id.tvLoginAccount)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvLoginPassword)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llLoginAccount).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llLoginPassword).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(accountET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(accountET.getWindowToken(), 0);
                }
            }
        });
        passwordET=(FormEditText)findViewById(R.id.etLoginPassword);
        if (AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_PASSWORD)!=null) {
            passwordET.setText(AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_PASSWORD));
        }
        passwordET.addValidator(new EmptyValidator(null));
        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvLoginPassword)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvLoginAccount)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llLoginAccount).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llLoginPassword).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(passwordET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(passwordET.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }


    public void onResume(){
        super.onResume();
        Log.e(TAG, " ===>"+ UmengRegistrar.getRegistrationId(mContext));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:{
                login();
                break;
            }
            case R.id.tvRegister:{
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tvForgotPassword:{
                Intent intent=new Intent(LoginActivity.this,ForgotPwdActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home: {
                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void login(){

        if (!accountET.testValidity()){
            return;
        }
        if (!passwordET.testValidity()){
            return;
        }
        String account=accountET.getText().toString();
        String password=passwordET.getText().toString();

        AppConfig.getAppConfig(mContext).set(AppConfig.CONF_LOGIN_ACCOUNT,account);
        AppConfig.getAppConfig(mContext).set(AppConfig.CONF_LOGIN_PASSWORD,password);

        Map<String,String> sb=new HashMap<String, String>();
        sb.put("imei",appContext.getIMSI());
        sb.put("umeng_token",UmengRegistrar.getRegistrationId(mContext));
        sb.put("username",account);
        sb.put("password",password);
        try {
            request(URLs.LOGIN_VALIDATE_HTTP, JsonUtil.toJson(sb));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parserResp(String resp){
        try{
            Log.e(TAG,resp);
            LoginResp loginResp=(LoginResp)JsonUtil.ObjFromJson(resp,LoginResp.class);
            if (loginResp.getData()!=null&&loginResp.getSuccess()==1){
                appContext.setProperty("login_flag","1");
                appContext.setProperty("uid",""+loginResp.getData().getId());
                if (loginResp.getMembers()!=null){
                    for (int i=0;i<loginResp.getMembers().size();i++){
                        addAccountEntity(loginResp.getMembers().get(i));
                    }
                }

//                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                Intent intent=new Intent(LoginActivity.this, TabActivity.class);
                Bundle bundle=new Bundle();
                if (isFirstLogin()) {
                    bundle.putInt("FragmentType",1);
                }else{
                    bundle.putInt("FragmentType",0);
                }
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }else{
                loginBtn.setProgress(CircularProgressButton.ERROR_STATE_PROGRESS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginBtn.setProgress(0);
                        loginBtn.setEnabled(true);
                    }
                }, 3);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void request(final String url,final String params){
        progress=AppUtil.showProgress(this,getString(R.string.login_loading));
        loginBtn.setEnabled(false);
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }else if(msg.what==0){
                    loginBtn.setErrorText(msg.obj.toString());
                }else if(msg.what==-1){
                    loginBtn.setErrorText(msg.obj.toString());
                }
                if (progress!=null) {
                    progress.dismiss();
                }
//                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//                Bundle bundle=new Bundle();
//                if (isFirstLogin()) {
//                    bundle.putInt("FragmentType",1);
//                }else{
//                    bundle.putInt("FragmentType",0);
//                }
//                intent.putExtras(bundle);
//                startActivity(intent);
//                finish();
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
                        msg.obj="登录失败";
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

    private boolean isFirstLogin(){
       try {
           Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
           QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
           queryBuilder.where().eq("type",1);
           queryBuilder.orderBy("id",true);
           PreparedQuery<AccountEntity> preparedQuery1=queryBuilder.prepare();
           List<AccountEntity> list=dao.query(preparedQuery1);
           if (list.size()>0){
               return false;
           }
       }catch (SQLException e){
           e.printStackTrace();
       }
       return true;

    }

    private void addAccountEntity(AccountEntity entity){
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
