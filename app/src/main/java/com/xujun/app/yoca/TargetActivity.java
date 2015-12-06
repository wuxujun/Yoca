package com.xujun.app.yoca;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.formedittextvalidator.DateValidator;
import com.andreabaccega.widget.FormEditText;
import com.xujun.app.yoca.fragment.AccountFragment;
import com.xujun.model.BaseResp;
import com.xujun.progressbutton.CircularProgressButton;
import com.j256.ormlite.dao.Dao;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.StringUtil;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by xujunwu on 14/12/28.
 */
public class TargetActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "TargetActivity";


    private static final int DATE_DIALOG_ID=1;
    private static final int SHOW_DATEPICK=0;
    private int mYear;
    private int mMonth;
    private int mDay;


    private Calendar c = null;

    private int          targetType=0;
    private FormEditText timeET;
    private SeekBar      targetSB;
    private CircularProgressButton   saveBtn;



    private InputMethodManager      imm;

    private AccountEntity localAccountEntity;


    public AccountEntity getAccountEntity(){
        return localAccountEntity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_target);

        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        mHeadTitle.setText(getResources().getString(R.string.target_setting));
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(this);
        mHeadButton.setVisibility(View.INVISIBLE);

        initView();
    }

    public void initView() {
        findViewById(R.id.btnTargetType).setOnClickListener(this);
        findViewById(R.id.btnTargetType2).setOnClickListener(this);
        findViewById(R.id.btnTargetType3).setOnClickListener(this);
        saveBtn=(CircularProgressButton)findViewById(R.id.btnSave);
        saveBtn.setIndeterminateProgressMode(true);
        saveBtn.setOnClickListener(this);

        targetSB=(SeekBar)findViewById(R.id.seekBar);
        targetSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvTargetValue)).setText(""+i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        timeET=(FormEditText)findViewById(R.id.etTargetTime);
        timeET.addValidator(new DateValidator(getResources().getString(R.string.account_Birthday_Hit), "yyyyMMdd"));
        timeET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onCreateDateDialog(timeET).show();
//                    setDateTime();
//                    Message msg=new Message();
//                    msg.what=SHOW_DATEPICK;
//                    mHandler.sendMessage(msg);
                }
                return false;
            }
        });

        timeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView) findViewById(R.id.tvTargetTime)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llTargetTime).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                if (hasFocus) {
                    // Open keyboard

//                    String val=timeET.getText().toString();
//                    if (val!=null&&val.length()==8){
//                        resetTextView();
                    imm.hideSoftInputFromWindow(timeET.getWindowToken(), 0);
//                    }else {
//                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(timeET, InputMethodManager.SHOW_FORCED);
//                    }
                } else {
                    // Close keyboard
                    imm.hideSoftInputFromWindow(timeET.getWindowToken(), 0);
                }
            }
        });

        timeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.e(TAG, charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, charSequence.toString() + "  " + i + "  " + i2 + "  " + i3);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = editable.toString();
                if (val != null && val.length() == 8) {
                    resetTextView();
                    imm.hideSoftInputFromWindow(timeET.getWindowToken(), 0);
                }
            }
        });



    }


    @Override
    public void onResume() {
        super.onResume();
        if (localAccountEntity!=null){
            initTargetData();
        }
    }


    public void initTargetData(){
        if (localAccountEntity.getTargetType()!=null) {
            if (localAccountEntity.getTargetType() == 1) {
                setTargetType(1);
                if (!StringUtil.isEmpty(localAccountEntity.getTargetWeight())) {
                    ((TextView)findViewById(R.id.tvTargetValue)).setText(StringUtil.doubleToStringOne(Double.parseDouble(localAccountEntity.getTargetWeight())));
                    ((SeekBar)findViewById(R.id.seekBar)).setProgress(Integer.parseInt(StringUtil.doubleToString(Double.parseDouble(localAccountEntity.getTargetWeight()))));
                }
            } else if (localAccountEntity.getTargetType() ==2) {
                setTargetType(2);
                if (!StringUtil.isEmpty(localAccountEntity.getTargetFat())) {
                    ((TextView)findViewById(R.id.tvTargetValue)).setText(StringUtil.doubleToStringOne(Double.parseDouble(localAccountEntity.getTargetFat())));
                    ((SeekBar)findViewById(R.id.seekBar)).setProgress(Integer.parseInt(StringUtil.doubleToString(Double.parseDouble(localAccountEntity.getTargetFat()))));
                }
            } else {
                setTargetType(3);
                if (!StringUtil.isEmpty(localAccountEntity.getTargetWeight())) {
                    ((TextView)findViewById(R.id.tvTargetValue)).setText(StringUtil.doubleToStringOne(Double.parseDouble(localAccountEntity.getTargetWeight())));
                    ((SeekBar)findViewById(R.id.seekBar)).setProgress(Integer.parseInt(StringUtil.doubleToString(Double.parseDouble(localAccountEntity.getTargetWeight()))));
                }
            }
        }
        if (!StringUtil.isEmpty(localAccountEntity.getDoneTime())){
            ((FormEditText)findViewById(R.id.etTargetTime)).setText(localAccountEntity.getDoneTime());
        }

        //女
        if(localAccountEntity.getSex()==0){
            ((TextView)findViewById(R.id.tvTopWeight)).setText(StringUtil.doubleToStringOne((localAccountEntity.getHeight()-100)));
        }else{
           //男
            ((TextView)findViewById(R.id.tvTopWeight)).setText(StringUtil.doubleToStringOne((localAccountEntity.getHeight() - 105)));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibHeadBack:{
                finish();
                break;
            }

            case R.id.btnTargetType:{
                setTargetType(1);
                break;
            }
            case R.id.btnTargetType2:{
                setTargetType(2);
                break;
            }
            case R.id.btnTargetType3:{
                setTargetType(3);
                break;
            }
            case R.id.btnSave:{
                saveTargetInfo();
                break;
            }

        }
    }

    public void saveTargetInfo(){
        if (!timeET.testValidity()){
            Log.e(TAG,"time  is not date.");
            return;
        }
        if (!appContext.isDate(timeET.getText().toString())){
            Log.e(TAG,"time  is not date format.");
            Toast.makeText(mContext,getResources().getString(R.string.error_Date),Toast.LENGTH_LONG).show();
            return;
        }

        if (localAccountEntity!=null){
            Log.e(TAG,"account save.");
            saveBtn.setProgress(50);
            localAccountEntity.setTargetType(targetType);
            if (targetType==2){
                localAccountEntity.setTargetFat("" + targetSB.getProgress());
                if (!StringUtil.isEmpty(localAccountEntity.getTargetWeight())) {
                    localAccountEntity.setTargetWeight(""+targetSB.getProgress());
                }
            }else {
                localAccountEntity.setTargetWeight("" + targetSB.getProgress());
                localAccountEntity.setTargetFat("2.0");
            }
            Log.e(TAG,"---> "+localAccountEntity.getTargetWeight()+"  "+targetSB.getProgress());
            localAccountEntity.setDoneTime(timeET.getText().toString());
            saveAccountEntity(localAccountEntity);
        }else{
            Toast.makeText(mContext,"请选择成员后,再设定目标！",Toast.LENGTH_LONG).show();
        }

    }

    private void saveAccountEntity(AccountEntity entity){
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
        saveBtn.setProgress(100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }

    private void resetTextView(){
        ((TextView)findViewById(R.id.tvTargetTime)).setTextColor(getResources().getColor(R.color.btn_color));
        findViewById(R.id.llTargetTime).setBackgroundColor(getResources().getColor(R.color.btn_color));
    }

    private void setTargetType(int type){
        switch (type){
            case 1:{
                ((Button)findViewById(R.id.btnTargetType)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((Button)findViewById(R.id.btnTargetType2)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnTargetType3)).setTextColor(getResources().getColor(R.color.btn_color));

                findViewById(R.id.llTargetType1).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llTargetType2).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llTargetType3).setBackgroundColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvTargetTitle)).setText("希望体重");
                ((TextView)findViewById(R.id.tvTargetUnit)).setText("Kg");
                break;
            }
            case 2:{
                ((Button)findViewById(R.id.btnTargetType)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnTargetType2)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((Button)findViewById(R.id.btnTargetType3)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llTargetType1).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llTargetType2).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llTargetType3).setBackgroundColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvTargetTitle)).setText("希望体脂");
                ((TextView)findViewById(R.id.tvTargetUnit)).setText("%");
                break;
            }
            case 3:{
                ((Button)findViewById(R.id.btnTargetType)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnTargetType2)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)findViewById(R.id.btnTargetType3)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llTargetType1).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llTargetType2).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llTargetType3).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvTargetTitle)).setText("希望体重");
                ((TextView)findViewById(R.id.tvTargetUnit)).setText("Kg");
                break;
            }
        }

    }

    public Dialog onCreateDateDialog(final FormEditText btn) {
        Dialog dialog = null;
        c = Calendar.getInstance();
        dialog = new DatePickerDialog(TargetActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = sdf.format(c.getTime());
                btn.setText(formattedDate);
            }
        }, c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH) // 传入天数
        );

        return dialog;
    }


    Handler mHandler=new Handler(){
      @Override
    public void handleMessage(Message msg){
          switch (msg.what){
              case SHOW_DATEPICK:{
                   showDialog(DATE_DIALOG_ID);
                  break;
              }
          }
      }
    };

    public Dialog onCreateDialog(int id)
    {
        switch (id){
            case DATE_DIALOG_ID:{
                return new DatePickerDialog(this,mDateSetListener,mYear,mMonth,mDay);
            }
        }
        return null;
    }

    public void onPrepareDialog(int id,Dialog dialog){
        switch (id){
            case DATE_DIALOG_ID:{
                ((DatePickerDialog)dialog).updateDate(mYear,mMonth,mDay);
                break;
            }
        }
    }

    private void setDateTime(){
        final Calendar c=Calendar.getInstance();
        mYear=c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);
        String val=""+mYear;
        if (mMonth>9){
            val+=""+mMonth;
        }else{
            val+="0"+mMonth;
        }
        if (mDay>9){
            val+=""+mDay;
        }else {
            val+="0"+mDay;
        }
        timeET.setText(val);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            mYear=i;
            mMonth=i2+1;
            mDay=i3;
            String val=""+mYear;
            if (mMonth>9){
                val+=""+mMonth;
            }else{
                val+="0"+mMonth;
            }
            if (mDay>9){
                val+=""+mDay;
            }else {
                val+="0"+mDay;
            }
            timeET.setText(val);
        }
    };
}

