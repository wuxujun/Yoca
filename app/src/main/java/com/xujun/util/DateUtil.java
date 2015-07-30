package com.xujun.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	public static final long INTERVAL_OF_WEEK = 1000 * 60 * 60 * 24 * 7;
	public static final long INTERVAL_OF_DAY = 1000 * 60 * 60 * 24;

	private DateUtil() {
		throw new RuntimeException("private constructor!");
	}

	public static String getCurrentDate(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sf.format(new Date());
	}
	
	public static long getDistance(int day, int hour,int minute) {
		Calendar then = Calendar.getInstance();
		then.set(Calendar.DAY_OF_WEEK, day);
		then.set(Calendar.HOUR_OF_DAY, hour);
		then.set(Calendar.MINUTE, 0);
		then.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();
		long distance = then.getTimeInMillis() - now.getTimeInMillis();
		if (distance < 0) {
			distance += INTERVAL_OF_WEEK;
		}
		return distance;
	}

	public static long getDistance(int hour) {
		Calendar then = Calendar.getInstance();
		then.set(Calendar.HOUR_OF_DAY, hour);
		then.set(Calendar.MINUTE, 0);
		then.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();
		long distance = then.getTimeInMillis() - now.getTimeInMillis();
		if (distance < 0) {
			distance += INTERVAL_OF_DAY;
		}
		return distance;
	}
	
	public static long getDistance(int hour,int minute){
		Calendar then = Calendar.getInstance();
		then.set(Calendar.HOUR_OF_DAY, hour);
		then.set(Calendar.MINUTE, minute);
		then.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();
		long distance = then.getTimeInMillis() - now.getTimeInMillis();
		if (distance < 0) {
			distance += INTERVAL_OF_DAY;
		}
		return distance;
	}
	
	public static long getDayDiff(String endDay){
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
            java.util.Date endDate = format.parse(endDay);
            long day = (endDate.getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000);
            System.out.println("相隔的天数=" + day);
            return day;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0l;
    }

    public static long getDayDiff(String startDay,String endDay){
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
            java.util.Date startDate=format.parse(startDay);
            java.util.Date endDate = format.parse(endDay);
            long day = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
            System.out.println("相隔的天数=" + day);
            return day;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0l;
    }

    public static Date dayToDate(String day){
        try{
            java.text.SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");

            java.util.Date endDate = format.parse(day);
            return endDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    public static String getDayString(Date date){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        try{
            return sf.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sf.format(new Date());
    }

    public static String getDateString(long time){
        SimpleDateFormat sf=new SimpleDateFormat("MM-dd HH:mm");
        java.util.Date dt=new Date(time);
        return sf.format(dt);
    }

    public static String getTimeString(long time){
        SimpleDateFormat sf=new SimpleDateFormat("HH:mm");
        java.util.Date dt=new Date(time);
        return sf.format(dt);
    }

    /**
     * 周日
     * @return
     */
    public static String getMondayDate() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        int curDay = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.setTime(date);
        if (curDay == 1) {
            calendar.add(GregorianCalendar.DATE, -7);
        } else {
            calendar.add(GregorianCalendar.DATE, 1 - curDay);
        }
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(calendar.getTime());
    }

    public static String getMonthForDate(String date) {
        Date newDate=dayToDate(date);
        SimpleDateFormat sf=new SimpleDateFormat("MM-dd");
        return sf.format(newDate.getTime());
    }

    public static String getDayForDate(String date) {
        Date newDate=dayToDate(date);
        SimpleDateFormat sf=new SimpleDateFormat("dd");
        return sf.format(newDate.getTime());
    }

    public static String getWeekForDate(String date){
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(dayToDate(date));
        int day=calendar.get(Calendar.DAY_OF_WEEK);
        String strWeek="周一";
        switch (day){
            case 0:
                strWeek="周六";
                break;
            case 1:
                strWeek="周日";
                break;
            case 2:
                strWeek="周一";
                break;
            case 3:
                strWeek="周二";
                break;
            case 4:
                strWeek="周三";
                break;
            case 5:
                strWeek="周四";
                break;
            case 6:
                strWeek="周五";
                break;
            default:
                strWeek="周六";
                break;
        }
        Log.e("========>"," "+date+"  ----------------> "+strWeek);
        return strWeek;
    }

    public static String getMondayOFWeek(){
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = sf.format(monday);
        return preMonday;
    }

    public static String getCurrentWeekday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus+6);
        Date monday = currentDate.getTime();

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = sf.format(monday);
        return preMonday;
    }

    public static String getSaturday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
        Date monday = currentDate.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = sf.format(monday);
        return preMonday;
    }

    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-1;         //因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

}