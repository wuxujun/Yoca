package com.xujun.app.yoca;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.widget.DetailHeader;
import com.xujun.model.TargetInfoResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.StringUtil;
import com.xujun.widget.AnimatedExpandableListView;
import com.xujun.widget.MySeekBar;

import java.lang.annotation.Target;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 详细页
 * Created by xujunwu on 15/4/10.
 */

@SuppressLint("ValidFragment")
public class DetailActivity extends BaseActivity{

    public static final String TAG = "DetailActivity";

    private int                     height;
    private int                     sex;
    private int                     age;
    private AppConfig               appConfig;

    private TargetInfoResp          targetInfoResp;

    List<HomeTargetEntity> groupItems=new ArrayList<HomeTargetEntity>();
    private AnimatedExpandableListView mListView;
    private ItemAdapter         adapter;

    private DetailHeader        mHeaderView;

    private AccountEntity localAccountEntity=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        targetInfoResp=(TargetInfoResp)appContext.readObject("TargetInfoResp");
        if (targetInfoResp!=null){
            Log.e(TAG, ":" + targetInfoResp.getTargetList().size());
        }

        appConfig=AppConfig.getAppConfig(mContext);
        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");

        if (localAccountEntity!=null){
            height=localAccountEntity.getHeight();
            sex=localAccountEntity.getSex();
            age=localAccountEntity.getAge();
        }
        adapter=new ItemAdapter(this);
        initView();
        mHeadTitle.setText(getText(R.string.main_detail));
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mHeadButton.setText(getText(R.string.btn_history));
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DetailActivity.this,HistoryActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account",localAccountEntity);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initView()
    {
        mListView=(AnimatedExpandableListView)findViewById(R.id.lvContent);
        mHeaderView=new DetailHeader(this);
        mListView.addHeaderView(mHeaderView);
        mListView.setAdapter(adapter);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.e(TAG, "onGroupClick ===>" + i);
                if (mListView.isGroupExpanded(i)) {
                    mListView.collapseGroupWithAnimation(i);
                } else {
                    mListView.expandGroupWithAnimation(i);
                }
                return true;
            }
        });
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        queryHealthData("2015-04-11");
    }

    private void queryHealthData(String pickTime){
        Log.e(TAG,"queryHealthData "+pickTime);
        if (localAccountEntity==null){
            return;
        }
        Log.e(TAG,"queryHealthData "+pickTime+" accountId="+localAccountEntity.getId());
        try{
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("aid",localAccountEntity.getId());
            weightHisQueryBuilder.orderBy("wid",false);
            WeightHisEntity weightHisEntity=weightHisQueryBuilder.queryForFirst();
            if (weightHisEntity!=null){
                if(mHeaderView!=null) {
                   mHeaderView.weightTextView.setText(StringUtil.doubleToStringOne(weightHisEntity.getWeight()));
                }
                updateHomeTargetValue(0, StringUtil.doubleToStringOne(weightHisEntity.getBmi()), appConfig.getBMITitle(weightHisEntity.getBmi()), appConfig.getBMIStatus(weightHisEntity.getBmi()), appConfig.getBMIValue(weightHisEntity.getBmi()));
                updateHomeTargetValue(1,StringUtil.doubleToStringOne(weightHisEntity.getWeight()),appConfig.getWeightTitle(height, sex, weightHisEntity.getWeight()),appConfig.getWeightStatus(height, sex, weightHisEntity.getWeight()),appConfig.getWeightValue(height, sex, weightHisEntity.getWeight()));
                updateHomeTargetValue(2,StringUtil.doubleToStringOne(weightHisEntity.getFat()),appConfig.getFatTitle(age, sex, weightHisEntity.getFat()),appConfig.getFatStatus(age, sex, weightHisEntity.getFat()),appConfig.getFatValue(age, sex, weightHisEntity.getFat()));
                updateHomeTargetValue(3,StringUtil.doubleToStringOne(weightHisEntity.getSubFat()),appConfig.getSubFatTitle(sex, weightHisEntity.getSubFat()),appConfig.getSubFatStatus(sex, weightHisEntity.getSubFat()),appConfig.getSubFatValue(sex, weightHisEntity.getSubFat()));
                updateHomeTargetValue(4,StringUtil.doubleToStringOne(weightHisEntity.getVisFat()),appConfig.getVisFatTitle(weightHisEntity.getVisFat()),appConfig.getVisFatStatus(weightHisEntity.getVisFat()),appConfig.getVisFatValue(weightHisEntity.getVisFat()));
                updateHomeTargetValue(5,StringUtil.doubleToStringOne(weightHisEntity.getWater()),appConfig.getWaterTitle(sex, weightHisEntity.getWater()),appConfig.getWaterStatus(sex, weightHisEntity.getWater()),appConfig.getWaterValue(sex, weightHisEntity.getWater()));
                updateHomeTargetValue(6,StringUtil.doubleToStringOne(weightHisEntity.getBMR()),appConfig.getBMRTitle(age, sex, weightHisEntity.getBMR()),appConfig.getBMRStatus(age, sex, weightHisEntity.getBMR()),appConfig.getBMRValue(age, sex, weightHisEntity.getBMR()));
                updateHomeTargetValue(7,StringUtil.doubleToStringOne(weightHisEntity.getBodyAge()),"正常",1,50);
                updateHomeTargetValue(8,StringUtil.doubleToStringOne(weightHisEntity.getMuscle()),appConfig.getMuscleTitle(height, sex, weightHisEntity.getMuscle()),appConfig.getMuscleStatus(height, sex, weightHisEntity.getMuscle()),appConfig.getMuscleValue(height, sex, weightHisEntity.getMuscle()));
                updateHomeTargetValue(9,StringUtil.doubleToStringOne(weightHisEntity.getBone()),appConfig.getBoneTitle(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneStatus(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneValue(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()));
                updateHomeTargetValue(10,StringUtil.doubleToStringOne(weightHisEntity.getProtein()),"正常",1,50);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        loadTargetData();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private void loadTargetData(){
        try {
            List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().where().eq("aid", localAccountEntity.getId()).query();
            if (homeTargetEntityList.size()>0) {
                groupItems.clear();
                groupItems.addAll(homeTargetEntityList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateHomeTargetValue(int targetType,String value,String valueTitle,int valueStatus,int progres){
        try{
            Dao<HomeTargetEntity, Integer> homeTargetDao= getDatabaseHelper().getHomeTargetDao();
            homeTargetDao.updateRaw("UPDATE `t_home_target` SET value ="+value+",valueTitle='"+valueTitle+"',valueStatus="+valueStatus+",progres="+progres+" WHERE aid="+localAccountEntity.getId()+" and type="+targetType+";");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static  class MenuItemView{
        public ImageButton      ibHis;
        public ImageView        icon;
        public TextView         status;
        public TextView         title;
        public TextView         unit;
        public TextView         value;
        public MySeekBar        seekBar;
    }

    double weight=0.0;
    class ItemAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter{

        private LayoutInflater inflater;

        public ItemAdapter(Context context){
            inflater=LayoutInflater.from(context);
        }


        public String getChild(int gPosition,int childPosition){
            HomeTargetEntity  entity=groupItems.get(gPosition);
            if (entity!=null){
                return getTargetContent(entity.getType());
            }
            return "无";
        }

        public long getChildId(int gPosition,int childPosition){
            return childPosition;
        }

        public View getRealChildView(int gPosition,int childPosition,boolean isLastChild,View convertView,ViewGroup parent){
            MenuItemView holder;
            String desc=getChild(gPosition,childPosition);
            if (convertView==null){
                holder=new MenuItemView();
                convertView=inflater.inflate(R.layout.activity_detail_item,parent,false);
                convertView.findViewById(R.id.llTargetValue).setVisibility(View.GONE);
                convertView.findViewById(R.id.llTargetChart).setVisibility(View.GONE);
                convertView.findViewById(R.id.llTargetDesc).setVisibility(View.VISIBLE);
                holder.title=(TextView)convertView.findViewById(R.id.tvTargetDesc);
                holder.ibHis=(ImageButton)convertView.findViewById(R.id.ibTargetHis);
               convertView.setTag(holder);
            }else{
                holder=(MenuItemView)convertView.getTag();
            }
            holder.title.setText(desc);
//            holder.ibHis.setOnClickListener(new HisClickListener(gPosition));
            return convertView;
        }

        public int getRealChildrenCount(int gPosition){
            Log.e(TAG,""+gPosition+"  1");
            return  1;
        }
        @Override
        public int getGroupCount(){
            return groupItems.size();
        }

        @Override
        public HomeTargetEntity getGroup(int position){
            return groupItems.get(position);
        }

        @Override
        public long getGroupId(int position){
            return position;
        }

        @Override
        public View getGroupView(int position,boolean isExpanded,View convertView,ViewGroup parent){
            MenuItemView holder;
            HomeTargetEntity entity=getGroup(position);
            if (convertView==null){
                convertView=inflater.inflate(R.layout.activity_detail_item,parent,false);
                convertView.findViewById(R.id.llTargetValue).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.llTargetDesc).setVisibility(View.GONE);

                holder=new MenuItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvTargetName);
                holder.status=(TextView)convertView.findViewById(R.id.tvTargetStatus);
                holder.value=(TextView)convertView.findViewById(R.id.tvTargetValue);
                holder.unit=(TextView)convertView.findViewById(R.id.tvTargetUnit);
                holder.seekBar=(MySeekBar)convertView.findViewById(R.id.mySeekBar);
                convertView.setTag(holder);
            }else {
                holder = (MenuItemView) convertView.getTag();
            }
            if(entity!=null){
                holder.title.setText(entity.getTitle());
                if (!StringUtil.isEmpty(entity.getValue())) {
                    holder.value.setText(entity.getValue());
                }
                if (entity.getProgres()!=null) {
                    holder.seekBar.setProgress(entity.getProgres());
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
            }
            return convertView;
        }

        public boolean hasStableIds(){
            return true;
        }
        public boolean isChildSelectable(int i,int j){
            return true;
        }
    }

    class HisClickListener implements View.OnClickListener{

        private int position;
        HisClickListener(int pos){
            position=pos;
        }
        @Override
        public void onClick(View view) {
            Log.e(TAG,"onClick() .."+position);
            HomeTargetEntity entity=groupItems.get(position);
            Intent intent=new Intent(DetailActivity.this, TargetHisActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("account",localAccountEntity);
            bundle.putInt("targetType",entity.getType());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private String getTargetContent(int type){
        try {
            List<TargetEntity> targetEntityList = getDatabaseHelper().getTargetInfoDao().queryBuilder().where().eq("type", type).query();
            if (targetEntityList.size()>0) {
                TargetEntity entity=targetEntityList.get(0);
                return entity.getContent();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "无";
    }


}
