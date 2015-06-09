package com.xujun.charting.data;

import java.util.ArrayList;

/**
 * Created by xujunwu on 14/12/31.
 */
public abstract class BarLineScatterCandleData<T extends BarLineScatterCandleRadarDataSet<? extends Entry>>
        extends BarLineScatterCandleRadarData<T> {

    public BarLineScatterCandleData(ArrayList<String> xVals) {
        super(xVals);
    }

    public BarLineScatterCandleData(String[] xVals) {
        super(xVals);
    }

    public BarLineScatterCandleData(ArrayList<String> xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }

    public BarLineScatterCandleData(String[] xVals, ArrayList<T> sets) {
        super(xVals, sets);
    }
}
