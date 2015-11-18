package com.xujun.app.yoca;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xujun.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.Properties;

/**
 * Created by xujunwu on 14/12/20.
 */
public class AppConfig {
    private final static String APP_CONFIG="config";

    public final static String APP_DEVICE_UUID="WSD01";

    public final static int    ACTION_WEIGH_DATA_UPLOAD=1;
    public final static int    ACTION_WEIGH_DATA_DOWNLOAD=2;
    public final static int    ACTION_ACCOUNT_SYNC=3;
    public final static int    ACTION_WARNDATA_SYNC=4;
    public final static int    ACTION_WEIGH_DATA_AVATAR=5;

    public final static String ACTION_START_WEIGH="com.xujun.yoca.ACTION_START_WEIGH";


    public final static String EXTRA_DATA_HEIGHT="com.xujun.yoca.EXTRA_DATA_HEIGHT";
    public final static String EXTRA_DATA_AGE="com.xujun.yoca.EXTRA_DATA_AGE";
    public final static String EXTRA_DATA_SEX="com.xujun.yoca.EXTRA_DATA_SEX";
    public final static String EXTRA_DATA_UNIT="com.xujun.yoca.EXTRA_DATA_UNIT";

    public final static String SMS_APPKEY="6de96e7d9e78";
    public final static String SMS_APPKSECRET="ee6510b139aa06a0a403dd4410646bef";


    public final static String WEIXIN_APPID="wx7b3c49ed03390fb2";
    public final static String WEIXIN_APPSECRET="232c9d206dbe83a905294ad00d0b8738";

    public final static String QQ_APPID="100334902";
    public final static String QQ_APPSECRET="c4b60d276b112c4aee8c30bbe62b1286";


    public final static String  SHARE_TITLE="YOCA";
    public final static String  SHARE_CONTENT="分享内容";
    public final static String  SHARE_WEBSITE="http://121.40.19.136";
    public final static String  SHARE_IMAGE="YOCA";


    public final static int   SEX_FAMALE=0;
    public final static int   SEX_MALE=1;
    public final static int   UNIT_KG=1;
    public final static int   UNIT_LB=2;

    public final static String  PARAM_SOURCE_TYPE="param_source_type";
    public final static String  PARAM_ACCOUNT_DATA_TYPE="param_account_data_type";

    public final static String  PARAM_ACCOUNT="param_account_info";
    public final static String  PARAM_SELECT_DATA_TYPE="param_select_data_type";

    public final static int     SELECT_DATA_TYPE_MODE=1;
    public final static int     SELECT_DATA_TYUE_LED=2;

    public final static String  CONF_BUST="conf_bust";
    public final static String  CONF_HIPS="conf_hips";
    public final static String  CONF_WAISTLINE="conf_waistline";

    public final static int    SUCCESS=100;

    public final static int    REQUEST_ENABLE_BT=1;
    public final static int    REQUEST_TAKE_PHOTO=3;
    public final static int    REQUEST_CHOOSE_PIC=4;
    public final static int    REQUEST_CROP_PHOTO=5;
    public final static int    REQUEST_SWITCH_ACCOUNT=6;
    public final static int    REQUEST_SHOW_MODE=7;
    public final static int    REQUEST_LED_LEVEL=8;


    public final static int   REQUEST_ACCOUNT_FRAGMENT_TYPE_NORMAL=100;
    public final static int   REQUEST_ACCOUNT_FRAGMENT_TYPE_MANAGER=200;
    public final static int   REQUEST_ACCOUNT_FRAGMENT_TYPE_OTHER=300;
    public final static int   REQUEST_ACCOUNT_ADD=400;

    public final static int   WRITE_DEVICE_SET_MODE=1;
    public final static int   WRITE_DEVICE_SET_LEDLIGHT=2;

    public final static String CONF_FIRST_START="conf_first_start";

