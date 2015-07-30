package com.xujun.app.yoca;

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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 7/16/15.
 */
public class HistoryDActivity extends BaseActivity{

    public static final String TAG = "HistoryDActivity";
    private AccountEntity localAccountEntity;
    private int             targetType;
    private String          targetTypeUnit;
    private AppConfig       appConfig;

    private List<WeightHisEntity> items=new ArrayList<WeightHisEntity>();
    private ItemAdapter     adapter;

    private boolean         isEdit=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);
        appConfig=AppConfig.getAppConfig(mContext);

        mHeadTitle.setText(getText(R.string.chart_history));
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mHeadButton.setText(getText(R.string.btn_Edit));
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit=!isEdit;
                adapter.notifyDataSetChanged();
                if (isEdit){
                    mHeadButton.setText(getText(R.string.btn_main_done));
                }else{
                    mHeadButton.setText(getText(R.string.btn_Edit));
                }
            }
        });

        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        targetType=getIntent().getIntExtra("targetType",0);
        adapter=new ItemAdapter();
        mListView=(ListView)findViewById(R.id.lvList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }
    private void loadData(){
        items.clear();
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
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            Log.e(TAG,"localAccountEntity is null");
        }
        adapter.notifyDataSetChanged();
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
                if (!targetTypeUnit.equals("0")) {
                    holder.unit.setText(targetTypeUnit);
                }else{
                    holder.unit.setText("");
                }
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
            adapter.notifyDataSetChanged();
        }
    }
}
