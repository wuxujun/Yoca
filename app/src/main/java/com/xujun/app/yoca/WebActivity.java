package com.xujun.app.yoca;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.xujun.sqlite.InfoEntity;
import com.xujun.util.StringUtil;

/**
 * Created by xujunwu on 15/6/9.
 */
public class WebActivity extends BaseActivity {

    private InfoEntity localArticelInfo;

    private WebView         mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mContext = getApplicationContext();
        appContext = (AppContext) getApplication();

        localArticelInfo=(InfoEntity)getIntent().getSerializableExtra("info");


        mWebView=(WebView)findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultFixedFontSize(15);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {

                }
                super.onProgressChanged(view, progress);
            }
        });
        if (localArticelInfo!=null){
            mHeadTitle.setText(localArticelInfo.getTitle());
            if (!StringUtil.isEmpty(localArticelInfo.getContent())){
                mWebView.loadDataWithBaseURL(null,localArticelInfo.getContent(),"text/html","utf-8",null);
            }
        }

        mHeadButton.setText(getText(R.string.btn_shared));
        mHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.getConfig().setPlatforms(SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
                mController.openShare(WebActivity.this, false);
            }
        });

        mHeadIcon.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        configPlatforms();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.item_menu_shared:{
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

        String appId=AppConfig.WEIXIN_APPID;
        String appSecret=AppConfig.WEIXIN_APPSECRET;
        UMWXHandler umwxHandler=new UMWXHandler(this,appId,appSecret);
        umwxHandler.addToSocialSDK();

        UMWXHandler umwxHandler1=new UMWXHandler(this,appId,appSecret);
        umwxHandler1.setToCircle(true);
        umwxHandler1.addToSocialSDK();

        appId=AppConfig.QQ_APPID;
        appSecret=AppConfig.QQ_APPSECRET;
        UMQQSsoHandler umqqSsoHandler=new UMQQSsoHandler(this,appId,appSecret);
//        umqqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        umqqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler=new QZoneSsoHandler(this,appId,appSecret);
        qZoneSsoHandler.addToSocialSDK();
    }

}
