package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.widget.FormEditText;
import com.j256.ormlite.dao.Dao;
import com.xujun.app.yoca.fragment.WarnFragment;
import com.xujun.app.yoca.widget.PickerView;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WarnEntity;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 15/4/6.
 */
public class WarnSetActivity extends BaseActivity implements View.OnClickListener{

    private FormEditText tagET;
    private PickerView minutesPV;
    private PickerView     hoursPV;
    private boolean        week1=false;
    private boolean        week2=false;
    private boolean        week3=false;
    private boolean        week4=false;
    private boolean        week5=false;
    private boolean        week6=false;
    private boolean        week7=false;


    private boolean        everyDay=false;
    private boolean        workdays=false;
    private boolean        weekend=false;

    private String            mMinutes="30";
    private String            mHours="12";

    List<String> hoursData = new ArrayList<String>();
    List<String> minutesData = new ArrayList<String>();


    private WarnEntity localWarnEntity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting_weight);

        mHeadTitle.setText(getResources().getString(R.string.setting_warn));

        mHeadButton.setText(getResources().getString(R.string.btn_Save));
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(this);
        mHeadButton.setOnClickListener(this);

        for (int i = 0; i < 24; i++)
        {
            hoursData.add(i<10? "0" + i:""+i);
        }
        for (int i = 0; i < 60; i++)
        {
            minutesData.add(i < 10 ? "0" + i : "" + i);
        }

        findViewById(R.id.btnWeekMon).setOnClickListener(this);
        findViewById(R.id.btnWeekTue).setOnClickListener(this);
        findViewById(R.id.btnWeekWed).setOnClickListener(this);
        findViewById(R.id.btnWeekThu).setOnClickListener(this);
        findViewById(R.id.btnWeekFri).setOnClickListener(this);
        findViewById(R.id.btnWeekSat).setOnClickListener(this);
        findViewById(R.id.btnWeekSun).setOnClickListener(this);
        findViewById(R.id.btnWeek).setOnClickListener(this);
        findViewById(R.id.btnWeekDays).setOnClickListener(this);
        findViewById(R.id.btnWeekend).setOnClickListener(this);

        minutesPV=(PickerView)findViewById(R.id.pvMinutes);
        minutesPV.setData(minutesData);
        minutesPV.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mMinutes=text;
            }
        });


        hoursPV=(PickerView)findViewById(R.id.pvHours);
        hoursPV.setData(hoursData);
        hoursPV.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mHours=text;
            }
        });

        tagET=(FormEditText)findViewById(R.id.etWeekTag);
