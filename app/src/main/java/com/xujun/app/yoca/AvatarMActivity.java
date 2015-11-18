package com.xujun.app.yoca;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xujunwu on 15/7/21.
 */
public class AvatarMActivity extends BaseActivity{
    public static final String TAG = "AvatarMActivity";

    private List<WeightHisEntity> items=new ArrayList<WeightHisEntity>();
    private ItemAdapter             mAdapter;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);

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
        mHeadTitle.setText("减肥像册");
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mHeadButton.setText(getText(R.string.btn_Edit));
        mHeadButton.setVisibility(View.INVISIBLE);
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    private void loadData(){
        try {
            items.clear();
            List<WeightHisEntity> weightHisEntityList = getDatabaseHelper().getWeightHisEntityDao().queryBuilder().where().isNotNull("avatar").query();
            if (weightHisEntityList.size()>0) {
                items.addAll(weightHisEntityList);
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
        loadData();
    }


    static  class ItemView{
        public CheckBox cbEdit;
        public ImageView icon;
        public TextView         time;
        public TextView         bust;
        public TextView         waistline;
        public TextView         hips;
        public TextView         weight;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.avatar_item,null);
                holder=new ItemView();
                holder.icon=(ImageView)convertView.findViewById(R.id.ivIcon);
                holder.time=(TextView)convertView.findViewById(R.id.tvTime);
                holder.bust=(TextView)convertView.findViewById(R.id.tvBust);
                holder.waistline=(TextView)convertView.findViewById(R.id.tvWaistline);
                holder.hips=(TextView)convertView.findViewById(R.id.tvHips);
                holder.weight=(TextView)convertView.findViewById(R.id.tvWeight);
//                holder.cbEdit=(CheckBox)convertView.findViewById(R.id.cbTragetEdit);
//                holder.cbEdit.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            WeightHisEntity entity=items.get(i);
            if(entity!=null){
                holder.time.setText(DateUtil.getDateString(entity.getAddtime()));
                if (!StringUtil.isEmpty(entity.getBust())) {
                    holder.bust.setText(getText(R.string.avatar_Bust)+":"+entity.getBust());
                }
                if (!StringUtil.isEmpty(entity.getWaistline())) {
                    holder.waistline.setText(getText(R.string.avatar_Waistline)+":"+entity.getWaistline());
                }
                if (!StringUtil.isEmpty(entity.getHips())) {
                    holder.hips.setText(getText(R.string.avatar_Hips)+":"+entity.getHips());
                }
                holder.weight.setText(StringUtil.doubleToStringOne(entity.getWeight()));
                if (!StringUtil.isEmpty(entity.getAvatar())){
                    if (entity.getAvatar().indexOf("crop_")>=0) {
                        holder.icon.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/" + entity.getAvatar()));
                    }else {
                        ImageLoader.getInstance().displayImage(URLs.IMAGE_URL+entity.getAvatar(), holder.icon,options);
                    }
                }
            }
//            holder.cbEdit.setOnCheckedChangeListener(new editClickListener(i));
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
            WeightHisEntity entity=items.get(position);
            if (entity!=null){
//                addHomeTargetEntity(entity);
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
}
