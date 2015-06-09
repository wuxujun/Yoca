package com.xujun.charting.utils;

/**
 * Created by xujunwu on 14/12/31.
 */
public interface ValueFormatter {
    /**
     * Called when a value (from labels, or inside the chart) is formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @return the formatted label ready for being drawn
     */
    public String getFormattedValue(float value);
}
