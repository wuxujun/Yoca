package com.xujun.charting.interfaces;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xujunwu on 14/12/31.
 */
public class OnDrawLineChartTouchListener  extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
