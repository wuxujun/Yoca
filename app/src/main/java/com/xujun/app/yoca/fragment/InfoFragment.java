package com.xujun.app.yoca.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
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
import com.xujun.model.InfoResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.AppUtil;
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

    private List<ArticleInfo> items=new ArrayList<ArticleInfo>();

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
                ArticleInfo info=items.get(i);
                Intent intent=new Intent(getSherlockActivity(),WebActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("info",info);
                intent.putExtras(bundle);
                getSherlockActivity().startActivity(intent);
            }
        });


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

    private void load(){
        if (items.size()==0) {

            Map<String, String> sb = new HashMap<String, String>();
            sb.put("imei", appContext.getIMSI());
            sb.put("umeng_token", UmengRegistrar.getRegistrationId(mContext));
            try {
                request(URLs.INFO_GET_URL, JsonUtil.toJson(sb));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parserResp(String resp){
        items.clear();
        try{
            InfoResp baseResp=(InfoResp)JsonUtil.ObjFromJson(resp,InfoResp.class);
            if (baseResp.getSuccess()==1){
                items.addAll(baseResp.getRoot());
            }
            mAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void request(final String url,final String params){
        progress= AppUtil.showProgress(getSherlockActivity(), mContext.getResources().getString(R.string.data_loading));
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                }
                if (progress!=null) {
                    progress.dismiss();
                }
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
        public ImageView icon;
        public TextView title;
        public TextView         content;
    }

    class ItemAdapter extends BaseAdapter {

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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.info_item,null);
                holder=new ItemView();
                holder.content=(TextView)convertView.findViewById(R.id.tvContent);
                holder.title=(TextView)convertView.findViewById(R.id.tvTitle);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivIcon);

                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }

            ArticleInfo info=items.get(i);
            if (info!=null){
                holder.title.setText(info.getTitle());
                if (!StringUtil.isEmpty(info.getNote())){
                    holder.content.setText(info.getNote());
                }
                if (!StringUtil.isEmpty(info.getImage())){
                    ImageLoader.getInstance().displayImage(info.getImage(),holder.icon);
                }
            }

            return convertView;
        }
    }


}
