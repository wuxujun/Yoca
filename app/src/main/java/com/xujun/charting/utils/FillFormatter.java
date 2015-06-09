package com.xujun.charting.utils;

import com.xujun.charting.data.LineData;
import com.xujun.charting.data.LineDataSet;

/**
 * Created by xujunwu on 14/12/31.
 */
public interface FillFormatter {

    /**
     * Returns the vertical (y-axis) position where the filled-line of the
     * DataSet should end.
     *
     * @param dataSet
     * @param data
     * @param chartMaxY
     * @param chartMinY
     * @return
     */
    public float getFillLinePosition(LineDataSet dataSet, LineData data, float chartMaxY,
                                     float chartMinY);
}
