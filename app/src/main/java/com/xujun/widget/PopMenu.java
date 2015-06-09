package com.xujun.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import com.xujun.app.yoca.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by xujunwu on 13-8-28.
 */
public class PopMenu{

    private List<String>     itemList=new ArrayList<String>();

    private Context          mContext;
    private View             mContentView;

    private PopupWindow      mPopupWindow;
    private ListView         mListView;

    public PopMenu(Context context,List<String>  lists){
        this.mContext=context;
        itemList=lists;
        mContentView= LayoutInflater.from(mContext).inflate(R.layout.popmenu,null);

        mListView=(ListView)mContentView.findViewById(R.id.listView);
        mListView.setAdapter(new ItemAdapter());
        mListView.setFocusableInTouchMode(true);
        mListView.setFocusable(true);

        mPopupWindow=new PopupWindow(mContentView,mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_width), ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void showAsDropDown(View parent){
        mPopupWindow.showAsDropDown(parent,10,mContext.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }

    public void dismiss(){
        mPopupWindow.dismiss();

    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mListView.setOnItemClickListener(listener);
    }


    class ItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i) {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView==null){
                convertView=LayoutInflater.from(mContext).inflate(R.layout.pomenu_item,null);
                holder=new ViewHolder();
                convertView.setTag(holder);
                holder.title=(TextView)convertView.findViewById(R.id.tvTitle);
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            holder.title.setText(itemList.get(position));
            return convertView;
        }

        private final class ViewHolder{
            TextView    title;
        }
    }



}
