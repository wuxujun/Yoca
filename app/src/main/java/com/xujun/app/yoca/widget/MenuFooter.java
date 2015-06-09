package com.xujun.app.yoca.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xujun.app.yoca.R;

/**
 * Created by xujunwu on 14/12/10.
 */
public class MenuFooter extends LinearLayout implements View.OnClickListener{


    private MenuController      menuController;

    private Context         mContext;
    private View            mContentView;

    public MenuFooter(Context context){
        super(context,null);
        init(context);
    }

    public void init(Context context){
        mContext=context;
        mContentView= LayoutInflater.from(mContext).inflate(R.layout.menu_footer,null);


        mContentView.findViewById(R.id.llAccountM).setOnClickListener(this);
        mContentView.findViewById(R.id.llOther).setOnClickListener(this);
        mContentView.findViewById(R.id.llGroup).setOnClickListener(this);
        mContentView.findViewById(R.id.llSetting).setOnClickListener(this);
        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        addView(mContentView,lp);
    }

    @Override
    public  void onClick(View view){
        switch (view.getId()){
            case R.id.llAccountM:{
                getMenuController().onMenuClicked(0);
                break;
            }
            case R.id.llOther:{
                getMenuController().onMenuClicked(1);
                break;
            }
            case R.id.llGroup:{
                getMenuController().onMenuClicked(2);
                break;
            }
            case R.id.llSetting:{
                getMenuController().onMenuClicked(3);
                break;
            }
        }
    }

    public MenuController getMenuController(){
        return this.menuController;
    }

    public void setMenuController(MenuController menuController){
        Log.e("---","----------------");
        this.menuController=menuController;
    }

}
