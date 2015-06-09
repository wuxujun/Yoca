package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xujun.app.yoca.fragment.ContentFragment;
import com.xujun.app.yoca.fragment.MyFragment;
import com.xujun.model.ArticleInfo;
import com.xujun.util.StringUtil;

/**
 * Created by xujunwu on 15/6/9.
 */
public class WebActivity extends SherlockActivity {



    private Context mContext;
    private AppContext appContext;

    private ArticleInfo     localArticelInfo;

    private WebView         mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mContext = getApplicationContext();
        appContext = (AppContext) getApplication();

        localArticelInfo=(ArticleInfo)getIntent().getSerializableExtra("info");


        mWebView=(WebView)findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultFixedFontSize(15);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(final WebView view,final String url){
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view,int progress){
                if (progress==100){

                }
                super.onProgressChanged(view,progress);
            }
        });
        if (localArticelInfo!=null){
            getActionBar().setTitle(localArticelInfo.getTitle());

            if (!StringUtil.isEmpty(localArticelInfo.getContent())){
                mWebView.loadDataWithBaseURL(null,localArticelInfo.getContent(),"text/html","utf-8",null);
            }
        }
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configPlatforms();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int keyCode=event.getKeyCode();
        if (event.getAction()==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getSupportMenuInflater().inflate(R.menu.shared, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.item_menu_shared:{
                mController.getConfig().setPlatforms(SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SMS,SHARE_MEDIA.EMAIL);
                mController.openShare(this,false);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     *分享
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    private void configPlatforms()
    {
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        SmsHandler smsHandler=new SmsHandler();
        smsHandler.addToSocialSDK();

        EmailHandler emailHandler=new EmailHandler();
        emailHandler.addToSocialSDK();

        String appId="wx967daebe835fbeac";
        String appSecret="5bb696d9ccd75a38c8a9bfe0675559b3";
        UMWXHandler umwxHandler=new UMWXHandler(this,appId,appSecret);
        umwxHandler.addToSocialSDK();

        UMWXHandler umwxHandler1=new UMWXHandler(this,appId,appSecret);
        umwxHandler1.setToCircle(true);
        umwxHandler1.addToSocialSDK();

        appId="100424468";
        appSecret="c739f704798a158208a74ab60104f0ba";
        UMQQSsoHandler umqqSsoHandler=new UMQQSsoHandler(this,appId,appSecret);
//        umqqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        umqqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler=new QZoneSsoHandler(this,appId,appSecret);
        qZoneSsoHandler.addToSocialSDK();
    }

}
