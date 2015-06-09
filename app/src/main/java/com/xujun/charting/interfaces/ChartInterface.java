package com.xujun.charting.interfaces;

import android.graphics.RectF;
import android.view.View;

/**
 * Created by xujunwu on 14/12/31.
 */
public interface ChartInterface {

    public float getOffsetBottom();

    public float getOffsetTop();

    public float getOffsetLeft();

    public float getOffsetRight();

    public float getDeltaX();

    public float getDeltaY();

    public float getYChartMin();

    public float getYChartMax();

    public int getWidth();

    public int getHeight();

    public RectF getContentRect();

    public View getChartView();
}
