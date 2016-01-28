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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.nostra13.universalimageloader.utils.L;
import com.xujun.model.BaseResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.InfoEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
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
    private Handler handlerMacSync=new Handler();

    private Handler handlerLast=new Handler();


    private Handler handlerHealthSync=new Handler();

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
                case AppConfig.ACTION_MAC_ADDRESS_SYNC:{
                    handlerMacSync.post(macAddrSync_run);
                    break;
                }
                case AppConfig.ACTION_WEIGHT_PACKET_SENDER:{
                   try{
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("value",msg.obj);
                        params.put("imei", mAppContext.getIMSI());
                        request(URLs.SYNC_WEIGHT_PACKET_URL, JsonUtil.toJson(params).toString(),null);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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
        initHealthData();
        syncLast();
    }

    private void syncLast(){
        handlerLast.post(sync_last);
    }

    private void initHealthData(){
        try{
            Dao<WeightEntity, Integer> dao = getDatabaseHelper().getWeightEntityDao();
            List<WeightEntity> list=dao.queryForAll();
            if (list.size()>0){
                handlerHealthSync.post(health_run);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void deleteHealthData(long aid,int dType){
        try{
            Dao<HealthEntity,Integer> dao=getDatabaseHelper().getHealthDao();
            DeleteBuilder<HealthEntity,Integer> deleteBuilder=dao.deleteBuilder();
            if (dType==1) {
                deleteBuilder.where().eq("dataType", dType).and().eq("aid",aid).and().lt("pickTime", DateUtil.getHealthTimeForWeek(0));
            }else if(dType==2){
                deleteBuilder.where().eq("dataType",dType).and().eq("aid",aid).and().lt("pickTime", DateUtil.getHealthTimeForMonth(0));
            }else{
                deleteBuilder.where().eq("dataType",dType).and().eq("aid",aid).and().lt("pickTime", DateUtil.getHealthTimeForYear(0));
            }
            deleteBuilder.delete();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    private void createHealthData(long aid,int dType){
        int size=7;
        if (dType==2){
            size=30;
        }else if(dType==3){
            size=12;
        }
        for (int i=0;i<size;i++){
            String time=getHealthTime(dType,i);
            int count=getHealthData(aid,time,dType);
            if (count==0){
                if (dType==3){
                    addYearHealth(aid, time);
                }else {
                    insertHealthForWeight(aid, dType, time);
                }
            }else{
                if (dType==3){
                    updateYearHealth(aid,time);
                }
            }

        }
    }

    private  void updateYearHealth(long aid,String time) {
        try {
            Dao<HealthEntity ,Integer> healthEntityDao=getDatabaseHelper().getHealthDao();
            QueryBuilder<HealthEntity, Integer> queryBuilder = healthEntityDao.queryBuilder();
            queryBuilder.where().eq("aid", aid).and().eq("pickTime", time);
            List<HealthEntity> list = queryBuilder.query();
            long hid=System.currentTimeMillis();
            if (list.size()>0){
                HealthEntity entity=list.get(0);
                hid=entity.getHid();
            }

            Dao<WeightHisEntity, Integer> dao = getDatabaseHelper().getWeightHisEntityDao();
            GenericRawResults<String[]> rawResults = dao.queryRaw("select count(*),sum(weight),sum(bmi),sum(fat),sum(subFat),sum(visFat)," +
                    "sum(BMR),sum(water),sum(muscle),sum(bone),sum(protein),sum(bodyAge) from t_weight_his where aid=" + aid + " and pickTime like '" + time + "%' ");
            List<String[]> results = rawResults.getResults();
            if (results.size() > 0) {
                for (int j=0;j<results.size();j++){
                    HealthEntity healthEntity=new HealthEntity();
                    healthEntity.setHid(hid);
                    healthEntity.setAid(aid);
                    healthEntity.setDataType(3);
                    healthEntity.setPickTime(time);
                    String[] resultArray = results.get(j);
                    int count = Integer.parseInt(resultArray[0]);
                    if (count==0){
                        healthEntity.setSholai("0");
                        healthEntity.setWeight("0");
                        healthEntity.setBmi("0");
                        healthEntity.setFat("0");
                        healthEntity.setSubFat("0");
                        healthEntity.setVisFat("0");
                        healthEntity.setBMR("0");
                        healthEntity.setWater("0");
                        healthEntity.setMuscle("0");
                        healthEntity.setBone("0");
                        healthEntity.setProtein("0");
                        healthEntity.setBodyAge("0");
                    }else {
//                        Log.e(TAG, "pickTime " + time + "  " + resultArray[0] + "  " + resultArray[1] + "  " + resultArray[2] + "  " + resultArray[3] + "  " + resultArray[4] + "  " + resultArray[5] + "  " + resultArray[6] + "  " + resultArray[7] + "  " + resultArray[8] + "  " + resultArray[9] + "  " + resultArray[10] + "  " + resultArray[11]);
//                        Log.e(TAG, "pickTime:" + time + "  " + StringUtil.toDouble(resultArray[0]) / count + "  " + StringUtil.toDouble(resultArray[1]) / count + "  " + StringUtil.toDouble(resultArray[2]) / count +
//                                "  " + StringUtil.toDouble(resultArray[3]) / count + "  " + StringUtil.toDouble(resultArray[4]) / count + "  " + StringUtil.toDouble(resultArray[5]) / count +
//                                "  " + StringUtil.toDouble(resultArray[6]) / count + "  " + StringUtil.toDouble(resultArray[7]) / count + "  " + StringUtil.toDouble(resultArray[8]) / count +
//                                "  " + StringUtil.toDouble(resultArray[9]) / count + "  " + StringUtil.toDouble(resultArray[10]) / count + "  " + StringUtil.toDouble(resultArray[11]) / count);
                        healthEntity.setSholai("0");
                        healthEntity.setWeight(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[1]) / count));
                        healthEntity.setBmi(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[2]) / count));
                        healthEntity.setFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[3]) / count));
                        healthEntity.setSubFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[4]) / count));
                        healthEntity.setVisFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[5]) / count));
                        healthEntity.setBMR(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[6]) / count));
                        healthEntity.setWater(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[7]) / count));
                        healthEntity.setMuscle(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[8]) / count));
                        healthEntity.setBone(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[9]) / count));
                        healthEntity.setProtein(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[10]) / count));
                        healthEntity.setBodyAge(StringUtil.doubleToString(StringUtil.toDouble(resultArray[11])/count));
                    }
                    addHealthEntity(healthEntity);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * 添加每月数据汇总 年
     * @param time
     */
    private  void addYearHealth(long aid,String time) {
        try {

            Dao<WeightHisEntity, Integer> dao = getDatabaseHelper().getWeightHisEntityDao();
            GenericRawResults<String[]> rawResults = dao.queryRaw("select count(*),sum(weight),sum(bmi),sum(fat),sum(subFat),sum(visFat)," +
                    "sum(BMR),sum(water),sum(muscle),sum(bone),sum(protein),sum(bodyAge) from t_weight_his where aid=" + aid + " and pickTime like '" + time + "%' ");
            List<String[]> results = rawResults.getResults();
            if (results.size() > 0) {
                for (int j=0;j<results.size();j++){
                    HealthEntity healthEntity=new HealthEntity();
                    healthEntity.setHid(System.currentTimeMillis());
                    healthEntity.setAid(aid);
                    healthEntity.setDataType(3);
                    healthEntity.setPickTime(time);
                    String[] resultArray = results.get(j);
                    int count = Integer.parseInt(resultArray[0]);
                    if (count==0){
                        healthEntity.setSholai("0");
                        healthEntity.setWeight("0");
                        healthEntity.setBmi("0");
                        healthEntity.setFat("0");
                        healthEntity.setSubFat("0");
                        healthEntity.setVisFat("0");
                        healthEntity.setBMR("0");
                        healthEntity.setWater("0");
                        healthEntity.setMuscle("0");
                        healthEntity.setBone("0");
                        healthEntity.setProtein("0");
                        healthEntity.setBodyAge("0");
                    }else {
//                        Log.e(TAG, "pickTime " + time + "  " + resultArray[0] + "  " + resultArray[1] + "  " + resultArray[2] + "  " + resultArray[3] + "  " + resultArray[4] + "  " + resultArray[5] + "  " + resultArray[6] + "  " + resultArray[7] + "  " + resultArray[8] + "  " + resultArray[9] + "  " + resultArray[10] + "  " + resultArray[11]);
//                        Log.e(TAG, "pickTime:" + time + "  " + StringUtil.toDouble(resultArray[0]) / count + "  " + StringUtil.toDouble(resultArray[1]) / count + "  " + StringUtil.toDouble(resultArray[2]) / count +
//                                "  " + StringUtil.toDouble(resultArray[3]) / count + "  " + StringUtil.toDouble(resultArray[4]) / count + "  " + StringUtil.toDouble(resultArray[5]) / count +
//                                "  " + StringUtil.toDouble(resultArray[6]) / count + "  " + StringUtil.toDouble(resultArray[7]) / count + "  " + StringUtil.toDouble(resultArray[8]) / count +
//                                "  " + StringUtil.toDouble(resultArray[9]) / count + "  " + StringUtil.toDouble(resultArray[10]) / count + "  " + StringUtil.toDouble(resultArray[11]) / count);
                        healthEntity.setSholai("0");
                        healthEntity.setWeight(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[1]) / count));
                        healthEntity.setBmi(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[2]) / count));
                        healthEntity.setFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[3]) / count));
                        healthEntity.setSubFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[4]) / count));
                        healthEntity.setVisFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[5]) / count));
                        healthEntity.setBMR(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[6]) / count));
                        healthEntity.setWater(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[7]) / count));
                        healthEntity.setMuscle(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[8]) / count));
                        healthEntity.setBone(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[9]) / count));
                        healthEntity.setProtein(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[10]) / count));
                        healthEntity.setBodyAge(StringUtil.doubleToString(StringUtil.toDouble(resultArray[11]) / count));
                    }
                    addHealthEntity(healthEntity);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /***
     * 计算周 月图表数据
     * @param aid
     * @param dType
     * @param pickTime
     */
    private void insertHealthForWeight(long aid,int dType,String pickTime){
        WeightEntity weightEntity=searchWeightForDate(aid,dType,pickTime);
        HealthEntity healthEntity=new HealthEntity();
        healthEntity.setHid(System.currentTimeMillis());
        healthEntity.setAid(aid);
        healthEntity.setDataType(dType);
        healthEntity.setPickTime(pickTime);
        if (weightEntity!=null&&weightEntity.getAid()>0){
            healthEntity.setSholai("0");
            healthEntity.setWeight(weightEntity.getWeight());
            healthEntity.setBmi(weightEntity.getBmi());
            healthEntity.setFat(weightEntity.getFat());
            healthEntity.setSubFat(weightEntity.getSubFat());
            healthEntity.setVisFat(weightEntity.getVisFat());
            healthEntity.setBMR(weightEntity.getBMR());
            healthEntity.setWater(weightEntity.getWater());
            healthEntity.setMuscle(weightEntity.getMuscle());
            healthEntity.setBone(weightEntity.getBone());
            healthEntity.setProtein(weightEntity.getProtein());
            healthEntity.setBodyAge(weightEntity.getBodyAge());
        }else{
            healthEntity.setSholai("0");
            healthEntity.setWeight("0");
            healthEntity.setBmi("0");
            healthEntity.setFat("0");
            healthEntity.setSubFat("0");
            healthEntity.setVisFat("0");
            healthEntity.setBMR("0");
            healthEntity.setWater("0");
            healthEntity.setMuscle("0");
            healthEntity.setBone("0");
            healthEntity.setProtein("0");
            healthEntity.setBodyAge("0");
        }
        addHealthEntity(healthEntity);
    }

    private String getHealthTime(int dType,int idx){
        String time=DateUtil.getWeekFirst();
        switch (dType){
            case 1:
            {
                time=DateUtil.getHealthTimeForWeek(idx);
                break;
            }
            case 2:{
                time=DateUtil.getHealthTimeForMonth(idx);
                break;
            }
            case 3:{
                time=DateUtil.getHealthTimeForYear(idx);
                break;
            }
        }
        return time;
    }

    private Runnable macAddrSync_run=new Runnable() {
        @Override
        public void run() {
            try{
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mac_addr",mAppContext.getProperty(AppConfig.DEVICE_MAC_ADDRESS));
                params.put("mid", mAppContext.getProperty(AppConfig.CONF_USER_UID));
                params.put("imei", mAppContext.getIMSI());
                request(URLs.SYNC_MACADDR_URL, JsonUtil.toJson(params).toString(),null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /***
     * 同步近七天记录
     */
    private Runnable sync_last=new Runnable() {
        @Override
        public void run() {
            try {
                int count = getAllWeightHis();
                if (count == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("syncid", "0");
                    params.put("start","0");
                    params.put("end","20");
                    params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID));
                    params.put("imei", mAppContext.getIMSI());
                    request(URLs.SYNC_WEIGHT_URL, JsonUtil.toJson(params).toString(), null);
                    request(URLs.SYNC_WEIGHT_HIS_URL, JsonUtil.toJson(params).toString(), null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void requestSyncWeight(int type,int start,int end){
        try{
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("syncid", "0");
            params.put("start",start);
            params.put("end",end);
            params.put("uid", mAppContext.getProperty(AppConfig.CONF_USER_UID));
            params.put("imei", mAppContext.getIMSI());
            if (type==0) {
                request(URLs.SYNC_WEIGHT_URL, JsonUtil.toJson(params).toString(), null);
            }else {
                request(URLs.SYNC_WEIGHT_HIS_URL, JsonUtil.toJson(params).toString(), null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getAllWeightHis(){
        try{
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            QueryBuilder<WeightEntity,Integer> queryBuilder=dao.queryBuilder();
            List<WeightEntity> lists=dao.queryForAll();
            if (lists.size()>0){
                Dao<WeightHisEntity,Integer> dao1=getDatabaseHelper().getWeightHisEntityDao();
                QueryBuilder<WeightHisEntity,Integer> queryBuilder1=dao1.queryBuilder();
                List<WeightHisEntity> lists1=dao1.queryForAll();
                if (lists1.size()>0) {
                    return lists1.size();
                }
                return lists.size();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private Runnable health_run=new Runnable(){

        @Override
        public void run() {
            try{
                Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
                QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
                Where<AccountEntity,Integer> where=queryBuilder.where();
                where.or(where.eq("type",0),where.eq("type",1));
                queryBuilder.orderBy("type", true);
                PreparedQuery<AccountEntity> preparedQuery=queryBuilder.prepare();
                List<AccountEntity> lists=dao.query(preparedQuery);
                if (lists.size()>0){
                    for (int i=0;i<lists.size();i++){
                        AccountEntity accountEntity=lists.get(i);
                        if (accountEntity!=null&&accountEntity.getId()>0){
                            deleteHealthData(accountEntity.getId(),1);
                            deleteHealthData(accountEntity.getId(),2);
                            deleteHealthData(accountEntity.getId(),3);
                            createHealthData(accountEntity.getId(),1);
                            createHealthData(accountEntity.getId(),2);
                            createHealthData(accountEntity.getId(),3);
                        }
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    };

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
            L.e(TAG, "无网络连接，暂不同步。");
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
//                        Log.e(TAG, "uploadAvatarData " + hisEntity.getAvatar());
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
                    if ((weightResp.getStart()+weightResp.getEnd())<weightResp.getTotal()){
                        requestSyncWeight(0,weightResp.getStart()+weightResp.getEnd(),20+weightResp.getStart()+weightResp.getEnd());
                    }
                }
            }else if(baseResp.getDataType().equals("getHealth")){
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
                    if ((weightHisResp.getStart()+weightHisResp.getEnd())<weightHisResp.getTotal()){
                        requestSyncWeight(1, weightHisResp.getStart() + weightHisResp.getEnd(),20+weightHisResp.getStart()+weightHisResp.getEnd());
                    }
                    Dao<WeightHisEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                    QueryBuilder<WeightHisEntity, Integer> weightQueryBuilder = weightEntityDao.queryBuilder();
                    List<WeightHisEntity> list=weightQueryBuilder.query();
                    if (list!=null){
                        handlerHealthSync.post(health_run);
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

    private WeightEntity searchWeightForDate(long aid,int dType,String pickTime){
        try {
            Dao<WeightEntity, Integer> weightEntities = getDatabaseHelper().getWeightEntityDao();
            QueryBuilder<WeightEntity, Integer> queryBuilder = weightEntities.queryBuilder();
            queryBuilder.where().eq("aid", aid).and().eq("pickTime", pickTime);
            List<WeightEntity> list = queryBuilder.query();
            if (list.size()>0){
                return list.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new WeightEntity();
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
//        try{
//            HealthEntity entity=searchForHealth(accountId, pickTime, targetType);
//            if (entity==null){
//                entity=new HealthEntity();
//                entity.setAccountId(accountId);
//                entity.setPickTime(pickTime);
//                entity.setCreateTime(System.currentTimeMillis());
//            }
//            entity.setTargetType(targetType);
//            entity.setTargetValue(targetValue);
//            entity.setIsSync(0);
//            Dao<HealthEntity,Integer> dao=getDatabaseHelper().getHealthDao();
//            dao.setAutoCommit(dao.startThreadConnection(),false);
//            dao.createOrUpdate(entity);
//            dao.commit(dao.startThreadConnection());
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
    }

    private void addHealthEntity(HealthEntity entity){
        try{
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

    private int getHealthData(long aid,String pickTime,int dType){
        try{
            Dao<HealthEntity,Integer> dao=getDatabaseHelper().getHealthDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select count(1) from t_health where aid="+aid+" and dataType="+dType+"  and pickTime='"+pickTime+"' ");
            List<String[]> results=rawResults.getResults();
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
