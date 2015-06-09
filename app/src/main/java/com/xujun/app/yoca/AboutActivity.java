package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * Created by xujunwu on 15/4/6.
 */
public class AboutActivity extends SherlockActivity {

    private Context mContext;
    private AppContext appContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();
        ((TextView)findViewById(R.id.tvAbountVersion)).setText("Version:"+appContext.getVersionName());
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("关于我们");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
