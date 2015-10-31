package com.xujun.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xujun.app.yoca.R;


/**
 * Created by xujunwu on 15/8/24.
 */
public class SelectPopupWindow  extends PopupWindow {

    private TextView        mTitleTextView;
    private ListView        mListView;

    public SelectPopupWindow(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View interalView = inflater.inflate(R.layout.select_list_popup, null);
        setContentView(interalView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);

        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(),
                (Bitmap) null));
        mListView = (ListView) interalView.findViewById(R.id.list);
        mTitleTextView=(TextView)interalView.findViewById(R.id.tv_select_dialog_title);

    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView mListView) {
        this.mListView = mListView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public void setTitleTextView(TextView mTitleTextView) {
        this.mTitleTextView = mTitleTextView;
    }
}
