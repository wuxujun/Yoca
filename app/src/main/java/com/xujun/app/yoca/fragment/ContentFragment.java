package com.xujun.app.yoca.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.AvatarEditAvtivity;
import com.xujun.app.yoca.DefaultActivity;
import com.xujun.app.yoca.DetailActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.TargetActivity;
import com.xujun.app.yoca.widget.ContentController;
import com.xujun.app.yoca.widget.ContentHeader;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HomeTargetEntity;
import com.xujun.sqlite.TargetEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.widget.MySeekBar;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("ValidFragment")
public class ContentFragment extends BaseFragment implements View.OnClickListener,ContentController{

    public static final String TAG = "ContentFragment";

    private LinearLayout            llContainer;
    private Animation anim_in,anim_out;
    private Handler                 mHandler;
    private int                     nIndex=0;

    private ItemAdapter             mAdapter;

    private ContentHeader           mContentHeader;


    private List<HomeTargetEntity> items=new ArrayList<HomeTargetEntity>();

    private List<String>        dayDatas=new ArrayList<String>();


    private AccountEntity           localAccountEntity=null;
    private long                     localWeightId=0;
    private double                  localWeight=0.0;

    private boolean                 bVisitor=false;

    private int                     currentDay=0;
    private SimpleDateFormat df=new SimpleDateFormat("MM-dd");
    private String                  strToday=df.format(new Date());

    private SimpleDateFormat dfDay=new SimpleDateFormat("yyyy-MM-dd");
    private String          strTodayDay=dfDay.format(new Date());

    private String          lastQueryDate=strTodayDay;

    private boolean         isTodayData=true;
    private SimpleDateFormat dfYearMonthDay=new SimpleDateFormat("yyyyMMdd");

    private int             nTargetType=0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContentView=inflater.inflate(R.layout.list_frame,null);
        mContentHeader=new ContentHeader(mContext);
        mContentHeader.setContentController(this);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);
        mListView.addHeaderView(mContentHeader);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                SherlockFragment sherlockFragment=new ChartFragment();
//                ((ChartFragment)sherlockFragment).loadData(localAccountEngity);
//                getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();

//                HomeTargetEntity entity=items.get(i);
//                nTargetType=entity.getType();
//                openChartView();
            }
        });

        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (localAccountEntity!=null){
            initHomeTarget();
        }
