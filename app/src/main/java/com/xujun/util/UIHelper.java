package com.xujun.util;

import android.app.Activity;

/**
 * Created by xujunwu on 15/4/20.
 */
public class UIHelper {

    public static void refreshActionBarMenu(Activity activity){
        activity.invalidateOptionsMenu();
    }
}
