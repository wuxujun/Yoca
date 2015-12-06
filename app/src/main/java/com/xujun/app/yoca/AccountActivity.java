package com.xujun.app.yoca;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.formedittextvalidator.DateValidator;
import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.NumericValidator;
import com.andreabaccega.widget.FormEditText;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.widget.ToggleButton;

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
public class AccountActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = "AccountActivity";

    private InputMethodManager      imm;
    private String                  imageName;
    private File                    imageFile;

    private FormEditText nickET;
    private FormEditText birthdayET;
    private FormEditText hasHanET;
    private ToggleButton    typeTB;
    private Button       sexWoram;
    private Button      sexMale;


    private Calendar c = null;

    private boolean      bSex=false;
    private boolean      bType=false;

    private Uri          origUri;
    private Uri          cropUri;

    private boolean         isKeyboardVisible;
    private int             dataType= AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_NORMAL;

    private int             sourceType=0;
    private AccountEntity       localAccountEntity;

    public AccountEntity getAccountEntity(){
        return localAccountEntity;
    }

    public void setDataType(int type){
        dataType=type;
    }
    public int   getDataType(){
        return dataType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_account);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mHeadTitle.setText(getResources().getString(R.string.account_Edit));
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(this);
        mHeadButton.setVisibility(View.INVISIBLE);
