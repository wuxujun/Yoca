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
import android.widget.Button;
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
import com.xujun.app.yoca.ChartDActivity;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在用图表
 * Created by xujunwu on 15/6/15.
 */
public class ChartLFragment extends BaseFragment implements View.OnClickListener{

    public static final String TAG = "ChartLFragment";


    private Button              btnChartDay;
    private Button              btnChartWeek;
    private Button              btnChartMonth;
    private Button              btnChartYear;

    private boolean             isEdit=false;

    private AppConfig appConfig;

    private List<WeightEntity>  datas=new ArrayList<WeightEntity>();
    private List<LineChartItem> items=new ArrayList<LineChartItem>();
    private List<TargetEntity>  targets=new ArrayList<TargetEntity>();
    private float               targetTotal;

    private ChartDataAdapter adapter;

    private AccountEntity localAccountEntity=null;

    private int           dataType=1;

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
        mContentView=inflater.inflate(R.layout.chart_list_frame,null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick  " + i);
                Intent intent=new Intent(getSherlockActivity(), ChartDActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account",localAccountEntity);
                bundle.putInt("targetType",i);
                intent.putExtras(bundle);
                getSherlockActivity().startActivity(intent);
            }
        });
        btnChartDay=(Button)mContentView.findViewById(R.id.btnChartDay);
        btnChartDay.setOnClickListener(this);
        btnChartWeek=(Button)mContentView.findViewById(R.id.btnChartWeek);
        btnChartWeek.setOnClickListener(this);
        btnChartMonth=(Button)mContentView.findViewById(R.id.btnChartMonth);
        btnChartMonth.setOnClickListener(this);
        btnChartYear=(Button)mContentView.findViewById(R.id.btnChartYear);
        btnChartYear.setOnClickListener(this);
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConfig=AppConfig.getAppConfig(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.e(TAG, "onResume()");
        if (localAccountEntity!=null){
            refreshData();
        }
    }

    public void refreshData(){
        load();
    }

    private void load(){
        loadData();

        items.clear();
        for (int i=0;i<targets.size();i++){
            TargetEntity entity=targets.get(i);
            LineData lineData=generateDataLine(entity.getType());
            entity.setContent(StringUtil.doubleToStringOne(targetTotal/7.0));
            items.add(new LineChartItem(entity,lineData,appContext));
            targetTotal=0f;
        }
        if (mListView!=null) {
            adapter=new ChartDataAdapter(appContext,items);
            mListView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
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
            ComparatorWeight comparatorWeight=new ComparatorWeight();
            Collections.sort(datas, comparatorWeight);
            List<TargetEntity> targetEntityList = getDatabaseHelper().getTargetInfoDao().queryBuilder().orderBy("type",true).where().notIn("type",0).query();
            if (targetEntityList.size()>0) {
                targets.addAll(targetEntityList);
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
                if (i==0){
                    xVals.add(DateUtil.getMonthForDate(date));
                }else {
                    xVals.add(DateUtil.getDayForDate(date));
                }
                switch (type){
                    case 2:
                        value=Float.parseFloat(entity.getBmi());
                        targetTotal+= Float.parseFloat(entity.getBmi());
                        break;
                    case 1:
                        value=Float.parseFloat(entity.getWeight());
                        targetTotal+= Float.parseFloat(entity.getWeight());
                        break;
                    case 3:
                        value=Float.parseFloat(entity.getFat());
                        targetTotal+= Float.parseFloat(entity.getFat());
                        break;
                    case 4:
                        value=Float.parseFloat(entity.getSubFat());
                        targetTotal+= Float.parseFloat(entity.getSubFat());
                        break;
                    case 5:
                        value=Float.parseFloat(entity.getVisFat());
                        targetTotal+= Float.parseFloat(entity.getVisFat());
                        break;
                    case 7:
                        value=Float.parseFloat(entity.getWater());
                        targetTotal+= Float.parseFloat(entity.getWater());
                        break;
                    case 6:
                        value=Float.parseFloat(entity.getBMR());
                        targetTotal+= Float.parseFloat(entity.getBMR());
                        break;
                    case 11:
                        value=Float.parseFloat(entity.getBodyAge());
                        targetTotal+= Float.parseFloat(entity.getBodyAge());
                        break;
                    case 8:
                        value=Float.parseFloat(entity.getMuscle());
                        targetTotal+= Float.parseFloat(entity.getMuscle());
                        break;
                    case 9:
                        value=Float.parseFloat(entity.getBone());
                        targetTotal+= Float.parseFloat(entity.getBone());
                        break;
                    case 10:
                        value=Float.parseFloat(entity.getProtein());
                        targetTotal+= Float.parseFloat(entity.getProtein());
                        break;
                    default:
                        value=Float.parseFloat(entity.getSholai());
                        targetTotal+=Float.parseFloat(entity.getSholai());
                        break;
                }
                yVals.add(new Entry(value,i));
            }
        }
        LineDataSet d1=new LineDataSet(yVals,appConfig.getTargetType(type));
        d1.setLineWidth(2.0f);
        d1.setCircleSize(3.5f);
        d1.setHighLightColor(Color.WHITE);
        d1.setFillColor(Color.WHITE);
        d1.setCircleColor(Color.WHITE);
        d1.setColor(Color.WHITE);
        d1.setDrawValues(false);

        ArrayList<LineDataSet> sets=new ArrayList<LineDataSet>();
        sets.add(d1);
        LineData cd=new LineData(xVals,sets);
        return cd;
    }

    @Override
    public void onClick(View view) {
        resetButtonNormal();
        switch (view.getId()){
            case R.id.btnChartDay:{
                dataType=0;
                btnChartDay.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_left));
                break;
            }
            case R.id.btnChartWeek:{
                dataType=1;
                btnChartWeek.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                break;
            }
            case R.id.btnChartMonth:{
                dataType=2;
                btnChartMonth.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                break;
            }
            case R.id.btnChartYear:{
                dataType=3;
                btnChartYear.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_right));
                break;
            }
        }
        load();
    }

    private void resetButtonNormal(){
        btnChartDay.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_bg_left));
        btnChartWeek.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.item_background));
        btnChartMonth.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.item_background));
        btnChartYear.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_bg_right));
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

    class ComparatorWeight implements Comparator{

        @Override
        public int compare(Object o, Object t1) {
            WeightEntity entity=(WeightEntity)o;
            WeightEntity entity1=(WeightEntity)t1;
            int flag=entity.getPickTime().compareTo(((WeightEntity) t1).getPickTime());
            return flag;
        }
    }

}
