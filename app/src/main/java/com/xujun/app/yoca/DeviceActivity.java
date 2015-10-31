package com.xujun.app.yoca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;
import com.xujun.model.InfoGResp;
import com.xujun.model.InfoResp;
import com.xujun.model.ParamInfo;
import com.xujun.model.ParamList;
import com.xujun.sqlite.ConfigEntity;
import com.xujun.util.JsonUtil;
import com.xujun.util.StringUtil;
import com.xujun.widget.SelectPopupWindow;
import com.xujun.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 7/17/15.
 */
public class DeviceActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = "DeviceActivity";

    private ToggleButton mWeightModelTB;
    private ToggleButton            mShowUnitTB;

    private String      mWeightModel;
    private String      mShowUnit;

    private List<ParamInfo>         items=new ArrayList<ParamInfo>();

    private SelectPopupWindow       mSelectPopupWindow;

    private ItemAdapter             mAdapter;

    private int                     selectDataType;

    private AdapterView.OnItemClickListener  onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ParamInfo paramInfo=items.get(i);
            if (paramInfo!=null){
                if (selectDataType==AppConfig.REQUEST_SHOW_MODE){
                    appContext.setProperty(AppConfig.DEVICE_SET_SHOW_MODEL,paramInfo.getValue());
                    appContext.setProperty(AppConfig.DEVICE_SET_SHOW_MODEL_TITLE,paramInfo.getTitle());
                    ((TextView)findViewById(R.id.tvShowModel)).setText(paramInfo.getTitle());
                }else{
                    appContext.setProperty(AppConfig.DEVICE_SET_LED_LEVEL,paramInfo.getValue());
                    appContext.setProperty(AppConfig.DEVICE_SET_LED_LEVEL_TITLE,paramInfo.getTitle());
                    ((TextView)findViewById(R.id.tvLedValue)).setText(paramInfo.getTitle());
                }
            }
            mSelectPopupWindow.dismiss();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mAdapter=new ItemAdapter();

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

        if (StringUtil.isEmpty(appContext.getProperty(AppConfig.DEVICE_SET_SHOW_MODEL_TITLE))){
            ((TextView)findViewById(R.id.tvShowModel)).setText("普通模式");
        }else{
            ((TextView)findViewById(R.id.tvShowModel)).setText(appContext.getProperty(AppConfig.DEVICE_SET_SHOW_MODEL_TITLE));
        }

        if (StringUtil.isEmpty(appContext.getProperty(AppConfig.DEVICE_SET_LED_LEVEL_TITLE))) {
            ((TextView) findViewById(R.id.tvLedValue)).setText("1级");
        }else{
            ((TextView) findViewById(R.id.tvLedValue)).setText(appContext.getProperty(AppConfig.DEVICE_SET_LED_LEVEL_TITLE));
        }

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
                showModeSelectPopupWindow(AppConfig.REQUEST_SHOW_MODE,view);
                break;
            }
            case R.id.llLedLevel: {
                showModeSelectPopupWindow(AppConfig.REQUEST_LED_LEVEL,view);
                break;
            }
        }
    }


    private void showModeSelectPopupWindow(int type,View view){
        selectDataType=type;
        mSelectPopupWindow=new SelectPopupWindow(mContext);
        mSelectPopupWindow.showAsDropDown(view);
        mSelectPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        mSelectPopupWindow.getListView().setAdapter(mAdapter);
        mSelectPopupWindow.getListView().setOnItemClickListener(onItemClickListener);
        if (type==AppConfig.REQUEST_SHOW_MODE) {
            mSelectPopupWindow.getTitleTextView().setText("请选择显示模式");
            readData(R.raw.show_mode);
        }else if(type==AppConfig.REQUEST_LED_LEVEL){
            mSelectPopupWindow.getTitleTextView().setText("请选择LED亮度");
            readData(R.raw.led_light);
        }
    }

    private void readData(int  res){
        try{
            InputStream  is=getResources().openRawResource(res);
            byte[]  buffer=new byte[is.available()];
            is.read(buffer);
            String json=new String(buffer,"utf-8");
            ParamList list=(ParamList) JsonUtil.ObjFromJson(json, ParamList.class);
            if (list.getRoot()!=null){
                items.clear();
                items.addAll(list.getRoot());
                mAdapter.notifyDataSetChanged();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private class ItemView{
        public TextView     title;
    }

    class ItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ItemView    holder;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.select_dialog_item,null);

                holder=new ItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvTitle);
                convertView.setTag(holder);
            }else {
                holder=(ItemView)convertView.getTag();
            }
            ParamInfo entity=items.get(position);
            if (entity!=null){
                holder.title.setText(entity.getTitle());
            }
            return convertView;
        }
    }

}