//        mHeadButton.setOnClickListener(this);
//        mHeadButton.setText(getText(R.string.btn_Target));
        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");

        sourceType=getIntent().getIntExtra(AppConfig.PARAM_SOURCE_TYPE,0);
        dataType=getIntent().getIntExtra(AppConfig.PARAM_ACCOUNT_DATA_TYPE,AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER);
        if ((localAccountEntity!=null&&localAccountEntity.getType()==1)||sourceType==1){
            mHeadTitle.setText("个人资料");
        }
        if (localAccountEntity!=null&&localAccountEntity.getType()==0){
            mHeadTitle.setText("成员信息");
        }

        initView();
    }


    public void initView() {
        nickET=(FormEditText)findViewById(R.id.etAccountNick);
        nickET.addValidator(new EmptyValidator(getResources().getString(R.string.account_Nick_Hit)));
        nickET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));

                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(hasHanET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                }
            }
        });
        birthdayET=(FormEditText)findViewById(R.id.etAccountBirthday);
        birthdayET.addValidator(new DateValidator(getResources().getString(R.string.account_Birthday_Hit), "yyyyMMdd"));
        birthdayET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onCreateDateDialog(birthdayET).show();
                }
                return false;
            }
        });
        birthdayET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView) findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView) findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView) findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));
                if (hasFocus) {
                    // Open keyboard
//                    String val=birthdayET.getText().toString();
//                    if (val!=null&&val.length()==8){
//                        resetTextViewColor();
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }else {
//                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(birthdayET, InputMethodManager.SHOW_FORCED);
//                    }
                } else {
                    // Close keyboard
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        birthdayET.addTextChangedListener(new TextWatcher() {
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
                Log.i(TAG, editable.toString());
                String val = editable.toString();
                if (val != null && val.length() == 8) {
                    resetTextViewColor();
                    imm.hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
                }
            }
        });
        hasHanET=(FormEditText)findViewById(R.id.etAccountHasHan);
        hasHanET.addValidator(new NumericValidator(getResources().getString(R.string.account_Heshan_Hit)));
        hasHanET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                ((TextView)findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((TextView)findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
                ((TextView)findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
                findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
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
                String val = editable.toString();
                if (val != null && val.length() == 3) {
                    resetTextViewColor();
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hasHanET.getWindowToken(), 0);
                }
            }
        });

        sexWoram=(Button)findViewById(R.id.btnSexWoram);
        sexWoram.setOnClickListener(this);
        sexMale=(Button)findViewById(R.id.btnSexMale);
        sexMale.setOnClickListener(this);

        findViewById(R.id.ibAvatar).setOnClickListener(this);
        findViewById(R.id.btnStart).setOnClickListener(this);

        if (localAccountEntity!=null) {
            nickET.setText(localAccountEntity.getUserNick());
            birthdayET.setText(localAccountEntity.getBirthday());
            Log.e(TAG,""+localAccountEntity.getHeight());
            if (localAccountEntity.getAvatar()!=null){
                Log.e(TAG, "" + localAccountEntity.getAvatar());
                if (ImageUtils.isFileExist(appContext.getCameraPath()+"/crop_"+localAccountEntity.getAvatar())) {
                    ((ImageButton) findViewById(R.id.ibAvatar)).setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar()));
                }else{
                    ((ImageButton)findViewById(R.id.ibAvatar)).setBackgroundResource(R.drawable.userbig);
                }
            }
            if (localAccountEntity.getSex()!=null&&localAccountEntity.getSex()==0){
                bSex=false;
            }else{
                bSex=true;
            }
            setSexButtonState();
            if (localAccountEntity.getHeight()!=null) {
                hasHanET.setText(String.valueOf(localAccountEntity.getHeight()));
            }
            if (localAccountEntity.getType()==2){
                dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_NORMAL;
            }else{
                dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
            }
            ((Button)findViewById(R.id.btnStart)).setText(getResources().getString(R.string.btn_Save));
        }

        typeTB=(ToggleButton)findViewById(R.id.tbAccountType);
        typeTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    bType = true;
                } else {
                    bType = false;
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (sourceType==1){
            if (!appContext.getProperty(AppConfig.CONF_USER_TYPE).equals("0")){
                nickET.setText(appContext.getProperty(AppConfig.CONF_USER_NICK));
                dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
            }
        }
    }

    public void loadData(AccountEntity accountEntity){
        dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
        Log.e(TAG, "loadData accountEngity....");

        if (accountEntity!=null&&accountEntity.getType()!=2){
            localAccountEntity=accountEntity;
                nickET.setText(accountEntity.getUserNick());
                birthdayET.setText(accountEntity.getBirthday());
                hasHanET.setText(accountEntity.getHeight());
                if(accountEntity.getAccountType()==1){
                    typeTB.setToggleOn();
                }else{
                    typeTB.setToggleOff();
                }
                ((Button)findViewById(R.id.btnStart)).setText(getResources().getString(R.string.btn_Save));
        }else{
            dataType=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER;
        }
    }

    private void resetTextViewColor(){
        ((TextView)findViewById(R.id.tvAccountHasHan)).setTextColor(getResources().getColor(R.color.btn_color));
        ((TextView)findViewById(R.id.tvAccountNick)).setTextColor(getResources().getColor(R.color.btn_color));
        ((TextView)findViewById(R.id.tvAccountBirthday)).setTextColor(getResources().getColor(R.color.btn_color));
        findViewById(R.id.llAccountNick).setBackgroundColor(getResources().getColor(R.color.btn_color));
        findViewById(R.id.llAccountBirthday).setBackgroundColor(getResources().getColor(R.color.btn_color));
        findViewById(R.id.llAccountHashan).setBackgroundColor(getResources().getColor(R.color.btn_color));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibHeadBack:{
                finish();
                break;
            }
            case R.id.btnHeadEdit:{
                Intent intent=new Intent(AccountActivity.this,TargetActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account",localAccountEntity);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            case R.id.ibAvatar:{
                CharSequence[] items={"手机相册","手机拍照"};
                imageChooseItem(items);
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
                    localAccountEntity.setAccountType(bType?1:0);
                    if (imageName!=null){
                        localAccountEntity.setAvatar(imageName);
                    }
                    if (getDataType()==AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER) {
                        localAccountEntity.setIsSync(0);
                        InsertAccountEntity(localAccountEntity);
                    }else {
                        AccountEntity entity = new AccountEntity();
                        entity.setId(System.currentTimeMillis());
                        entity.setUserNick(nickET.getText().toString());
                        entity.setType(queryAccountType());
                        entity.setBirthday(birthdayET.getText().toString());
                        entity.setHeshan(Integer.parseInt(hasHanET.getText().toString()));
                        entity.setSex(bSex ? 1 : 0);
                        entity.setAccountType(bType?1:0);
                        if (imageName!=null) {
                            entity.setAvatar(imageName);
                        }
                        entity.setIsSync(0);
                        entity.setStatus(0);
                        entity.setAge(getAge(birthdayET.getText().toString()));
                        InsertAccountEntity(entity);
                    }
                    finish();
                }else {
                    // add member info
                    AccountEntity entity = new AccountEntity();
                    entity.setId(System.currentTimeMillis());
                    entity.setUserNick(nickET.getText().toString());
                    entity.setType(queryAccountType());
                    entity.setBirthday(birthdayET.getText().toString());
                    entity.setHeshan(Integer.parseInt(hasHanET.getText().toString()));
                    entity.setSex(bSex ? 1 : 0);
                    entity.setAccountType(bType?1:0);
                    if (imageName!=null) {
                        entity.setAvatar(imageName);
                    }
                    entity.setStatus(0);
                    entity.setIsSync(0);
                    entity.setAge(getAge(birthdayET.getText().toString()));
                    if (getDataType()!=AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER) {
                        Log.e(TAG,".......insert into accountEntity...");
                        InsertAccountEntity(entity);
                    }
                    if (sourceType==1){
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable(AppConfig.PARAM_ACCOUNT,entity);
                        intent.putExtras(bundle);
                        AccountActivity.this.setResult(AppConfig.SUCCESS, intent);
                        AccountActivity.this.finish();
                    }else {
                        finish();
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
            findViewById(R.id.llSexMale).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
            findViewById(R.id.llSexWoram).setBackgroundColor(getResources().getColor(R.color.btn_color));

        }else{
            sexWoram.setTextColor(getResources().getColor(R.color.btn_color_selected));
            sexMale.setTextColor(getResources().getColor(R.color.btn_color));
            findViewById(R.id.llSexWoram).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
            findViewById(R.id.llSexMale).setBackgroundColor(getResources().getColor(R.color.btn_color));
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
                if (resultCode==RESULT_OK)
                {
                    startActionPhoto(origUri,cropUri);
                }
                break;
            }
            case AppConfig.REQUEST_CHOOSE_PIC:{
                if (resultCode==RESULT_OK){
                    ((ImageButton)findViewById(R.id.ibAvatar)).setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath()+"/crop_"+imageName));
                }
                break;
            }
            case AppConfig.REQUEST_CROP_PHOTO:{
                if (resultCode==RESULT_OK){
                    ((ImageButton)findViewById(R.id.ibAvatar)).setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath()+"/crop_"+imageName));
                }
                break;
            }
        }

    }

    public void startActionPhoto(Uri data,Uri output){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data,"image/*");
        intent.putExtra("output",output);
        intent.putExtra("crop",true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",640);
        intent.putExtra("outputY",640);
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
        imageName=System.currentTimeMillis()+".jpg";
        cropUri=Uri.fromFile(new File(appContext.getCameraPath()+"/crop_"+imageName));
        origUri=Uri.fromFile(new File(appContext.getCameraPath()+"/"+imageName));
        if (type==0) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("outputX",640);
            intent.putExtra("outputY",640);
            startActivityForResult(Intent.createChooser(intent,"选择照片"), AppConfig.REQUEST_CROP_PHOTO);
        }else{
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,origUri);
            startActivityForResult(intent, AppConfig.REQUEST_TAKE_PHOTO);
        }
    }

    public void imageChooseItem(final CharSequence[] items){
        AlertDialog imageDialog=new AlertDialog.Builder(this).setTitle("上传照片").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectPhoto(i);
            }
        }).create();
        imageDialog.show();
    }

    public Dialog onCreateDateDialog(final FormEditText btn) {
        Dialog dialog = null;
        c = Calendar.getInstance();
        dialog = new DatePickerDialog(AccountActivity.this, new DatePickerDialog.OnDateSetListener() {
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
