package com.xujun.app.yoca.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.andreabaccega.formedittextvalidator.DateValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.NumericValidator;
import com.andreabaccega.widget.FormEditText;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.MenuFragment;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xujunwu on 14/12/17.
 */
public class AccountFragment extends SherlockFragment implements View.OnClickListener{

    public static final String TAG = "AccountFragment";

    private  View   mContentView;

    
    private FormEditText nickET;
    private FormEditText birthdayET;
    private FormEditText hasHanET;
    private Button       sexWoram;
    private Button      sexMale;

    private boolean      bSex=false;

    private Uri          origUri;
    private Uri          cropUri;

    private boolean         isKeyboardVisible;
    private int             dataType= AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_NORMAL;

    private Context         mContext;
    private AppContext appContext;

    private DatabaseHelper databaseHelper;

    private AccountEntity       localAccountEntity;

    public AccountEntity getAccountEntity(){
        return localAccountEntity;
    }

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }
    public void setDataType(int type){
        dataType=type;
    }
    public int   getDataType(){
        return dataType;
    }


    private Calendar c = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        getSherlockActivity().getActionBar().setTitle(getResources().getString(R.string.account_Edit));
        if (getDataType()==AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER){
            getSherlockActivity().getActionBar().setTitle(getResources().getString(R.string.account_Visitor));
        }
        getSherlockActivity().getActionBar().setHomeAsUpIndicator(R.drawable.back);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"AccountFragment onCreateView()...");
        mContentView=inflater.inflate(R.layout.layout_account,null);

        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mContentView.getWindowVisibleDisplayFrame(r);
                int heightDiff = mContentView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100)
                    isKeyboardVisible = true;
                else
                    isKeyboardVisible = false;
            }
        });
        mContentView.findViewById(R.id.ibAvatar).setOnClickListener(this);
        nickET=(FormEditText)mContentView.findViewById(R.id.etAccountNick);
        nickET.addValidator(new EmptyValidator(getResources().getString(R.string.account_Nick_Hit)));
        nickET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)mContentView.findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)mContentView.findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)mContentView.findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                mContentView.findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));

                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(hasHanET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                }
            }
        });
        birthdayET=(FormEditText)mContentView.findViewById(R.id.etAccountBirthday);
        birthdayET.addValidator(new DateValidator(getResources().getString(R.string.account_Birthday_Hit),"yyyyMMdd"));
        birthdayET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    onCreateDateDialog(birthdayET).show();
                }
                return false;
            }
        });
        birthdayET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)mContentView.findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)mContentView.findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)mContentView.findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                mContentView.findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
                    Log.e(TAG,"1");
                    String val=birthdayET.getText().toString();
                    if (val!=null&&val.length()==8){
                        resetTextViewColor();
                        ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
                    }else {
                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(birthdayET, InputMethodManager.SHOW_FORCED);
                    }
                } else {
                    Log.e(TAG,"2");
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
                }
            }
        });
        birthdayET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.e(TAG,charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG,charSequence.toString()+"  "+i+"  "+i2+"  "+i3);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG,editable.toString());
                String val=editable.toString();
                if (val!=null&&val.length()==8){
                    resetTextViewColor();
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
                }
            }
        });
        hasHanET=(FormEditText)mContentView.findViewById(R.id.etAccountHasHan);
        hasHanET.addValidator(new NumericValidator(getResources().getString(R.string.account_Heshan_Hit)));
        hasHanET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)mContentView.findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)mContentView.findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)mContentView.findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                if (hasFocus) {
                    // Open keyboard
                    Log.e(TAG,"3");
                    String val=hasHanET.getText().toString();
                    if (val!=null&&val.length()==3){
                        resetTextViewColor();
                        ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                    }else {
                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(hasHanET, InputMethodManager.SHOW_FORCED);
                    }
                } else {
                    // Close keyboard
                    Log.e(TAG,"3");
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                }
            }
        });
        hasHanET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val=editable.toString();
                if (val!=null&&val.length()==3){
                    resetTextViewColor();
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                }
            }
        });

        sexWoram=(Button)mContentView.findViewById(R.id.btnSexWoram);
        sexWoram.setOnClickListener(this);
        sexMale=(Button)mContentView.findViewById(R.id.btnSexMale);
        sexMale.setOnClickListener(this);

        mContentView.findViewById(R.id.ibAvatar).setOnClickListener(this);
        mContentView.findViewById(R.id.btnStart).setOnClickListener(this);

        if (localAccountEntity!=null) {
            nickET.setText(localAccountEntity.getUserNick());
            birthdayET.setText(localAccountEntity.getBirthday());
            Log.e(TAG,""+localAccountEntity.getHeight());
            if (localAccountEntity.getSex()==0){
                bSex=false;
            }else{
                bSex=true;
            }
            setSexButtonState();
             hasHanET.setText(String.valueOf(localAccountEntity.getHeight()));
            ((Button)mContentView.findViewById(R.id.btnStart)).setText(getResources().getString(R.string.btn_Save));
        }

        return mContentView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.d(TAG, "onResume");
    }

    public void loadData(AccountEntity accountEntity){
        dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
        Log.e(TAG,"loadData accountEngity....");

        if (accountEntity!=null&&accountEntity.getType()!=2){
            if (mContentView!=null) {
                nickET.setText(accountEntity.getUserNick());
                birthdayET.setText(accountEntity.getBirthday());
                hasHanET.setText(accountEntity.getHeight());

                ((Button)mContentView.findViewById(R.id.btnStart)).setText(getResources().getString(R.string.btn_Save));

            }else{
                localAccountEntity=accountEntity;
            }
        }else{
            dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
        }
    }

    private void resetTextViewColor(){
        ((TextView)mContentView.findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
        ((TextView)mContentView.findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
        ((TextView)mContentView.findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
        mContentView.findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
        mContentView.findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
        mContentView.findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibAvatar:{
//                SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentByTag("dialog");
//                if (fragment!=null){
//                    getFragmentManager().beginTransaction().remove(fragment);
//                }
//                getFragmentManager().beginTransaction().addToBackStack(null);
//
//                DialogFragment newFragment=new ADialogFragment();
//                newFragment.show(getFragmentManager().beginTransaction(),"dialog");
                break;
            }
            case R.id.btnStart:{
                if (!birthdayET.testValidity()){
                    return;
                }
                if (!hasHanET.testValidity()){
                    return;
                }
                if (!isDate(birthdayET.getText().toString())){
                    Toast.makeText(mContext,getResources().getString(R.string.account_Birthday_error),Toast.LENGTH_LONG);
                    return;
                }
                if (localAccountEntity!=null){
                    localAccountEntity.setUserNick(nickET.getText().toString());
                    localAccountEntity.setBirthday(birthdayET.getText().toString());
                    localAccountEntity.setHeshan(Integer.parseInt(hasHanET.getText().toString()));
                    localAccountEntity.setSex(bSex?1:0);
                    InsertAccountEntity(localAccountEntity);
                    SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentById(R.id.menu_frame);
                    if(fragment instanceof MenuFragment) {
                        ((MenuFragment)fragment).onResume();
                    }
                    if (dataType==AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER){
                        getFragmentManager().beginTransaction().replace(R.id.content_frame,new MemberMFragment()).commit();
                    }
                }else {
                    AccountEntity entity = new AccountEntity();
                    entity.setUserNick(nickET.getText().toString());
                    entity.setType(queryAccountType());
                    entity.setBirthday(birthdayET.getText().toString());
                    entity.setHeshan(Integer.parseInt(hasHanET.getText().toString()));
                    entity.setSex(bSex ? 1 : 0);
                    entity.setStatus(0);
                    entity.setAge(getAge(birthdayET.getText().toString()));
                    Log.e(TAG, "++--- " + entity.getAge());
                    if (getDataType()!=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER) {
                        InsertAccountEntity(entity);
                    }
                    if (getDataType()==AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER){
                        SherlockFragment sherlockFragment=new MemberMFragment();
                        getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();
                    }else {
                        SherlockFragment fragment = (SherlockFragment) getFragmentManager().findFragmentById(R.id.content_frame);
                        if (fragment instanceof ContentFragment) {
                            if (getDataType() == AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER) {
                                entity.setType(3);
                                ((ContentFragment) fragment).loadVisitor(entity);
                            }else {
                                entity.setType(1);
                                ((ContentFragment) fragment).loadData(entity);
                            }
                            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
                        } else {
                            SherlockFragment sherlockFragment = new ContentFragment();
                            if (getDataType() == AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER) {
                                entity.setType(3);
                                ((ContentFragment) sherlockFragment).loadVisitor(entity);
                            }else{
                                entity.setType(1);
                                ((ContentFragment)sherlockFragment).loadData(entity);
                            }
                            getFragmentManager().beginTransaction().replace(R.id.content_frame, sherlockFragment).commit();
                        }
                    }
                }


                break;
            }
            case R.id.btnSexMale:{
                bSex=true;
                setSexButtonState();
                break;
            }
            case R.id.btnSexWoram:{
                bSex=false;
                setSexButtonState();
                break;
            }
        }
    }

    private void setSexButtonState(){
        if (bSex){
            sexMale.setTextColor(getResources().getColor(R.color.btn_color_selected));
            sexWoram.setTextColor(getResources().getColor(R.color.btn_color));
            mContentView.findViewById(R.id.llSexMale).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
            mContentView.findViewById(R.id.llSexWoram).setBackgroundColor(getResources().getColor(R.color.btn_color));

        }else{
            sexWoram.setTextColor(getResources().getColor(R.color.btn_color_selected));
            sexMale.setTextColor(getResources().getColor(R.color.btn_color));
            mContentView.findViewById(R.id.llSexWoram).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
            mContentView.findViewById(R.id.llSexMale).setBackgroundColor(getResources().getColor(R.color.btn_color));

        }
    }

    private int queryAccountType(){
        try{
            Dao<AccountEntity,Integer> dao1=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder1=dao1.queryBuilder();
            queryBuilder1.where().eq("type",1);
            queryBuilder1.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery1=queryBuilder1.prepare();
            List<AccountEntity> accountEntityList=dao1.query(preparedQuery1);
            if (accountEntityList.size()==0){
                return 1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case AppConfig.REQUEST_TAKE_PHOTO:{
                if (resultCode==getActivity().RESULT_OK)
                {
                    Log.e(TAG,"......");
                    startPhotoZoom(origUri);
                }
                break;
            }
            case AppConfig.REQUEST_CHOOSE_PIC:{
                Log.e(TAG,",,,,,,,,,");
                break;
            }
            case AppConfig.REQUEST_CROP_PHOTO:{
                if (resultCode!=getActivity().RESULT_CANCELED){
                    if (data!=null){

                        Bundle bundle=data.getExtras();
                        if (bundle!=null){
                            Bitmap photo_tmp=bundle.getParcelable("data");

                        }
                    }
                }
                break;
            }
        }

    }

    public void startPhotoZoom(Uri uri){
        int w_h=(int)(200*appContext.scaledDensity+0.5f);
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop",true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",w_h);
        intent.putExtra("outputY",w_h);
        intent.putExtra("return-date",true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent,AppConfig.REQUEST_CHOOSE_PIC);
    }

    public void closeKeyboardIfOpen()
    {
        InputMethodManager imm;
        imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isKeyboardVisible)
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void InsertAccountEntity(AccountEntity entity){
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }

        SherlockFragment fragment=(SherlockFragment) getFragmentManager().findFragmentById(R.id.menu_frame);
        if (fragment instanceof MenuFragment){
            ((MenuFragment)fragment).refreshAccount();
        }

    }

    private boolean isDate(String birthday){
        String str=birthday.substring(0,4)+"-"+birthday.substring(4,6)+"-"+birthday.substring(6);
        String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(str);
        boolean dateType = mat.matches();
        return dateType;
    }

    private int getAge(String birthday){
        try{
            String str=birthday.substring(0,4)+"-"+birthday.substring(4,6)+"-"+birthday.substring(6);

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date date=sdf.parse(str);
            Date today=new Date();
            long day=(today.getTime()-date.getTime())/(24*60*60*1000)+1;
            return (int)Math.rint(day / 365f);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return 0;
    }


    public void selectPhoto(int type){
        if (type==0) {
            origUri=Uri.fromFile(new File(appContext.getCameraPath()+"/"+System.currentTimeMillis()+".jpg"));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            startActivityForResult(intent, AppConfig.REQUEST_TAKE_PHOTO);
        }else{
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,AppConfig.REQUEST_TAKE_PHOTO);

        }
    }

    @SuppressLint("ValidFragment")
    public  class ADialogFragment extends SherlockDialogFragment{

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Dialog);
        }

        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){
            View v=inflater.inflate(R.layout.layout_dialog_avatar,container,false);

            v.findViewById(R.id.btnCamear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPhoto(0);
                    dismiss();
                }
            });

            v.findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPhoto(1);
                    dismiss();
                }
            });
            v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            return v;
        }
    }


    public Dialog onCreateDateDialog(final FormEditText btn) {
        Dialog dialog = null;
        c = Calendar.getInstance();
        dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

}
