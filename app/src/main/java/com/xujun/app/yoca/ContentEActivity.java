package com.xujun.app.yoca;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.widget.ContentController;
import com.xujun.app.yoca.widget.ContentHeader;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;
import com.xujun.widget.MySeekBar;

import java.lang.annotation.Target;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class ContentEActivity extends SherlockActivity implements ContentController{

    public static final String TAG = "ContentEFragment";

    private Context                 mContext;
    private AppContext appContext;

    private DatabaseHelper          databaseHelper;

    private List<HomeTargetEntity> items=new ArrayList<HomeTargetEntity>();
    private ListView mListView;
    private ItemAdapter             mAdapter;

    private ContentHeader           mContentHeader;

    private AccountEntity           localAccountEngity=null;

    private int                     currentDay=0;
    private SimpleDateFormat df=new SimpleDateFormat("MM-dd");
    private String                  strToday=df.format(new Date());

    private SimpleDateFormat dfDay=new SimpleDateFormat("yyyy-MM-dd");
    private String          strTodayDay=dfDay.format(new Date());


    private SimpleDateFormat dfYearMonthDay=new SimpleDateFormat("yyyyMMdd");


    private int targetTotal=9;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        localAccountEngity=(AccountEntity)getIntent().getSerializableExtra("account");

        mContentHeader=new ContentHeader(mContext);
        mContentHeader.setContentController(this);
        mListView=(ListView)findViewById(R.id.lvList);
        mListView.addHeaderView(mContentHeader);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG,"onItemClick  "+i);
//                SherlockFragment sherlockFragment=new ChartFragment();
//                ((ChartFragment)sherlockFragment).loadData(localAccountEngity);
//                getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();
            }
        });
        mContentHeader.sharedButton.setVisibility(View.INVISIBLE);

        loadHomeTarget();
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
//        queryHealthData(strTodayDay);
//        if (items.size()>0){
//            Log.e(TAG,"List size "+items.size()+"  update.....");
//        }
//        initTargetData();
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getSupportMenuInflater().inflate(R.menu.main_done, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_main_done:{
                finish();
                break;
            }
        }
       return super.onOptionsItemSelected(item);
    }

    private void initTargetData()
    {
        if (localAccountEngity!=null&&localAccountEngity.getTargetWeight()!=null) {
            ((TextView)findViewById(R.id.tvTargetValueTotal)).setText(StringUtil.doubleToStringOne(Double.parseDouble(localAccountEngity.getTargetWeight())));
        }
        if (localAccountEngity!=null&&localAccountEngity.getDoneTime()!=null) {
            ((TextView)findViewById(R.id.tvTargetDayNum)).setText("" + DateUtil.getDayDiff(localAccountEngity.getDoneTime()));
        }
    }

    private void queryHealthData(String pickTime){
        Log.e(TAG,"queryHealthData "+pickTime);
        if (localAccountEngity==null){
            return;
        }
        items.clear();
        Log.e(TAG,"queryHealthData "+pickTime+" accountId="+localAccountEngity.getId());
        AppConfig appConfig=AppConfig.getAppConfig(mContext);
        int sex=localAccountEngity.getSex();
        int age=localAccountEngity.getAge();
        int height=localAccountEngity.getHeight();

        try{
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("aid",localAccountEngity.getId());
            weightHisQueryBuilder.orderBy("wid",false);
            WeightHisEntity weightHisEntity=weightHisQueryBuilder.queryForFirst();
            if (weightHisEntity!=null){
                ((TextView) findViewById(R.id.tvWeightValue)).setText(StringUtil.doubleToStringOne(weightHisEntity.getWeight()));

            }

            Dao<AccountEntity,Integer> accountEntityIntegerDao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity, Integer> queryBuilder1 = accountEntityIntegerDao.queryBuilder();
            queryBuilder1.where().eq("id",localAccountEngity.getId());
            if (queryBuilder1.queryForFirst()!=null){
                localAccountEngity=queryBuilder1.queryForFirst();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 加载成员信息
     * @param account
     */
    public void loadData(AccountEntity account){
        if (account!=null) {
            Log.e(TAG,"loadData()... account is not null "+account.getUserNick()+" "+account.getHeight()+"  "+account.getAge()+" "+account.getSex()
                    +""+account.getWeight()+" "+account.getFat()+"  "+account.getSubFat()+"  "+account.getVisFat());
            localAccountEngity=account;
            queryHealthData(strTodayDay);
        }
    }

   private  boolean isDelete(){
       if (targetTotal>3){
           return true;
       }
       Toast.makeText(mContext,"指标显示数量不能小于3项.",Toast.LENGTH_SHORT).show();
       return false;
   }

    /***
     * 刷新数据
     */
    private void refreshDayData(){
        Calendar cal=Calendar.getInstance();
        if(currentDay==-1){
            ((TextView)findViewById(R.id.tvDataDay)).setText(getResources().getString(R.string.main_Yesterday));
            cal.add(Calendar.DATE,-1);
            queryHealthData(dfDay.format(cal.getTime()));
            if (localAccountEngity.getDoneTime()!=null) {
                ((TextView)findViewById(R.id.tvTargetDayNum)).setText("" + DateUtil.getDayDiff(dfYearMonthDay.format(cal.getTime()),localAccountEngity.getDoneTime()));
            }
        }else if (currentDay==0){
            ((TextView)findViewById(R.id.tvDataDay)).setText(getResources().getString(R.string.main_today));
            queryHealthData(strTodayDay);
            if (localAccountEngity.getDoneTime()!=null) {
                ((TextView)findViewById(R.id.tvTargetDayNum)).setText("" + DateUtil.getDayDiff(dfYearMonthDay.format(new Date()),localAccountEngity.getDoneTime()));
            }
        }else{
            cal.add(Calendar.DATE,currentDay);
            String str=df.format(cal.getTime());
            ((TextView)findViewById(R.id.tvDataDay)).setText(str);
            queryHealthData(dfDay.format(cal.getTime()));
            if (localAccountEngity.getDoneTime()!=null) {
                ((TextView)findViewById(R.id.tvTargetDayNum)).setText("" + DateUtil.getDayDiff(dfYearMonthDay.format(cal.getTime()),localAccountEngity.getDoneTime()));
            }
        }
    }

    public AccountEntity getLocalAccountEngity() {
        return localAccountEngity;
    }

    public void setLocalAccountEngity(AccountEntity localAccountEngity) {
        this.localAccountEngity = localAccountEngity;
    }

    @Override
    public void onViewTargetClicked() {
        Intent intent=new Intent(ContentEActivity.this, TargetActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("account",localAccountEngity);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onViewLeftClicked() {

    }

    @Override
    public void onViewRightClicked() {

    }

    @Override
    public void onViewDetailClicked() {

    }

    @Override
    public void onViewSharedClicked() {

    }


    static  class ItemView{
        public CheckBox         cbEdit;
        public ImageView icon;
        public TextView         status;
        public TextView         title;
        public TextView         unit;
        public TextView         value;
        public MySeekBar        seekBar;
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
}
