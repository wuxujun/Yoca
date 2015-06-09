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


/**
 * Created by xujunwu on 14/12/20.
 */
public class NotifyService extends Service {

    private Notification    mNotification;
    private NotificationManager     mNotificationManager;

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg){

        }
    }

    final Messenger messenger=new Messenger(new IncomingHandler());

    @Override
    public void onCreate(){
        super.onCreate();

        Intent startIntent=new Intent(this,AlarmReceiver.class);
        startIntent.setAction("com.xujun.app.yoca.alarm.action");
        PendingIntent sender=PendingIntent.getBroadcast(this,0,startIntent,0);
        long firsttime= SystemClock.elapsedRealtime();
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firsttime,1*60*1000,sender);
        initNotifiManager();
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
