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
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.util.DateUtil;

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
    private List<WeightEntity>  datas=new ArrayList<WeightEntity>();
    private List<LineChartItem> items=new ArrayList<LineChartItem>();



    private Button btnChartDay;
    private Button              btnChartWeek;
    private Button              btnChartMonth;
    private Button              btnChartYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_d);
        appConfig=AppConfig.getAppConfig(mContext);
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        localAccountEntity=(AccountEntity)getIntent().getSerializableExtra("account");
        targetType=getIntent().getIntExtra("targetType",0);

        mHeadTitle.setText(appConfig.getTargetType(targetType));
        mHeadButton.setVisibility(View.INVISIBLE);

        btnChartDay=(Button)findViewById(R.id.btnChartDay);
        btnChartDay.setOnClickListener(this);
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        items.clear();
        items.add(new LineChartItem(generateDataLine(targetType), appContext));

        TargetEntity targetEntity=getTargetContent(targetType);
        if (targetEntity!=null) {
            chartFooter.setUnit(targetEntity.getUnitTitle());
            chartFooter.setRemark(targetEntity.getContent());
        }
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
                if (i==0) {
                    xVals.add(DateUtil.getMonthForDate(date));
                }else{
                    xVals.add(DateUtil.getDayForDate(date));
                }
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
    public void onClick(View view) {
        resetButtonNormal();
        switch (view.getId()){
            case R.id.btnChartDay:{
                btnChartDay.setBackground(getResources().getDrawable(R.drawable.header_tab_left));
                break;
            }
            case R.id.btnChartWeek:{
                btnChartWeek.setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                break;
            }
            case R.id.btnChartMonth:{
                btnChartMonth.setBackgroundColor(getResources().getColor(R.color.btn_color_selected));
                break;
            }
            case R.id.btnChartYear:{
                btnChartYear.setBackground(getResources().getDrawable(R.drawable.header_tab_right));
                break;
            }
        }
    }

    private void resetButtonNormal(){
        btnChartDay.setBackground(getResources().getDrawable(R.drawable.header_tab_bg_left));
        btnChartWeek.setBackgroundColor(getResources().getColor(R.color.item_background));
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
            WeightEntity entity=(WeightEntity)o;
            WeightEntity entity1=(WeightEntity)t1;
            int flag=entity.getPickTime().compareTo(((WeightEntity) t1).getPickTime());
            return flag;
        }
    }
}
