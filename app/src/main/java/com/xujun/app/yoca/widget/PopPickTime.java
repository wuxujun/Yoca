package com.xujun.app.yoca.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.AccountEntity;
import com.xujun.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujunwu on 7/17/15.
 */
public class PopPickTime {

    private List<String> items=new ArrayList<String>();

    private ItemAdapter      mAdapter;

    private Context          mContext;
    private AppContext       mAppContext;
    private View             mContentView;

    private ListView         mListView;

    private PopupWindow     mPopupWindow;

    public PopPickTime(Context context, AppContext appContext, List<String> list){
        this.mContext=context;
        this.mAppContext=appContext;
        items=list;

        mContentView=LayoutInflater.from(mContext).inflate(R.layout.dialog_account,null);
        initListView();
    }

    private void initListView(){
        mAdapter=new ItemAdapter();
        mListView=(ListView)mContentView.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFocusableInTouchMode(true);
        mListView.setFocusable(true);

        mPopupWindow=new PopupWindow(mContentView,mContext.getResources().getDimensionPixelOffset(R.dimen.popmenu_width),ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void showAsDropDown(View parent){
        mPopupWindow.showAsDropDown(parent, 10, mContext.getResources().getDimensionPixelOffset(R.dimen.popmenu_yoff));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mListView.setOnItemClickListener(listener);
    }

    public void dismiss(){
        mPopupWindow.dismiss();
    }

    static class ListItemView{
        public TextView title;
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
                convertView= LayoutInflater.from(mContext).inflate(R.layout.picktime_item,null);

                listItemView=new ListItemView();
                listItemView.title=(TextView)convertView.findViewById(R.id.tvItemTitle);
                convertView.setTag(listItemView);
            }else {
                listItemView=(ListItemView)convertView.getTag();
            }

            listItemView.title.setText(items.get(position));
            return convertView;
        }
    }

}
