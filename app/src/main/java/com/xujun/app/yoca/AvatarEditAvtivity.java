package com.xujun.app.yoca;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.widget.FormEditText;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;
import com.xujun.widget.MySeekBar;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数据拍照
 * Created by xujunwu on 15/7/21.
 */
public class AvatarEditAvtivity extends BaseActivity{
    public static final String TAG = "AvatarEditAvtivity";


    private List<HomeTargetEntity> items=new ArrayList<HomeTargetEntity>();
    private ListView mListView;
    private ItemAdapter             mAdapter;

    private AccountEntity localAccountEntity=null;
    private long           localWeightId=0;
    private WeightHisEntity localWeightEntity=null;

    private int                     currentDay=0;
    private SimpleDateFormat df=new SimpleDateFormat("MM-dd");
    private String                  strToday=df.format(new Date());

    private SimpleDateFormat dfDay=new SimpleDateFormat("yyyy-MM-dd");
    private String          strTodayDay=dfDay.format(new Date());

    private SimpleDateFormat dfYearMonthDay=new SimpleDateFormat("yyyyMMdd");

    private int targetTotal=9;



    private String                  imageName;
    private File                    imageFile;
    private Uri          origUri;
    private Uri          cropUri;
    private boolean      isAvatar=false;


    private InputMethodManager imm;
    private FormEditText bustET;
    private FormEditText waistlineET;
    private FormEditText hipsET;
    private ImageView  cameraIB;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_list_frame);
        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        localWeightId=getIntent().getLongExtra("weightId",0);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        mListView=(ListView)findViewById(R.id.lvList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick  " + i);
//                SherlockFragment sherlockFragment=new ChartFragment();
//                ((ChartFragment)sherlockFragment).loadData(localAccountEngity);
//                getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();
            }
        });

        String time=getIntent().getStringExtra("dataTime");
        if (!StringUtil.isEmpty(time)){
            mHeadTitle.setText(time+"数据");
        }else {
            mHeadTitle.setText("今日数据");
        }
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mHeadButton.setText(getText(R.string.btn_Save));
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAvatar();
            }
        });
        initUIView();

        loadHomeTarget();
    }

    private void updateHomeTargetValue(int targetType,String value,String valueTitle,int valueStatus,int progres){
        try{
            Dao<HomeTargetEntity, Integer> homeTargetDao= getDatabaseHelper().getHomeTargetDao();
            homeTargetDao.updateRaw("UPDATE `t_home_target` SET value ="+value+",valueTitle='"+valueTitle+"',valueStatus="+valueStatus+",progres="+progres+" WHERE aid="+localAccountEntity.getId()+" and type="+targetType+";");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void saveAvatar(){
        String bust="0";
        if (!StringUtil.isEmpty(bustET.getText().toString())){
//            Toast.makeText(mContext, getText(R.string.avatar_Bust_Hit), Toast.LENGTH_SHORT).show();
//            return;
            bust=bustET.getText().toString();
        }
        String waistline="0";
        if (!StringUtil.isEmpty(waistlineET.getText().toString())){
//            Toast.makeText(mContext,getText(R.string.avatar_Waistline_Hit), Toast.LENGTH_SHORT).show();
//            return;
            waistline=waistlineET.getText().toString();
        }
        String hips="0";
        if (!StringUtil.isEmpty(hipsET.getText().toString())){
//            Toast.makeText(mContext,getText(R.string.avatar_Hips_Hit), Toast.LENGTH_SHORT).show();
//            return;
            hips=hipsET.getText().toString();
        }
        if (!isAvatar||StringUtil.isEmpty(imageName)){
            Toast.makeText(mContext,getText(R.string.avatar_Avatar_Hit), Toast.LENGTH_SHORT).show();
            return;
        }
        appContext.setProperty(bust, AppConfig.CONF_BUST);
        appContext.setProperty(waistline,AppConfig.CONF_WAISTLINE);
        appContext.setProperty(hips, AppConfig.CONF_HIPS);
        if (localWeightId>0&&localWeightEntity!=null){
            localWeightEntity.setBust(bust);
            localWeightEntity.setWaistline(waistline);
            localWeightEntity.setHips(hips);
            if (isAvatar&&!StringUtil.isEmpty(imageName)){
                localWeightEntity.setAvatar("crop_" + imageName);
            }
            localWeightEntity.setIsSync(0);
            updateWeightEntity(localWeightEntity);
            Log.e(TAG,""+localWeightId+"  "+localWeightEntity.getAvatar());
        }
        finish();
    }

    private void initUIView(){
        cameraIB=(ImageView)findViewById(R.id.ibCamera);
        cameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items={"手机相册","手机拍照"};
                imageChooseItem(items);
            }
        });
        bustET=(FormEditText)findViewById(R.id.etBust);
        bustET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Bust_Hit)));
        bustET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bustET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(bustET.getWindowToken(), 0);
                }
            }
        });
        waistlineET=(FormEditText)findViewById(R.id.etWaistline);
        waistlineET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Waistline_Hit)));
        waistlineET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(waistlineET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(waistlineET.getWindowToken(), 0);
                }
            }
        });
        hipsET=(FormEditText)findViewById(R.id.etHips);
        hipsET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Hips_Hit)));
        hipsET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(hipsET, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(hipsET.getWindowToken(), 0);
                }
            }
        });
        if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_BUST))){
            bustET.setText(appContext.getProperty(AppConfig.CONF_BUST));
        }
        if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_WAISTLINE))){
            waistlineET.setText(appContext.getProperty(AppConfig.CONF_WAISTLINE));
        }
        if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_HIPS))){
            hipsET.setText(appContext.getProperty(AppConfig.CONF_HIPS));
        }
    }

    private void loadHomeTarget(){
        AppConfig appConfig=AppConfig.getAppConfig(mContext);
        int sex=localAccountEntity.getSex();
        int age=localAccountEntity.getAge();
        int height=localAccountEntity.getHeight();

        try {
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("wid", localWeightId);
            WeightHisEntity weightHisEntity=weightHisQueryBuilder.queryForFirst();
            if (weightHisEntity!=null){
                updateHomeTargetValue(2,StringUtil.doubleToStringOne(weightHisEntity.getBmi()), appConfig.getBMITitle(weightHisEntity.getBmi()), appConfig.getBMIStatus(weightHisEntity.getBmi()), appConfig.getBMIValue(weightHisEntity.getBmi()));
                updateHomeTargetValue(1,StringUtil.doubleToStringOne(weightHisEntity.getWeight()),appConfig.getWeightTitle(height, sex, weightHisEntity.getWeight()),appConfig.getWeightStatus(height, sex, weightHisEntity.getWeight()),appConfig.getWeightValue(height, sex, weightHisEntity.getWeight()));
                updateHomeTargetValue(3,StringUtil.doubleToStringOne(weightHisEntity.getFat()),appConfig.getFatTitle(age, sex, weightHisEntity.getFat()),appConfig.getFatStatus(age, sex, weightHisEntity.getFat()),appConfig.getFatValue(age, sex, weightHisEntity.getFat()));
                updateHomeTargetValue(4,StringUtil.doubleToStringOne(weightHisEntity.getSubFat()),appConfig.getSubFatTitle(sex, weightHisEntity.getSubFat()),appConfig.getSubFatStatus(sex, weightHisEntity.getSubFat()),appConfig.getSubFatValue(sex, weightHisEntity.getSubFat()));
                updateHomeTargetValue(5,StringUtil.doubleToStringOne(weightHisEntity.getVisFat()),appConfig.getVisFatTitle(weightHisEntity.getVisFat()),appConfig.getVisFatStatus(weightHisEntity.getVisFat()),appConfig.getVisFatValue(weightHisEntity.getVisFat()));
                updateHomeTargetValue(7,StringUtil.doubleToStringOne(weightHisEntity.getWater()),appConfig.getWaterTitle(sex, weightHisEntity.getWater()),appConfig.getWaterStatus(sex, weightHisEntity.getWater()),appConfig.getWaterValue(sex, weightHisEntity.getWater()));
                updateHomeTargetValue(6,StringUtil.doubleToStringOne(weightHisEntity.getBMR()),appConfig.getBMRTitle(age, sex, weightHisEntity.getBMR()),appConfig.getBMRStatus(age, sex, weightHisEntity.getBMR()),appConfig.getBMRValue(age, sex, weightHisEntity.getBMR()));
                if(weightHisEntity.getBodyAge()!=null) {
                    updateHomeTargetValue(11, StringUtil.doubleToStringOne(weightHisEntity.getBodyAge()), "标准", 1, 50);
                }
                updateHomeTargetValue(8,StringUtil.doubleToStringOne(weightHisEntity.getMuscle()),appConfig.getMuscleTitle(age, sex, weightHisEntity.getMuscle()),appConfig.getMuscleStatus(age, sex, weightHisEntity.getMuscle()),appConfig.getMuscleValue(age, sex, weightHisEntity.getMuscle()));
                updateHomeTargetValue(9,StringUtil.doubleToStringOne(weightHisEntity.getBone()),appConfig.getBoneTitle(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneStatus(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneValue(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()));
                if(weightHisEntity.getProtein()!=null) {
                    updateHomeTargetValue(10, StringUtil.doubleToStringOne(weightHisEntity.getProtein()), "标准", 1, 50);
                }
            }

            items.clear();
            List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().orderBy("type",true).where().eq("aid", localAccountEntity.getId()).and().notIn("type",0).query();
            if (homeTargetEntityList.size()>0) {
                items.addAll(homeTargetEntityList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (mAdapter==null){
            mAdapter=new ItemAdapter();
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
        queryHealthData();
    }



    private void queryHealthData(){
        if (localAccountEntity==null){
            return;
        }
        AppConfig appConfig=AppConfig.getAppConfig(mContext);
        try{
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("aid",localAccountEntity.getId()).and().eq("wid",localWeightId);
            localWeightEntity=weightHisQueryBuilder.queryForFirst();
            if (localWeightEntity!=null){
                if (!StringUtil.isEmpty(localWeightEntity.getBust())){
                    bustET.setText(localWeightEntity.getBust());
                }else{
                    if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_BUST))){
                        bustET.setText(appContext.getProperty(AppConfig.CONF_BUST));
                    }
                }
                if (!StringUtil.isEmpty(localWeightEntity.getWaistline())){
                    waistlineET.setText(localWeightEntity.getWaistline());
                }else{
                    if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_WAISTLINE))){
                        waistlineET.setText(appContext.getProperty(AppConfig.CONF_WAISTLINE));
                    }
                }
                if (!StringUtil.isEmpty(localWeightEntity.getHips())){
                    hipsET.setText(localWeightEntity.getHips());
                }else{
                    if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_HIPS))){
                        hipsET.setText(appContext.getProperty(AppConfig.CONF_HIPS));
                    }
                }
                if (!StringUtil.isEmpty(localWeightEntity.getAvatar())){
                    if (localWeightEntity.getAvatar().indexOf("crop_")>=0) {
                        cameraIB.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/" + localWeightEntity.getAvatar()));
                    }else {
                        ImageLoader.getInstance().displayImage(URLs.IMAGE_URL + localWeightEntity.getAvatar(), cameraIB, options);
                    }
                }
                ((TextView)findViewById(R.id.tvTime)).setText(DateUtil.getTimeString(localWeightEntity.getAddtime()));
                ((TextView)findViewById(R.id.tvYoca)).setText("Sholai指数:   "+appConfig.getSholaiValue(localAccountEntity,localWeightEntity));

                Log.e(TAG, "---------->" + localWeightEntity.getAddtime());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public AccountEntity getLocalAccountEntity() {
        return localAccountEntity;
    }

    public void setLocalAccountEntity(AccountEntity localAccountEntity) {
        this.localAccountEntity = localAccountEntity;
    }

    static  class ItemView{
        public CheckBox cbEdit;
        public ImageView icon;
        public TextView         status;
        public TextView         title;
        public TextView         unit;
        public TextView         value;
        public MySeekBar seekBar;
    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ItemView holder;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.edit_content_item,null);
                holder=new ItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvTargetName);
                holder.status=(TextView)convertView.findViewById(R.id.tvTargetStatus);
                holder.value=(TextView)convertView.findViewById(R.id.tvTargetValue);
                holder.unit=(TextView)convertView.findViewById(R.id.tvTargetUnit);
                holder.seekBar=(MySeekBar)convertView.findViewById(R.id.mySeekBar);
                holder.cbEdit=(CheckBox)convertView.findViewById(R.id.cbTragetEdit);
                holder.cbEdit.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            HomeTargetEntity entity=items.get(i);
            if(entity!=null){
                holder.title.setText(entity.getTitle());
                if (!StringUtil.isEmpty(entity.getValue())) {
                    holder.value.setText(entity.getValue());
                }

                if (!StringUtil.isEmpty(entity.getUnit())) {
                    holder.unit.setText(entity.getUnit());
                    if (entity.getUnit().equals("0")){
                        holder.unit.setText("");
                    }
                }

                if (!StringUtil.isEmpty(entity.getValueTitle())) {
                    holder.status.setText(entity.getValueTitle());
                }
                if (entity.getProgres()!=null) {
                    holder.seekBar.setProgress(entity.getProgres());
                }
                if (entity.getValueStatus()==0){
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                }else if(entity.getValueStatus()==1){
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_green));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_green));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_green));
                }else{
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_red));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_red));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_red));
                }
                if (entity.getIsShow()==1){
                    holder.cbEdit.setChecked(true);
                }

            }
            holder.cbEdit.setOnCheckedChangeListener(new editClickListener(i));
            return convertView;
        }


    }

    class editClickListener implements CheckBox.OnCheckedChangeListener{

        private int position;
        editClickListener(int pos){
            position=pos;
        }
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            HomeTargetEntity entity=items.get(position);
            if (entity!=null){
                entity.setIsShow(b?1:0);
                addHomeTargetEntity(entity);
            }
        }
    }

    private void addHomeTargetEntity(HomeTargetEntity entity){
        try{
            Dao<HomeTargetEntity,Integer> dao=getDatabaseHelper().getHomeTargetDao();
            dao.setAutoCommit(dao.startThreadConnection(), false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateWeightEntity(WeightHisEntity entity){
        try{
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
            Log.e(TAG,""+entity.getAvatar());
        }catch (SQLException e){
            e.printStackTrace();
        }
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
                    isAvatar=true;
                    BitmapFactory.Options bitmapOpions= new BitmapFactory.Options();
                    bitmapOpions.inSampleSize=8;
                    int degree= ImageUtils.readPictureDegree(appContext.getCameraPath() + "/crop_" + imageName);
                    Log.e(TAG, "==========>" + degree);
                    Bitmap bitmap=ImageUtils.rotaingImageView(degree,ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + imageName));
                    cameraIB.setImageBitmap(bitmap);
                }
                break;
            }
            case AppConfig.REQUEST_CROP_PHOTO:{
                if (data!=null) {
                    startActionPhoto(data.getData(),cropUri);
                }
                break;
            }
        }

    }

    public void startActionPhoto(Uri data,Uri output){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data,"image/*");
        intent.putExtra("output", output);
        intent.putExtra("crop", true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",640);
        intent.putExtra("outputY",800);
        intent.putExtra("return-date",true);
        startActivityForResult(intent, AppConfig.REQUEST_CHOOSE_PIC);
    }

    public void selectPhoto(int type){
        imageName=System.currentTimeMillis()+".jpg";
        cropUri= Uri.fromFile(new File(appContext.getCameraPath() + "/crop_" + imageName));
        origUri=Uri.fromFile(new File(appContext.getCameraPath()+"/"+imageName));
        if (type==0) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            startActivityForResult(Intent.createChooser(intent, "选择照片"), AppConfig.REQUEST_CROP_PHOTO);
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
}
