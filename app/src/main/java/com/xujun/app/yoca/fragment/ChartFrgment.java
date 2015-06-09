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
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.charting.charts.LineChart;
import com.xujun.charting.data.Entry;
import com.xujun.charting.data.LineData;
import com.xujun.charting.data.LineDataSet;
import com.xujun.charting.interfaces.OnChartGestureListener;
import com.xujun.charting.interfaces.OnChartValueSelectedListener;
import com.xujun.charting.utils.LimitLine;
import com.xujun.charting.utils.XLabels;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
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
        Log.e(TAG,"onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView()");
        mContentView=inflater.inflate(R.layout.layout_chart,null);

        mContentView.findViewById(R.id.btnChartWeek).setOnClickListener(this);
        mContentView.findViewById(R.id.btnChartMonth).setOnClickListener(this);
        mContentView.findViewById(R.id.btnChartYear).setOnClickListener(this);


        mChart=(LineChart)mContentView.findViewById(R.id.lineChart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setStartAtZero(true);
        mChart.setDrawYValues(true);
        mChart.setDrawBorder(true);
        mChart.setDrawGridBackground(false);
        mChart.setDrawHorizontalGrid(true);
        mChart.setDrawVerticalGrid(true);
        mChart.setDrawXLabels(true);
        mChart.getXLabels().setPosition(XLabels.XLabelPosition.BOTTOM);


        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        if (localAccountEntity!=null){
            loadData();
            initData();
        }
        return mContentView;
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

        ((TextView)mContentView.findViewById(R.id.tvMValue)).setText(StringUtil.doubleToStringOne(minValue));
        ((TextView)mContentView.findViewById(R.id.tvAValue)).setText(StringUtil.doubleToStringOne(averageValue));
        ((TextView)mContentView.findViewById(R.id.tvHValue)).setText(StringUtil.doubleToStringOne(maxValue));
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
                    entity.setTargetValue(rs[1]);
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
                xVals.add(DateUtil.getWeekForDate(date));
                yVals.add(new Entry(Float.parseFloat(entity.getTargetValue()),i));
            }
        }
//        xVals.add("0");
//        yVals.add(new Entry(0,count));
        String title=appConfig.getTargetType(nTargetType)+"  单位:"+appConfig.getTargetTypeUnit(nTargetType);
        if (nTargetType==0||nTargetType==4){
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
    public void onValueSelected(Entry e, int dataSetIndex) {
        Log.e(TAG,"onValueSelected =>"+e.getVal()+"  ");
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChartWeek:
            {
                setChartType(1);
                break;
            }
            case R.id.btnChartMonth:
            {
                setChartType(2);
                break;
            }
            case R.id.btnChartYear:
            {
                setChartType(3);
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
}
