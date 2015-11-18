package com.xujun.app.yoca.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.TargetEntity;

/**
 * 列表图
 * Created by xujunwu on 15/6/15.
 */
public class LineChartItem extends ChartData{

    private ChartData<?>   mChartData;
    public LineChartItem(ChartData<?> cd){
        this.mChartData=cd;
    }
    private TargetEntity  targetEntity;

    public LineChartItem(TargetEntity target,LineData cd,Context context){
        this(cd);
        targetEntity=target;
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
            holder.title=(TextView)convertView.findViewById(R.id.tvChartTitle);
            holder.average=(TextView)convertView.findViewById(R.id.tvChartAverage);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        if (targetEntity!=null){
            holder.title.setText(targetEntity.getTitle());
            holder.average.setText("日平均值:"+targetEntity.getContent());
        }
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);
        holder.chart.setTouchEnabled(false);
        holder.chart.getLegend().setEnabled(false);
        holder.chart.getLegend().setTextColor(Color.WHITE);
        XAxis xAxis=holder.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false); //去除横线
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
        TextView    title;
        TextView    average;
    }
}
