package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WarnEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 14/12/15.
 */
public class HomeActivity extends SherlockActivity implements View.OnClickListener{

    private static final String TAG = "HomeActivity";

    private Context mContext;
    private AppContext      appContext;

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
        setContentView(R.layout.layout_home);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.ibWeibo).setOnClickListener(this);
        findViewById(R.id.ibWeixin).setOnClickListener(this);
        findViewById(R.id.ibQQ).setOnClickListener(this);

        initPlatforms();
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
//        testData();
    }

    private void initPlatforms()
    {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(HomeActivity.this, "100424468",
                "c7394704798a158208a74ab60104f0ba");
        qqSsoHandler.addToSocialSDK();

//        UMWXHandler wxHandler = new UMWXHandler(HomeActivity.this,"","");
//        wxHandler.addToSocialSDK();

        mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }


    private void initData(){
        try{
            Dao<WarnEntity,Integer> dao=getDatabaseHelper().getWarnEntityDao();
            QueryBuilder<WarnEntity,Integer> queryBuilder=dao.queryBuilder();
            queryBuilder.where().eq("type",0);
            queryBuilder.orderBy("wid",true);
            PreparedQuery<WarnEntity> preparedQuery=queryBuilder.prepare();
            List<WarnEntity> lists=dao.query(preparedQuery);
            if (lists.size()==0){
                WarnEntity entity=new WarnEntity();
                entity.setType(0);
                entity.setValue("Add");
                entity.setStatus(0);
                dao.createOrUpdate(entity);
            }


            Dao<AccountEntity,Integer> dao1=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder1=dao1.queryBuilder();
            queryBuilder1.where().eq("type",2);
            queryBuilder1.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery1=queryBuilder1.prepare();
            List<AccountEntity> accountEntityList=dao1.query(preparedQuery1);
            if (accountEntityList.size()==0){
                AccountEntity entity=new AccountEntity();
                entity.setType(2);
                entity.setUserNick("Add");
                entity.setStatus(0);
                dao1.createOrUpdate(entity);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:{
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnRegister:{
                Intent intent=new Intent(HomeActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.ibWeixin:
            {
                mController.doOauthVerify(HomeActivity.this,SHARE_MEDIA.WEIXIN,new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mController.getPlatformInfo(HomeActivity.this,SHARE_MEDIA.WEIXIN,new SocializeListeners.UMDataListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(HomeActivity.this,"获取平台数据开始...",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete(int status, Map<String, Object> info) {
                                if (status==200&&info!=null){
                                    Log.e(TAG,info.toString()+"");
                                }else{
                                    Toast.makeText(HomeActivity.this,"发生错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                });

                break;
            }
            case R.id.ibQQ:
            {
                mController.doOauthVerify(HomeActivity.this,SHARE_MEDIA.QQ,new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }
                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mController.getPlatformInfo(HomeActivity.this,SHARE_MEDIA.QQ,new SocializeListeners.UMDataListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(HomeActivity.this,"获取平台数据开始...",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete(int status, Map<String, Object> info) {
                                if (status==200&&info!=null){
                                    Log.e(TAG,""+info.toString());
                                }else{
                                    Toast.makeText(HomeActivity.this,"发生错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA share_media) {
                        Toast.makeText(HomeActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Toast.makeText(HomeActivity.this,"授权取消息",Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            }
            case R.id.ibWeibo:{
                mController.doOauthVerify(HomeActivity.this, SHARE_MEDIA.SINA,new SocializeListeners.UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                        mController.getPlatformInfo(HomeActivity.this,SHARE_MEDIA.SINA,new SocializeListeners.UMDataListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(HomeActivity.this,"获取平台数据开始...",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete(int status, Map<String, Object> stringObjectMap) {
                                if (status==200&&stringObjectMap!=null){
                                    Log.e(TAG,"..."+stringObjectMap.toString());
                                }else{
                                    Log.e(TAG,"发生错误."+status);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(SocializeException e, SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                });
                break;
            }
        }
    }

    private void testData()
    {
        try{
            WeightHisEntity entity=new WeightHisEntity();
            entity.setAid(1);
            entity.setPickTime("2015-03-23");
            entity.setWeight(Double.parseDouble("10.00"));
            entity.setFat(Double.parseDouble("10.00"));
            entity.setSubFat(Double.parseDouble("10.00"));
            entity.setVisFat(Double.parseDouble("10.00"));
            entity.setWater(Double.parseDouble("10.00"));
            entity.setBMR(Double.parseDouble("10.00"));
            entity.setBodyAge(Integer.parseInt("10"));
            entity.setMuscle(Double.parseDouble("10.00"));
            entity.setBone(Double.parseDouble("10.00"));
            entity.setAddtime(System.currentTimeMillis());
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());

            GenericRawResults<String[]> rawResults=dao.queryRaw("select pickTime,count(*),sum(weight),sum(fat),sum(subFat),sum(visFat)," +
                    "sum(water),sum(BMR),sum(bodyAge),sum(muscle),sum(bone) from t_weight_his group by pickTime");
            List<String[]> results=rawResults.getResults();
            String[] resultArray=results.get(0);
            Log.e(TAG, "pickTime "+resultArray[0]+"  "+resultArray[1]+"  "+resultArray[2]+"  "+resultArray[3]+"  "+resultArray[4]+"  "+resultArray[5]+"  "+resultArray[6]+"  "+resultArray[7]+"  "+resultArray[8]+"  "+resultArray[9]+"  "+resultArray[10]);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private UMSocialService mController= UMServiceFactory.getUMSocialService("com.umeng.login");
}
