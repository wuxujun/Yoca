package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 15/5/17.
 */
public class HistoryFragment extends SherlockFragment{
    public static final String TAG = "HistoryFragment";

    private View        mContentView;

    private Context mContext;
    private AppContext appContext;

    private int                 targetType=0;
    private String              targetTypeUnit;

    private ItemAdapter         mAdapter;
    private ListView mListView;

    private boolean             isEdit=false;

    private AppConfig appConfig;

    private List<WeightHisEntity> items=new ArrayList<WeightHisEntity>();

    private AccountEntity localAccountEntity=null;

    private DatabaseHelper databaseHelper;


    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }
    public void setTargetType(int type){
        targetType=type;
    }

    public void setEdit(boolean flag){
        isEdit=flag;
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setLocalAccountEntity(AccountEntity accountEntity)
    {
        localAccountEntity=accountEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView()");
        mContentView=inflater.inflate(R.layout.list_frame,null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);
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

        if (localAccountEntity!=null){
            Log.e(TAG,"OnCreateView "+localAccountEntity.getId());
        }

        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();

        appConfig=AppConfig.getAppConfig(mContext);
//        targetType=getIntent().getIntExtra("targetType", 0);
//        localAccountEngity=(AccountEntity)getIntent().getSerializableExtra("account");
//        targetTypeUnit=appConfig.getTargetTypeUnit(targetType);
//        targetTypeName=appConfig.getTargetType(targetType);

        mAdapter=new ItemAdapter();
        getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getSherlockActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getSherlockActivity().getActionBar().setTitle("历史记录");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");
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
        if (localAccountEntity!=null){
            items.clear();

            targetTypeUnit=appConfig.getTargetTypeUnit(targetType);
            try {
                Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
                weightHisQueryBuilder.where().eq("aid", localAccountEntity.getId());
                weightHisQueryBuilder.orderBy("wid", false);
                List<WeightHisEntity> list=weightHisQueryBuilder.query();
                if (list!=null&&list.size()>0){
                    items.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            Log.e(TAG,"localAccountEntity is null");
        }
    }

    static  class ItemView{
        public ImageView icon;
        public TextView title;
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
