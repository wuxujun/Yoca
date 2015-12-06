package com.xujun.app.yoca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;
import com.xujun.model.BaseResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WarnEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.JsonUtil;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;
import com.xujun.widget.ToggleButton;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 15/4/6.
 */
public class WarnActivity extends BaseActivity{

    private List<WarnEntity> items=new ArrayList<WarnEntity>();

    private ItemAdapter adapter;

    public boolean         isEdit=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);

        mListView=(ListView)findViewById(R.id.lvList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WarnEntity entity = items.get(i);
                if (entity != null) {
                    Intent intent = new Intent(WarnActivity.this, WarnSetActivity.class);
                    startActivity(intent);
                }
            }
        });
        mHeadTitle.setText(getResources().getString(R.string.setting_warn));
        mHeadButton.setText(getResources().getString(R.string.btn_Edit));
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                if (isEdit) {
                    mHeadButton.setText(getText(R.string.btn_main_done));
                } else {
                    mHeadButton.setText(getText(R.string.btn_manager));
                }
                if (adapter!=null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        items.clear();
        loadData();
        synchData();
        if (adapter==null){
            adapter=new ItemAdapter();
            mListView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int keyCode=event.getKeyCode();
        if (event.getAction()==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void loadData(){
        try{
            Dao<WarnEntity,Integer> dao=getDatabaseHelper().getWarnEntityDao();
            QueryBuilder<WarnEntity,Integer> queryBuilder=dao.queryBuilder();
            queryBuilder.where().eq("status",0);
            queryBuilder.orderBy("wid", false);
            PreparedQuery<WarnEntity> preparedQuery=queryBuilder.prepare();
            List<WarnEntity> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                items.addAll(lists);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void synchData()
    {
        try {
            Dao<WarnEntity, Integer> warnEntities = getDatabaseHelper().getWarnEntityDao();
            QueryBuilder<WarnEntity, Integer> warnQueryBuilder = warnEntities.queryBuilder();
            warnQueryBuilder.where().eq("isSync", 0).and().eq("type",1);
            List<WarnEntity> list=warnQueryBuilder.query();
            if (list.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list);
                if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", appContext.getProperty(AppConfig.CONF_USER_UID));
                }
                params.put("imei",appContext.getIMSI());
                params.put("umeng_token", UmengRegistrar.getRegistrationId(mContext));
                request(URLs.WARN_SYNC_URL, JsonUtil.toJson(params).toString());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (JSONException je){
            je.printStackTrace();
        }
    }

    private void parserResp(String resp){
        try{
            BaseResp baseResp=(BaseResp)JsonUtil.ObjFromJson(resp,BaseResp.class);
            if (baseResp.getDataType().equals("syncwarns")) {
                if (baseResp.getStatus() == 1) {
                    Dao<WarnEntity, Integer> warnEntityDao = getDatabaseHelper().getWarnEntityDao();
                    warnEntityDao.updateRaw("UPDATE `t_warn` SET isSync = 1 WHERE isSync=0;");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void request(final String url,final String params){
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp((String)msg.obj);
                    MobclickAgent.onEvent(mContext, "SyncData");
                }else {
                    MobclickAgent.onEvent(mContext,"SyncDataFaild");
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
                        msg.obj="登录失败";
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

        public LinearLayout llAdd;
        public LinearLayout     llItem;
        public ImageView icon;
        public TextView title;

        public TextView         warnTitle;
        public TextView         warnDesc;
        public TextView         warnTag;
        public ToggleButton tbOff;


        public ImageButton delIB;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.warn_item,null);
                holder=new ItemView();
                holder.llAdd=(LinearLayout)convertView.findViewById(R.id.llWarnAdd);
                holder.llItem=(LinearLayout)convertView.findViewById(R.id.llWarnItem);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivItemIcon);
                holder.title=(TextView)convertView.findViewById(R.id.tvItemTitle);
                holder.delIB=(ImageButton)convertView.findViewById(R.id.ibWarnDel);

                holder.warnTitle=(TextView)convertView.findViewById(R.id.tvWarnTitle);
                holder.warnDesc=(TextView)convertView.findViewById(R.id.tvWarnDesc);
                holder.warnTag=(TextView)convertView.findViewById(R.id.tvWarnTag);
                holder.tbOff=(ToggleButton)convertView.findViewById(R.id.tbOff);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            WarnEntity entity=items.get(i);
            if (entity.getType()==0){
                holder.llAdd.setVisibility(View.VISIBLE);
                holder.llItem.setVisibility(View.GONE);
            }else{
                holder.llAdd.setVisibility(View.GONE);
                holder.llItem.setVisibility(View.VISIBLE);
                if (isEdit){
                    holder.delIB.setVisibility(View.VISIBLE);
                }else{
                    holder.delIB.setVisibility(View.GONE);
                }
                holder.warnTitle.setText(entity.getValue());
                holder.warnTag.setText(""+entity.getNote());
                String desc="";
                if (entity.getWeek_mon()!=null&&entity.getWeek_mon()==1){
                    desc+="周一 ";
                }
                if (entity.getWeek_tue()!=null&&entity.getWeek_tue()==1){
                    desc+="周二 ";
                }
                if (entity.getWeek_wed()!=null&&entity.getWeek_wed()==1){
                    desc+="周三 ";
                }
                if (entity.getWeek_thu()!=null&&entity.getWeek_thu()==1){
                    desc+="周四 ";
                }
                if (entity.getWeek_fri()!=null&&entity.getWeek_fri()==1){
                    desc+="周五 ";
                }
                if (entity.getWeek_sat()!=null&&entity.getWeek_sat()==1){
                    desc+="周六 ";
                }
                if (entity.getWeek_sun()!=null&&entity.getWeek_sun()==1){
                    desc+="周日 ";
                }
                holder.warnDesc.setText(desc);
            }
            holder.delIB.setOnClickListener(new DelClickListener(i));
            return convertView;
        }
    }

    class DelClickListener implements View.OnClickListener{

        private int position;
        DelClickListener(int pos){
            position=pos;
        }
        @Override
        public void onClick(View view) {
            WarnEntity entity=items.get(position);
            items.remove(position);
            deleteWarn(entity);
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteWarn(WarnEntity entity){
        try{
            Dao<WarnEntity, Integer> warnEntityDao = getDatabaseHelper().getWarnEntityDao();
            warnEntityDao.updateRaw("UPDATE `t_warn` SET isSync = 0,status=3 WHERE wid="+entity.getWId()+";");
        }catch (SQLException e) {
            e.printStackTrace();
        }
        synchData();
    }

}
