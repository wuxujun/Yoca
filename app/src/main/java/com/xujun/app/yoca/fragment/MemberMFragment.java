package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xujun.app.yoca.AccountActivity;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.fragment.AccountFragment;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.util.ImageUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 14/12/21.
 * 成员管理
 */
public class MemberMFragment extends SherlockFragment{

    private static final String TAG = "MemberMFragment";

    private View            mContentView;
    private ListView        mListView;

    private Context mContext;
    private AppContext appContext;

    private DatabaseHelper databaseHelper;

    public boolean         isEdit=false;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }


    private List<AccountEntity>  items=new ArrayList<AccountEntity>();

    private ItemAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();
        getSherlockActivity().getActionBar().setTitle(getResources().getString(R.string.menu_account_manager));
        getSherlockActivity().getActionBar().setHomeAsUpIndicator(R.drawable.back);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.list_frame, null);
        mListView=(ListView)mContentView.findViewById(R.id.lvList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AccountEntity accountEntity=items.get(i);
                Intent intent=new Intent(mContext, AccountActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("account",accountEntity);
                intent.putExtras(bundle);
                getSherlockActivity().startActivity(intent);
            }
        });
        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
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
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }

    private void loadData(){
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
    }

    public void manager(boolean edit){
        if (isEdit&&edit){
            isEdit=false;
        }else {
            isEdit = edit;
        }
        setHasOptionsMenu(true);
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    static  class ItemView{
        public ImageView        icon;
        public TextView         title;
        public TextView         memberName;
        public TextView         memberDesc;
        public LinearLayout     memberItem;
        public LinearLayout     addItem;
        public ImageButton      delIB;
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
                convertView=LayoutInflater.from(mContext).inflate(R.layout.member_item,null);
                holder=new ItemView();
                holder.addItem=(LinearLayout)convertView.findViewById(R.id.llMemberAdd);
                holder.memberItem=(LinearLayout)convertView.findViewById(R.id.llMemberItem);
                holder.memberName=(TextView)convertView.findViewById(R.id.tvMemberName);
                holder.memberDesc=(TextView)convertView.findViewById(R.id.tvMemberDesc);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivMemberIcon);
                holder.title=(TextView)convertView.findViewById(R.id.tvItemTitle);
                holder.delIB=(ImageButton)convertView.findViewById(R.id.ibAccountDel);

                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            AccountEntity entity=items.get(i);
            if (entity.getType()==2){
                holder.memberItem.setVisibility(View.GONE);
                holder.addItem.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.GONE);
                holder.delIB.setVisibility(View.GONE);
            }else{
                holder.addItem.setVisibility(View.GONE);
                holder.memberItem.setVisibility(View.VISIBLE);
                holder.memberName.setText(entity.getUserNick());
                if (isEdit&&entity.getType()==0){
                    holder.delIB.setVisibility(View.VISIBLE);
                }else{
                    holder.delIB.setVisibility(View.GONE);
                }
                if (entity.getAvatar()!=null){
                    holder.icon.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath()+"/crop_"+entity.getAvatar()));
                }
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
            Log.e(TAG,"onClick() .."+position);
            AccountEntity entity=items.get(position);
            if (entity.getType()==0){
                items.remove(position);
                deleteAccount(entity);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void deleteAccount(AccountEntity entity){
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.delete(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}


