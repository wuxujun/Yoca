package com.xujun.app.yoca.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.AppException;
import com.xujun.app.yoca.ContentEActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.WebActivity;
import com.xujun.model.ArticleInfo;
import com.xujun.model.BaseResp;
import com.xujun.model.InfoGResp;
import com.xujun.model.InfoResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.InfoEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.AppUtil;
import com.xujun.util.BitmapManager;
import com.xujun.util.DateUtil;
import com.xujun.util.JsonUtil;
import com.xujun.util.MHttpClient;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 15/6/7.
 */
public class InfoFragment extends BaseFragment {

    private static final String TAG = "InfoFragment";
    private ProgressDialog progress;

    private ItemAdapter         mAdapter;

    private boolean             isEdit=false;

    private AppConfig appConfig;

    private List<InfoEntity> items=new ArrayList<InfoEntity>();
    private List<String>   groups=new ArrayList<String>();

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        mContentView=inflater.inflate(R.layout.list_frame,null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG,"onItemClick  "+i);
                InfoEntity info=items.get(i);
                if(!info.isGroup()) {
                    Intent intent = new Intent(getSherlockActivity(), WebActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("info",info);
                    intent.putExtras(bundle);
                    getSherlockActivity().startActivity(intent);
                }
            }
        });

        loadLocalData();
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConfig=AppConfig.getAppConfig(mContext);
        mAdapter=new ItemAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.e(TAG, "onResume()");
        load();
    }

    private void loadLocalData(){
        try{
            Dao<InfoEntity,Integer> dao=getDatabaseHelper().getInfoDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select groupName from t_info group by groupName ");
            List<String[]> results=rawResults.getResults();
            Log.d(TAG, " select result size:" + results.size());
            if (results.size()>0) {
                items.clear();
                String[] resultArray=results.get(0);
                for (int i=0;i<resultArray.length;i++){
                    if (!StringUtil.isEmpty(resultArray[i])){
                        InfoEntity a=new InfoEntity();
                        a.setGroup(true);
                        a.setTitle(resultArray[i]);
                        groups.add(resultArray[i]);
                        items.add(a);
                        queryInfoForGroup(resultArray[i]);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void queryInfoForGroup(String gname){
        try {
            Dao<InfoEntity,Integer> dao=getDatabaseHelper().getInfoDao();
            QueryBuilder<InfoEntity,Integer> queryBuilder=dao.queryBuilder();
            queryBuilder.where().eq("groupName",gname);
            queryBuilder.orderBy("id", true);
            PreparedQuery<InfoEntity> preparedQuery1=queryBuilder.prepare();
            List<InfoEntity> list=dao.query(preparedQuery1);
            if (list.size()>0){
                items.addAll(list);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private String getInfoMaxid(){
        try{
            Dao<InfoEntity,Integer> dao=getDatabaseHelper().getInfoDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select max(id) from t_info ");
            List<String[]> results=rawResults.getResults();
            Log.d(TAG, " getInfoMaxid select result size:" + results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                if (!StringUtil.isEmpty(resultArray[0])) {
                    return  resultArray[0];
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "0";
    }

    private void load(){
        if (items.size()==0) {
            Map<String, String> sb = new HashMap<String, String>();
            sb.put("imei", appContext.getIMSI());
            sb.put("umeng_token", UmengRegistrar.getRegistrationId(mContext));
            sb.put("id",getInfoMaxid());
            try {
                request(URLs.INFO_GET_URL, JsonUtil.toJson(sb));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addArticeInfo(InfoEntity entity){
        try{
            if (entity!=null){
                Log.d(TAG,"addArticeInfo "+entity.getId()+" "+entity.getTitle());
            }
            Dao<InfoEntity,Integer> dao=getDatabaseHelper().getInfoDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void parserResp(String resp){
        try{
            InfoGResp baseResp=(InfoGResp)JsonUtil.ObjFromJson(resp, InfoGResp.class);
            if (baseResp.getSuccess()==1){
                for (int i=0;i<baseResp.getRoot().size();i++){
                    InfoResp info=baseResp.getRoot().get(i);
                    if (info!=null){
                        for (int j=0;j<info.getInfos().size();j++){
                            addArticeInfo(info.getInfos().get(j));
                        }
                    }
                }
                loadLocalData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void request(final String url,final String params){
        //progress= AppUtil.showProgress(getSherlockActivity(), mContext.getResources().getString(R.string.data_loading));
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }
//                if (progress!=null) {
//                    progress.dismiss();
//                }
            }
        };

        new Thread(){
            public void run(){
                Message msg=new Message();
                try{
                    String resp=appContext.sendRequestData(url, params,null);
                    if (resp!=null){
                        msg.what=1;
                        msg.obj=resp;
                    }else{
                        msg.what=0;
                        msg.obj="数据获取失败";
                    }
                }catch (AppException e){
                    e.printStackTrace();
                    msg.what=-1;
                    msg.obj=e;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    static  class ItemView{
        public FrameLayout head;
        public ImageView icon;
        public TextView title;
        public LinearLayout     item;
        public TextView         itemTitle;
        public ImageView         itemIcon;

        public TextView         groupName;
    }

    class ItemAdapter extends BaseAdapter {

        private boolean isHead=false;
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

        public boolean isEnabled(int position){
            InfoEntity info=items.get(position);
            if (info.isGroup()){
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ItemView holder;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.info_item,null);
                holder=new ItemView();
                holder.groupName=(TextView)convertView.findViewById(R.id.tvGroupName);
                holder.title=(TextView)convertView.findViewById(R.id.tvTitle);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivIcon);

                holder.head=(FrameLayout)convertView.findViewById(R.id.llHead);
                holder.item=(LinearLayout)convertView.findViewById(R.id.llItem);
                holder.itemTitle=(TextView)convertView.findViewById(R.id.tvItemTitle);
                holder.itemIcon=(ImageView)convertView.findViewById(R.id.ivItemIcon);

                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }

            InfoEntity info=items.get(i);
            if (info!=null){
                if (info.isGroup()){
                    holder.head.setVisibility(View.GONE);
                    holder.item.setVisibility(View.GONE);
                    holder.groupName.setText(info.getTitle());
                    isHead=true;
                }else{
                    if (isHead){
                        holder.groupName.setVisibility(View.GONE);
                        holder.item.setVisibility(View.GONE);
                        holder.title.setText(info.getTitle());
                        if (!StringUtil.isEmpty(info.getImage())) {
                            ImageLoader.getInstance().displayImage(info.getImage(), holder.icon,options);
                        }
                        isHead=false;
                    }else {
                        holder.groupName.setVisibility(View.GONE);
                        holder.head.setVisibility(View.GONE);
                        holder.itemTitle.setText(info.getTitle());
                        if (!StringUtil.isEmpty(info.getImage())) {
                            ImageLoader.getInstance().displayImage(info.getImage(), holder.itemIcon,options);
                        }
                    }
                }
            }

            return convertView;
        }
    }


}
