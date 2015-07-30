package com.xujun.app.yoca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.util.ImageUtils;
import com.xujun.util.StringUtil;

import java.util.List;

/**
 * Created by xujunwu on 7/16/15.
 */
public class AccountAdapter extends BaseAdapter{

    private Context             context;//运行上下文
    private AppContext          mAppContext;
    private List<AccountEntity> listItems;
    private LayoutInflater      listContainer;//视图容器
    private int 				itemViewResource;//自定义项视图源

    static class ListItemView{				//自定义控件集合
        public TextView title;
        public ImageView img;
    }

    public AccountAdapter(Context context,List<AccountEntity> lists,AppContext appContext,int resource){
        this.context = context;
        this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = lists;
        this.mAppContext=appContext;
    }

    @Override
    public int getCount() {
        return listItems.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView  listItemView = null;

        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);
            listItemView = new ListItemView();
            listItemView.title=(TextView)convertView.findViewById(R.id.tvItemTitle);
            listItemView.img=(ImageView)convertView.findViewById(R.id.ivItemIcon);
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
        AccountEntity entity=listItems.get(position);
        if (entity!=null){
            listItemView.title.setText(entity.getUserNick());
            if (!StringUtil.isEmpty(entity.getAvatar())){
                listItemView.img.setImageBitmap(ImageUtils.getBitmapByPath(mAppContext.getCameraPath() + "/crop_" + entity.getAvatar()));
            }
        }
        return convertView;
    }
}
