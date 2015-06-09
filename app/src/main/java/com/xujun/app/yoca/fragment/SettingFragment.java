package com.xujun.app.yoca.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.update.UmengUpdateAgent;
import com.xujun.app.yoca.AboutActivity;
import com.xujun.app.yoca.AppConfig;
import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.HomeActivity;
import com.xujun.app.yoca.LockActivity;
import com.xujun.app.yoca.LockSetupActivity;
import com.xujun.app.yoca.R;
import com.xujun.app.yoca.WarnActivity;
import com.xujun.app.yoca.fragment.WarnFragment;
import com.xujun.widget.ToggleButton;

/**
 * Created by xujunwu on 14/12/10.
 */
public class SettingFragment extends SherlockFragment implements View.OnClickListener{

    public static final String TAG = "SettingFragment";

    private  View   mContentView;


    private Context mContext;
    private AppContext appContext;

    private ToggleButton            mPrePassTB;
    private ToggleButton            mAutoLoginTB;


    private String mAutoLogin="0";
    private String mUserLock="0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView=inflater.inflate(R.layout.layout_setting,null);

        mContentView.findViewById(R.id.llWarn).setOnClickListener(this);
        mContentView.findViewById(R.id.llGroup).setOnClickListener(this);
//        mContentView.findViewById(R.id.llPrvPassword).setOnClickListener(this);
        mContentView.findViewById(R.id.llVersion).setOnClickListener(this);
        mContentView.findViewById(R.id.llAbout).setOnClickListener(this);
        mContentView.findViewById(R.id.tvLogout).setOnClickListener(this);

        ((TextView)mContentView.findViewById(R.id.tvVersion)).setText("当前版本:" + appContext.getVersionName());

        mPrePassTB=(ToggleButton)mContentView.findViewById(R.id.tbPrePwd);
        mPrePassTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    Intent intent=new Intent(getSherlockActivity(),LockSetupActivity.class);
                    getSherlockActivity().startActivity(intent);
                }else{
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_LOCK_TYPE,"0");
                    Intent intent=new Intent(getSherlockActivity(),LockActivity.class);
                    getSherlockActivity().startActivity(intent);
                }
            }
        });

        mAutoLoginTB=(ToggleButton)mContentView.findViewById(R.id.tbAutoLogin);

        mAutoLoginTB.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_AUTO_LOGIN,"1");
                }else{
                    AppConfig.getAppConfig(mContext).set(AppConfig.USER_AUTO_LOGIN,"0");
                }
            }
        });


        if (mAutoLogin!=null&&mAutoLogin.equals("1")){
            mAutoLoginTB.setToggleOn();
        }
        return mContentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext=getSherlockActivity().getApplicationContext();
        appContext=(AppContext)getActivity().getApplication();

        mAutoLogin=AppConfig.getAppConfig(mContext).get(AppConfig.USER_AUTO_LOGIN);
        getSherlockActivity().getActionBar().setHomeAsUpIndicator(R.drawable.back);
//        getSherlockActivity().getActionBar().setSubtitle(getResources().getString(R.string.menu_setting));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llWarn:{
//                SherlockFragment fragment=(SherlockFragment)getFragmentManager().findFragmentById(R.id.content_frame);
//                if(fragment instanceof WarnFragment) {
//                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
//                }else{
//                    getFragmentManager().beginTransaction().replace(R.id.content_frame,new WarnFragment()).commit();
//                }
                Intent intent=new Intent(getSherlockActivity(),WarnActivity.class);
                getSherlockActivity().startActivity(intent);

                break;
            }
            case R.id.llGroup:{

                break;
            }
            case R.id.llPrvPassword:{

                break;
            }
            case R.id.tbAutoLogin:{

            }
            case R.id.llVersion:{
                UmengUpdateAgent.setDefault();
                UmengUpdateAgent.forceUpdate(getSherlockActivity());
                break;
            }
            case R.id.llAbout:{
                Intent intent=new Intent(getSherlockActivity(),AboutActivity.class);
                getSherlockActivity().startActivity(intent);
                break;
            }
            case R.id.tvLogout:{
                appContext.setProperty("login_flag","0");
                Intent intent=new Intent(getSherlockActivity(),HomeActivity.class);
                startActivity(intent);
                getSherlockActivity().finish();
                break;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.d(TAG, "onResume");

        mUserLock=AppConfig.getAppConfig(mContext).get(AppConfig.USER_LOCK_PASS);
        if (mUserLock!=null&&mUserLock.equals("1")){
            mPrePassTB.setToggleOn();
        }else{
            mPrePassTB.setToggleOff();
        }
    }


    public static class UAlertDialogFragment extends SherlockDialogFragment {
        public static UAlertDialogFragment newInstance(int title){
            UAlertDialogFragment fragment=new UAlertDialogFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("title",title);
            fragment.setArguments(bundle);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Light_Panel);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

            View v=inflater.inflate(R.layout.layout_dialog_settag,container,false);

            return v;
        }

    }
}
