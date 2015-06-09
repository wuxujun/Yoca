package com.xujun.charting.data;

import java.util.ArrayList;

/**
 * Created by xujunwu on 14/12/31.
 */
public class PieData extends ChartData<PieDataSet> {

    public PieData(ArrayList<String> xVals) {
        super(xVals);
    }

    public PieData(String[] xVals) {
        super(xVals);
    }

    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    private static ArrayList<PieDataSet> toArrayList(PieDataSet dataSet) {
        ArrayList<PieDataSet> sets = new ArrayList<PieDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the DataSet this PieData object represents.
     *
     * @return
     */
    public PieDataSet getDataSet() {
        return (PieDataSet) mDataSets.get(0);
    }
}
