package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.Adapter.LineChartItem;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WeightEntity;
import com.xujun.util.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 15/6/15.
 */
public class ChartLFragment extends SherlockFragment {

    public static final String TAG = "ChartLFragment";

    private View mContentView;

    private Context mContext;
    private AppContext appContext;

    private ListView mListView;

    private boolean             isEdit=false;

    private AppConfig appConfig;

    private List<WeightEntity>  datas=new ArrayList<WeightEntity>();
    private List<LineChartItem> items=new ArrayList<LineChartItem>();

    private AccountEntity localAccountEntity=null;

    private DatabaseHelper databaseHelper;


    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    public void setLocalAccountEntity(AccountEntity accountEntity){
        Log.e(TAG,"setLocalAccountEntity()...");
        localAccountEntity=accountEntity;
    }

    public AccountEntity getLocalAccountEntity(){
        return localAccountEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        mContentView=inflater.inflate(R.layout.list_frame,null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick  " + i);
            }
        });
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        appConfig=AppConfig.getAppConfig(mContext);
        getSherlockActivity().getActionBar().setTitle("图表汇总");
        getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getSherlockActivity().getActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
        if (localAccountEntity!=null){
            refreshData();
        }
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    public void refreshData(){
        load();
    }

    private void load(){
        loadData();
        for (int i=0;i<8;i++){
            items.add(new LineChartItem(generateDataLine(i),appContext));
        }
        if (mListView!=null) {
            mListView.setAdapter(new ChartDataAdapter(appContext, items));
        }
    }

    public void loadData(){
        datas.clear();
        try {
            Dao<WeightEntity, Integer> weightEntities = getDatabaseHelper().getWeightEntityDao();
            QueryBuilder<WeightEntity, Integer> queryBuilder = weightEntities.queryBuilder();
            queryBuilder.where().eq("aid", localAccountEntity.getId());
            queryBuilder.orderBy("pickTime", false);
            List<WeightEntity> list = queryBuilder.query();
            for (int i = 0; i < list.size()&&i<7; i++) {
                WeightEntity entity = list.get(i);
                datas.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LineData generateDataLine(int type){
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        WeightEntity entity;
        int count=datas.size();
        String date="";
        float value=0.0f;
        for (int i = 0; i < count; i++) {
            entity=datas.get(i);
            if (entity!=null) {
                date=entity.getPickTime();
                xVals.add(DateUtil.getWeekForDate(date));
                switch (type){
                    case 0:
                        value=Float.parseFloat(entity.getBmi());
                        break;
                    case 1:
                        value=Float.parseFloat(entity.getWeight());
                        break;
                    case 2:
                        value=Float.parseFloat(entity.getFat());
                        break;
                    case 3:
                        value=Float.parseFloat(entity.getSubFat());
                        break;
                    case 4:
                        value=Float.parseFloat(entity.getVisFat());
                        break;
                    case 5:
                        value=Float.parseFloat(entity.getWater());
                        break;
                    case 6:
                        value=Float.parseFloat(entity.getBMR());
                        break;
                    case 7:
                        value=Float.parseFloat(entity.getBodyAge());
                        break;
                    case 8:
                        value=Float.parseFloat(entity.getMuscle());
                        break;
                    case 9:
                        value=Float.parseFloat(entity.getBone());
                        break;
                    default:
                        value=Float.parseFloat(entity.getProtein());
                        break;
                }
                yVals.add(new Entry(value,i));
            }
        }
        LineDataSet d1=new LineDataSet(yVals,appConfig.getTargetType(type));
        d1.setLineWidth(1.0f);
        d1.setCircleSize(2.5f);
        d1.setHighLightColor(Color.GREEN);
        d1.setDrawValues(false);

        ArrayList<LineDataSet> sets=new ArrayList<LineDataSet>();
        sets.add(d1);
        LineData cd=new LineData(xVals,sets);
        return cd;
    }


    private class ChartDataAdapter extends ArrayAdapter<LineChartItem>{

        public ChartDataAdapter(Context context,List<LineChartItem> objs){
            super(context,0,objs);
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            return getItem(position).getView(position,convertView,getContext());
        }

        @Override
        public int getItemViewType(int position){
            return 1;
        }

        @Override
        public int getViewTypeCount(){
            return 3;
        }
    }



}
