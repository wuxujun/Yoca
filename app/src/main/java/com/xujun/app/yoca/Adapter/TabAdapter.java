package com.xujun.app.yoca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.HealthEntity;

import java.util.List;

/**
 * Created by xujunwu on 15/4/6.
 */
public class TabAdapter extends BaseAdapter{
    private Context context;//运行上下文
    private List<HealthEntity> listItems;//数据集合
    private LayoutInflater listContainer;//视图容器
    private int 						itemViewResource;//自定义项视图源

    private int                         currentIndex;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    static class ListItemView{				//自定义控件集合
        public TextView title;
        public ImageView img;
    }

    public TabAdapter(Context context,List<HealthEntity> lists,int resource){
        this.context = context;
        this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
        this.itemViewResource = resource;
        this.listItems = lists;
    }

    public int getCount() {
        return listItems.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView  listItemView = null;

        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();
            listItemView.title=(TextView)convertView.findViewById(R.id.tv_tabitem);

            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }
        HealthEntity info=listItems.get(position);
        if (info!=null){
//            listItemView.title.setText(AppConfig.getAppConfig(context).getTargetType(info.getTargetType()));
        }

        if (currentIndex==position){
            listItemView.title.setTextColor(context.getResources().getColor(R.color.red));
            convertView.setSelected(true);
        }else{
            listItemView.title.setTextColor(context.getResources().getColor(R.color.black));
            convertView.setSelected(false);
        }
        return convertView;
    }

}
