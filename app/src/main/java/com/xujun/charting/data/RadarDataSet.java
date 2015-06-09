package com.xujun.charting.data;


import java.util.ArrayList;

/**
 * Created by xujunwu on 14/12/31.
 */
public class RadarDataSet extends LineRadarDataSet<Entry> {

    public RadarDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<Entry> copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        RadarDataSet copied = new RadarDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }
}