    public final static String DATA_VERSION= "data_version";
    public final static String CONF_APP_UNIQUEID="APP_UNIQUEID";
    public final static String CONF_COOKIE="cookie";


    public final static String USER_SHOW_TARGET="pre_show_target";
    public final static String USER_AUTO_LOGIN="pre_auto_login";
    public final static String USER_LOCK_PASS="pre_user_lock";
    public final static String USER_LOCK_TYPE="pre_user_lock_type";

    public final static String DEVICE_SET_SHOW_MODEL="pre_device_show_model";
    public final static String DEVICE_SET_SHOW_MODEL_TITLE="pre_device_show_model_title";
    public final static String DEVICE_SET_WEIGHT_MODEL="pre_device_weight_model";
    public final static String DEVICE_SET_SHOW_UNIT="pre_device_show_unit";
    public final static String DEVICE_SET_LED_LEVEL="pre_device_led_level";
    public final static String DEVICE_SET_LED_LEVEL_TITLE="pre_device_led_level_title";



    public final static String WEIBO_APPID="2846029855";
    public final static String WEIBO_APPSECRET="6edd97b068547492519b8eefa084f344";

    public final static String CONF_LOGIN_ACCOUNT="conf_login_account";
    public final static String CONF_LOGIN_PASSWORD="conf_login_password";

    public final static String CONF_LOGIN_FIRST="conf_login_first";
    public final static String CONF_LOGIN_FLAG="conf_login_flag";

    public final static String CONF_USER_TYPE="conf_login_user_type";
    public final static String CONF_USER_UID="conf_login_uid";
    public final static String CONF_USER_AVATAR="conf_user_avatar";
    public final static String CONF_USER_NICK="conf_user_nick";

    public final static String LOCK="lock";
    public final static String LOCK_KEY="lock_key";

    public final static String HOME_TARGET_SHOW_INDEX_0="home_target_index_0";
    public final static String HOME_TARGET_SHOW_INDEX_1="home_target_index_1";
    public final static String HOME_TARGET_SHOW_INDEX_2="home_target_index_2";
    public final static String HOME_TARGET_SHOW_INDEX_3="home_target_index_3";
    public final static String HOME_TARGET_SHOW_INDEX_4="home_target_index_4";
    public final static String HOME_TARGET_SHOW_INDEX_5="home_target_index_5";
    public final static String HOME_TARGET_SHOW_INDEX_6="home_target_index_6";
    public final static String HOME_TARGET_SHOW_INDEX_7="home_target_index_7";
    public final static String HOME_TARGET_SHOW_INDEX_8="home_target_index_8";


    private Context mContext;
    private static AppConfig    appConfig;

    public static AppConfig getAppConfig(Context context){
        if (appConfig==null){
            appConfig=new AppConfig();
            appConfig.mContext=context;
        }
        return appConfig;
    }

    public static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getCookie(){
        return get(CONF_COOKIE);
    }

    public String get(String key){
        Properties props=get();
        return (props!=null)?props.getProperty(key):null;
    }

    public Properties get(){
        FileInputStream fis=null;
        Properties props=new Properties();
        try {
            File dirConf=mContext.getDir(APP_CONFIG,Context.MODE_PRIVATE);
            fis=new FileInputStream(dirConf.getPath()+File.separator+APP_CONFIG);
            props.load(fis);
        }catch (Exception e){

        }finally {
            try {
                fis.close();
            }catch (Exception e){

            }
        }
        return props;
    }

