package com.xujun.app.yoca.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.xujun.app.yoca.R;
import com.xujun.sqlite.TargetEntity;
import com.xujun.util.StringUtil;

import java.util.ArrayList;

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
    private ArrayList<String>   xVal=new ArrayList<String>();
    private LineData        lineData;

    public LineChartItem(TargetEntity target,LineData cd,ArrayList<String> xVals,Context context){
        this(cd);
        targetEntity=target;
        this.xVal=xVals;
        this.lineData=cd;
    }
    public int getItemType(){
        return 1;
    }

    public View getView(int position,View convertView,Context context){
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.line_chart_item,null);
            holder.linearLayout=(LinearLayout)convertView.findViewById(R.id.llItem);
            holder.chart=(CombinedChart)convertView.findViewById(R.id.lineChart);
            holder.title=(TextView)convertView.findViewById(R.id.tvChartTitle);
            holder.average=(TextView)convertView.findViewById(R.id.tvChartAverage);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        switch (position%3){
            case 0:
                holder.linearLayout.setBackgroundResource(R.drawable.chart_item_normal0);
                break;
            case 1:
                holder.linearLayout.setBackgroundResource(R.drawable.chart_item_normal1);
                break;
            case 2:
                holder.linearLayout.setBackgroundResource(R.drawable.chart_item_normal2);
                break;
        }

        if (targetEntity!=null){
            holder.title.setText(targetEntity.getTitle());
            holder.average.setText("日平均值:"+targetEntity.getContent());
        }
//        holder.chart.setLogEnabled(true);
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);
        holder.chart.setTouchEnabled(false);
        holder.chart.getLegend().setEnabled(false);
        holder.chart.getLegend().setTextColor(Color.WHITE);
        XAxis xAxis=holder.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(true); //去除横线
        xAxis.setDrawAxisLine(true);
        YAxis leftAxis=holder.chart.getAxisLeft();
        leftAxis.setLabelCount(5,true);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis=holder.chart.getAxisRight();
        rightAxis.setLabelCount(5, true);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(false);

        CombinedData data=new CombinedData(this.xVal);
        data.setData(this.lineData);
        holder.chart.setData(data);
        holder.chart.animateXY(2000,2000);
        return convertView;
    }

    private static class ViewHolder{
        LinearLayout    linearLayout;
        CombinedChart chart;
        TextView    title;
        TextView    average;
    }
}
