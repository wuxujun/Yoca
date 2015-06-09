package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;
import com.xujun.util.UIHelper;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 15/4/20.
 */
public class TargetHisActivity extends SherlockActivity{

    public static final String TAG = "TargetHisActivity";

    private Context mContext;
    private AppContext      appContext;

    private int                 targetType;
    private String              targetTypeUnit;
    private String              targetTypeName;

    private ItemAdapter         mAdapter;
    private ListView            mListView;

    private boolean             isEdit=false;

    private AppConfig           appConfig;


    private List<WeightHisEntity> items=new ArrayList<WeightHisEntity>();

    private AccountEntity localAccountEngity=null;

    private DatabaseHelper databaseHelper;


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
        mContext = getApplicationContext();
        appContext = (AppContext) getApplication();
        appConfig=AppConfig.getAppConfig(mContext);
        targetType=getIntent().getIntExtra("targetType", 0);
        localAccountEngity=(AccountEntity)getIntent().getSerializableExtra("account");
        targetTypeUnit=appConfig.getTargetTypeUnit(targetType);
        targetTypeName=appConfig.getTargetType(targetType);

        mAdapter=new ItemAdapter();
        mListView=(ListView)findViewById(R.id.lvList);
        mListView.setAdapter(mAdapter);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        getActionBar().setTitle(targetTypeName+"-历史记录");
    }

    @Override
    public void onResume() {
        super.onResume();
        queryHisData();
    }


    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    public void queryHisData(){
        if (localAccountEngity!=null){
            items.clear();
            try {
                Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
                weightHisQueryBuilder.where().eq("aid", localAccountEngity.getId());
                weightHisQueryBuilder.orderBy("wid", false);

                List<WeightHisEntity> list=weightHisQueryBuilder.query();
                if (list!=null&&list.size()>0){
                    items.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public boolean onPrepareOptionsMenu (Menu menu) {
        if (isEdit){
            getSupportMenuInflater().inflate(R.menu.main_done, menu);
        }else {
            getSupportMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.item_main_edit:{
                isEdit=true;
                mAdapter.notifyDataSetChanged();
                UIHelper.refreshActionBarMenu(this);
                break;
            }
            case R.id.item_main_done:{
                isEdit=false;
                mAdapter.notifyDataSetChanged();
                UIHelper.refreshActionBarMenu(this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    static  class ItemView{
        public ImageView icon;
        public TextView         title;
        public TextView         value;
        public TextView         unit;
        public LinearLayout llEdit;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.target_his_item,null);
                holder=new ItemView();
                holder.value=(TextView)convertView.findViewById(R.id.tvTargetValue);
                holder.title=(TextView)convertView.findViewById(R.id.tvTargetTime);
                holder.unit=(TextView)convertView.findViewById(R.id.tvTargetUnit);
                holder.llEdit=(LinearLayout)convertView.findViewById(R.id.llTargetEdit);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            WeightHisEntity entity=items.get(i);
            if(entity!=null){
                holder.title.setText(DateUtil.getDateString(entity.getAddtime()));
                switch (targetType){
                    case 0:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getBMR()));
                        break;
                    case 1:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getWeight()));
                        break;
                    case 2:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getFat()));
                        break;
                    case 3:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getSubFat()));
                        break;
                    case 4:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getVisFat()));
                        break;
                    case 5:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getWater()));
                        break;
                    case 6:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getBMR()));
                        break;
                    case 8:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getMuscle()));
                        break;
                    case 9:
                        holder.value.setText(StringUtil.doubleToStringOne(entity.getBone()));
                        break;
                }
                holder.unit.setText(targetTypeUnit);
            }
            holder.llEdit.setVisibility(isEdit?View.VISIBLE:View.GONE);
            holder.llEdit.setOnClickListener(new EditClickListener(i));
            return convertView;
        }
    }

    class EditClickListener implements View.OnClickListener{

        private int position;
        EditClickListener(int pos){
            position=pos;
        }
        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick() .." + position);
            WeightHisEntity entity=items.get(position);

            try {
                DeleteBuilder<WeightHisEntity,Integer> deleteBuilder=getDatabaseHelper().getWeightHisEntityDao().deleteBuilder();
                deleteBuilder.where().eq("wid",entity.getWid());
                deleteBuilder.delete();
            }catch (SQLException e){
                e.printStackTrace();
            }
            items.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }
}