    private void setProps(Properties p){
        FileOutputStream fos=null;
        try {
            File dirConf=mContext.getDir(APP_CONFIG,Context.MODE_PRIVATE);
            File conf=new File(dirConf,APP_CONFIG);
            fos=new FileOutputStream(conf);
            p.store(fos,null);
            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            }catch (Exception e){

            }
        }
    }

    public void set(Properties ps){
        Properties props=get();
        props.putAll(ps);
        setProps(props);
    }
    public void set(String key,String value){
        Properties props=get();
        props.setProperty(key,value);
        setProps(props);
    }

    public void remove(String... key){
        Properties props=get();
        for (String k:key){
            props.remove(k);
        }
        setProps(props);
    }

    public String getTargetType(int type){
        String result="";
        switch (type){
            case 1:
                return "体重";
            case 2:
                return "BMI";
            case 3:
                return "体脂率";
            case 4:
                return "皮下脂肪";
            case 5:
                return "内脏脂肪";
            case 6:
                return "基础代谢";
            case 7:
                return "水份含量";
            case 8:
                return "肌肉重量";
            case 9:
                return "骨量重量";
            case 10:
                return "蛋白质量";
            case 11:
                return "身体年龄";
            case 0:
                return "Sholai指数";
        }
        return result;

    }

    public String getTargetTypeUnit(int type){
        String result="0";
        switch (type){
            case 1:
                return "Kg";
            case 2:
                return "";
            case 3:
                return "%";
            case 4:
                return "%";
            case 5:
                return "0";
            case 6:
                return "Cal";
            case 7:
                return "%";
            case 8:
                return "Kg";
            case 9:
                return "Kg";
        }
        return result;
    }

    public String getChartTitle(int type){
        return getTargetType(type)+"－趋势图";
    }

    public int getWeightStatus(int height,int sex,double value){
        int result=1;
        switch (sex){
            case 0:{
                int val=height-100;
                if (value<(val*1.1)){
                    result=0;
                }else if (value>(val*1.1)){
                    result=2;
                }

                break;
            }
            default:{
                int val=height-105;
                if (value<(val*1.1)){
                    result=0;
                }else if (value>(val*1.1)){
                    result=2;
                }
                break;
            }
        }
        return result;
    }

    /**
     *
     * @param height
     * @param sex
     * @param value
     * @return
     */
    public String getWeightTitle(int height,int sex,double value){
        String result="标准";
        switch (sex){
            case 0:{
                double val=(height-100)*0.9;
                if (value<(val*1.1)){
                    result="偏瘦";
                }else if (value>(val*1.1)){
                    result="偏胖";
                }

                break;
            }
            default:{
                double val=(height-100)*0.9;
                if (value<(val*1.1)){
                        result="偏瘦";
                    }else if (value>(val*1.1)){
                        result="偏胖";
                    }
                break;
            }
        }
        return result;
    }

    public int getFatStatus(int age,int sex,double value){
        int result=1;
        switch (sex){
            case 0:{
                if (age>=18&&age<=39){
                    if (value<21.0){
                        result=0;
                    }else if (value>27.0){
                        result=2;
                    }
                }else if(age>=40&&age<=59){
                    if (value<22.0){
                        result=0;
                    }else if (value>29.0){
                        result=2;
                    }
                }else if(age>=60){
                    if (value<23.0){
                        result=0;
                    }else if (value>29.0){
                        result=2;
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=39){
                    if (value<11.0){
                        result=0;
                    }else if (value>17.0){
                        result=2;
                    }
                }else if(age>=40&&age<=59){
                    if (value<12.0){
                        result=0;
                    }else if (value>17.0){
                        result=2;
                    }
                }else if(age>=60){
                    if (value<14.0){
                        result=0;
                    }else if (value>19.0){
                        result=2;
                    }
                }
                break;
            }
        }
        return result;
    }

    public String getFatTitle(int age,int sex,double value){
        String result="健康";
        switch (sex){
            case 0:{
                if (age>=18&&age<=39){
                    if (value<21.0){
                        result="偏瘦";
                    }else if (value>27.0){
                        result="偏胖";
                    }
                }else if(age>=40&&age<=59){
                    if (value<22.0){
                        result="偏瘦";
                    }else if (value>29.0){
                        result="偏胖";
                    }
                }else if(age>=60){
                    if (value<23.0){
                        result="偏瘦";
                    }else if (value>29.0){
                        result="偏胖";
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=39){
                    if (value<11.0){
                        result="偏瘦";
                    }else if (value>17.0){
                        result="偏胖";
                    }
                }else if(age>=40&&age<=59){
                    if (value<12.0){
                        result="偏瘦";
                    }else if (value>17.0){
                        result="偏胖";
                    }
                }else if(age>=60){
                    if (value<14.0){
                        result="偏瘦";
                    }else if (value>19.0){
                        result="偏胖";
                    }
                }
                break;
            }
        }
        return result;
    }

    public float getFatRefer(int sex,int age){
        float result=0.0f;
        switch (sex){
            case 0:{
                if (age>=18&&age<=39){
                   result=13.0f;
                }else if(age>=40&&age<=59){
                    result=14.0f;
                }else if(age>=60){
                    result=16.0f;
                }
                break;
            }
            default:{
                if (age>=18&&age<=39){
                    result=24.0f;
                }else if(age>=40&&age<=59){
                    result=25.0f;
                }else if(age>=60){
                    result=26.0f;
                }
                break;
            }
        }
        return result;
    }

    public int getSubFatStatus(int sex,double value){
        int result=1;
        switch (sex){
            case 0:{
                if (value<18.5){
                    result=0;
                }else if(value>26.7){
                    result=2;
                }
                break;
            }
            default:{
                if (value<8.6){
                    result=0;
                }else if(value>16.7){
                    result=2;
                }
                break;
            }
        }
        return result;
    }

    /***
     * 皮下脂肪率
     * @param sex
     * @param value
     * @return
     */
    public String getSubFatTitle(int sex,double value){
        String result="标准";
        switch (sex){
            case 0:{
                if (value<18.5){
                    result="偏低";
                }else if(value>26.7){
                    result="偏高";
                }
                break;
            }
            default:{
                if (value<8.6){
                    result="偏低";
                }else if(value>16.7){
                    result="偏高";
                }
                break;
            }
        }
        return result;
    }


    public float getSubFatRefer(int sex){
        float result=10.0f;
        switch (sex){
            case 0:{
                result=20.0f;
                break;
            }
            default:{
                result=12.0f;
                break;
            }
        }
        return result;
    }

    public int getVisFatStatus(double value){
        if (value>1&&value<9){
            return 0;
        }else if(value>10&&value<14){
            return 2;
        }else if(value>15){
            return 3;
        }
        return 1;
    }
    /**
     * 内脏脂肪率
     * @param value
     * @return
     */
    public String getVisFatTitle(double value){
        if (value>1&&value<9){
            return "正常";
        }else if(value>10&&value<14){
            return "偏高";
        }else if(value>15){
            return "偏高";
        }
        return "标准";
    }

    public int getVisFatRefer(){
        int result=5;
        return result;
    }

    public int getWaterStatus(int sex,double value){
        if (sex==1){
            if (value>=55&&value<=65){
                return 1;
            }else if(value<55){
                return 0;
            }
            return 2;
        }else{
            if (value>=45&&value<=60){
                return 1;
            }else if(value<45){
                return 0;
            }
            return 2;
        }
    }

    /**
     * 水分含量
     * @param sex
     * @param value
     * @return
     */
    public String getWaterTitle(int sex,double value){
        if (sex==1){
            if (value>=55&&value<=65){
                return "正常";
            }else if(value<55){
                return "偏低";
            }
            return "偏高";
        }else{
            if (value>=45&&value<=60){
                return "正常";
            }else if(value<45){
                return "偏低";
            }
            return "偏高";
        }
    }

    public int getWaterRefer(int sex){
        int result=60;
        if (sex==0){
            result=50;
        }
        return result;
    }


    public int getBMRStatus(int age,int sex,double value){
        int result=1;
        switch (sex){
            case 0:{
                if (age>=18&&age<=29){
                    if (value<23.6){
                        result=0;
                    }else if(value>23.6){
                        result=2;
                    }
                }else if(age>=30&&age<=49){
                    if (value<21.7){
                        result=0;
                    }else if(value>21.7){
                        result=2;
                    }
                }else if(age>=50&&age<=69){
                    if (value<20.7){
                        result=0;
                    }else if(value>20.7){
                        result=2;
                    }
                }else if(age>=70){
                    if (value<20.7){
                        result=0;
                    }else if(value>20.7){
                        result=2;
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=29){
                    if (value<24.0){
                        result=0;
                    }else if(value>24.0){
                        result=2;
                    }
                }else if(age>=30&&age<=49){
                    if (value<22.3){
                        result=0;
                    }else if(value>22.3){
                        result=2;
                    }
                }else if(age>=50&&age<=69){
                    if (value<21.5){
                        result=0;
                    }else if(value>21.5){
                        result=2;
                    }
                }else if(age>=70){
                    if (value<21.5){
                        result=0;
                    }else if(value>21.5){
                        result=2;
                    }
                }
                break;
            }
        }

        return result;
    }

    /***
     * 基础代谢率
     * @param age 年龄
     * @param sex  性别
     * @param value 值
     * @return
     */
    public String getBMRTitle(int age,int sex,double value){
        String result="标准";
        switch (sex){
            case 0:{
                if (age>=18&&age<=29){
                    if (value<23.6){
                        result="偏低";
                    }else if(value>23.6){
                        result="偏高";
                    }
                }else if(age>=30&&age<=49){
                    if (value<21.7){
                        result="偏低";
                    }else if(value>21.7){
                        result="偏高";
                    }
                }else if(age>=50&&age<=69){
                    if (value<20.7){
                        result="偏低";
                    }else if(value>20.7){
                        result="偏高";
                    }
                }else if(age>=70){
                    if (value<20.7){
                        result="偏低";
                    }else if(value>20.7){
                        result="偏高";
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=29){
                    if (value<24.0){
                        result="偏低";
                    }else if(value>24.0){
                        result="偏高";
                    }
                }else if(age>=30&&age<=49){
                    if (value<22.3){
                        result="偏低";
                    }else if(value>22.3){
                        result="偏高";
                    }
                }else if(age>=50&&age<=69){
                    if (value<21.5){
                        result="偏低";
                    }else if(value>21.5){
                        result="偏高";
                    }
                }else if(age>=70){
                    if (value<21.5){
                        result="偏低";
                    }else if(value>21.5){
                        result="偏高";
                    }
                }
                break;
            }
        }

        return result;
    }

    public float getBMRRefer(int sex,int age){
        float result=60.0f;
        switch (sex){
            case 0:{
                if (age>=18&&age<=29){
                    result=2360f;
                }else if(age>=30&&age<=49){
                    result=2170f;
                }else if(age>=50&&age<=69){
                    result=2070f;
                }else if(age>=70){
                    result=2070f;
                }
                break;
            }
            default:{
                if (age>=18&&age<=29){
                    result=2400f;
                }else if(age>=30&&age<=49){
                    result=2230f;
                }else if(age>=50&&age<=69){
                   result=2150f;
                }else if(age>=70){
                   result=2150f;
                }
                break;
            }
        }
        return result;
    }

    public int getMuscleStatus(int height,int sex,double value){
        int result=1;
        switch (sex){
            case 0:
            {
                if (height<150){
                    if (value<29.1) {
                        result =0;
                    }else if(value>34.7){
                        result=2;
                    }
                }else if(height>150&&height<160){
                    if (value<32.9){
                        result=0;
                    }else if(value>37.5){
                        result=2;
                    }
                }else{
                    if(value<36.5){
                        result=0;
                    }else if(value>42.5){
                        result=2;
                    }
                }
                break;
            }
            default:{
                if (height<160){
                    if (value<38.5) {
                        result = 0;
                    }else if(value>46.5){
                        result=2;
                    }
                }else if(height>160&&height<170){
                    if (value<42.0){
                        result=0;
                    }else if(value>52.4){
                        result=2;
                    }
                }else{
                    if(value<49.4){
                        result=0;
                    }else if(value>59.4){
                        result=2;
                    }
                }
                break;
            }
        }

        return result;
    }


    /***
     * 肌肉比例
     * @param height 身高
     * @param sex
     * @param value
     * @return
     */
    public String getMuscleTitle(int height,int sex,double value){
        String result="标准";
        switch (sex){
            case 0:
            {
                if (height<150){
                    if (value<29.1) {
                        result = "偏低";
                    }else if(value>34.7){
                        result="偏高";
                    }
                }else if(height>150&&height<160){
                    if (value<32.9){
                        result="偏低";
                    }else if(value>37.5){
                        result="偏高";
                    }
                }else{
                    if(value<36.5){
                        result="偏低";
                    }else if(value>42.5){
                        result="偏高";
                    }
                }
                break;
            }
            default:{
                if (height<160){
                    if (value<38.5) {
                        result = "偏低";
                    }else if(value>46.5){
                        result="偏高";
                    }
                }else if(height>160&&height<170){
                    if (value<42.0){
                        result="偏低";
                    }else if(value>52.4){
                        result="偏高";
                    }
                }else{
                    if(value<49.4){
                        result="偏低";
                    }else if(value>59.4){
                        result="偏高";
                    }
                }
                break;
            }
        }

        return result;
    }

    public float getMuscleRefer(int sex,int height){
        float result=60.0f;
        switch (sex){
            case 0:{
                if (height<150){
                    result=31.0f;
                }else if(height>150&&height<160){
                    result=35.0f;
                }else if(height>160){
                    result=38.0f;
                }
                break;
            }
            default:{
                if (height<160){
                    result=42.0f;
                }else if(height>160&&height<170){
                    result=47.0f;
                }else if(height>170){
                    result=55.0f;
                }
                break;
            }
        }
        return result;
    }

    public int getBoneStatus(double weight,int sex,double value){
        int result=1;
        switch (sex){
            case 0:
            {
                if (weight<45){
                    if (value<1.8) {
                        result =0;
                    }else if(value>1.8){
                        result=2;
                    }
                }else if(weight>45&&weight<60){
                    if (value<2.2){
                        result=0;
                    }else if(value>2.2){
                        result=2;
                    }
                }else{
                    if(value<2.5){
                        result=0;
                    }else if(value>2.5){
                        result=2;
                    }
                }
                break;
            }
            default:{
                if (weight<60){
                    if (value<2.5) {
                        result =0;
                    }else if(value>2.5){
                        result=2;
                    }
                }else if(weight>60&&weight<75){
                    if (value<2.9){
                        result=0;
                    }else if(value>2.9){
                        result=2;
                    }
                }else{
                    if(value<3.2){
                        result=0;
                    }else if(value>3.2){
                        result=2;
                    }
                }
                break;
            }
        }
        return result;
    }

    /***
     * 骨量
     * @param weight 体重
     * @param sex
     * @param value
     * @return
     */
    public String getBoneTitle(double weight,int sex,double value){
        String result="标准";
        switch (sex){
            case 0:
            {
                if (weight<45){
                    if (value<1.8) {
                        result = "偏低";
                    }else if(value>1.8){
                        result="偏高";
                    }
                }else if(weight>45&&weight<60){
                    if (value<2.2){
                        result="偏低";
                    }else if(value>2.2){
                        result="偏高";
                    }
                }else{
                    if(value<2.5){
                        result="偏低";
                    }else if(value>2.5){
                        result="偏高";
                    }
                }
                break;
            }
            default:{
                if (weight<60){
                    if (value<2.5) {
                        result = "偏低";
                    }else if(value>2.5){
                        result="偏高";
                    }
                }else if(weight>60&&weight<75){
                    if (value<2.9){
                        result="偏低";
                    }else if(value>2.9){
                        result="偏高";
                    }
                }else{
                    if(value<3.2){
                        result="偏低";
                    }else if(value>3.2){
                        result="偏高";
                    }
                }
                break;
            }
        }
        return result;
    }


    public float getBoneRefer(int sex,double weight){
        float result=60.0f;
        switch (sex){
            case 1:{
                if (weight<60){
                    result=2.5f;
                }else if(weight>60&&weight<75){
                    result=2.9f;
                }else if(weight>75){
                    result=3.2f;
                }
                break;
            }
            default:{
                if (weight<45){
                    result=1.8f;
                }else if(weight>45&&weight<60){
                    result=2.2f;
                }else if(weight>60){
                    result=2.5f;
                }
                break;
            }
        }
        return result;
    }

    public int getBMIStatus(double value){
        if (value>=24&&value<=28)
        {
            return 2;
        }else if(value>28){
            return 3;
        }else if(value<18.5){
            return 0;
        }
        return 1;
    }

    public String getBMITitle(double value){
        if (value>=24&&value<=28)
        {
            return "偏高";
        }else if(value>28){
            return "偏高";
        }else if(value<18.5){
            return "偏瘦";
        }
        return "健康";
    }

    public float getBMIRefer(){
        return 21.0f;
    }

    public int getWeightValue(int height,int sex,double value){
        int result=50;
        switch (sex){
            case 0:{
                double val=(height-100)*0.9;
                if (value<(val*1.1)){
                    result=26;
                }else if (value>(val*1.1)){
                    result=76;
                }

                break;
            }
            default:{
                double val=(height-100)*0.9;
                if (value<(val*1.1)){
                    result=25;
                }else if (value>(val*1.1)){
                    result=75;
                }
                break;
            }
        }
        return result;
    }

    public int getFatValue(int age,int sex,double value){
        int result=50;
        switch (sex){
            case 0:{
                if (age>=18&&age<=39){
                    if (value<21.0){
                        result=25;
                    }else if (value>27.0){
                        result=75;
                    }
                }else if(age>=40&&age<=59){
                    if (value<22.0){
                        result=26;
                    }else if (value>29.0){
                        result=76;
                    }
                }else if(age>=60){
                    if (value<23.0){
                        result=26;
                    }else if (value>29.0){
                        result=80;
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=39){
                    if (value<11.0){
                        result=25;
                    }else if (value>17.0){
                        result=70;
                    }
                }else if(age>=40&&age<=59){
                    if (value<12.0){
                        result=23;
                    }else if (value>17.0){
                        result=76;
                    }
                }else if(age>=60){
                    if (value<14.0){
                        result=24;
                    }else if (value>19.0){
                        result=73;
                    }
                }
                break;
            }
        }
        return result;
    }

    /***
     * 皮下脂肪率
     * @param sex
     * @param value
     * @return
     */
    public int getSubFatValue(int sex,double value){
        int result=50;
        switch (sex){
            case 0:{
                if (value<18.5){
                    result=24;
                }else if(value>26.7){
                    result=70;
                }
                break;
            }
            default:{
                if (value<8.6){
                    result=18;
                }else if(value>16.7){
                    result=72;
                }
                break;
            }
        }
        return result;
    }

    /**
     * 内脏脂肪率
     * @param value
     * @return
     */
    public int getVisFatValue(double value){
        int result=20;
        if (value>1&&value<9){
            result=50;
        }else if(value>10&&value<14){
            result=75;
        }else if(value>15){
            result=90;
        }
        return result;
    }

    /**
     * 水分含量
     * @param sex
     * @param value
     * @return
     */
    public int getWaterValue(int sex,double value){
        int result=50;
        switch (sex){
            case 0:
            {
                if (value<=45){
                    result=25;
                }else if(value>=60){
                    result=78;
                }
                break;
            }
            default:
            {
                if (value<=55){
                    result=22;
                }else if(value>=65){
                    result=80;
                }
                break;
            }
        }
        return result;
    }


    /***
     * 基础代谢率
     * @param age 年龄
     * @param sex  性别
     * @param value 值
     * @return
     */
    public int getBMRValue(int age,int sex,double value){
        int result=50;
        switch (sex){
            case 0:{
                if (age>=18&&age<=29){
                    if (value<23.6){
                        result=23;
                    }else if(value>23.6){
                        result=80;
                    }
                }else if(age>=30&&age<=49){
                    if (value<21.7){
                        result=24;
                    }else if(value>21.7){
                        result=76;
                    }
                }else if(age>=50&&age<=69){
                    if (value<20.7){
                        result=28;
                    }else if(value>20.7){
                        result=71;
                    }
                }else if(age>=70){
                    if (value<20.7){
                        result=18;
                    }else if(value>20.7){
                        result=75;
                    }
                }
                break;
            }
            default:{
                if (age>=18&&age<=29){
                    if (value<24.0){
                        result=28;
                    }else if(value>24.0){
                        result=79;
                    }
                }else if(age>=30&&age<=49){
                    if (value<22.3){
                        result=25;
                    }else if(value>22.3){
                        result=78;
                    }
                }else if(age>=50&&age<=69){
                    if (value<21.5){
                        result=28;
                    }else if(value>21.5){
                        result=78;
                    }
                }else if(age>=70){
                    if (value<21.5){
                        result=28;
                    }else if(value>21.5){
                        result=72;
                    }
                }
                break;
            }
        }

        return result;
    }

    /***
     * 肌肉比例
     * @param height 身高
     * @param sex
     * @param value
     * @return
     */
    public int getMuscleValue(int height,int sex,double value){
        int result=50;
        switch (sex){
            case 0:
            {
                if (height<150){
                    if (value<29.1) {
                        result=26;
                    }else if(value>34.7){
                        result=75;
                    }
                }else if(height>150&&height<160){
                    if (value<32.9){
                        result=22;
                    }else if(value>37.5){
                        result=78;
                    }
                }else{
                    if(value<36.5){
                        result=23;
                    }else if(value>42.5){
                        result=68;
                    }
                }
                break;
            }
            default:{
                if (height<160){
                    if (value<38.5) {
                        result=19;
                    }else if(value>46.5){
                        result=68;
                    }
                }else if(height>160&&height<170){
                    if (value<42.0){
                        result=28;
                    }else if(value>52.4){
                        result=78;
                    }
                }else{
                    if(value<49.4){
                        result=28;
                    }else if(value>59.4){
                        result=68;
                    }
                }
                break;
            }
        }

        return result;
    }


    /***
     * 骨量
     * @param weight 体重
     * @param sex
     * @param value
     * @return
     */
    public int getBoneValue(double weight,int sex,double value){
        int result=50;
        switch (sex){
            case 0:
            {
                if (weight<45){
                    if (value<1.8) {
                        result=25;
                    }else if(value>1.8){
                        result=74;
                    }
                }else if(weight>45&&weight<60){
                    if (value<2.2){
                        result=28;
                    }else if(value>2.2){
                        result=68;
                    }
                }else{
                    if(value<2.5){
                        result=22;
                    }else if(value>2.5){
                        result=68;
                    }
                }
                break;
            }
            default:{
                if (weight<60){
                    if (value<2.5) {
                        result=28;
                    }else if(value>2.5){
                        result=68;
                    }
                }else if(weight>60&&weight<75){
                    if (value<2.9){
                        result=28;
                    }else if(value>2.9){
                        result=68;
                    }
                }else{
                    if(value<3.2){
                        result=28;
                    }else if(value>3.2){
                        result=68;
                    }
                }
                break;
            }
        }

        return result;
    }


    public int getBMIValue(double value){
        int result=50;
        if (value>=24&&value<=28){
            result=65;
        }else if(value>28){
            result=90;
        }else if(value<18.5){
            result=25;
        }
        return result;
    }


}