//        configPlatforms();
    }

    private void initHomeTarget(){
        if (localAccountEntity!=null) {
            try {
                List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().where().eq("aid", localAccountEntity.getId()).query();
                if (homeTargetEntityList.size() <= 0) {
                    Dao<TargetEntity, Integer> targetDao = getDatabaseHelper().getTargetInfoDao();
                    QueryBuilder<TargetEntity, Integer> targetQueryBuilder = targetDao.queryBuilder();
                    targetQueryBuilder.where().eq("status", 0);
                    List<TargetEntity> list = targetQueryBuilder.query();
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            HomeTargetEntity entity = new HomeTargetEntity();
                            entity.setTitle(list.get(i).getTitle());
                            entity.setType(list.get(i).getType());
                            entity.setUnit(list.get(i).getUnit());
                            entity.setAid(localAccountEntity.getId());
                            entity.setHeight(localAccountEntity.getHeight());
                            entity.setSex(localAccountEntity.getSex());
                            entity.setAge(localAccountEntity.getAge());
//                            if (list.get(i).getType()<3){
                                entity.setIsShow(1);
//                            }
                            addHomeTargetEntity(entity);
                        }
                    }
                }else{
                    for (int i1=0;i1<homeTargetEntityList.size();i1++)
                    {
                        HomeTargetEntity entity=homeTargetEntityList.get(i1);
                        if (entity!=null){
                            entity.setUnit(getTargetUnit(entity.getType()));
                            addHomeTargetEntity(entity);
                        }
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    private String getTargetUnit(int type){
        try{
            Dao<TargetEntity,Integer> dao=getDatabaseHelper().getTargetInfoDao();
            QueryBuilder<TargetEntity, Integer> targetQueryBuilder = dao.queryBuilder();
            targetQueryBuilder.where().eq("type", type);
            List<TargetEntity> list = targetQueryBuilder.query();
            if (list.size()>0){
                return ((TargetEntity)list.get(0)).getUnit();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "0";
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        loadWeightHis();
        refreshDayData();
        queryHealthData(strTodayDay);
        refreshView();

        if (mAdapter==null){
            mAdapter=new ItemAdapter();
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void loadWeightHis()
    {
        if (localAccountEntity!=null) {
//            mHeadTitle.setText(localAccountEntity.getUserNick());
            if (!StringUtil.isEmpty(localAccountEntity.getAvatar())){
//                getSherlockActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//               mHeadIcon.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar()));
            }
            try {
                Dao<WeightEntity, Integer> weightEntities = getDatabaseHelper().getWeightEntityDao();
                QueryBuilder<WeightEntity, Integer> queryBuilder = weightEntities.queryBuilder();
                queryBuilder.where().eq("aid", localAccountEntity.getId());
                queryBuilder.orderBy("pickTime", false);
                List<WeightEntity> list = queryBuilder.query();
                dayDatas.clear();
                for (int i = 0; i < list.size(); i++) {
                    WeightEntity entity = list.get(i);
                    if (i == 0 && !(entity.getPickTime().equals(strTodayDay))) {
                        dayDatas.add(strTodayDay);
                    }
                    dayDatas.add(entity.getPickTime());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadHomeTarget(){
        try {
            items.clear();
            List<HomeTargetEntity> homeTargetEntityList = getDatabaseHelper().getHomeTargetDao().queryBuilder().orderBy("type",true).where().eq("aid", localAccountEntity.getId()).and().eq("isShow",1).and().notIn("type",0).query();
            if (homeTargetEntityList.size()>0) {
                items.addAll(homeTargetEntityList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startEffect(){
        mContentHeader.startEffect();
    }

    public void RefreshHistoryData(){
        queryHealthData(strTodayDay);
    }


    private void refreshView()
    {
        mContentHeader.isShowContent(isTodayData);
       if (localAccountEntity!=null&&localAccountEntity.getTargetWeight()!=null) {
           if (!localAccountEntity.getTargetWeight().equals("0")) {
               mContentHeader.targetValue.setText(StringUtil.doubleToStringOne(Double.parseDouble(localAccountEntity.getTargetWeight())));
           }
       }

       if (localAccountEntity!=null&& !StringUtil.isEmpty(localAccountEntity.getDoneTime())&&!localAccountEntity.getDoneTime().equals("0")) {
          mContentHeader.targetDay.setText("" + DateUtil.getDayDiff(localAccountEntity.getDoneTime()));
           if (!localAccountEntity.getTargetWeight().equals("0")&&localWeight>0){
               long days=DateUtil.getDayDiff(localAccountEntity.getDoneTime());
               mContentHeader.setWeekValue((int) days / 7,localWeight,StringUtil.toDouble(localAccountEntity.getTargetWeight()));
           }
           mContentHeader.setTargetViewStatus(false);
       }else{
           mContentHeader.setTargetViewStatus(true);
       }

//        String showTarget=appContext.getProperty(AppConfig.USER_SHOW_TARGET);
//        if (StringUtil.isEmpty(showTarget)){
//            mContentHeader.getTargetView().setVisibility(View.GONE);
//        }else{
//            mContentHeader.getTargetView().setVisibility(View.GONE);
//            if (showTarget.equals("1")){
                mContentHeader.getTargetView().setVisibility(View.VISIBLE);
//            }
//        }
    }
    private void queryHealthData(String pickTime){
        if (localAccountEntity==null){
            isTodayData=false;
            return;
        }
        if (localAccountEntity!=null&&localAccountEntity.getId()==0){
            return;
        }
        AppConfig appConfig=AppConfig.getAppConfig(mContext);
        int sex=localAccountEntity.getSex();
        int age=localAccountEntity.getAge();
        int height=localAccountEntity.getHeight();
        try{
            Dao<WeightHisEntity,Integer> weightHisEntityDao=getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("aid",localAccountEntity.getId()).and().eq("pickTime",pickTime);
            weightHisQueryBuilder.orderBy("wid",false);
            WeightHisEntity weightHisEntity=weightHisQueryBuilder.queryForFirst();
            if (weightHisEntity!=null&&mContentView!=null){
                mContentHeader.setWeightValue(StringUtil.doubleToStringOne(weightHisEntity.getWeight()));
//                mContentHeader.setWeightValue("182.3");
                localWeightId=weightHisEntity.getWid();
                localWeight=weightHisEntity.getWeight();
                updateHomeTargetValue(2,StringUtil.doubleToStringOne(weightHisEntity.getBmi()), appConfig.getBMITitle(weightHisEntity.getBmi()), appConfig.getBMIStatus(weightHisEntity.getBmi()), appConfig.getBMIValue(weightHisEntity.getBmi()));
                updateHomeTargetValue(1,StringUtil.doubleToStringOne(weightHisEntity.getWeight()),appConfig.getWeightTitle(height, sex, weightHisEntity.getWeight()),appConfig.getWeightStatus(height, sex, weightHisEntity.getWeight()),appConfig.getWeightValue(height, sex, weightHisEntity.getWeight()));
                updateHomeTargetValue(3,StringUtil.doubleToStringOne(weightHisEntity.getFat()),appConfig.getFatTitle(age, sex, weightHisEntity.getFat()),appConfig.getFatStatus(age, sex, weightHisEntity.getFat()),appConfig.getFatValue(age, sex, weightHisEntity.getFat()));
                updateHomeTargetValue(4,StringUtil.doubleToStringOne(weightHisEntity.getSubFat()),appConfig.getSubFatTitle(sex, weightHisEntity.getSubFat()),appConfig.getSubFatStatus(sex, weightHisEntity.getSubFat()),appConfig.getSubFatValue(sex, weightHisEntity.getSubFat()));
                updateHomeTargetValue(5,StringUtil.doubleToString(Math.ceil(weightHisEntity.getVisFat())),appConfig.getVisFatTitle(weightHisEntity.getVisFat()),appConfig.getVisFatStatus(weightHisEntity.getVisFat()),appConfig.getVisFatValue(weightHisEntity.getVisFat()));
                updateHomeTargetValue(7,StringUtil.doubleToStringOne(weightHisEntity.getWater()),appConfig.getWaterTitle(sex, weightHisEntity.getWater()),appConfig.getWaterStatus(sex, weightHisEntity.getWater()),appConfig.getWaterValue(sex, weightHisEntity.getWater()));
                updateHomeTargetValue(6,StringUtil.doubleToStringOne(weightHisEntity.getBMR()),appConfig.getBMRTitle(age, sex, weightHisEntity.getBMR()),appConfig.getBMRStatus(age, sex, weightHisEntity.getBMR()),appConfig.getBMRValue(age, sex, weightHisEntity.getBMR()));
                if(weightHisEntity.getBodyAge()!=null) {
                    updateHomeTargetValue(11, StringUtil.doubleToStringOne(weightHisEntity.getBodyAge()), "标准", 1, 50);
                }
                updateHomeTargetValue(8,StringUtil.doubleToStringOne(weightHisEntity.getMuscle()),appConfig.getMuscleTitle(age, sex, weightHisEntity.getMuscle()),appConfig.getMuscleStatus(age, sex, weightHisEntity.getMuscle()),appConfig.getMuscleValue(age, sex, weightHisEntity.getMuscle()));
                updateHomeTargetValue(9,StringUtil.doubleToStringOne(weightHisEntity.getBone()),appConfig.getBoneTitle(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneStatus(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()),appConfig.getBoneValue(weightHisEntity.getWeight(), sex, weightHisEntity.getBone()));
                if(weightHisEntity.getProtein()!=null) {
                    updateHomeTargetValue(10, StringUtil.doubleToStringOne(weightHisEntity.getProtein()), "标准", 1, 50);
                }
                isTodayData=true;
            }else{
                isTodayData=false;
                items.clear();
                if (mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                }
            }

//            Log.e(TAG,"recordsize:"+dao1.queryForAll().size());
//            GenericRawResults<String[]> rawResults=dao1.queryRaw("select pickTime,count(*),sum(weight),sum(fat),sum(subFat),sum(visFat)," +
//                    "sum(water),sum(BMR),sum(bodyAge),sum(muscle),sum(bone) from t_weight_his where aid="+localAccountEngity.getId()+" and pickTime='"+pickTime+"' group by pickTime");
//            List<String[]> results=rawResults.getResults();
//            if (results.size()>0) {
//                String[] resultArray = results.get(0);
//                int count=Integer.parseInt(resultArray[1]);
//                Log.e(TAG, "pickTime " + resultArray[0] + "  " + resultArray[1] + "  " + resultArray[2] + "  " + resultArray[3] + "  " + resultArray[4] + "  " + resultArray[5] + "  " + resultArray[6] + "  " + resultArray[7] + "  " + resultArray[8] + "  " + resultArray[9] + "  " + resultArray[10]);
//                if (mContentView!=null) {
//                    ((TextView) mContentView.findViewById(R.id.tvWeightValue)).setText(StringUtil.doubleToStringOne(Double.parseDouble(resultArray[2]) / count));
//                }
//            }else {
//                if (mContentView!=null) {
//                    ((TextView) mContentView.findViewById(R.id.tvWeightValue)).setText("0.0");
//                }
//            }

//            Dao<HealthEntity,Integer> healthDao=getDatabaseHelper().getHealthDao();
//            Log.e(TAG,"health size="+healthDao.queryForAll().size());
//            QueryBuilder<HealthEntity, Integer> queryBuilder = healthDao.queryBuilder();
//            queryBuilder.where().eq("accountId",localAccountEngity.getId()).and().eq("pickTime",pickTime);
//            List<HealthEntity> healthEntities=queryBuilder.query();
//            items.addAll(healthEntities);

            Dao<AccountEntity,Integer> accountEntityIntegerDao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity, Integer> queryBuilder1 = accountEntityIntegerDao.queryBuilder();
            queryBuilder1.where().eq("id",localAccountEntity.getId());
            if (queryBuilder1.queryForFirst()!=null){
                localAccountEntity=queryBuilder1.queryForFirst();
                if (StringUtil.isEmpty(localAccountEntity.getTargetWeight())) {
                    localAccountEntity.setTargetWeight(StringUtil.doubleToString(localWeight));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if (isTodayData) {
            loadHomeTarget();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
        if (mContentHeader!=null) {
            refreshView();
        }
    }


    private void updateHomeTargetValue(int targetType,String value,String valueTitle,int valueStatus,int progres){
        try{
            Dao<HomeTargetEntity, Integer> homeTargetDao= getDatabaseHelper().getHomeTargetDao();
            homeTargetDao.updateRaw("UPDATE `t_home_target` SET value ="+value+",valueTitle='"+valueTitle+"',valueStatus="+valueStatus+",progres="+progres+" WHERE aid="+localAccountEntity.getId()+" and type="+targetType+";");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void scane(){
//        mScanLine.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation=new TranslateAnimation(TranslateAnimation.ABSOLUTE,0f,TranslateAnimation.ABSOLUTE,0f,
                TranslateAnimation.RELATIVE_TO_PARENT,0f,TranslateAnimation.RELATIVE_TO_PARENT,0.85f);
        mAnimation.setDuration(2000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
//        mScanLine.setAnimation(mAnimation);
    }

    /**
     * 加载成员信息
     * @param account
     */
    public void loadData(AccountEntity account){
        if (account!=null) {
            bVisitor=false;
            dayDatas.clear();
            items.clear();
            if (mAdapter!=null){
                mAdapter.notifyDataSetChanged();
            }
            currentDay=0;
            localAccountEntity=account;
            if (mContentView!=null&&localAccountEntity!=null){
                getSherlockActivity().getActionBar().setTitle(localAccountEntity.getUserNick());
                if (!StringUtil.isEmpty(localAccountEntity.getAvatar())){
                    getSherlockActivity().getActionBar().setHomeAsUpIndicator(ImageUtils.bitmapToDrawable(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar())));
                }
                if (dayDatas.size()==0){
                    loadWeightHis();
                }
                queryHealthData(strTodayDay);
            }
        }
    }

    /***
     * 来客测试
     * @param accountEntity
     */
    public void loadVisitor(AccountEntity accountEntity){
        bVisitor=true;
        if (accountEntity!=null){
            localAccountEntity=accountEntity;
        }
    }

    public void updateWeightValue(String val,int type) {
        switch (type){
            case 1:{
                startEffect();
                break;
            }
            case 2:{
                mContentHeader.stopEffect(val);
                break;
            }
            case -1:{
                mContentHeader.stopEffect(-1);
                break;
            }
            case 3:{
                mContentHeader.setStatus(val);
                mContentHeader.stopEffect(3);
                updateResult();
                break;
            }
            default:
            {
               mContentHeader.setStatus(val);
                mContentHeader.startEffect();
                break;
            }
        }
    }

    /**
     * 更新检测结果
     */
    public void updateResult()
    {
        queryHealthData(strTodayDay);
        refreshView();
    }

    private void showCrouton(String message){

        final Crouton crouton;
        crouton=Crouton.makeText(getSherlockActivity(),message,INFINITE);
        crouton.setConfiguration(CONFIGURATION).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llTargetSet:{
//                ((TargetActivity)sherlockFragment).loadData(localAccountEngity);
                Intent intent=new Intent(mContext,TargetActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account",localAccountEntity);
                intent.putExtras(bundle);
                getSherlockActivity().startActivity(intent);
                break;
            }
            case R.id.ibRight:{
                currentDay--;
                if (currentDay<0){
                    showCrouton("无数据");
                    items.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    refreshDayData();
                }
                break;
            }
            case R.id.ibLeft:{
                currentDay++;
                if (currentDay>=dayDatas.size()){
                   showCrouton("无数据");
                    items.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    refreshDayData();
                }

                break;
            }
            case R.id.flDetail:{
                if (isTodayData) {
//                    Intent intent = new Intent(getSherlockActivity(), DetailActivity.class);
                    Intent intent=new Intent(getSherlockActivity(), AvatarEditAvtivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("account",localAccountEntity);
                    bundle.putString("dataTime",lastQueryDate);
                    intent.putExtras(bundle);
                    getSherlockActivity().startActivity(intent);
                }
                break;
            }
            case R.id.ibMainShared:{
                break;
            }
            default:
            {
              break;
            }
        }
    }

    public void openShare(){
        ImageUtils.getListBitmap(mListView,appContext.getCameraPath()+"/share.png");
        configPlatforms();
        mController.getConfig().setPlatforms(SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
        mController.openShare(getSherlockActivity(),false);
    }

    private void openChartView(){
        Intent intent=new Intent(getSherlockActivity(), DefaultActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("account",localAccountEntity);
        bundle.putInt("targetType",nTargetType);
        intent.putExtras(bundle);
        getSherlockActivity().startActivity(intent);

    }

    /***
     * 刷新数据
     */
    private void refreshDayData(){
        if(localAccountEntity!=null) {
            if (currentDay>=dayDatas.size()||currentDay<0){
                return;
            }
            if (currentDay == 0) {
                mContentHeader.currentDate.setText(getResources().getString(R.string.main_today));
                lastQueryDate=strTodayDay;
                queryHealthData(strTodayDay);
                if (localAccountEntity.getDoneTime() != null && mContentView != null) {
                    if (!localAccountEntity.getDoneTime().equals("0")) {
                        mContentHeader.targetDay.setText("" + DateUtil.getDayDiff(dfYearMonthDay.format(new Date()), localAccountEntity.getDoneTime()));

                    }
                }

            } else {

                String str =dayDatas.get(currentDay);
                java.util.Date endDate = DateUtil.dayToDate(str);
                String day=DateUtil.getDayString(endDate);
                mContentHeader.currentDate.setText(df.format(endDate));
                lastQueryDate=str;
                queryHealthData(lastQueryDate);
                if (localAccountEntity.getDoneTime() != null && mContentView != null) {
                    if (!localAccountEntity.getDoneTime().equals("0")) {
                        mContentHeader.targetDay.setText("" + DateUtil.getDayDiff(day, localAccountEntity.getDoneTime()));

                    }
                }
            }
        }
    }

    @Override
    public void onViewTargetClicked() {
        Intent intent=new Intent(mContext,TargetActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("account",localAccountEntity);
        intent.putExtras(bundle);
        getSherlockActivity().startActivity(intent);
    }

    @Override
    public void onViewLeftClicked() {
         currentDay++;
        if (currentDay>=dayDatas.size()){
            showCrouton("无数据");
            items.clear();
            mAdapter.notifyDataSetChanged();
        }else {
            refreshDayData();
        }
    }

    @Override
    public void onViewRightClicked() {
        currentDay--;
        if (currentDay<0){
            showCrouton("无数据");
            items.clear();
            mAdapter.notifyDataSetChanged();
        }else {
            refreshDayData();
        }
    }

    @Override
    public void onViewDetailClicked() {
        if (isTodayData) {
//            Intent intent = new Intent(getSherlockActivity(), DetailActivity.class);
            Intent intent = new Intent(getSherlockActivity(), AvatarEditAvtivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("account",localAccountEntity);
            bundle.putLong("weightId",localWeightId);
            bundle.putString("dataTime",lastQueryDate);
            intent.putExtras(bundle);
            getSherlockActivity().startActivity(intent);
        }
    }

    @Override
    public void onViewSharedClicked() {
        mController.getConfig().setPlatforms(SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SMS,SHARE_MEDIA.EMAIL);
        mController.openShare(getSherlockActivity(),false);
    }

    static  class ItemView{
        public ImageButton      ibChart;
        public ImageView        icon;
        public TextView         status;
        public TextView         title;
        public TextView         unit;
        public TextView         value;
        public MySeekBar        seekBar;
    }

    class ItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
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
            ItemView holder;
            if (convertView==null){
                convertView=LayoutInflater.from(mContext).inflate(R.layout.home_content_item,null);
                holder=new ItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvTargetName);
                holder.status=(TextView)convertView.findViewById(R.id.tvTargetStatus);
                holder.value=(TextView)convertView.findViewById(R.id.tvTargetValue);
                holder.unit=(TextView)convertView.findViewById(R.id.tvTargetUnit);
                holder.seekBar=(MySeekBar)convertView.findViewById(R.id.mySeekBar);
                holder.ibChart=(ImageButton)convertView.findViewById(R.id.ibTargetChart);
                convertView.findViewById(R.id.llTargetChart0).setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            HomeTargetEntity entity=items.get(i);
            if(entity!=null){
                holder.title.setText(entity.getTitle());
                if (!StringUtil.isEmpty(entity.getValue())) {
                    holder.value.setText(entity.getValue());
                }
                if (entity.getProgres()!=null) {
                    holder.seekBar.setProgress(entity.getProgres());
                }
                if (!StringUtil.isEmpty(entity.getUnit())) {
                    holder.unit.setText(entity.getUnit());
                    if (entity.getUnit().equals("0")){
                        holder.unit.setText("");
                    }
                }
                if (!StringUtil.isEmpty(entity.getValueTitle())) {
                    holder.status.setText(entity.getValueTitle());
                }
                if (entity.getValueStatus()==0){
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_yellow));
                }else if(entity.getValueStatus()==1){
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_green));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_green));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_green));
                }else{
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.line_red));
                    holder.value.setTextColor(mContext.getResources().getColor(R.color.line_red));
                    holder.unit.setTextColor(mContext.getResources().getColor(R.color.line_red));
                }
            }
            holder.ibChart.setOnClickListener(new chartClickListener(i));
            return convertView;
        }
    }

    class chartClickListener implements View.OnClickListener{

        private int position;
        chartClickListener(int pos){
            position=pos;
        }
        @Override
        public void onClick(View view) {
            HomeTargetEntity entity=items.get(position);
            nTargetType=entity.getType();
            openChartView();
        }
    }

    public AccountEntity getLocalAccountEntity() {
        return localAccountEntity;
    }

    public void setLocalAccountEntity(AccountEntity localAccountEntity) {
        this.localAccountEntity = localAccountEntity;
    }


    /**
     *分享
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    private void configPlatforms()
    {
        String title=AppConfig.SHARE_TITLE;
        String content=AppConfig.SHARE_CONTENT;
        String website=AppConfig.SHARE_WEBSITE;


        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        SinaShareContent  sinaShareContent=new SinaShareContent();
//        sinaShareContent.setShareContent(content);
        sinaShareContent.setShareImage(new UMImage(mContext, appContext.getCameraPath() + "/share.png"));
//        sinaShareContent.setTargetUrl(website);
        mController.setShareMedia(sinaShareContent);


        SmsHandler smsHandler=new SmsHandler();
        smsHandler.addToSocialSDK();
        SmsShareContent sms=new SmsShareContent();
        sms.setShareContent(content+website);
        mController.setShareMedia(sms);

        EmailHandler emailHandler=new EmailHandler();
        emailHandler.addToSocialSDK();
        MailShareContent mail=new MailShareContent();
        mail.setTitle(title);
        mail.setShareContent(content);
        mail.setShareImage(new UMImage(mContext,appContext.getCameraPath()+"/share.png"));
        mController.setShareMedia(mail);

        String appId=AppConfig.WEIXIN_APPID;
        String appSecret=AppConfig.WEIXIN_APPSECRET;
        UMWXHandler umwxHandler=new UMWXHandler(getSherlockActivity(),appId,appSecret);
        umwxHandler.addToSocialSDK();

        UMWXHandler umwxHandler1=new UMWXHandler(getSherlockActivity(),appId,appSecret);
        umwxHandler1.setToCircle(true);
        umwxHandler1.addToSocialSDK();

        WeiXinShareContent weiXinShareContent=new WeiXinShareContent();
//        weiXinShareContent.setShareContent(content);
//        weiXinShareContent.setTitle(title);
        weiXinShareContent.setTargetUrl("");
        weiXinShareContent.setShareImage(new UMImage(mContext, appContext.getCameraPath() + "/share.png"));
        mController.setShareMedia(weiXinShareContent);

        CircleShareContent circleShareContent=new CircleShareContent();
//        circleShareContent.setShareContent(content);
//        circleShareContent.setTargetUrl(website);
        circleShareContent.setShareImage(new UMImage(mContext, appContext.getCameraPath() + "/share.png"));
        mController.setShareMedia(circleShareContent);


        appId=AppConfig.QQ_APPID;
        appSecret=AppConfig.QQ_APPSECRET;
        UMQQSsoHandler umqqSsoHandler=new UMQQSsoHandler(getSherlockActivity(),appId,appSecret);
        umqqSsoHandler.addToSocialSDK();
        QQShareContent qqShareContent=new QQShareContent();
//        qqShareContent.setShareContent(content);
//        qqShareContent.setTitle(title);
//        qqShareContent.setTargetUrl(website);
        qqShareContent.setShareImage(new UMImage(mContext, appContext.getCameraPath() + "/share.png"));

        mController.setShareMedia(qqShareContent);

        QZoneSsoHandler qZoneSsoHandler=new QZoneSsoHandler(getSherlockActivity(),appId,appSecret);
        qZoneSsoHandler.addToSocialSDK();
        QZoneShareContent qzone=new QZoneShareContent();
//        qzone.setShareContent(content);
//        qzone.setTargetUrl(website);
        qzone.setShareImage(new UMImage(mContext, appContext.getCameraPath() + "/share.png"));
        qzone.setTitle(title);
        mController.setShareMedia(qzone);
    }


    private static final Style INFINITE=new Style.Builder().setBackgroundColorValue(Style.holoGreenLight).build();
    private static final Configuration CONFIGURATION=new Configuration.Builder().setDuration(Configuration.DURATION_SHORT).build();

}
