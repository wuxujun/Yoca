package com.xujun.charting.data;

import java.util.ArrayList;

/**
 * Created by xujunwu on 14/12/31.
 */
public class CandleData extends BarLineScatterCandleData<CandleDataSet> {

    public CandleData(ArrayList<String> xVals) {
        super(xVals);
    }

    public CandleData(String[] xVals) {
        super(xVals);
    }

    public CandleData(ArrayList<String> xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(ArrayList<String> xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public CandleData(String[] xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    private static ArrayList<CandleDataSet> toArrayList(CandleDataSet dataSet) {
        ArrayList<CandleDataSet> sets = new ArrayList<CandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
