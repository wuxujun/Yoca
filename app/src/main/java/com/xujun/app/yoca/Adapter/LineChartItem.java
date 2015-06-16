package com.xujun.app.yoca.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.xujun.app.yoca.R;

/**
 * Created by xujunwu on 15/6/15.
 */
public class LineChartItem extends ChartData{

    private ChartData<?>   mChartData;
    public LineChartItem(ChartData<?> cd){
        this.mChartData=cd;
    }

    public LineChartItem(LineData cd,Context context){
        this(cd);
    }
    public int getItemType(){
        return 1;
    }

    public View getView(int position,View convertView,Context context){
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.line_chart_item,null);
            holder.chart=(LineChart)convertView.findViewById(R.id.lineChart);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);
        holder.chart.setTouchEnabled(false);
        holder.chart.getLegend().setTextColor(Color.WHITE);
        XAxis xAxis=holder.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis=holder.chart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis=holder.chart.getAxisRight();
        rightAxis.setLabelCount(5);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(false);
        holder.chart.setData((LineData) mChartData);

        holder.chart.animateX(750);
        return convertView;
    }

    private static class ViewHolder{
        LineChart   chart;
    }
}
