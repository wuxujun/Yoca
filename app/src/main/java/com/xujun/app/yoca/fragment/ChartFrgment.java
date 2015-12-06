package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.ConfigEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xujunwu on 15/4/16.
 */
public class ChartFrgment extends SherlockFragment implements View.OnClickListener,OnChartGestureListener,OnChartValueSelectedListener {

    private final static String TAG="ChartFragment";

    private  View   mContentView;

    private Context mContext;
    private AppContext appContext;
    private AppConfig   appConfig;

    private DatabaseHelper databaseHelper;

    private AccountEntity localAccountEntity=null;

    private LineChart mChart;

    private int            nTargetType=0;
    private int             showType=0;
    private String          beginDay;
    private String          endDay;
    private List<HealthEntity> items=new ArrayList<HealthEntity>();

    private double                  minValue=0.0;
    private double                  maxValue=0.0;
    private double                  averageValue=0.0;

    private TextView            mCurrentTimeTV;
    private int                 nCurrentTimeIndex;

    private List<ConfigEntity>  configEntityList=new ArrayList<ConfigEntity>();


    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    public void setTargetType(int type){
        nTargetType=type;
    }

    public void setDataDay(int dType,String bDay,String eDay){
        showType=dType;
        beginDay=bDay;
        endDay=eDay;
    }
    public void setAccountEntity(AccountEntity accountEntity){
        localAccountEntity=accountEntity;
    }

    public AccountEntity getAccountEntity(){
        return localAccountEntity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        appConfig=AppConfig.getAppConfig(mContext);
        Log.e(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        mContentView=inflater.inflate(R.layout.layout_chart,null);

        mContentView.findViewById(R.id.btnChartWeek).setOnClickListener(this);
        mContentView.findViewById(R.id.btnChartMonth).setOnClickListener(this);
        mContentView.findViewById(R.id.btnChartYear).setOnClickListener(this);
        mContentView.findViewById(R.id.ibDateLeft).setOnClickListener(this);
        mContentView.findViewById(R.id.ibDateRight).setOnClickListener(this);
        mCurrentTimeTV=(TextView)mContentView.findViewById(R.id.tvChartDate);

        mChart=(LineChart)mContentView.findViewById(R.id.lineChart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescriptionColor(Color.WHITE);
        mChart.setNoDataText("无数据");
        XAxis xAxis=mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis=mChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis=mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(true);
        mChart.setBackgroundColor(getResources().getColor(R.color.chart_background_color));
        mChart.setGridBackgroundColor(Color.GRAY);
        mChart.setDescription("");

        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        loadConfigData();
        if (localAccountEntity!=null){
            loadData();
        }
        return mContentView;
    }

    public void loadData(){
        items.clear();
//        if (nTargetType>=7){
//            nTargetType=nTargetType+1;
//        }
        try{
            Dao<HealthEntity,Integer> healthDao=getDatabaseHelper().getHealthDao();
            GenericRawResults<String[]> genericRawResults=healthDao.queryRaw("select pickTime,targetValue from t_health where accountId="+localAccountEntity.getId()+" and  targetType="+nTargetType+"  and datetime(pickTime)>=datetime('"+beginDay+"')  and datetime(pickTime)<=datetime('"+endDay+"')");
            List<String[]> list=genericRawResults.getResults();
            Log.e(TAG,localAccountEntity.getId()+"  "+beginDay+"    "+endDay +" list size"+list.size()+"  "+list.toString());
            if (list.size()>0){
                for (int i=0;i<list.size();i++){
                    String[] rs=list.get(i);
                    HealthEntity entity=new HealthEntity();
                    entity.setPickTime(rs[0]);
//                    entity.setTargetValue(rs[1]);
                    items.add(entity);
                }
            }
//            QueryBuilder<HealthEntity, Integer> queryBuilder = healthDao.queryBuilder();
//            queryBuilder.where().eq("accountId",localAccountEntity.getId()).and().eq("targetType",nTargetType);
//            List<HealthEntity> healthEntities=queryBuilder.query();
//            items.addAll(healthEntities);
            GenericRawResults<String[]> rawResults=healthDao.queryRaw("select count(*),min(targetValue),max(targetValue),sum(targetValue) from t_health where accountId="+localAccountEntity.getId()+" and  targetType="+nTargetType);
            List<String[]> results=rawResults.getResults();
            if (results.size()>0){
                String[] resultArray = results.get(0);
                minValue=0.0;
                if (!StringUtil.isEmpty(resultArray[1])) {
                    minValue = Double.parseDouble(resultArray[1]);
                }
                maxValue=0.0;
                if (!StringUtil.isEmpty(resultArray[2])) {
                    maxValue = Double.parseDouble(resultArray[2]);
                }
                averageValue=0.0;
                if (!StringUtil.isEmpty(resultArray[3])&&(!StringUtil.isEmpty(resultArray[0]))) {
                    averageValue = Double.parseDouble(resultArray[3]) / Integer.parseInt(resultArray[0]);
                }
                Log.e(TAG,""+resultArray[0]+"  "+resultArray[1]+"  "+resultArray[2]+"  "+resultArray[3]);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        refreshData();

        ((TextView)mContentView.findViewById(R.id.tvMValue)).setText(StringUtil.doubleToStringOne(minValue));
        ((TextView)mContentView.findViewById(R.id.tvAValue)).setText(StringUtil.doubleToStringOne(averageValue));
        ((TextView)mContentView.findViewById(R.id.tvHValue)).setText(StringUtil.doubleToStringOne(maxValue));
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
//                xVals.add(date.substring(date.indexOf("-")+1));
                if (showType==0) {
                    xVals.add(DateUtil.getWeekForDate(date));
                }else if (showType==1){
                    xVals.add(DateUtil.getDayForDate(date));
                }
//                yVals.add(new Entry(Float.parseFloat(entity.getTargetValue()),i));
            }
        }
//        xVals.add("0");
//        yVals.add(new Entry(0,count));
        String title=appConfig.getTargetType(nTargetType)+"  单位:"+appConfig.getTargetTypeUnit(nTargetType);
        if (nTargetType==0||nTargetType==4||nTargetType==7){
            title=appConfig.getTargetType(nTargetType);
        }
        LineDataSet set1 = new LineDataSet(yVals, title);
        set1.setColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleColor(getResources().getColor(R.color.chart_line_color));
        set1.setLineWidth(2f);

        set1.setHighLightColor(getResources().getColor(R.color.chart_line_color));
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setDrawCubic(true);
        set1.setDrawValues(true);
        set1.setValueTextColor(Color.GREEN);
        set1.setFillColor(getResources().getColor(R.color.chart_line_color));
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
        mChart.animateX(1500);
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
            case 7:
            {
                result=0.0f;
                break;
            }
            case 8:{
                result=appConfig.getMuscleRefer(localAccountEntity.getSex(),localAccountEntity.getHeight());
                break;
            }
            case 9:{
                if (StringUtil.isEmpty(localAccountEntity.getWeight())){
                    result=0.0f;
                }else {
                    result = appConfig.getBoneRefer(localAccountEntity.getSex(), Double.parseDouble(localAccountEntity.getWeight()));
                }
                break;
            }
            case 10:
            {
                result=0.0f;
                break;
            }
        }
        return result;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.d(TAG, "onResume");
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChartWeek:
            {
                showType=0;
                setChartType(1);
                loadConfigData();
                break;
            }
            case R.id.btnChartMonth:
            {
                showType=1;
                setChartType(2);
                loadConfigData();
                break;
            }
            case R.id.btnChartYear:
            {
                showType=2;
                setChartType(3);
                loadConfigData();
                break;
            }
            case R.id.ibDateLeft:{
                nCurrentTimeIndex++;
                if (nCurrentTimeIndex>=configEntityList.size()){
                    nCurrentTimeIndex=configEntityList.size();
                }
                ConfigEntity entity=configEntityList.get(nCurrentTimeIndex);
                mCurrentTimeTV.setText(entity.getTitle());
                beginDay=entity.getBeginDay();
                endDay=entity.getEndDay();
                loadData();
                break;
            }
            case R.id.ibDateRight:{
                nCurrentTimeIndex--;
                if (nCurrentTimeIndex<0){
                    nCurrentTimeIndex=0;
                }
                ConfigEntity entity=configEntityList.get(nCurrentTimeIndex);
                mCurrentTimeTV.setText(entity.getTitle());
                beginDay=entity.getBeginDay();
                endDay=entity.getEndDay();
                loadData();
                break;
            }
        }
    }

