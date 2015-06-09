package com.xujun.app.yoca;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.xujun.app.yoca.widget.LockPatternView;
import com.xujun.util.StringUtil;

import java.util.List;

/**
 * Created by xujunwu on 15/4/6.
 */
public class LockActivity extends SherlockActivity implements
        LockPatternView.OnPatternListener {
    private static final String TAG = "LockActivity";

    private Context mContext;
    private AppContext      appContext;
    private List<LockPatternView.Cell> lockPattern;
    private LockPatternView lockPatternView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(AppConfig.LOCK,
                MODE_PRIVATE);
        String patternString = preferences.getString(AppConfig.LOCK_KEY,
                null);
        if (patternString == null) {
            finish();
            return;
        }
        lockPattern = LockPatternView.stringToPattern(patternString);
        setContentView(R.layout.lock_activity);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);

        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
        Log.d(TAG, "onPatternCellAdded");
        Log.e(TAG, LockPatternView.patternToString(pattern));
        // Toast.makeText(this, LockPatternView.patternToString(pattern),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        Log.d(TAG, "onPatternDetected");
        if (pattern.equals(lockPattern)) {
            String type=AppConfig.getAppConfig(mContext).get(AppConfig.USER_LOCK_TYPE);
            if (type!=null&&type.equals("0")) {
                AppConfig.getAppConfig(mContext).set(AppConfig.USER_LOCK_PASS, "0");
            }
            finish();
        } else {
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG)
                    .show();
        }

    }

}
