package com.xujun.app.yoca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;
import com.xujun.model.BaseResp;
import com.xujun.sqlite.SendRecord;
import com.xujun.sqlite.WarnEntity;
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
 * Created by xujunwu on 15/10/15.
 */
public class RecordActivity extends BaseActivity{

    private List<SendRecord> items=new ArrayList<SendRecord>();

    private ItemAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_frame);
        mListView=(ListView)findViewById(R.id.lvList);
        mHeadTitle.setText("发布记录");
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mHeadButton.setText("清空");
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        items.clear();
        loadData();
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

    private void clearData(){
        try{
            Dao<SendRecord,Integer> dao=getDatabaseHelper().getSendRecordDao();
            dao.executeRaw("delete from t_send_record");
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadData();
        adapter.notifyDataSetChanged();
    }

    private void loadData(){
        try{
            Dao<SendRecord,Integer> dao=getDatabaseHelper().getSendRecordDao();
            QueryBuilder<SendRecord,Integer> queryBuilder=dao.queryBuilder();
            PreparedQuery<SendRecord> preparedQuery=queryBuilder.prepare();
            List<SendRecord> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                items.addAll(lists);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static  class ItemView{

        public TextView title;
        public TextView  content;
        public TextView status;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.item_record,null);
                holder=new ItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvTitle);
                holder.content=(TextView)convertView.findViewById(R.id.tvContent);
                holder.status=(TextView)convertView.findViewById(R.id.tvStatus);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            SendRecord entity=items.get(i);
            if (entity!=null){
                if (!StringUtil.isEmpty(entity.getDevAddress())) {
                    holder.title.setText("" + entity.getAddTime() + "  " + entity.getDevAddress());
                }
                if (!StringUtil.isEmpty(entity.getData())){
                    holder.content.setText(entity.getData());
                }
                holder.status.setText("状态:"+entity.getStatus());
            }

            return convertView;
        }
    }
}
