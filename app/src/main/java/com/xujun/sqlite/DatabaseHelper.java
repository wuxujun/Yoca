package com.xujun.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by xujunwu on 14/12/10.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME="yoca.db";
    private static final int    DATABASE_VERSION=16;

    private Dao<AccountEntity,Integer>   accountDao;
    private Dao<WarnEntity,Integer>      warnDao;
    private Dao<WeightEntity,Integer>    weightDao;
    private Dao<WeightHisEntity,Integer> weightHisDao;
    private Dao<HealthEntity,Integer>    healthDao;
    private Dao<HomeTargetEntity,Integer> homeTargetDao;
    private Dao<TargetEntity,Integer>     targetInfoDao;
    private Dao<SendRecord,Integer>       sendRecordDao;

    private Dao<ConfigEntity,Integer>    configDao;
    private Dao<InfoEntity,Integer>      infoDao;

    private static final AtomicInteger usageCounter=new AtomicInteger(0);
    private static DatabaseHelper          helper=null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getDatabaseHelper(Context context){
        if (helper==null){
            helper=new DatabaseHelper(context);
        }
        usageCounter.incrementAndGet();
        return helper;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, com.j256.ormlite.support.ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource,AccountEntity.class);
            TableUtils.createTable(connectionSource,WarnEntity.class);
            TableUtils.createTable(connectionSource,WeightEntity.class);
            TableUtils.createTable(connectionSource,WeightHisEntity.class);
            TableUtils.createTable(connectionSource,HealthEntity.class);
            TableUtils.createTable(connectionSource,ConfigEntity.class);
            TableUtils.createTable(connectionSource,HomeTargetEntity.class);
            TableUtils.createTable(connectionSource,TargetEntity.class);
            TableUtils.createTable(connectionSource,InfoEntity.class);
            TableUtils.createTable(connectionSource,SendRecord.class);

        }catch (SQLException e){
            Log.e(DatabaseHelper.class.getName(), " unable to create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, com.j256.ormlite.support.ConnectionSource connectionSource, int i, int i2) {
        try{
            if(i<5) {
                TableUtils.dropTable(connectionSource, AccountEntity.class, true);
                TableUtils.dropTable(connectionSource, WarnEntity.class, true);
                TableUtils.dropTable(connectionSource, WeightEntity.class, true);
                TableUtils.dropTable(connectionSource, WeightHisEntity.class, true);
                TableUtils.dropTable(connectionSource, HealthEntity.class, true);
                onCreate(sqLiteDatabase, connectionSource);
            }
            if (i==6){
                TableUtils.dropTable(connectionSource,ConfigEntity.class,true);
                TableUtils.createTable(connectionSource, ConfigEntity.class);

            }
            if (i==7){
                TableUtils.dropTable(connectionSource,HomeTargetEntity.class,true);
                TableUtils.createTable(connectionSource,HomeTargetEntity.class);
            }
            if (i<13){
                TableUtils.dropTable(connectionSource,AccountEntity.class,true);
                TableUtils.dropTable(connectionSource, WarnEntity.class, true);
                TableUtils.dropTable(connectionSource, WeightEntity.class, true);
                TableUtils.dropTable(connectionSource, WeightHisEntity.class, true);
                TableUtils.dropTable(connectionSource, HealthEntity.class, true);
                TableUtils.dropTable(connectionSource, ConfigEntity.class,true);
                TableUtils.dropTable(connectionSource, HomeTargetEntity.class,true);
                TableUtils.dropTable(connectionSource, TargetEntity.class,true);
                onCreate(sqLiteDatabase, connectionSource);
            }
            if (i==13){
                TableUtils.dropTable(connectionSource,AccountEntity.class,true);
                TableUtils.createTable(connectionSource,AccountEntity.class);
            }
            if (i==14){
                TableUtils.createTable(connectionSource,SendRecord.class);
            }
            if (i==15){
                TableUtils.dropTable(connectionSource,AccountEntity.class,true);
                TableUtils.createTable(connectionSource,AccountEntity.class);
            }

        }catch (SQLException e){
            Log.e(DatabaseHelper.class.getName(),"Can't drop database ",e);
            throw new RuntimeException(e);
        }
    }

    public Dao<AccountEntity,Integer> getAccountEntityDao()throws SQLException{
        if (accountDao==null){
            accountDao=getDao(AccountEntity.class);
        }
        return accountDao;
    }

    public Dao<WarnEntity,Integer> getWarnEntityDao()throws SQLException{
        if (warnDao==null){
            warnDao=getDao(WarnEntity.class);
        }
        return warnDao;
    }

    public Dao<WeightEntity,Integer> getWeightEntityDao()throws SQLException{
        if (weightDao==null){
            weightDao=getDao(WeightEntity.class);
        }
        return weightDao;
    }

    public Dao<WeightHisEntity,Integer> getWeightHisEntityDao()throws SQLException{
        if (weightHisDao==null){
            weightHisDao=getDao(WeightHisEntity.class);
        }
        return weightHisDao;
    }

    public Dao<HealthEntity,Integer> getHealthDao()throws SQLException{
        if (healthDao==null){
            healthDao=getDao(HealthEntity.class);
        }
        return healthDao;
    }


    public Dao<ConfigEntity,Integer> getConfigDao()throws SQLException{
        if (configDao==null){
            configDao=getDao(ConfigEntity.class);
        }
        return configDao;
    }

    public Dao<HomeTargetEntity,Integer> getHomeTargetDao() throws SQLException{
        if (homeTargetDao==null){
            homeTargetDao=getDao(HomeTargetEntity.class);
        }
        return homeTargetDao;
    }

    public Dao<TargetEntity,Integer> getTargetInfoDao() throws SQLException
    {
        if (targetInfoDao==null){
            targetInfoDao=getDao(TargetEntity.class);
        }
        return targetInfoDao;
    }

    public Dao<InfoEntity,Integer> getInfoDao()throws  SQLException{
        if (infoDao==null){
            infoDao=getDao(InfoEntity.class);
        }
        return infoDao;
    }

    public Dao<SendRecord,Integer> getSendRecordDao() throws  SQLException{
        if (sendRecordDao==null){
            sendRecordDao=getDao(SendRecord.class);
        }
        return sendRecordDao;
    }

    public void close(){
        if (usageCounter.decrementAndGet()==0){
            super.close();
            accountDao=null;
            warnDao=null;
            weightDao=null;
            weightHisDao=null;
            healthDao=null;
            configDao=null;
            homeTargetDao=null;
            infoDao=null;
            sendRecordDao=null;
            helper=null;
        }
    }
}
