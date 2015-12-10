package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.utils.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xujun.app.yoca.Adapter.LineChartItem;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.AvatarEditAvtivity;
import com.xujun.app.yoca.ChartDActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.widget.PopPickTime;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;
import com.xujun.widget.RadiusImageView;

import java.lang.annotation.Target;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private List<String>        dayDatas=new ArrayList<String>();

    private List<HealthEntity>  datas=new ArrayList<HealthEntity>();
    private List<LineChartItem> items=new ArrayList<LineChartItem>();
    private List<TargetEntity>  targets=new ArrayList<TargetEntity>();
    private float               targetTotal;

    private List<WeightHisEntity> dayItems=new ArrayList<WeightHisEntity>();

    private ChartDataAdapter adapter;

    private AccountEntity localAccountEntity=null;

    private int           dataType=1;
    private LinearLayout        mDayLinearLayout;
    private TextView            mDayTV;
    private ListView            mDayListView;
    private ItemAdapter         mDayAdapter;

    private PopPickTime         popPickTime;
    private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    private String              queryDate=df.format(new Date());

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public void setLocalAccountEntity(AccountEntity accountEntity){
        localAccountEntity=accountEntity;
    }

    public AccountEntity getLocalAccountEntity(){
        return localAccountEntity;
    }

    AdapterView.OnItemClickListener popDateItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (popPickTime!=null){
                popPickTime.dismiss();
                queryDate=dayDatas.get(i);
                mDayTV.setText(queryDate);
                refreshData();
            }
        }
    };

    private AdapterView.OnItemClickListener onDayItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            WeightHisEntity entity=dayItems.get(i);
            if (entity!=null){
                Intent intent=new Intent(getSherlockActivity(), AvatarEditAvtivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account", localAccountEntity);
                bundle.putString("dataTime", entity.getPickTime());
                bundle.putLong("weightId", entity.getWid());
                intent.putExtras(bundle);
                getSherlockActivity().startActivity(intent);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView=inflater.inflate(R.layout.chart_list_frame,null);
        mDayLinearLayout=(LinearLayout)mContentView.findViewById(R.id.llDayData);
        mDayTV=(TextView)mContentView.findViewById(R.id.tvDay);
        mContentView.findViewById(R.id.llPickTime).setOnClickListener(this);
        mDayTV.setText(queryDate);
        mDayListView=(ListView)mContentView.findViewById(R.id.listDay);
        if (mDayListView!=null){
            mDayListView.setAdapter(mDayAdapter);
        }
        mDayListView.setOnItemClickListener(onDayItemClickListener);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (dataType>0) {
                    TargetEntity entity = targets.get(i);
                    Intent intent = new Intent(getSherlockActivity(), ChartDActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("account", localAccountEntity);
                    bundle.putInt("targetType", entity.getType());
                    bundle.putInt("dataType", dataType);
                    intent.putExtras(bundle);
                    getSherlockActivity().startActivity(intent);
                }
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
        mDayAdapter=new ItemAdapter();
        popPickTime=new PopPickTime(mContext,appContext,dayDatas);
        popPickTime.setOnItemClickListener(popDateItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        if(StringUtil.isEmpty(appConfig.get(AppConfig.CONF_CHART_TYPE))){
            dataType=1;
        }else {
            dataType = Integer.parseInt(appConfig.get(AppConfig.CONF_CHART_TYPE));
        }
        if (localAccountEntity!=null){
            refreshData();
        }
        setHeadButtonStatus();
    }

    private void setHeadButtonStatus(){
        resetButtonNormal();
        switch (dataType){
            case 0:
                btnChartDay.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_left));
                break;
            case 1:
                btnChartWeek.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                break;
            case 2:
                btnChartMonth.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                break;
            case 3:
                btnChartYear.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_right));
                break;
        }
    }

    public void refreshData(){
        if (dataType>0) {
            mDayLinearLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            load();
        }else{
            mDayLinearLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            dayItems.clear();
            try {
                Dao<WeightHisEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                if (dayDatas.size()==0) {
                    GenericRawResults<String[]> rawResults = weightEntityDao.queryRaw("select pickTime,count(1) from t_weight_his where aid=" + localAccountEntity.getId() + "  group by pickTime order by pickTime desc");
                    List<String[]> results = rawResults.getResults();
                    if (results.size() > 0) {
                        dayDatas.clear();
                        queryDate = results.get(0)[0];
                        for (int j = 0; j < results.size(); j++) {
                            String[] resultArray = results.get(j);
                            dayDatas.add(resultArray[0]);
                        }
                    }
                    mDayTV.setText(queryDate);
                }

                QueryBuilder<WeightHisEntity, Integer> queryBuilder = weightEntityDao.queryBuilder();
                queryBuilder.where().eq("aid", localAccountEntity.getId()).and().eq("pickTime",queryDate);
                queryBuilder.orderBy("wid", false);
                List<WeightHisEntity> list = queryBuilder.query();
                for (int i = 0; i < list.size(); i++) {
                    WeightHisEntity entity = list.get(i);
                    dayItems.add(entity);
                }
//                ComparatorWeight comparatorWeight=new ComparatorWeight();
//                Collections.sort(dayItems, comparatorWeight);
                mDayAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void load(){
        loadData();
        items.clear();
        int total=datas.size();
        for (int i=0;i<targets.size();i++){
            TargetEntity entity=targets.get(i);
            LineData lineData=generateDataLine(entity.getType());
            if (dataType==1) {
                entity.setContent(StringUtil.doubleToStringOne(targetTotal / total));
            }else if(dataType==2){
                entity.setContent(StringUtil.doubleToStringOne(targetTotal / total));
            }else{
                entity.setContent(StringUtil.doubleToStringOne(targetTotal / total));
            }
            items.add(new LineChartItem(entity,lineData,getXVals(),appContext));
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
        targets.clear();
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
            ComparatorHealth comparatorHealth=new ComparatorHealth();
            Collections.sort(datas, comparatorHealth);
            List<TargetEntity> targetEntityList = getDatabaseHelper().getTargetInfoDao().queryBuilder().orderBy("type",true).where().notIn("type",0).query();
            if (targetEntityList.size()>0) {
                targets.addAll(targetEntityList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                if (i==0){
                    if (dataType==3){
                        xVals.add(date);
                    }else {
                        xVals.add(DateUtil.getMonthForDate(date));
                    }
                }else {
                    if (dataType==3){
                        xVals.add(DateUtil.getYearForMonth(date));
                    }else {
                        xVals.add(DateUtil.getDayForDate(date));
                    }
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
        d1.setCircleSize(3.0f);
        d1.setDrawCubic(true);
        d1.setCubicIntensity(0.2f);
//        d1.setDrawCircles(false);
        d1.setHighLightColor(Color.WHITE);
        d1.setFillColor(Color.WHITE);
        d1.setFillAlpha(100);
        d1.setDrawHorizontalHighlightIndicator(true);
//        d1.setFillFormatter(new FillFormatter() {
//            @Override
//            public float getFillLinePosition(LineDataSet dataSet, LineDataProvider dataProvider) {
//                return -5;
//            }
//        });
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
    public void onClick(View view) {
        if (view.getId()==R.id.llPickTime){
            popPickTime.showAsDropDown(view);
        }else {
            resetButtonNormal();
            mDayLinearLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            switch (view.getId()) {
                case R.id.btnChartDay: {
                    appConfig.set(AppConfig.CONF_CHART_TYPE, "0");
                    dataType = 0;
                    btnChartDay.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_left));
                    mListView.setVisibility(View.GONE);
                    mDayLinearLayout.setVisibility(View.VISIBLE);
                    refreshData();
                    break;
                }
                case R.id.btnChartWeek: {
                    appConfig.set(AppConfig.CONF_CHART_TYPE, "1");
                    dataType = 1;
                    btnChartWeek.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                    load();
                    break;
                }
                case R.id.btnChartMonth: {
                    appConfig.set(AppConfig.CONF_CHART_TYPE, "2");
                    dataType = 2;
                    btnChartMonth.setBackgroundColor(getSherlockActivity().getResources().getColor(R.color.btn_color_selected));
                    load();
                    break;
                }
                case R.id.btnChartYear: {
                    appConfig.set(AppConfig.CONF_CHART_TYPE, "3");
                    dataType = 3;
                    btnChartYear.setBackground(getSherlockActivity().getResources().getDrawable(R.drawable.header_tab_right));
                    load();
                    break;
                }
            }
        }
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

    class ComparatorHealth implements Comparator{

        @Override
        public int compare(Object o, Object t1) {
            HealthEntity entity=(HealthEntity)o;
            HealthEntity entity1=(HealthEntity)t1;
            int flag=entity.getPickTime().compareTo(((HealthEntity) t1).getPickTime());
            return flag;
        }
    }

    class ComparatorWeight implements Comparator{

        @Override
        public int compare(Object o, Object t1) {
            WeightHisEntity entity=(WeightHisEntity)o;
            WeightHisEntity entity1=(WeightHisEntity)t1;
            int flag=entity.getPickTime().compareTo(((WeightHisEntity) t1).getPickTime());
            return flag;
        }
    }

    static  class ItemView{
        public LinearLayout llItem;
        public ImageView icon;
        public TextView time;
        public TextView sholai;
        public TextView title;
    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dayItems.size();
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

            final ItemView holder;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.item_chart_day,null);
                holder=new ItemView();
                holder.llItem=(LinearLayout)convertView.findViewById(R.id.llItem);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivIcon);
                holder.time=(TextView)convertView.findViewById(R.id.tvTime);
                holder.sholai=(TextView)convertView.findViewById(R.id.tvSholai);
                holder.title=(TextView)convertView.findViewById(R.id.tvItemValue);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            switch (i%3){
                case 0:
                    holder.llItem.setBackgroundResource(R.drawable.chart_item_normal0);
                    break;
                case 1:
                    holder.llItem.setBackgroundResource(R.drawable.chart_item_normal1);
                    break;
                case 2:
                    holder.llItem.setBackgroundResource(R.drawable.chart_item_normal2);
                    break;
            }

            WeightHisEntity entity=dayItems.get(i);
            if (entity!=null) {
                holder.time.setText(DateUtil.getTimeString(entity.getAddtime()));
                if (!StringUtil.isEmpty(entity.getAvatar())){
                    holder.icon.setVisibility(View.VISIBLE);
                    if (ImageUtils.isFileExist(appContext.getCameraPath() + "/crop_" + entity.getAvatar())) {
                        holder.icon.setImageBitmap(ImageUtils.getRoundedCornerBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + entity.getAvatar()),20,10));
                    }else{
                        ImageLoader.getInstance().loadImage(URLs.IMAGE_URL + entity.getAvatar(), options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
                                Bitmap bitmap= ImageUtils.zoomBitmap(loadedImage,holder.icon.getWidth(),holder.icon.getHeight());
                                holder.icon.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, 20,10));
                            }
                        });
//                        ImageLoader.getInstance().displayImage(URLs.IMAGE_URL + entity.getAvatar(),holder.icon,options);

                    }

                }else{
                    holder.icon.setVisibility(View.INVISIBLE);
                }
                if (localAccountEntity!=null){
                    holder.sholai.setText("Sholai指数:" + appConfig.getSholaiValue(localAccountEntity, entity));
                    int suc=appConfig.getSholaiSuc(localAccountEntity,entity);
                    holder.title.setText(suc+"项合格  "+(8-suc)+" 项不合格");

                }
            }
            return convertView;
        }
    }

}
