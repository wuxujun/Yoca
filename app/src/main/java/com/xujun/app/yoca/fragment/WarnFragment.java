package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.WarnEntity;
import com.xujun.widget.ToggleButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 14/12/21.
 * 提醒管理
 */
public class WarnFragment extends SherlockFragment{

    private static final String TAG = "WarnFragment";

    private View            mContentView;
    private ListView        mListView;

    private Context mContext;
    private AppContext appContext;

    private DatabaseHelper databaseHelper;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }


    private List<WarnEntity>  items=new ArrayList<WarnEntity>();

    private ItemAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();

        getSherlockActivity().getActionBar().setTitle(getResources().getString(R.string.setting_warn));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.list_frame, null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG,"OnItemClick  "+i);
                WarnEntity entity=items.get(i);
                if (entity!=null) {
                    SherlockFragment fragment = (SherlockFragment) getFragmentManager().findFragmentById(R.id.content_frame);
                    if (fragment instanceof WarnSetFragment) {
                        ((WarnSetFragment)fragment).loadData(entity);
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
                    } else {
                        SherlockFragment sherlockFragment=new WarnSetFragment();
                        ((WarnSetFragment)sherlockFragment).loadData(entity);
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, sherlockFragment).commit();
                    }
                }
            }
        });
        return mContentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        items.clear();
        loadData();
        if (adapter==null){
            adapter=new ItemAdapter();
            mListView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void loadData(){
        try{
            Dao<WarnEntity,Integer> dao=getDatabaseHelper().getWarnEntityDao();
            QueryBuilder<WarnEntity,Integer> queryBuilder=dao.queryBuilder();
            queryBuilder.where().eq("status",0);
            queryBuilder.orderBy("id",false);
            PreparedQuery<WarnEntity> preparedQuery=queryBuilder.prepare();
            List<WarnEntity> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                items.addAll(lists);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    static  class ItemView{

        public LinearLayout     llAdd;
        public LinearLayout     llItem;
        public ImageView        icon;
        public TextView         title;

        public TextView         warnTitle;
        public TextView         warnDesc;
        public TextView         warnTag;
        public ToggleButton     tbOff;
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
                convertView=LayoutInflater.from(mContext).inflate(R.layout.warn_item,null);
                holder=new ItemView();
                holder.llAdd=(LinearLayout)convertView.findViewById(R.id.llWarnAdd);
                holder.llItem=(LinearLayout)convertView.findViewById(R.id.llWarnItem);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivItemIcon);
                holder.title=(TextView)convertView.findViewById(R.id.tvItemTitle);

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
            return convertView;
        }
    }
}


