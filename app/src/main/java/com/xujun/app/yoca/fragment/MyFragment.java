package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xujun.app.yoca.AccountMActivity;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.AvatarMActivity;
import com.xujun.app.yoca.HomeActivity;
import com.xujun.app.yoca.MainActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.SettingActivity;
import com.xujun.app.yoca.TabActivity;
import com.xujun.app.yoca.WarnActivity;
import com.xujun.app.yoca.WebActivity;
import com.xujun.app.yoca.widget.MenuFooter;
import com.xujun.model.ArticleInfo;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;
import com.xujun.widget.AnimatedExpandableListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.smssdk.gui.GroupListView;

/**
 * Created by xujunwu on 15/4/6.
 */
public class MyFragment  extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "MyFragment";

    List<String> items=new ArrayList<String>();
    private ItemAdapter         adapter;


    private AccountEntity           localAccountEntity=null;

    private TextView                userNick;
    private ImageView               userAvatar;
    private TextView                userAccount;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new ItemAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.layout_my, null);
        userNick=(TextView)mContentView.findViewById(R.id.tvUserNick);
        userAccount=(TextView)mContentView.findViewById(R.id.tvAccount);
        userAvatar=(ImageView)mContentView.findViewById(R.id.ivMyAvatar);

        mListView=(ListView)mContentView.findViewById(R.id.list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick  " + i);
                if (i == 0) {
                    Intent intent = new Intent(getActivity(), AccountMActivity.class);
                    startActivity(intent);
                }
//                else if (i == 1) {
//                    Intent intent = new Intent(getActivity(), WarnActivity.class);
//                    startActivity(intent);
//                }
                else if (i == 1) {
                    Intent intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                }else if (i==2){
                    Intent intent=new Intent(getActivity(),AvatarMActivity.class);
                    startActivity(intent);
                }
            }
        });
        return mContentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        setHasOptionsMenu(true);
        loadData();
    }

    private void loadData() {
        items.clear();
        items.add("用户管理");
//        items.add("数据管理");
        items.add("设置");
        items.add("减肥像册");
        adapter.notifyDataSetChanged();
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder1=dao.queryBuilder();
            queryBuilder1.where().eq("type",1);
            queryBuilder1.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery1=queryBuilder1.prepare();
            List<AccountEntity> list=dao.query(preparedQuery1);
            if (list.size()>0){
                localAccountEntity=list.get(0);
            }
            if (localAccountEntity!=null){
                userNick.setText(localAccountEntity.getUserNick());
                if (!StringUtil.isEmpty(localAccountEntity.getAvatar())){
                    Log.e(TAG, localAccountEntity.getAvatar());
                    if (!localAccountEntity.getAvatar().equals("0")) {
                        if (ImageUtils.isFileExist(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar())) {
                            userAvatar.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar()));
                        }else{
                            userAvatar.setImageResource(R.drawable.ic_my_item_user);
                        }
                    }
                }
                String  userType=appContext.getProperty(AppConfig.CONF_USER_TYPE);
                if (!appContext.getProperty(AppConfig.CONF_USER_TYPE).equals("0")){
                    ImageLoader.getInstance().displayImage(appContext.getProperty(AppConfig.CONF_USER_AVATAR), userAvatar, options);
                    if (userType.equals("1")){
                        userAccount.setText("帐号:微信用户");
                    }else if(userType.equals("2")){
                        userAccount.setText("帐号:QQ用户");
                    }else if(userType.equals("3")){
                        userAccount.setText("帐号:微博用户");
                    }
                } else {

                    userAccount.setText("帐号:" + AppConfig.getAppConfig(mContext).get(AppConfig.CONF_LOGIN_ACCOUNT));
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHeadEdit:{
                Intent intent=new Intent(getSherlockActivity(),HomeActivity.class);
                startActivity(intent);
                getSherlockActivity().finish();
                break;
            }
        }
    }

    static  class ItemView{
        public ImageView icon;
        public TextView         title;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.account_item,null);
                holder=new ItemView();
                holder.title=(TextView)convertView.findViewById(R.id.tvMenuItemTitle);
                holder.icon=(ImageView)convertView.findViewById(R.id.ivMenuItemIcon);
                convertView.setTag(holder);
            }else {
                holder = (ItemView) convertView.getTag();
            }
            String msg=items.get(i);
            if (msg!=null){
                holder.title.setText(msg);
            }
            if (i==0){
                holder.icon.setImageResource(R.drawable.ic_my_item_user);
            }
//            else if(i==1){
//                holder.icon.setImageResource(R.drawable.ic_my_item_chart);
//            }
            else if (i==1){
                holder.icon.setImageResource(R.drawable.ic_my_item_set);
            }else if (i==2){
                holder.icon.setImageResource(R.drawable.ic_my_item_photo);
            }
            return convertView;
        }
    }

}
