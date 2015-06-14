package com.xujun.app.yoca;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Highlight;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.Adapter.TabAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.util.StringUtil;
import com.xujun.widget.HorizontalListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xujunwu on 14/12/29.
 */
public class ChartActivity extends SherlockActivity implements SeekBar.OnSeekBarChangeListener,OnChartGestureListener,OnChartValueSelectedListener{

    private static final String TAG = "ChartFragment";

    private Context mContext;
    private AppContext appContext;
    private AppConfig   appConfig;

    private DatabaseHelper databaseHelper;

    private AccountEntity localAccountEntity=null;

    private LineChart mChart;

    private int            nTargetType=0;
    private List<HealthEntity>      items=new ArrayList<HealthEntity>();

    private double                  minValue=0.0;
    private double                  maxValue=0.0;
    private double                  averageValue=0.0;

    private HorizontalListView      tabListView;
    private TabAdapter              tabAdapter;
    private List<HealthEntity>      tabLists=new ArrayList<HealthEntity>();

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    public AccountEntity getAccountEntity(){
        return localAccountEntity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chart);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();

        appConfig=AppConfig.getAppConfig(mContext);
        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        nTargetType=getIntent().getIntExtra("targetType",0);
        getActionBar().setTitle(getResources().getString(R.string.char_target_title));

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        mChart=(LineChart)findViewById(R.id.lineChart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        XAxis xAxis=mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis=mChart.getAxisLeft();
        leftAxis.setLabelCount(5);
        YAxis rightAxis=mChart.getAxisRight();
        rightAxis.setLabelCount(5);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(true);

        initBottomTabbar();

        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG,"............");
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBottomTabbar(){
        for (int i=0;i<8;i++) {
            HealthEntity entity = new HealthEntity();
            entity.setTargetType(i + 1);
            tabLists.add(entity);
        }
        tabListView=(HorizontalListView)findViewById(R.id.lv_tab);
        tabAdapter=new TabAdapter(mContext,tabLists,R.layout.tab_item);
        tabListView.setAdapter(tabAdapter);
        tabListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tabAdapter.setCurrentIndex(i);
                tabAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData()
    {
        mChart.setBackgroundColor(getResources().getColor(R.color.chart_background_color));
        mChart.setGridBackgroundColor(Color.GRAY);
        mChart.setHighlightEnabled(true);
        mChart.setDescription("");
        refreshData();
        mChart.animateX(1500);

        ((TextView)findViewById(R.id.tvMValue)).setText(StringUtil.doubleToStringOne(minValue));
        ((TextView)findViewById(R.id.tvAValue)).setText(StringUtil.doubleToStringOne(averageValue));
        ((TextView)findViewById(R.id.tvHValue)).setText(StringUtil.doubleToStringOne(maxValue));
    }

    public void loadData(){
        items.clear();
        try{
            Dao<HealthEntity,Integer> healthDao=getDatabaseHelper().getHealthDao();
            Log.e(TAG, "health all size=" + healthDao.queryForAll().size());
            QueryBuilder<HealthEntity, Integer> queryBuilder = healthDao.queryBuilder();
            queryBuilder.where().eq("accountId",localAccountEntity.getId()).and().eq("targetType",nTargetType);
            List<HealthEntity> healthEntities=queryBuilder.query();
            items.addAll(healthEntities);
            GenericRawResults<String[]> rawResults=healthDao.queryRaw("select count(*),min(targetValue),max(targetValue),sum(targetValue) from t_health where accountId="+localAccountEntity.getId()+" and  targetType="+nTargetType);
            List<String[]> results=rawResults.getResults();
            if (results.size()>0){
                String[] resultArray = results.get(0);
                minValue=Double.parseDouble(resultArray[1]);
                maxValue=Double.parseDouble(resultArray[2]);
                averageValue=Double.parseDouble(resultArray[3])/Integer.parseInt(resultArray[0]);
                Log.e(TAG,""+resultArray[0]+"  "+resultArray[1]+"  "+resultArray[2]+"  "+resultArray[3]);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        initData();
    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean hasFocus) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void refreshData()
    {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        HealthEntity entity;
        int count=items.size();
        String date="";
        for (int i = 0; i < count; i++) {
            entity=items.get(i);
            if (entity!=null) {
                date=entity.getPickTime();
                xVals.add(date.substring(date.indexOf("-")+1));
                yVals.add(new Entry(Float.parseFloat(entity.getTargetValue()),i));
            }
        }
        xVals.add("0");
        yVals.add(new Entry(0,count));
        String title=AppConfig.getAppConfig(mContext).getTargetType(nTargetType)+"  单位:"+AppConfig.getAppConfig(mContext).getTargetTypeUnit(nTargetType);
        LineDataSet set1 = new LineDataSet(yVals, title);
        set1.setColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleColor(getResources().getColor(R.color.chart_line_color));
        set1.setLineWidth(2f);
        set1.setHighLightColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setDrawCubic(true);
        set1.setValueTextColor(Color.WHITE);
        set1.setDrawValues(true);
        set1.setFillColor(getResources().getColor(R.color.chart_line_color));
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        // set data
        mChart.setData(data);
    }

    private float getRefer(){
        float result=0.0f;
        switch (nTargetType){
            case 0:
            {
                result=appConfig.getBMIRefer();
                break;
            }
            case 1:
            {
                if (localAccountEntity.getSex()==0){
                    result=localAccountEntity.getHeight()-100;
                }else{
                    result=localAccountEntity.getHeight()-105;
                }
                break;
            }
            case 2:
            {
                result=appConfig.getFatRefer(localAccountEntity.getSex(),localAccountEntity.getAge());
                break;
            }
            case 3:
            {
                result=appConfig.getSubFatRefer(localAccountEntity.getSex());
                break;
            }
            case 4:
            {
                result=appConfig.getVisFatRefer();
                break;
            }
            case 5:
            {
                result=appConfig.getWaterRefer(localAccountEntity.getSex());
                break;
            }
            case 6:
            {
                result=appConfig.getBMRRefer(localAccountEntity.getSex(),localAccountEntity.getAge());
                break;
            }
            case 8:{
                 result=appConfig.getMuscleRefer(localAccountEntity.getSex(),localAccountEntity.getHeight());
                break;
            }
            case 9:{
                result=appConfig.getBoneRefer(localAccountEntity.getSex(),Double.parseDouble(localAccountEntity.getWeight()));
                break;
            }
        }
        return result;
    }


}