    private void setChartType(int type){
        switch (type){
            case 1:{
                ((Button)mContentView.findViewById(R.id.btnChartWeek)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((Button)mContentView.findViewById(R.id.btnChartMonth)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)mContentView.findViewById(R.id.btnChartYear)).setTextColor(getResources().getColor(R.color.btn_color));

                mContentView.findViewById(R.id.llChartWeek).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                mContentView.findViewById(R.id.llChartMonth).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llChartYear).setBackgroundColor(getResources().getColor(R.color.btn_color));

                break;
            }
            case 2:{
                ((Button)mContentView.findViewById(R.id.btnChartWeek)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)mContentView.findViewById(R.id.btnChartMonth)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                ((Button)mContentView.findViewById(R.id.btnChartYear)).setTextColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llChartWeek).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llChartMonth).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                mContentView.findViewById(R.id.llChartYear).setBackgroundColor(getResources().getColor(R.color.btn_color));
                break;
            }
            case 3:{
                ((Button)mContentView.findViewById(R.id.btnChartWeek)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)mContentView.findViewById(R.id.btnChartMonth)).setTextColor(getResources().getColor(R.color.btn_color));
                ((Button)mContentView.findViewById(R.id.btnChartYear)).setTextColor(getResources().getColor(R.color.btn_color_selected));
                mContentView.findViewById(R.id.llChartWeek).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llChartMonth).setBackgroundColor(getResources().getColor(R.color.btn_color));
                mContentView.findViewById(R.id.llChartYear).setBackgroundColor(getResources().getColor(R.color.btn_color_selected));

                break;
            }
        }

    }


    private void loadConfigData(){
        try{
            Calendar c=Calendar.getInstance();
            Log.e(TAG,"............."+c.get(Calendar.WEEK_OF_YEAR));
            int week=c.get(Calendar.WEEK_OF_YEAR);
            if (showType==1){
                week=c.get(Calendar.MONTH)+1;
            }
            configEntityList.clear();
            Dao<ConfigEntity,Integer> dao=getDatabaseHelper().getConfigDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select title,week,beginDay,endDay from t_config where type="+showType+" and week<="+week+" order by week desc");
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
                    if (i==0){
                        mCurrentTimeTV.setText(entity.getTitle());
                    }
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
