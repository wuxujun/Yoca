package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.DatabaseHelper;

/**
 * Created by xujunwu on 7/16/15.
 */
public class BaseFragment extends SherlockFragment{

    protected Context mContext;
    protected AppContext appContext;

    protected View mContentView;


    protected ListView mListView;

    protected DatabaseHelper databaseHelper;


    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        appContext = (AppContext) getActivity().getApplication();
    }

    @Override
    public void onDestroy() {
        if (databaseHelper!=null){
            databaseHelper.close();
            databaseHelper=null;
        }
        super.onDestroy();
    }
}
