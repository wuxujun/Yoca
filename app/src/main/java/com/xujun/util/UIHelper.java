package com.xujun.util;

import android.app.Activity;
import android.content.Intent;

import com.xujun.app.yoca.HomeActivity;
import com.xujun.app.yoca.IntroductionActivity;

/**
 * Created by xujunwu on 15/4/20.
 */
public class UIHelper {

    public static void refreshActionBarMenu(Activity activity){
        activity.invalidateOptionsMenu();
    }

    public static void openHome(Activity activity){
        Intent intent=new Intent(activity,HomeActivity.class);
        activity.startActivity(intent);
    }

    public static void openIntroduction(Activity activity){
        Intent intent=new Intent(activity, IntroductionActivity.class);
        activity.startActivity(intent);
    }
}
