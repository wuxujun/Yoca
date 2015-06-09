package com.xujun.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

/***
 * 
* @ClassName: StringUtil
* @Description: TODO(这里用一句话描述这个类的作用)
* @author xujunwu
* @date 2013-5-4 下午4:02:49
*
 */

public class StringUtil {

	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	
	
	public static int getWindowWidth(Context context){
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
	}
	
	public static int getWindowHeight(Context context){
		DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
	}
	
	//TODO 无小数点字符串
	public static String doubleToString(double v){
		DecimalFormat valueDF = new DecimalFormat("##0");
		return valueDF.format(v);
	}
	
	//TODO 1位小数点字符串
	public static String doubleToStringOne(double v){
		DecimalFormat valueDF = new DecimalFormat("##0.0");
		return valueDF.format(v);
	}
	
	//TODO 2位小数点字符串
	public static String doubleToStringTwo(double v){
		DecimalFormat valueDF = new DecimalFormat("##0.00");
		return valueDF.format(v);
	}
	
	public static String getUrlFileName(String url){
		String fileName= url.substring(url.lastIndexOf("/")+1); 
		return fileName;
	}
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 *  判断是否邮箱
	* @Title: isEmail
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param email
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 
	* @Title: toInt
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param str
	* @param @param defValue
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 
	* @Title: toInt
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param obj
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 
	* @Title: toLong
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param obj
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 
	* @Title: toBool
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param b
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}
	
	/***
	 * 字符串转 double
	* @Title: toDouble
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param b
	* @param @return 设定文件
	* @return 返回类型
	* @throws
	 */
	public static double toDouble(String b){
		try{
			return Double.parseDouble(b);
		}catch(Exception e){
			
		}
		return 0.0;
	}


    public static String getExternalStorageState(){
        return Environment.getExternalStorageState();
    }

    public boolean getExternalStoreState(){
        if (Environment.MEDIA_MOUNTED.equals(StringUtil.getExternalStorageState())){
            return true;
        }
        return false;
    }

    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    public static int dip2px(Context context,float px){
        final float scale=getScreenDensity(context);
        return (int)(px*scale+0.5);
    }

    public static String utf8ToGb2312(String str){
        String result="";
        try{
            result=new String(str.getBytes("utf-8"),"gb2312");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return result;
    }

    public static String gbToUtf8(String str){
        StringBuffer sb=new StringBuffer();
        try{
            for (int i=0;i<str.length();i++){
                String s=str.substring(i,i+1);
                if (s.charAt(0)>0x80){
                    byte[] bytes=s.getBytes("Unicode");
                    String binaryStr="";
                    for (int j=2;j<bytes.length;j+=2){
                        String hexStr=getHexString(bytes[j+1]);
                        String binStr=getBinaryString(Integer.valueOf(hexStr,16));
                        binaryStr+=binStr;

                        hexStr=getHexString(bytes[j]);
                        binStr=getBinaryString(Integer.valueOf(hexStr,16));
                        binaryStr+=binStr;

                    }
                    String s1="1110"+binaryStr.substring(0,4);
                    String s2="10"+binaryStr.substring(4,10);
                    String s3="10"+binaryStr.substring(10,16);
                    byte[] bs=new byte[3];
                    bs[0]=Integer.valueOf(s1,2).byteValue();
                    bs[1]=Integer.valueOf(s2,2).byteValue();
                    bs[2]=Integer.valueOf(s3,2).byteValue();
                   String ss=new String(bs,"UTF-8");
                   sb.append(ss);
                }else{
                    sb.append(s);
                }
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static  String getHexString(byte b){
        String hexStr=Integer.toHexString(b);
        int m=hexStr.length();
        if (m<2){
            hexStr="0"+hexStr;
        }else{
            hexStr=hexStr.substring(m-2);
        }
        return hexStr;
    }

    private static String getBinaryString(int i){
        String binaryStr=Integer.toBinaryString(i);
        int length=binaryStr.length();
        for (int l=0;l<8-length;l++){
            binaryStr="0"+binaryStr;
        }

        return binaryStr;
    }

    public static String urldecode(String str){
        String result="";
        try{
            result= URLDecoder.decode(str.replaceAll("%%","&&%"),"utf-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
