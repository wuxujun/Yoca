package com.xujun.app.yoca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.actionbarsherlock.app.SherlockActivity;
import com.j256.ormlite.dao.Dao;
import com.umeng.message.UmengRegistrar;
import com.xujun.model.LoginResp;
import com.xujun.model.TargetInfoResp;
import com.xujun.sqlite.ConfigEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.TargetEntity;
import com.xujun.util.AppUtil;
import com.xujun.util.JsonUtil;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xujunwu on 14/12/15.
 */
public class StartActivity extends SherlockActivity{
    public static final String TAG = "StartActivity";

    private static final int DELAY=1000;

    private Context mContext;
    private AppContext      appContext;

    private ProgressDialog progress;

    private String mAutoLogin="0";


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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_start);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();
        mAutoLogin=AppConfig.getAppConfig(mContext).get(AppConfig.USER_AUTO_LOGIN);
    }
    @Override
    public void onResume() {
        super.onResume();
//        testRandom3();
        Log.e(TAG,"onResume()");
        if (appContext.getNetworkType()>0){
            initConfig();
        }else if(appContext.getProperty("login_flag").equals("1")&& !StringUtil.isEmpty(appContext.getProperty("uid"))){
            Intent intent=new Intent(StartActivity.this,TabActivity.class);
//            Intent intent=new Intent(StartActivity.this,MainActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("FragmentType",0);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, DELAY);
        }
    }

    private void testRandom3(){
        HashSet<Integer> integerHashSet=new HashSet<Integer>();
        Random random=new Random();
        for (int i = 0; i <10; i++) {
            int randomInt=random.nextInt(100);
            Log.e(TAG,"生成的randomInt="+randomInt);
            if (!integerHashSet.contains(randomInt)) {
                integerHashSet.add(randomInt);
                Log.e(TAG, "添加进HashSet的randomInt=" + randomInt);
            }else {
                Log.e(TAG, "该数字已经被添加,不能重复添加");
            }
        }
        Log.e(TAG, "/////以上为testRandom3()的测试///////");
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }


    private void initConfig(){
        Map<String, String> sb = new HashMap<String, String>();
        sb.put("imei", appContext.getIMSI());
        try{
            requestConfig(URLs.INIT_CONFIG_URL,JsonUtil.toJson(sb));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                Intent intent=new Intent(StartActivity.this,TabActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("FragmentType",0);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }, DELAY);
    }

    private void autoLogin()
    {
        String account="";
        if (AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT)!=null) {
            account= AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT);
        }
        String password="";
        if (AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT)!=null) {
            password= AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_PASSWORD);
        }

        if (account!=""&&password!="") {
            Map<String, String> sb = new HashMap<String, String>();
            sb.put("imei", appContext.getIMSI());
            sb.put("umeng_token",UmengRegistrar.getRegistrationId(mContext));
            sb.put("username", account);
            sb.put("password", password);
            try {
                request(URLs.LOGIN_VALIDATE_HTTP, JsonUtil.toJson(sb));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void parserResp(String resp){

        try{
            LoginResp loginResp=(LoginResp)JsonUtil.ObjFromJson(resp,LoginResp.class);
            if (loginResp.getData()!=null&&loginResp.getSuccess()==1){
                appContext.setProperty("uid",""+loginResp.getData().getId());
                appContext.setProperty("login_flag","1");
                openMain();
            }else{
                appContext.setProperty("login_flag","0");
                openMain();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void request(final String url,final String params){
        progress= AppUtil.showProgress(this, getString(R.string.login_loading));
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }
                if (progress!=null) {
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

    private void parserConfigResp(String resp){
        Log.e(TAG,resp);
        try{
            TargetInfoResp targetInfoResp=(TargetInfoResp)JsonUtil.ObjFromJson(resp,TargetInfoResp.class);
            if (targetInfoResp.getTargetList()!=null&&targetInfoResp.getSuccess()==1){
                appContext.saveObject(targetInfoResp,"TargetInfoResp");
                if (targetInfoResp.getTargetList().size()>0){
                    for (int j=0;j<targetInfoResp.getTargetList().size();j++){
                        addTargetEntity(targetInfoResp.getTargetList().get(j));
                    }
                }
                if (targetInfoResp.getConfigs()!=null&&targetInfoResp.getConfigs().size()>0){
                    for (int i=0;i<targetInfoResp.getConfigs().size();i++){
                        addConfigEntity(targetInfoResp.getConfigs().get(i));
                    }
                }
                String loginFlag=appContext.getProperty("login_flag");
                Log.e(TAG,"login flag "+loginFlag);
                if (loginFlag!=null&&loginFlag.equals("0")){
                    if (mAutoLogin!=null&&mAutoLogin.equals("1")){
                        if (appContext.getProperty("userType").equals("0")) {
                            autoLogin();
                        }else{
                            openMain();
                        }
                    }else{
                        Intent intent=new Intent(StartActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else if(loginFlag==null){
                    Intent intent=new Intent(StartActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    openMain();
                }

            }else{
                Intent intent=new Intent(StartActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestConfig(final String url,final String params){
        progress= AppUtil.showProgress(this, getString(R.string.login_loading));
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserConfigResp(msg.obj.toString());
                }
                if (progress!=null) {
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

    private void addConfigEntity(ConfigEntity entity){
        try{
            Dao<ConfigEntity,Integer> dao=getDatabaseHelper().getConfigDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void addTargetEntity(TargetEntity entity){
        try{
            Dao<TargetEntity,Integer> dao=getDatabaseHelper().getTargetInfoDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
