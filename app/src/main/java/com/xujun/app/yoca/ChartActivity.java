package com.xujun.app.yoca;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.Adapter.TabAdapter;
import com.xujun.charting.charts.LineChart;
import com.xujun.charting.data.Entry;
import com.xujun.charting.data.LineData;
import com.xujun.charting.data.LineDataSet;
import com.xujun.charting.interfaces.OnChartGestureListener;
import com.xujun.charting.interfaces.OnChartValueSelectedListener;
import com.xujun.charting.utils.LimitLine;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.util.StringUtil;
import com.xujun.widget.HorizontalListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        mChart.setStartAtZero(true);
        mChart.setDrawYValues(false);
        mChart.setDrawBorder(true);
        mChart.setDrawGridBackground(false);
        mChart.setDrawHorizontalGrid(true);
        mChart.setDrawVerticalGrid(true);
        mChart.setDrawXLabels(true);
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
        mChart.setValueTextColor(getResources().getColor(R.color.chart_label_color));
        mChart.setBackgroundColor(getResources().getColor(R.color.chart_background_color));
        mChart.setGridColor(Color.GRAY);
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
    public void onValueSelected(Entry entry, int i) {

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
        set1.setFillColor(getResources().getColor(R.color.chart_line_color));
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        LimitLine ll1 = new LimitLine(getRefer());
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setDrawValue(true);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT);
        data.addLimitLine(ll1);
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

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleColor(getResources().getColor(R.color.chart_line_color));
        set1.setLineWidth(2f);
        set1.setHighLightColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setDrawCubic(true);

//        set1.setDrawCircles(false);
        set1.setFillColor(getResources().getColor(R.color.chart_line_color));

        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        LimitLine ll1 = new LimitLine(30f);
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setDrawValue(true);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT);

//        LimitLine ll2 = new LimitLine(-30f);
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setDrawValue(true);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT);

        data.addLimitLine(ll1);
//        data.addLimitLine(ll2);

        // set data
        mChart.setData(data);
    }
}
