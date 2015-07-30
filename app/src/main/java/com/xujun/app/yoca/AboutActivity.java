package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * Created by xujunwu on 15/4/6.
 */
public class AboutActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);

        ((TextView)findViewById(R.id.tvAbountVersion)).setText("Version:" + appContext.getVersionName());
        mHeadTitle.setText("关于我们");
        mHeadButton.setVisibility(View.INVISIBLE);
        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
