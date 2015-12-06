package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xujun.util.StringUtil;
import com.xujun.widget.NumericKeyboard;
import com.xujun.widget.PasswordTextView;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by xujunwu on 15/4/6.
 */
public class LockActivity extends SherlockActivity {
    private static final String TAG = "LockActivity";

    private Context mContext;
    private AppContext      appContext;

    @ViewInject(R.id.tvInfo)
    private TextView           mTVInfo;

    @ViewInject(R.id.nk)
    private NumericKeyboard nk;
    @ViewInject(R.id.et_pwd1)
    private PasswordTextView    etPwd1;

    @ViewInject(R.id.et_pwd2)
    private PasswordTextView    etPwd2;

    @ViewInject(R.id.et_pwd3)
    private PasswordTextView    etPwd3;

    @ViewInject(R.id.et_pwd4)
    private PasswordTextView    etPwd4;

    private int         type;

    private String      input;
    private StringBuffer    fBuffer=new StringBuffer();


    private static final Style INFINITE=new Style.Builder().setBackgroundColorValue(Style.holoGreenLight).build();
    private static final Configuration CONFIGURATION=new Configuration.Builder().setDuration(Configuration.DURATION_SHORT).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_activity);
        ViewUtils.inject(this);
        mContext=getApplicationContext();
        appContext=(AppContext)getApplication();
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        type=getIntent().getIntExtra("lockType",0);
        if (type==1){
            getSupportActionBar().setTitle("密码设置");
        }else{
            getSupportActionBar().setTitle("屏幕解锁");
        }
        initUI();
    }


    private void initUI(){
        nk.setOnNumberClick(new NumericKeyboard.OnNumberClick() {
            @Override
            public void onNumberReturn(int number) {
                setText(number+"");
            }
        });
        etPwd4.setOnTextChangedListener(new PasswordTextView.OnTextChangedListener() {
            public void textChanged(String content) {
                input = etPwd1.getTextContent() + etPwd2.getTextContent() + etPwd3.getTextContent() + etPwd4.getTextContent();
                if (type == 1) {
                    type = 2;
                    mTVInfo.setText("请再次输入密码");
                    fBuffer.append(input);
                    clearText();
                } else if (type == 0) {
                    String key=appContext.getProperty(AppConfig.LOCK_KEY);
                    if (input.equals(key)){
                        finish();
                    }else{
                        showCrouton("密码输入错误.");
                    }
                } else if (type == 2) {
                    if (input.equals(fBuffer.toString())) {
                        showCrouton("密码设置成功");
                        appContext.setProperty(AppConfig.LOCK_KEY,input);
                        finish();
                    } else {
                        showCrouton("两次输入密码不一致");
                        clearText();
                    }
                }
            }
        });
    }


    private void showCrouton(String message){
        Crouton crouton;
        crouton=Crouton.makeText(this,message,INFINITE);
        crouton.setConfiguration(CONFIGURATION).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(type==1){
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clearText(){
        etPwd1.setTextContent("");
        etPwd2.setTextContent("");
        etPwd3.setTextContent("");
        etPwd4.setTextContent("");

    }

    private void deleteText(){
        if (!StringUtil.isEmpty(etPwd4.getTextContent())){
            etPwd4.setTextContent("");
        }else if (!StringUtil.isEmpty(etPwd3.getTextContent())){
            etPwd3.setTextContent("");
        }else if (!StringUtil.isEmpty(etPwd2.getTextContent())){
            etPwd2.setTextContent("");
        }else if (!StringUtil.isEmpty(etPwd1.getTextContent())){
            etPwd1.setTextContent("");
        }
    }

    private void setText(String text){
        if (StringUtil.isEmpty(etPwd1.getTextContent())){
            etPwd1.setTextContent(text);
        }else if (StringUtil.isEmpty(etPwd2.getTextContent())){
            etPwd2.setTextContent(text);
        }else if (StringUtil.isEmpty(etPwd3.getTextContent())){
            etPwd3.setTextContent(text);
        }else if (StringUtil.isEmpty(etPwd4.getTextContent())){
            etPwd4.setTextContent(text);
        }
    }

    public void doClick(View v){
        switch (v.getId()){
            case R.id.btn_again:{
                clearText();
                break;
            }
            case R.id.btn_delete:{
                deleteText();
                break;
            }
        }
    }

}
