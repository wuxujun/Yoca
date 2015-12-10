package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.Adapter.LineChartItem;
import com.xujun.app.yoca.widget.ChartController;
import com.xujun.app.yoca.widget.ChartFooter;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xujunwu on 7/16/15.
 */
public class ChartDActivity extends BaseActivity implements View.OnClickListener,ChartController{

    public static final String TAG = "ChartDActivity";
    private AccountEntity   localAccountEntity;
    private int             targetType;
    private AppConfig       appConfig;


    private ChartFooter      chartFooter;
    private ChartDataAdapter adapter;
    private List<HealthEntity>  datas=new ArrayList<HealthEntity>();
    private List<LineChartItem> items=new ArrayList<LineChartItem>();

    private HomeTargetEntity    homeTargetEntity;
    private float               targetTotla;
    private int                 dataType=1;

    private Button              btnChartWeek;
    private Button              btnChartMonth;
    private Button              btnChartYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_d);
        appConfig=AppConfig.getAppConfig(mContext);
        if (StringUtil.isEmpty(appConfig.get(AppConfig.CONF_CHART_TYPE))){
            dataType=1;
        }else {
            dataType = Integer.parseInt(appConfig.get(AppConfig.CONF_CHART_TYPE));
        }
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        targetType=getIntent().getIntExtra("targetType",0);
        dataType=getIntent().getIntExtra("dataType",1);
        mHeadTitle.setText(appConfig.getTargetType(targetType));
        mHeadButton.setVisibility(View.INVISIBLE);

        btnChartWeek=(Button)findViewById(R.id.btnChartWeek);
        btnChartWeek.setOnClickListener(this);
        btnChartMonth=(Button)findViewById(R.id.btnChartMonth);
        btnChartMonth.setOnClickListener(this);
        btnChartYear=(Button)findViewById(R.id.btnChartYear);
        btnChartYear.setOnClickListener(this);

        mListView=(ListView)findViewById(R.id.list);
        chartFooter=new ChartFooter(mContext);
        chartFooter.setChartController(this);
        mListView.addFooterView(chartFooter);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                    Intent intent=new Intent(ChartDActivity.this,HistoryDActivity.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putSerializable("account",localAccountEntity);
//                    bundle.putInt("targetType",targetType);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }
    private void loadData(){
        switch (dataType){
            case 1:
                btnChartWeek.setBackground(getResources().getDrawable(R.drawable.header_tab_left));
                break;
            case 2:
                btnChartMonth.setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                break;
            case 3:
                btnChartYear.setBackground(getResources().getDrawable(R.drawable.header_tab_right));
                break;
        }
        datas.clear();
        try {
            Dao<HealthEntity, Integer> healthEntities = getDatabaseHelper().getHealthDao();
            QueryBuilder<HealthEntity, Integer> queryBuilder = healthEntities.queryBuilder();
            queryBuilder.where().eq("aid", localAccountEntity.getId()).and().eq("dataType",dataType);
            queryBuilder.orderBy("pickTime", false);
            List<HealthEntity> list = queryBuilder.query();
            for (int i = 0; i < list.size(); i++) {
                HealthEntity entity = list.get(i);
                if (StringUtil.toDouble(entity.getWeight())>0.0) {
                   datas.add(entity);
                }
            }
            ComparatorWeight comparatorWeight=new ComparatorWeight();
            Collections.sort(datas, comparatorWeight);

            List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().where().eq("aid", localAccountEntity.getId()).and().eq("type",targetType).query();
            if (homeTargetEntityList!=null&&homeTargetEntityList.size()>0){
                homeTargetEntity=homeTargetEntityList.get(0);
                if (homeTargetEntity!=null){
                    chartFooter.setIsShow(homeTargetEntity.getIsShow());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        items.clear();
        TargetEntity targetEntity=getTargetContent(targetType);
        if (targetEntity!=null) {
            chartFooter.setUnit(targetEntity.getUnitTitle());
            chartFooter.setRemark(targetEntity.getContent());
        }
        LineData lineData=generateDataLine(targetType);
        if (targetEntity!=null&&!StringUtil.isEmpty(StringUtil.doubleToStringOne(targetTotla/datas.size()))) {
            targetEntity.setContent(StringUtil.doubleToStringOne(targetTotla /datas.size()));
        }
        items.add(new LineChartItem(targetEntity, lineData,getXVals(), appContext));

        if (mListView!=null) {
            adapter=new ChartDataAdapter(appContext,items);
            mListView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    private TargetEntity getTargetContent(int type){
        try {
            List<TargetEntity> targetEntityList = getDatabaseHelper().getTargetInfoDao().queryBuilder().where().eq("type", type).query();
            if (targetEntityList.size()>0) {
                TargetEntity entity=targetEntityList.get(0);
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String>  getXVals(){
        ArrayList<String> xVals = new ArrayList<String>();
        HealthEntity entity;
        int count=datas.size();
        String date="";
        for (int i = 0; i < count; i++) {
            entity = datas.get(i);
            if (entity != null) {
                date = entity.getPickTime();
                if (i == 0) {
                    if (dataType == 3) {
                        xVals.add(date);
                    } else {
                        xVals.add(DateUtil.getMonthForDate(date));
                    }
                } else {
                    if (dataType == 3) {
                        xVals.add(DateUtil.getYearForMonth(date));
                    } else {
                        xVals.add(DateUtil.getDayForDate(date));
                    }
                }
            }
        }
        return  xVals;
    }

    private LineData generateDataLine(int type){
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        HealthEntity entity;
        int count=datas.size();
        String date="";
        float value=0.0f;
        for (int i = 0; i < count; i++) {
            entity=datas.get(i);
            if (entity!=null) {
                date=entity.getPickTime();
                if (i==0) {
                    xVals.add(DateUtil.getMonthForDate(date));
                }else{
                    xVals.add(DateUtil.getDayForDate(date));
                }
                switch (type){
                    case 2:
                        value=Float.parseFloat(entity.getBmi());
                        targetTotla+=Float.parseFloat(entity.getBmi());
                        break;
                    case 1:
                        value=Float.parseFloat(entity.getWeight());
                        targetTotla+=Float.parseFloat(entity.getWeight());
                        break;
                    case 3:
                        value=Float.parseFloat(entity.getFat());
                        targetTotla+=Float.parseFloat(entity.getFat());
                        break;
                    case 4:
                        value=Float.parseFloat(entity.getSubFat());
                        targetTotla+=Float.parseFloat(entity.getBmi());
                        break;
                    case 5:
                        value=Float.parseFloat(entity.getVisFat());
                        targetTotla+=Float.parseFloat(entity.getVisFat());
                        break;
                    case 7:
                        value=Float.parseFloat(entity.getWater());
                        targetTotla+=Float.parseFloat(entity.getWater());
                        break;
                    case 6:
                        value=Float.parseFloat(entity.getBMR());
                        targetTotla+=Float.parseFloat(entity.getBMR());
                        break;
                    case 11:
                        value=Float.parseFloat(entity.getBodyAge());
                        targetTotla+=Float.parseFloat(entity.getBodyAge());
                        break;
                    case 8:
                        value=Float.parseFloat(entity.getMuscle());
                        targetTotla+=Float.parseFloat(entity.getMuscle());
                        break;
                    case 9:
                        value=Float.parseFloat(entity.getBone());
                        targetTotla+=Float.parseFloat(entity.getBone());
                        break;
                    case 10:
                        value=Float.parseFloat(entity.getProtein());
                        targetTotla+=Float.parseFloat(entity.getProtein());
                        break;
                }
                yVals.add(new Entry(value,i));
            }
        }
        LineDataSet d1=new LineDataSet(yVals,appConfig.getTargetType(type));
        d1.setLineWidth(2.0f);
        d1.setCircleSize(3.0f);
        d1.setDrawCubic(true);
        d1.setCubicIntensity(0.2f);
        d1.setHighLightColor(Color.WHITE);
        d1.setFillColor(Color.WHITE);
        d1.setCircleColor(Color.WHITE);
        d1.setColor(Color.WHITE);

        d1.setDrawValues(false);

//        ArrayList<LineDataSet> sets=new ArrayList<LineDataSet>();
//        sets.add(d1);
        LineData cd=new LineData();
        cd.addDataSet(d1);

        return cd;
    }

    @Override
    public void onViewHistoryClicked() {
        Intent intent=new Intent(ChartDActivity.this,HistoryDActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("account", localAccountEntity);
        bundle.putInt("targetType", targetType);
        intent.putExtras(bundle);
        startActivity(intent);
//                    finish();
    }

    @Override
    public void onTargetShow(boolean flag) {
        if (homeTargetEntity!=null){
            homeTargetEntity.setIsShow(flag?1:0);
            addHomeTargetEntity(homeTargetEntity);
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

    @Override
    public void onClick(View view) {
        resetButtonNormal();
        switch (view.getId()){
            case R.id.btnChartWeek:{
                dataType=1;
                btnChartWeek.setBackground(getResources().getDrawable(R.drawable.header_tab_left));
                break;
            }
            case R.id.btnChartMonth:{
                dataType=2;
                btnChartMonth.setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                break;
            }
            case R.id.btnChartYear:{
                dataType=3;
                btnChartYear.setBackground(getResources().getDrawable(R.drawable.header_tab_right));
                break;
            }
        }
        loadData();
    }

    private void resetButtonNormal(){
        btnChartWeek.setBackground(getResources().getDrawable(R.drawable.header_tab_bg_left));
        btnChartMonth.setBackgroundColor(getResources().getColor(R.color.item_background));
        btnChartYear.setBackground(getResources().getDrawable(R.drawable.header_tab_bg_right));
    }


    private class ChartDataAdapter extends ArrayAdapter<LineChartItem> {

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

    class ComparatorWeight implements Comparator {

        @Override
        public int compare(Object o, Object t1) {
            HealthEntity entity=(HealthEntity)o;
            HealthEntity entity1=(HealthEntity)t1;
            int flag=entity.getPickTime().compareTo(((HealthEntity) t1).getPickTime());
            return flag;
        }
    }
}
