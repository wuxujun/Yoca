package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.xujun.app.yoca.widget.LockPatternView;
import com.xujun.app.yoca.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xujunwu on 15/4/6.
 */
public class LockSetupActivity extends SherlockActivity implements LockPatternView.OnPatternListener,View.OnClickListener{

    private static final String TAG = "LockSetupActivity";

    private Context mContext;
    private AppContext              appContext;

    private LockPatternView     lockPatternView;
    private Button              leftButton;
    private Button              rightButton;

    private static final int STEP_1 = 1; // 开始
    private static final int STEP_2 = 2; // 第一次设置手势完成
    private static final int STEP_3 = 3; // 按下继续按钮
    private static final int STEP_4 = 4; // 第二次设置手势完成
    // private static final int SETP_5 = 4; // 按确认按钮

    private int step;

    private List<LockPatternView.Cell> choosePattern;

    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_setup_activity);
        appContext=(AppContext)getApplication();
        mContext=getApplicationContext();


        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        leftButton = (Button) findViewById(R.id.left_btn);
        rightButton = (Button) findViewById(R.id.right_btn);

        step = STEP_1;
        updateView();

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateView() {
        switch (step) {
            case STEP_1:
                leftButton.setText(R.string.btn_Cancel);
                rightButton.setText("");
                rightButton.setEnabled(false);
                choosePattern = null;
                confirm = false;
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;
            case STEP_2:
                leftButton.setText(R.string.btn_try_again);
                rightButton.setText(R.string.btn_goon);
                rightButton.setEnabled(true);
                lockPatternView.disableInput();
                break;
            case STEP_3:
                leftButton.setText(R.string.btn_Cancel);
                rightButton.setText("");
                rightButton.setEnabled(false);
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;
            case STEP_4:
                leftButton.setText(R.string.btn_Cancel);
                if (confirm) {
                    rightButton.setText(R.string.btn_try_again);
                    rightButton.setEnabled(true);
                    lockPatternView.disableInput();
                } else {
                    rightButton.setText("");
                    lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    lockPatternView.enableInput();
                    rightButton.setEnabled(false);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
                    finish();
                } else if (step == STEP_2) {
                    step = STEP_1;
                    updateView();
                }
                break;

            case R.id.right_btn:
                if (step == STEP_2) {
                    step = STEP_3;
                    updateView();
                } else if (step == STEP_4) {

                    SharedPreferences preferences = getSharedPreferences(
                            AppConfig.LOCK, MODE_PRIVATE);
                    preferences
                            .edit()
                            .putString(AppConfig.LOCK_KEY,
                                    LockPatternView.patternToString(choosePattern))
                            .commit();

                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_LOCK_PASS,"1");
//                    Intent intent = new Intent(this, LockActivity.class);
//                    startActivity(intent);
                    finish();
                }

                break;

            default:
                break;
        }

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
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        Log.d(TAG, "onPatternDetected");

        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
            Toast.makeText(this,
                    R.string.lockpattern_recording_incorrect_too_short,
                    Toast.LENGTH_LONG).show();
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            return;
        }

        if (choosePattern == null) {
            choosePattern = new ArrayList<LockPatternView.Cell>(pattern);
            //           Log.d(TAG, "choosePattern = "+choosePattern.toString());
//            Log.d(TAG, "choosePattern.size() = "+choosePattern.size());
            Log.d(TAG, "choosePattern = "+ Arrays.toString(choosePattern.toArray()));

            step = STEP_2;
            updateView();
            return;
        }
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]

        Log.d(TAG, "choosePattern = "+Arrays.toString(choosePattern.toArray()));
        Log.d(TAG, "pattern = "+Arrays.toString(pattern.toArray()));

        if (choosePattern.equals(pattern)) {
//            Log.d(TAG, "pattern = "+pattern.toString());
//            Log.d(TAG, "pattern.size() = "+pattern.size());
            Log.d(TAG, "pattern = "+Arrays.toString(pattern.toArray()));

            confirm = true;
        } else {
            confirm = false;
        }

        step = STEP_4;
        updateView();

    }

}
