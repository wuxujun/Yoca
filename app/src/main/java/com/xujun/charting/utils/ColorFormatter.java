package com.xujun.charting.utils;


import com.xujun.charting.data.Entry;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 *
 * Created by xujunwu on 14/12/31.
 */
public interface ColorFormatter {

    public int getColor(Entry e, int index);
}
