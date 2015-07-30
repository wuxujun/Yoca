package com.xujun.app.yoca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;
import com.xujun.util.StringUtil;
import com.xujun.widget.ToggleButton;

/**
 * Created by xujunwu on 7/17/15.
 */
public class DeviceActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = "SettingActivity";

    private ToggleButton mWeightModelTB;
    private ToggleButton            mShowUnitTB;

    private String      mWeightModel;
    private String      mShowUnit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mWeightModel=AppConfig.getAppConfig(mContext).get(AppConfig.DEVICE_SET_WEIGHT_MODEL);
        mWeightModelTB=(ToggleButton)findViewById(R.id.tbWeightModel);
        mWeightModelTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    AppConfig.getAppConfig(mContext).set(AppConfig.DEVICE_SET_WEIGHT_MODEL,"1");
                    ((TextView)findViewById(R.id.tvWeightModel)).setText(getText(R.string.weight_model_found));
                }else{
                    AppConfig.getAppConfig(mContext).set(AppConfig.DEVICE_SET_WEIGHT_MODEL,"0");
                    ((TextView)findViewById(R.id.tvWeightModel)).setText(getText(R.string.weight_model_memory));
                }
            }
        });

        ((TextView)findViewById(R.id.tvWeightModel)).setText(getText(R.string.weight_model_memory));
        if (!StringUtil.isEmpty(mWeightModel)&&mWeightModel.equals("1")){
            mWeightModelTB.setToggleOn();
            ((TextView)findViewById(R.id.tvWeightModel)).setText(getText(R.string.weight_model_found));
        }
        mShowUnit=AppConfig.getAppConfig(mContext).get(AppConfig.DEVICE_SET_SHOW_UNIT);
        mShowUnitTB=(ToggleButton)findViewById(R.id.tbShowUnit);
        mShowUnitTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    AppConfig.getAppConfig(mContext).set(AppConfig.DEVICE_SET_SHOW_UNIT, "2");
                    ((TextView)findViewById(R.id.tvShowUnit)).setText(getText(R.string.unit_lb));
                } else {
                    AppConfig.getAppConfig(mContext).set(AppConfig.DEVICE_SET_SHOW_UNIT, "1");
                    ((TextView)findViewById(R.id.tvShowUnit)).setText(getText(R.string.unit_kg));
                }
            }
        });
        ((TextView)findViewById(R.id.tvShowUnit)).setText(getText(R.string.unit_kg));
        if (!StringUtil.isEmpty(mShowUnit)&&mShowUnit.equals("2")){
            mShowUnitTB.setToggleOn();
            ((TextView)findViewById(R.id.tvShowUnit)).setText(getText(R.string.unit_lb));
        }


        ((TextView)findViewById(R.id.tvShowModel)).setText("普通模式");

        ((TextView)findViewById(R.id.tvLedValue)).setText("1级");


        findViewById(R.id.llShowModel).setOnClickListener(this);
        findViewById(R.id.llLedLevel).setOnClickListener(this);

        mHeadTitle.setText(getResources().getString(R.string.setting_device));
        mHeadButton.setVisibility(View.INVISIBLE);
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ibHeadBack:{
                finish();
                break;
            }
            case R.id.llShowModel:{
//                Intent intent=new Intent(SettingActivity.this,WarnActivity.class);
//                startActivity(intent);
                break;
            }
            case R.id.llAbout: {
//                Intent intent=new Intent(SettingActivity.this,AboutActivity.class);
//                startActivity(intent);
                break;
            }
        }
    }

}
