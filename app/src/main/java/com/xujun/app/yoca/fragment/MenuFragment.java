package com.xujun.app.yoca.fragment;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.MainActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.fragment.AccountFragment;
import com.xujun.app.yoca.fragment.ContentFragment;
import com.xujun.app.yoca.fragment.MemberMFragment;
import com.xujun.app.yoca.fragment.SettingFragment;
import com.xujun.app.yoca.widget.MenuController;
import com.xujun.app.yoca.widget.MenuFooter;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.ImageUtils;
import com.xujun.widget.AnimatedExpandableListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends SherlockFragment implements MenuController{

	public static final String TAG = "MenuFragment";  

    private  View   mContentView;


    List<GroupItem>  groupItems=new ArrayList<GroupItem>();
    private AnimatedExpandableListView mListView;
    private ItemAdapter         adapter;

    private MenuFooter          mMenuFooterView;

    private AppContext appContext;
    private DatabaseHelper          databaseHelper;

    private AccountEntity           localAccountEngity=null;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView()");
        mContentView=inflater.inflate(R.layout.layout_menu,null);
        mContentView.findViewById(R.id.llAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentById(R.id.content_frame);
                if(fragment instanceof ContentFragment) {
                    Log.e(TAG,"ContentFragment...");
                    if (localAccountEngity!=null) {
                        ((ContentFragment) fragment).loadData(localAccountEngity);
                    }
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
                }else{
                    Log.e(TAG,"new ContentFragment()...");
                    SherlockFragment sherlockFragment=new ContentFragment();
                    if (localAccountEngity!=null) {
                        ((ContentFragment) sherlockFragment).loadData(localAccountEngity);
                    }
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();
                }
                ((MainActivity)getSherlockActivity()).getSlidingMenu().toggle();
            }
        });

        mListView=(AnimatedExpandableListView)mContentView.findViewById(R.id.lvContent);
        mMenuFooterView=new MenuFooter(getActivity());
        mMenuFooterView.setMenuController(this);
        mListView.addFooterView(mMenuFooterView);
        mListView.setAdapter(adapter);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (mListView.isGroupExpanded(i)) {
                    mListView.collapseGroupWithAnimation(i);
                } else {
                    mListView.expandGroupWithAnimation(i);
                }
                return true;
            }
        });
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                GroupItem groupItem=groupItems.get(i);
                AccountEntity accountEntity=groupItem.items.get(i2);
                SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentById(R.id.content_frame);
                if(fragment instanceof ContentFragment) {
                    ((ContentFragment)fragment).loadData(accountEntity);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
                }else{
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,new ContentFragment()).commit();
                }
                ((MainActivity)getSherlockActivity()).getSlidingMenu().toggle();
                return false;
            }
        });
        return mContentView;
    }
	
	@Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);  
        Log.d(TAG, "onAttach");  
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        appContext=(AppContext)getActivity().getApplication();
        adapter=new ItemAdapter(getActivity());
    }

    private void queryMenuEntity(){
        groupItems.clear();
        GroupItem groupItem=new GroupItem();
        groupItem.title="家庭成员";
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
//            Where<AccountEntity,Integer> where=queryBuilder.where();
//            where.or(where.eq("type", 0), where.eq("type", 1));
            queryBuilder.where().eq("type",0);
            queryBuilder.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery=queryBuilder.prepare();
            List<AccountEntity> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                for (int i=0;i<lists.size();i++){
                    AccountEntity ae=lists.get(i);
                    groupItem.items.add(ae);
                }
            }


            QueryBuilder<AccountEntity,Integer> queryBuilder1=dao.queryBuilder();
            queryBuilder1.where().eq("type",1);
            queryBuilder1.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery1=queryBuilder1.prepare();
            List<AccountEntity> list=dao.query(preparedQuery1);
            if (list.size()>0){
                localAccountEngity=list.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        groupItems.add(groupItem);


        if (localAccountEngity!=null&&mContentView!=null){
            ((TextView)mContentView.findViewById(R.id.tvMenuUserNick)).setText(localAccountEngity.getUserNick());
            if (localAccountEngity.getAvatar()!=null) {
                ((ImageView) mContentView.findViewById(R.id.ivMyAvatar)).setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEngity.getAvatar()));
            }
        }
    }
  
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        Log.d(TAG, "onActivityCreated");  
    }  
  
    @Override  
    public void onStart() {  
        super.onStart();  
        Log.d(TAG, "onStart");  
    }  
  
    @Override  
    public void onResume() {  
        super.onResume();  
        Log.e(TAG, "onResume");
        queryMenuEntity();
    }  
  
    @Override  
    public void onPause() {  
        super.onPause();  
        Log.d(TAG, "onPause");  
    }  
  
    @Override  
    public void onStop() {  
        super.onStop();  
        Log.d(TAG, "onStop");  
    }  
  
    @Override  
    public void onDestroyView() {  
        super.onDestroyView();  
        Log.d(TAG, "onDestroyView");

    }  
  
    @Override  
    public void onDestroy() {  
        Log.d(TAG, "onDestroy");
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }  
  
    @Override  
    public void onDetach() {  
        super.onDetach();  
        Log.d(TAG, "onDetach");  
    }

    public void refreshAccount(){
        queryMenuEntity();
    }

    static class GroupItem{
        String title;
        List<AccountEntity> items=new ArrayList<AccountEntity>();
    }

    static  class MenuItemView{

        public ImageView        icon;
        public TextView         title;
    }
    class ItemAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter{

        private LayoutInflater  inflater;

        public ItemAdapter(Context context){
            inflater=LayoutInflater.from(context);
        }


        public AccountEntity getChild(int gPosition,int childPosition){
            return groupItems.get(gPosition).items.get(childPosition);
        }

        public long getChildId(int gPosition,int childPosition){
            return childPosition;
        }

        public View getRealChildView(int gPosition,int childPosition,boolean isLastChild,View convertView,ViewGroup parent){
            MenuItemView holder;
            AccountEntity item=getChild(gPosition,childPosition);
            if (convertView==null){
                holder=new MenuItemView();
                convertView=inflater.inflate(R.layout.menu_item,parent,false);
                convertView.findViewById(R.id.llGroupItem).setVisibility(View.GONE);
                convertView.findViewById(R.id.llMemberItem).setVisibility(View.VISIBLE);
                holder.title=(TextView)convertView.findViewById(R.id.tvMenuItemTitle);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivMenuItemIcon);
                convertView.setTag(holder);
            }else{
                holder=(MenuItemView)convertView.getTag();
            }
            holder.title.setText(item.getUserNick());
            if (item.getAvatar()!=null){
                holder.icon.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath()+"/crop_"+item.getAvatar()));
            }
            Log.e(TAG,"......");
            return convertView;
        }

        public int getRealChildrenCount(int gPosition){
            return  groupItems.get(gPosition).items.size();
        }
        @Override
        public int getGroupCount(){
            return groupItems.size();
        }

        @Override
        public GroupItem getGroup(int position){
            return groupItems.get(position);
        }

        @Override
        public long getGroupId(int position){
            return position;
        }

        @Override
        public View getGroupView(int position,boolean isExpanded,View convertView,ViewGroup parent){
            MenuItemView holder;
            GroupItem gItem=getGroup(position);
            if (convertView==null){
                convertView=inflater.inflate(R.layout.menu_item,parent,false);
                convertView.findViewById(R.id.llGroupItem).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.llMemberItem).setVisibility(View.GONE);
                holder=new MenuItemView();
                holder.icon=(ImageView)convertView.findViewById(R.id.ivGroupItemIcon);
                holder.title=(TextView)convertView.findViewById(R.id.tvGroupItemTitle);
                convertView.setTag(holder);
            }else {
                holder = (MenuItemView) convertView.getTag();
            }
            holder.title.setText(gItem.title);
            return convertView;
        }

        public boolean hasStableIds(){
            return true;
        }
        public boolean isChildSelectable(int i,int j){
            return true;
        }
    }


    @Override
    public void onMenuClicked(int type){
        Log.d(TAG,"onMenuClicked  "+type);
        SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentById(R.id.content_frame);
       switch (type){
           case 0:{
               if(fragment instanceof MemberMFragment) {
                   getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
               }else {
                   getFragmentManager().beginTransaction().replace(R.id.content_frame,new MemberMFragment()).commit();
               }
               break;
           }
           case 1:
           {
               if(fragment instanceof AccountFragment) {
                   ((AccountFragment)fragment).setDataType(AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER);
                   getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
               }else {
                   SherlockFragment sherlockFragment=new AccountFragment();
                   ((AccountFragment)sherlockFragment).setDataType(AppConfig.REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER);
                   getFragmentManager().beginTransaction().replace(R.id.content_frame,sherlockFragment).commit();
               }
               break;
           }
           case 2:
           {
               break;
           }
           case 3:
           {
               if(fragment instanceof SettingFragment) {
                   getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
               }else {
                   getFragmentManager().beginTransaction().replace(R.id.content_frame,new SettingFragment()).commit();
               }
            break;
           }
       }

        ((MainActivity)getSherlockActivity()).getSlidingMenu().toggle();
    }


}
