package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.sqlite.ConfigEntity;
import com.xujun.sqlite.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xujunwu on 15/4/27.
 */
public class SelectDialog extends SherlockActivity implements View.OnClickListener{

    private Context mContext;
    private AppContext appContext;

    private List<ConfigEntity>  configEntityList=new ArrayList<ConfigEntity>();

    private TextView            mTitleView;
    private Button              mCancelBtn;

    private int                 dataType;


    private ItemAdapter      mAdapter;
    private ListView         mListView;

    private DatabaseHelper  databaseHelper;
    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(SelectDialog.this);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.select_dialog);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        dataType=getIntent().getIntExtra("dataType",0);

        mCancelBtn=(Button)findViewById(R.id.btn_select_dialog_cancel);
        mCancelBtn.setOnClickListener(this);
        mTitleView=(TextView)findViewById(R.id.tv_select_dialog_title);
        mTitleView.setText("请选择周");
        if (dataType==1){
            mTitleView.setText("请选择月份");
        }
        initListView();
        loadData();
    }

    public void onDestroy(){
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    private void initListView(){
        mAdapter=new ItemAdapter();
        mListView=(ListView)findViewById(R.id.lv_select);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConfigEntity entity=configEntityList.get(i);
                if (entity!=null){
                    Intent intent=new Intent();
                    intent.putExtra("beginDay",entity.getBeginDay());
                    intent.putExtra("endDay",entity.getEndDay());
                    SelectDialog.this.setResult(RESULT_OK,intent);
                    SelectDialog.this.finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_select_dialog_cancel:{
                finish();
                break;
            }
        }
    }

    private void loadData(){
        try{
            Calendar c=Calendar.getInstance();
            Log.e("......","............."+c.get(Calendar.WEEK_OF_YEAR));
            int week=c.get(Calendar.WEEK_OF_YEAR);
            if (dataType==1){
                week=c.get(Calendar.MONTH)+1;
            }
            configEntityList.clear();
            Dao<ConfigEntity,Integer> dao=getDatabaseHelper().getConfigDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select title,week,beginDay,endDay from t_config where type="+dataType+" and week<="+week+" order by week desc");
            List<String[]> results=rawResults.getResults();
            if (results.size()>0) {
                for (int i=0;i<results.size();i++){
                    String[] rs=results.get(i);
                    ConfigEntity entity=new ConfigEntity();
                    entity.setTitle(rs[0]);
                    entity.setWeek(Integer.parseInt(rs[1]));
                    entity.setBeginDay(rs[2]);
                    entity.setEndDay(rs[3]);
                    configEntityList.add(entity);
                }
            }

//            QueryBuilder<ConfigEntity,Integer> queryBuilder=dao.queryBuilder();
//            queryBuilder.where().eq("type",0).and().eq("week",week);
//            PreparedQuery<ConfigEntity> preparedQuery=queryBuilder.prepare();
//            List<ConfigEntity> lists=dao.query(preparedQuery);
//            if (lists.size()>0){
//                configEntityList.addAll(lists);
//            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }

    static class ListItemView{
        public TextView   title;
    }

    class ItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return configEntityList.size();
        }

        @Override
        public Object getItem(int i) {
            return configEntityList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ListItemView listItemView=null;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.select_dialog_item,null);

                listItemView=new ListItemView();
                listItemView.title=(TextView)convertView.findViewById(R.id.tvTitle);
                convertView.setTag(listItemView);
            }else {
                listItemView=(ListItemView)convertView.getTag();
            }
            ConfigEntity entity=configEntityList.get(position);
            if (entity!=null){
                listItemView.title.setText(entity.getTitle());
            }

            return convertView;
        }
    }
}
