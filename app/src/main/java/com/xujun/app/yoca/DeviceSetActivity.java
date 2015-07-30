package com.xujun.app.yoca;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xujun.sqlite.AccountEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 7/15/15.
 */

public class DeviceSetActivity extends BaseActivity{

    private List<AccountEntity> items=new ArrayList<AccountEntity>();

    private ItemAdapter      mAdapter;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_account);



        initListView();
        loadData();
    }

    private void initListView(){
        mAdapter=new ItemAdapter();
        mListView=(ListView)findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AccountEntity entity=items.get(i);
                if (entity!=null){
                    Intent intent=new Intent();
//                    intent.putExtra("beginDay",entity.getBeginDay());
//                    intent.putExtra("endDay",entity.getEndDay());
                    DeviceSetActivity.this.setResult(RESULT_OK,intent);
                    DeviceSetActivity.this.finish();
                }
            }
        });
    }

    private void loadData(){
        items.clear();
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
            Where<AccountEntity,Integer> where=queryBuilder.where();
            where.or(where.eq("type",0),where.eq("type",2),where.eq("type",1));
            queryBuilder.orderBy("type",true);
            PreparedQuery<AccountEntity> preparedQuery=queryBuilder.prepare();
            List<AccountEntity> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                items.addAll(lists);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }

    static class ListItemView{
        public TextView   title;
    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ListItemView listItemView=null;
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.select_dialog_item,null);
                listItemView=new ListItemView();
                listItemView.title=(TextView)convertView.findViewById(R.id.tvTitle);
                convertView.setTag(listItemView);
            }else {
                listItemView=(ListItemView)convertView.getTag();
            }
            AccountEntity entity=items.get(position);
            if (entity!=null){
                listItemView.title.setText(entity.getUserNick());
            }
            return convertView;
        }
    }
}
