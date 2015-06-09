package com.xujun.app.yoca;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by xujunwu on 14/12/20.
 */
public class AlarmReceiver extends BroadcastReceiver{

    public void onReceive(Context context,Intent intent){
        if (intent.getAction().equals("com.xujun.app.yoca.alarm.action")){
            Intent startIntent=new Intent();
            startIntent.setClass(context,NotifyService.class);
            context.startService(startIntent);
        }
    }
}
