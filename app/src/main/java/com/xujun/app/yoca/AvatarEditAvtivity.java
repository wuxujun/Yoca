package com.xujun.app.yoca;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.widget.MySeekBar;

import org.w3c.dom.Text;

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
 * Created by xujunwu on 15/7/21.
 */
public class AvatarEditAvtivity extends BaseActivity{
    public static final String TAG = "AvatarEditAvtivity";


    private List<HomeTargetEntity> items=new ArrayList<HomeTargetEntity>();
    private ListView mListView;
    private ItemAdapter             mAdapter;

    private AccountEntity localAccountEngity=null;
    private int           localWeightId=0;
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
    private ImageButton  cameraIB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar_list_frame);

        localAccountEngity=(AccountEntity)getIntent().getSerializableExtra("account");
        localWeightId=getIntent().getIntExtra("weightId",0);
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

        mHeadTitle.setText(getText(R.string.main_avatar_edit));
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

    private void saveAvatar(){
        if (StringUtil.isEmpty(bustET.getText().toString())){
            Toast.makeText(mContext, getText(R.string.avatar_Bust_Hit), Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(waistlineET.getText().toString())){
            Toast.makeText(mContext,getText(R.string.avatar_Waistline_Hit), Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(hipsET.getText().toString())){
            Toast.makeText(mContext,getText(R.string.avatar_Hips_Hit), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAvatar||StringUtil.isEmpty(imageName)){
            Toast.makeText(mContext,getText(R.string.avatar_Avatar_Hit), Toast.LENGTH_SHORT).show();
            return;
        }
        if (localWeightId>0&&localWeightEntity!=null){
            localWeightEntity.setBust(bustET.getText().toString());
            localWeightEntity.setWaistline(waistlineET.getText().toString());
            localWeightEntity.setHips(hipsET.getText().toString());
            if (isAvatar&&!StringUtil.isEmpty(imageName)){
                localWeightEntity.setAvatar("crop_" + imageName);
            }
            localWeightEntity.setIsSync(0);
            updateWeightEntity(localWeightEntity);
        }
        finish();
    }

    private void initUIView(){
        cameraIB=(ImageButton)findViewById(R.id.ibCamera);
        cameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items={"手机相册","手机拍照"};
                imageChooseItem(items);
            }
        });
        bustET=(FormEditText)findViewById(R.id.etBust);
        bustET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Bust_Hit)));
        waistlineET=(FormEditText)findViewById(R.id.etWaistline);
        bustET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Waistline_Hit)));
        hipsET=(FormEditText)findViewById(R.id.etHips);
        hipsET.addValidator(new EmptyValidator(getResources().getString(R.string.avatar_Hips_Hit)));
    }

    private void loadHomeTarget(){
        try {
            items.clear();
            List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().where().eq("aid", localAccountEngity.getId()).query();
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
        if (localAccountEngity==null){
            return;
        }
        AppConfig appConfig=AppConfig.getAppConfig(mContext);

        try{
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("aid",localAccountEngity.getId()).and().eq("wid",localWeightId);
            localWeightEntity=weightHisQueryBuilder.queryForFirst();
            if (localWeightEntity!=null){
                if (!StringUtil.isEmpty(localWeightEntity.getBust())){
                    bustET.setText(localWeightEntity.getBust());
                }
                if (!StringUtil.isEmpty(localWeightEntity.getWaistline())){
                    waistlineET.setText(localWeightEntity.getWaistline());
                }
                if (!StringUtil.isEmpty(localWeightEntity.getHips())){
                    hipsET.setText(localWeightEntity.getHips());
                }
                if (!StringUtil.isEmpty(localWeightEntity.getAvatar())){
                    cameraIB.setImageBitmap(ImageUtils.getBitmapByPath(localWeightEntity.getAvatar()));
                }
                ((TextView)findViewById(R.id.tvTime)).setText(DateUtil.getTimeString(localWeightEntity.getAddtime()));
                Log.e(TAG,"---------->"+localWeightEntity.getAddtime());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public AccountEntity getLocalAccountEngity() {
        return localAccountEngity;
    }

    public void setLocalAccountEngity(AccountEntity localAccountEngity) {
        this.localAccountEngity = localAccountEngity;
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
            dao.setAutoCommit(dao.startThreadConnection(),false);
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
                    cameraIB.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + imageName));
                }
                break;
            }
            case AppConfig.REQUEST_CROP_PHOTO:{
                ContentResolver resolver=getContentResolver();
                Uri uri=data.getData();
                if (resultCode==RESULT_OK){
                    Bitmap img=null;
                    try{
                      Bitmap bitmap= BitmapFactory.decodeStream(resolver.openInputStream(uri));
                        img=ImageUtils.zoomBitmap(bitmap,640,640);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    if (img!=null) {
                        try {
                            ImageUtils.saveImageToSD(appContext.getCameraPath() + "/crop_" + imageName, img, 100);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        isAvatar=true;
                        cameraIB.setImageBitmap(img);
                    }
//                    cameraIB.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath()+"/crop_"+imageName));
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

    public void selectPhoto(int type){
        imageName=System.currentTimeMillis()+".jpg";
        cropUri= Uri.fromFile(new File(appContext.getCameraPath() + "/crop_" + imageName));
        origUri=Uri.fromFile(new File(appContext.getCameraPath()+"/"+imageName));
        if (type==0) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
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