//        tagET.addValidator(new EmptyValidator(getResources().getString(R.string.setting_warn_tag_hit)));
    }

    @Override
    public void onResume() {
        super.onResume();
        initWarnEntityData();
    }

    public void loadData(WarnEntity warnEntity){
        if (warnEntity.getType()>0){
            localWarnEntity=warnEntity;
        }
    }

    private void initWarnEntityData(){
        if (localWarnEntity!=null){
            hoursPV.setSelected(localWarnEntity.getHours());
            minutesPV.setSelected(localWarnEntity.getMinutes());
            tagET.setText(localWarnEntity.getNote());
            if (localWarnEntity.getRepeats()!=null) {
                if (localWarnEntity.getRepeats() == 0) {
                    everyDay = true;
                } else if (localWarnEntity.getRepeats() == 1) {
                    workdays = true;
                } else if (localWarnEntity.getRepeats() == 2) {
                    weekend = true;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibHeadBack:{
                finish();
                break;
            }
            case R.id.btnHeadEdit:{
                onMenuSave();
                break;
            }
            case R.id.btnWeekMon:{
                week1=!week1;
                if (week1) {
                    ((Button)findViewById(R.id.btnWeekMon)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekMon)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekTue:{
                week2=!week2;
                if (week2) {
                    ((Button)findViewById(R.id.btnWeekTue)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekTue)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekWed:{
                week3=!week3;
                if (week3) {
                    ((Button)findViewById(R.id.btnWeekWed)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekWed)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekThu:{
                week4=!week4;
                if (week4) {
                    ((Button)findViewById(R.id.btnWeekThu)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekThu)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekFri:{
                week5=!week5;
                if (week5) {
                    ((Button)findViewById(R.id.btnWeekFri)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekFri)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekSat:{
                week6=!week6;
                if (week6) {
                    ((Button)findViewById(R.id.btnWeekSat)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekSat)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeekSun:{
                week7=!week7;
                if (week7) {
                    ((Button)findViewById(R.id.btnWeekSun)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                }else {
                    ((Button)findViewById(R.id.btnWeekSun)).setTextColor(getResources().getColor(R.color.btn_color));
                }
                break;
            }
            case R.id.btnWeek:{
                ((Button)findViewById(R.id.btnWeek)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekDays)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekend)).setTextColor(getResources().getColor(R.color.btn_color));
                everyDay=!everyDay;
                setEveryDay();
                break;
            }
            case R.id.btnWeekDays:{
                ((Button)findViewById(R.id.btnWeek)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekDays)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekend)).setTextColor(getResources().getColor(R.color.btn_color));
                workdays=!workdays;
                setWeekDays();
                break;
            }
            case R.id.btnWeekend:{
                ((Button)findViewById(R.id.btnWeek)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekDays)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnWeekend)).setTextColor(getResources().getColor(R.color.btn_color));
                weekend=!weekend;
                setWeekend();
                break;
            }
        }
    }

    private void setEveryDay(){
        week1=week2=week3=week4=week5=week6=week7=everyDay;
        ((Button)findViewById(R.id.btnWeekMon)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekTue)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekThu)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekWed)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekFri)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekSat)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekSun)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));


        ((Button)findViewById(R.id.btnWeek)).setTextColor(getResources().getColor(everyDay?R.color.btn_color_selected:R.color.btn_color));
    }

    private void setWeekDays(){
        week1=week2=week3=week4=week5=workdays;
        ((Button)findViewById(R.id.btnWeekMon)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekTue)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekThu)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekWed)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekFri)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
        week6=week7=false;
        ((Button)findViewById(R.id.btnWeekSat)).setTextColor(getResources().getColor(R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekSun)).setTextColor(getResources().getColor(R.color.btn_color));


        ((Button)findViewById(R.id.btnWeekDays)).setTextColor(getResources().getColor(workdays?R.color.btn_color_selected:R.color.btn_color));
    }

    private void setWeekend(){
        week1=week2=week3=week4=week5=false;
        ((Button)findViewById(R.id.btnWeekMon)).setTextColor(getResources().getColor(R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekTue)).setTextColor(getResources().getColor(R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekThu)).setTextColor(getResources().getColor(R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekWed)).setTextColor(getResources().getColor(R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekFri)).setTextColor(getResources().getColor(R.color.btn_color));
        week6=week7=weekend;
        ((Button)findViewById(R.id.btnWeekSat)).setTextColor(getResources().getColor(weekend?R.color.btn_color_selected:R.color.btn_color));
        ((Button)findViewById(R.id.btnWeekSun)).setTextColor(getResources().getColor(weekend?R.color.btn_color_selected:R.color.btn_color));

        ((Button)findViewById(R.id.btnWeekend)).setTextColor(getResources().getColor(weekend?R.color.btn_color_selected:R.color.btn_color));
    }

    public void onMenuCancel(){
    }

    public void onMenuSave(){
//        if (!tagET.testValidity()){
//            return;
//        }
        WarnEntity entity=new WarnEntity();
        if (localWarnEntity!=null){
            entity.setWId(localWarnEntity.getWId());
        }else{
            entity.setWId(System.currentTimeMillis());
        }
        entity.setType(1);
        entity.setStatus(0);
        entity.setValue(mHours + ":" + mMinutes);
        if (everyDay){
            entity.setRepeats(0);
        }else if (workdays){
            entity.setRepeats(1);
        }else if (weekend){
            entity.setRepeats(2);
        }
        entity.setWeek_mon(week1 ? 1 : 0);
        entity.setWeek_tue(week2 ? 1 : 0);
        entity.setWeek_wed(week3 ? 1 : 0);
        entity.setWeek_thu(week4 ? 1 : 0);
        entity.setWeek_fri(week5 ? 1 : 0);
        entity.setWeek_sat(week6 ? 1 : 0);
        entity.setWeek_sun(week7 ? 1 : 0);
        entity.setHours(Integer.parseInt(mHours));
        entity.setMinutes(Integer.parseInt(mMinutes));
        if (StringUtil.isEmpty(tagET.getText().toString())){
            entity.setNote("");
        }else {
            entity.setNote(tagET.getText().toString());
        }
        insertWarnEntity(entity);
    }


    private void insertWarnEntity(WarnEntity entity){
        try{
            Dao<WarnEntity,Integer> dao=getDatabaseHelper().getWarnEntityDao();
            dao.createOrUpdate(entity);
        }catch (SQLException e){
            e.printStackTrace();
        }
        finish();
    }

}
