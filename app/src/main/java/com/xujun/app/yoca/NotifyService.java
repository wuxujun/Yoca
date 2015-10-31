package com.xujun.app.yoca;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.utils.L;
import com.xujun.app.yoca.fragment.ContentFragment;
import com.xujun.model.BaseResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.JsonUtil;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;

import org.json.JSONException;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xujunwu on 14/12/20.
 */
public class NotifyService extends Service {

    private static final String TAG="NotifyService";

    private AppContext      mAppContext;
    private Context         mContext;

    private DatabaseHelper databaseHelper;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(mAppContext);
        }
        return databaseHelper;
    }

    private Notification    mNotification;
    private NotificationManager     mNotificationManager;

    private Handler handlerUploadData=new Handler();
    private Handler handlerDownloadData=new Handler();
    private Handler handlerAccountSync=new Handler();
    private Handler handlerWarnSync=new Handler();
    private Handler handlerAvatarSync=new Handler();

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case AppConfig.ACTION_WEIGH_DATA_UPLOAD:{
                    L.e("HandleMessage ACTION_WEIGH_DATA_UPLOAD");
                    handlerUploadData.post(uploadData_run);
                    break;
                }
                case AppConfig.ACTION_WEIGH_DATA_DOWNLOAD:{
                    L.e("HandleMessage ACTION_WEIGH_DATA_DOWNLOAD");
                    handlerDownloadData.post(downloadData_run);
                    break;
                }
                case AppConfig.ACTION_ACCOUNT_SYNC:{
                    L.e(TAG,"HandleMessage ACTION_ACCOUNT_SYNC");
                    handlerAccountSync.post(accountSync_run);
                    break;
                }
                case AppConfig.ACTION_WARNDATA_SYNC:{
                    L.e(TAG,"HandleMessage ACTION_WARNDATA_SYNC");
                    handlerWarnSync.post(warnDataSync_run);
                    break;
                }
                case AppConfig.ACTION_WEIGH_DATA_AVATAR:{
                    handlerAvatarSync.post(avatarDataSync_run);
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger messenger=new Messenger(new IncomingHandler());

    @Override
    public void onCreate(){
        super.onCreate();
        mAppContext=(AppContext)getApplication();
        mContext=getApplicationContext();

        Intent startIntent=new Intent(this,AlarmReceiver.class);
        startIntent.setAction("com.xujun.app.yoca.alarm.action");
        PendingIntent sender=PendingIntent.getBroadcast(this,0,startIntent,0);
        long firsttime= SystemClock.elapsedRealtime();
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firsttime,1*60*1000,sender);
        initNotifiManager();
    }

    private Runnable uploadData_run=new Runnable() {
        @Override
        public void run() {
            uploadWeightData();
        }
    };

    private Runnable downloadData_run=new Runnable() {
        @Override
        public void run() {
            L.e(TAG, "downloadData_run");
        }
    };

    private Runnable accountSync_run=new Runnable() {
        @Override
        public void run() {
            L.e(TAG,"accountSync_run");
            syncAccount();
        }
    };

    private Runnable warnDataSync_run=new Runnable() {
        @Override
        public void run() {
            L.e(TAG, "warnDataSync_run");
        }
    };

    private Runnable avatarDataSync_run=new Runnable() {
        @Override
        public void run() {
            L.e(TAG,"avatarDataSync_run");
            uploadAvatarData();
        }
    };

    private void uploadAvatarData(){
        if (mAppContext.getNetworkType()<1){
            L.e(TAG,"无网络连接，暂不同步。");
            return;
        }
        try {
            Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("isSync", 0);
            List<WeightHisEntity> list=weightHisQueryBuilder.query();
            if (list.size()>0){
                for (int i=0;i<list.size();i++){
                    WeightHisEntity hisEntity=list.get(i);
                    if (!StringUtil.isEmpty(hisEntity.getAvatar())){
                        Log.e(TAG, "uploadAvatarData " + hisEntity.getAvatar());
                        Map<String,Object> params=new HashMap<String, Object>();
                        params.put("wid",hisEntity.getWid());
                        if (!StringUtil.isEmpty(mAppContext.getProperty(AppConfig.CONF_USER_UID))) {
                            params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID));
                        }
                        if (!StringUtil.isEmpty(hisEntity.getBust())){
                            params.put("bust",hisEntity.getBust());
                        }else{
                            params.put("bust","0");
                        }
                        if (!StringUtil.isEmpty(hisEntity.getWaistline())){
                            params.put("waistline",hisEntity.getWaistline());
                        }else{
                            params.put("waistline","0");
                        }
                        if (!StringUtil.isEmpty(hisEntity.getHips())){
                            params.put("hips",hisEntity.getHips());
                        }else{
                            params.put("hips","0");
                        }
                        params.put("aid",hisEntity.getAid());
                        params.put("imei",mAppContext.getIMSI());
                        Map<String,File> files=new HashMap<String, File>();
                        files.put("file",new File(mAppContext.getCameraPath()+"/"+hisEntity.getAvatar()));
                        weightHisEntityDao.updateRaw("UPDATE `t_weight_his` SET isSync = 3 WHERE isSync=0 and wid="+hisEntity.getWid()+";");
                        request(URLs.IMAGE_UPLOAD,JsonUtil.toJson(params).toString(),files);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    private void uploadWeightData()
    {
        if (mAppContext.getNetworkType()<1){
            L.e(TAG,"无网络连接，暂不同步。");
            return;
        }
        try {
            Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("isSync", 0);
            List<WeightHisEntity> list=weightHisQueryBuilder.query();
            if (list.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list);
                if (!StringUtil.isEmpty(mAppContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID));
                }
                params.put("imei", mAppContext.getIMSI());
                weightHisEntityDao.updateRaw("UPDATE `t_weight_his` SET isSync = 3 WHERE isSync=0;");
                request(URLs.WEIGHT_HIS_SYNC_URL, JsonUtil.toJson(params).toString(),null);
            }

            Dao<WeightEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightEntityDao();
            QueryBuilder<WeightEntity, Integer> weightQueryBuilder = weightEntityDao.queryBuilder();
            weightQueryBuilder.where().eq("isSync", 0);
            List<WeightEntity> weightEntityList=weightQueryBuilder.query();
            if (weightEntityList.size()>0) {
                Log.e(TAG,"0000------------>============"+weightEntityList.size());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("root", weightEntityList);
                if (!StringUtil.isEmpty(mAppContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID).toString());
                }
                params.put("imei", mAppContext.getIMSI());
                weightEntityDao.updateRaw("UPDATE `t_weight` SET isSync=3 where isSync=0;");
                request(URLs.WEIGHT_SYNC_URL, JsonUtil.toJson(params).toString(),null);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (JSONException je){
            je.printStackTrace();
        }
    }

    /**
     * 同步帐号
     */
    private void syncAccount(){
        try{
            Dao<AccountEntity,Integer> accountEntities=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> accountEntityIntegerQueryBuilder=accountEntities.queryBuilder();
            accountEntityIntegerQueryBuilder.where().eq("isSync",0).and().notIn("type", 2);
            List<AccountEntity> list1=accountEntityIntegerQueryBuilder.query();
            if (list1.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list1);
                if (!StringUtil.isEmpty(mAppContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID).toString());
                }
                params.put("imei",mAppContext.getIMSI());
                accountEntities.updateRaw("UPDATE `t_account` SET isSync = 3 WHERE isSync=0;");
                request(URLs.ACCOUNT_SYNC_URL,JsonUtil.toJson(params).toString(),null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginSyncDaa(){
        if (mAppContext.getNetworkType()<1){
            L.e("无网络连接，暂不同步。");
            return;
        }
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("syncid", getMaxWeightForSyncId());
            if (!StringUtil.isEmpty(mAppContext.getProperty(AppConfig.CONF_USER_UID))) {
                params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID).toString());
            }
            params.put("imei", mAppContext.getIMSI());
            request(URLs.SYNC_WEIGHT_URL, JsonUtil.toJson(params).toString(),null);

            params.put("syncid", getMaxWeightHisForSyncId());
            request(URLs.SYNC_WEIGHT_HIS_URL, JsonUtil.toJson(params).toString(),null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void request(final String url,final String params,final Map<String,File> files){
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }
            }
        };

        new Thread(){
            public void run(){
                Message msg=new Message();
                try{
                    String resp=mAppContext.sendRequestData(url, params,files);
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

    private void parserResp(String resp){
        L.e("parserResp:" + resp);
        try{
            BaseResp baseResp=(BaseResp)JsonUtil.ObjFromJson(resp,BaseResp.class);
            if (baseResp.getDataType().equals("syncweighthis")){
                if (baseResp.getStatus()==1){
                    Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                    weightHisEntityDao.updateRaw("UPDATE `t_weight_his` SET isSync = 1 WHERE isSync=3;");
                }
            }else if(baseResp.getDataType().equals("syncweight")){
                if (baseResp.getStatus()==1) {
                    Dao<WeightEntity, Integer> weightEngityDao = getDatabaseHelper().getWeightEntityDao();
                    weightEngityDao.updateRaw("UPDATE `t_weight` SET isSync=1 where isSync=3;");
                }
            }else if(baseResp.getDataType().equals("syncaccount")){
                if (baseResp.getStatus()==1) {
                    Dao<AccountEntity, Integer> accountEntityDao = getDatabaseHelper().getAccountEntityDao();
                    accountEntityDao.updateRaw("UPDATE `t_account` SET isSync=1 where isSync=3;");
                }
            }else if(baseResp.getDataType().equals("getWeights")){
                WeightResp weightResp=(WeightResp)JsonUtil.ObjFromJson(resp,WeightResp.class);
                if (weightResp.getRoot()!=null&&weightResp.getRoot().size()>0){
                    for (int i=0;i<weightResp.getRoot().size();i++){
                        addWeightEntity(weightResp.getRoot().get(i));
                    }
                }
            }else if(baseResp.getDataType().equals("getWeightHiss")){
                WeightHisResp weightHisResp=(WeightHisResp)JsonUtil.ObjFromJson(resp,WeightHisResp.class);
                if (weightHisResp.getRoot()!=null&&weightHisResp.getRoot().size()>0){
                    for (int j=0;j<weightHisResp.getRoot().size();j++){
                        addWeightHisEntity(weightHisResp.getRoot().get(j));
                    }
                    Dao<WeightHisEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                    QueryBuilder<WeightHisEntity, Integer> weightQueryBuilder = weightEntityDao.queryBuilder();
                    List<WeightHisEntity> list=weightQueryBuilder.query();
                    if (list!=null){
                        Log.d(TAG,"===============> weight list------------> size "+list.size());
                    }
                }
            }else if(baseResp.getDataType().equals("uploadImage")&&baseResp.getSuccess()==1){
                Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                weightHisEntityDao.updateRaw("UPDATE `t_weight_his` SET isSync = 1,avatar='"+baseResp.getFilename()+"' WHERE isSync=3 and wid="+baseResp.getDataId()+";");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private HealthEntity searchForHealth(long accountId,String pickTime,int targetType){
        try{
            List<HealthEntity> healths=getDatabaseHelper().getHealthDao().queryBuilder().where().eq("accountId",accountId).and().eq("pickTime",pickTime).and().eq("targetType", targetType).query();
            if (healths.size()>0){
                return healths.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private void AddHealthForDay(long accountId,String pickTime,int targetType,String targetValue){
        try{
            HealthEntity entity=searchForHealth(accountId, pickTime, targetType);
            if (entity==null){
                entity=new HealthEntity();
                entity.setAccountId(accountId);
                entity.setPickTime(pickTime);
                entity.setCreateTime(System.currentTimeMillis());
            }
            entity.setTargetType(targetType);
            entity.setTargetValue(targetValue);
            entity.setIsSync(0);
            Dao<HealthEntity,Integer> dao=getDatabaseHelper().getHealthDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void addWeightEntity(WeightEntity entity){
        try{
            if (entity!=null){
                Log.d(TAG,"------------>"+entity.getPickTime()+" "+entity.getWeight()+"  "+entity.getIsSync());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),0,entity.getBmi());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),1,entity.getWeight());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),2,entity.getFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),3,entity.getSubFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),4,entity.getVisFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),5,entity.getWater());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),6,entity.getBMR());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),7,entity.getBodyAge());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),8,entity.getMuscle());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),9,entity.getBone());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),10,entity.getProtein());
            }
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void addWeightHisEntity(WeightHisEntity entity){
        try{
            if (entity!=null){
                Log.d(TAG,"addWeightHisEntity "+entity.getPickTime()+" "+entity.getWeight());
            }
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int  getMaxWeightForSyncId(){
        try{
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select max(syncid) from t_weight ");
            List<String[]> results=rawResults.getResults();
            Log.d(TAG, " select result size:" + results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                if (!StringUtil.isEmpty(resultArray[0])) {
                    return Integer.parseInt(resultArray[0]);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private int  getMaxWeightHisForSyncId(){
        try{
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select max(syncid) from t_weight_his ");
            List<String[]> results=rawResults.getResults();
            Log.d(TAG, " select result size:" + results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                if (!StringUtil.isEmpty(resultArray[0])) {
                    return Integer.parseInt(resultArray[0]);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return messenger.getBinder();
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    private void initNotifiManager(){
        mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int icon=R.drawable.ic_launcher;
        mNotification=new Notification();
        mNotification.icon=icon;
        mNotification.tickerText="New Message";
        mNotification.defaults|=Notification.DEFAULT_SOUND;
        mNotification.flags=Notification.FLAG_AUTO_CANCEL;
    }

    private void showNotification()
    {
        mNotification.when=System.currentTimeMillis();
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,i,0);
        mNotification.contentIntent=pendingIntent;
        mNotificationManager.notify(0,mNotification);
    }

    private int getNetworkInfoType(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo.State mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (mobile== NetworkInfo.State.CONNECTED){
            return 1;
        }
        NetworkInfo.State wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi== NetworkInfo.State.CONNECTED){
            return 2;
        }
        return 0;
    }


    int count=0;
    class PollingThread extends Thread{
        @Override
        public void run(){
            count++;
            if (count%5==0){
                showNotification();

            }
        }
    }
}
