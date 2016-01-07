package com.xujun.app.yoca;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.WeightHisEntity;
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
    public final static int    ACTION_MAC_ADDRESS_SYNC=6;
    public final static int    ACTION_WEIGHT_PACKET_SENDER=7;

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
    public final static String CONF_CURRENT_ACCOUNT="conf_current_account";


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
    public final static String DEVICE_MAC_ADDRESS="pre_device_mac_address";


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

    public final static String CONF_CHART_TYPE="conf_chart_type";


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
        props.setProperty(key, value);
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
                return "";
            case 6:
                return "Cal";
            case 7:
                return "%";
            case 8:
                return "%";
            case 9:
                return "Kg";
            case 10:
                return "%";
            case 11:
                return "";
        }
        return result;
    }

    public String getChartTitle(int type){
        return getTargetType(type)+"－趋势图";
    }

    public int getWeightStatus(int height,int sex,double value){
        int result=1;
        int w=height-105;
        if (sex==1){
            w=height-100;
        }
        if (value>(w*1.1)) {
            result=2;
        }else if (value<(w*0.9)){
            result=0;
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
        int w=height-105;
        if (sex==1){
            w=height-100;
        }
        if (value>(w*1.1)) {
            result="偏胖";
        }else if (value<(w*0.9)){
            result="偏瘦";
        }
        return result;
    }

    public int getFatStatus(int nAge,int nSex,double value){
        int result=1;

        switch (nSex){
            case 0:{
                if (nAge>17&&nAge<40){
                    if (value>16){
                        result=2;
                    }else if(value<11) {
                        result=0;
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>17){
                        result=2;
                    }else if(value<12) {
                        result=0;
                    }
                }else if(nAge>59){
                    if (value>19){
                        result=2;
                    }else if(value<14) {
                        result=0;
                    }
                }
                break;
            }
            case 1:{
                if (nAge>17&&nAge<40){
                    if (value>27){
                        result=2;
                    }else if(value<21) {
                        result=0;
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>28){
                        result=2;
                    }else if(value<22) {
                        result=0;
                    }
                }else if(nAge>59){
                    if (value>29){
                        result=2;
                    }else if(value<23) {
                        result=0;
                    }
                }
                break;
            }
        }
        return  result;
    }

    public String getFatTitle(int nAge,int nSex,double value){
        String result="标准";
        switch (nSex){
            case 0:{
                if (nAge>17&&nAge<40){
                    if (value>16){
                        result="偏高";
                    }else if(value<11) {
                        result="偏低";
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>17){
                        result="偏高";
                    }else if(value<12) {
                        result="偏低";
                    }
                }else if(nAge>59){
                    if (value>19){
                        result="偏高";
                    }else if(value<14) {
                        result="偏低";
                    }
                }
                break;
            }
            case 1:{
                if (nAge>17&&nAge<40){
                    if (value>27){
                        result="偏高";
                    }else if(value<21) {
                        result="偏低";
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>28){
                        result="偏高";
                    }else if(value<22) {
                        result="偏低";
                    }
                }else if(nAge>59){
                    if (value>29){
                        result="偏高";
                    }else if(value<23) {
                        result="偏低";
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
            case 1:{
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

    public int getSubFatStatus(int nSex,double value){
        int result=1;
        switch (nSex){
            case 1:{
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
     * @param nSex
     * @param value
     * @return
     */
    public String getSubFatTitle(int nSex,double value){
        String result="标准";
        switch (nSex){
            case 1:{
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
            case 1:{
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
        int result=1;
        if (value<1){
            result=0;
        }else if(value>9){
            result=2;
        }
        return result;
    }
    /**
     * 内脏脂肪率
     * @param value
     * @return
     */
    public String getVisFatTitle(double value){
        if (value<1){
            return "偏低";
        }else if(value>9){
            return "偏高";
        }
        return "标准";
    }

    public int getVisFatRefer(){
        int result=5;
        return result;
    }

    public int getWaterStatus(int nSex,double value){
        int result=1;
        if (nSex==0) {
            if (value>65.0){
                result=2;
            }else if(value<55.0) {
                result=0;
            }
        }else {
            if (value>60){
                result=2;
            }else  if (value<45) {
                result=0;
            }
        }
        return result;
    }

    /**
     * 水分含量
     * @param nSex
     * @param value
     * @return
     */
    public String getWaterTitle(int nSex,double value){
        String result="标准";
        if (nSex==0) {
            if (value>65.0){
                result="偏高";
            }else if(value<55.0) {
                result="偏低";
            }
        }else {
            if (value>60){
                result="偏高";
            }else  if (value<45) {
                result="偏低";
            }
        }
        return result;
    }

    public int getWaterRefer(int sex){
        int result=60;
        if (sex==1){
            result=50;
        }
        return result;
    }


    public int getBMRStatus(int nAge,int nSex,double value){
        int result=1;
        switch (nSex){
            case 0:{
                if(nAge<=17){
                    if (value>1386){
                        result=2;
                    }else if(value<1134) {
                        result=0;
                    }
                }else if(nAge>17&&nAge<30){
                    if (value>1716){
                        result=2;
                    }else if(value<1404) {
                        result=0;
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1717){
                        result=2;
                    }else if(value<1405) {
                        result=0;
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1656){
                        result=2;
                    }else if(value<1355) {
                        result=0;
                    }
                }else{
                    if (value>1538){
                        result=2;
                    }else if(value<1405) {
                        result=0;
                    }
                }
                break;
            }
            case 1:{
                if(nAge<=17){
                    if (value>1392){
                        result=2;
                    }else if(value<1139) {
                        result=0;
                    }
                }else if (nAge>17&&nAge<30){
                    if (value>1428){
                        result=2;
                    }else if(value<1168) {
                        result=0;
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1432){
                        result=2;
                    }else if(value<1172) {
                        result=0;
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1366){
                        result=2;
                    }else if(value<1118) {
                        result=0;
                    }
                }else{
                    if (value>1139){
                        result=2;
                    }else if(value<932) {
                        result=0;
                    }
                }
                break;
            }
        }
        return result;
    }

    /***
     * 基础代谢率
     * @param nAge 年龄
     * @param nSex  性别
     * @param value 值
     * @return
     */
    public String getBMRTitle(int nAge,int nSex,double value){
        String result="标准";

        switch (nSex){
            case 0:{
                if(nAge<=17){
                    if (value>1386){
                        result="偏高";
                    }else if(value<1134) {
                        result="偏低";
                    }
                }else if(nAge>17&&nAge<30){
                    if (value>1716){
                        result="偏高";
                    }else if(value<1404) {
                        result="偏低";
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1717){
                        result="偏高";
                    }else if(value<1405) {
                        result="偏低";
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1656){
                        result="偏高";
                    }else if(value<1355) {
                        result="偏低";
                    }
                }else{
                    if (value>1538){
                        result="偏高";
                    }else if(value<1405) {
                        result="偏低";
                    }
                }
                break;
            }
            case 1:{
                if(nAge<=17){
                    if (value>1392){
                        result="偏高";
                    }else if(value<1139) {
                        result="偏低";
                    }
                }else if (nAge>17&&nAge<30){
                    if (value>1428){
                        result="偏高";
                    }else if(value<1168) {
                        result="偏低";
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1432){
                        result="偏高";
                    }else if(value<1172) {
                        result="偏低";
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1366){
                        result="偏高";
                    }else if(value<1118) {
                        result="偏低";
                    }
                }else{
                    if (value>1139){
                        result="偏高";
                    }else if(value<932) {
                        result="偏低";
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
            case 1:{
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

    public int getMuscleStatus(int age,int sex,double value){
        int result=1;
        switch (sex){
            case 0:
            {
                if (age<16){
                    if (value<46.0) {
                        result = 0;
                    }else if(value>51.0){
                        result=2;
                    }
                }else if(age>15&&age<31){
                    if (value<45.0){
                        result=0;
                    }else if(value>50.0){
                        result=2;
                    }
                }else if(age>30&&age<61){
                    if (value<44.0){
                        result=0;
                    }else if(value>49.0){
                        result=2;
                    }
                }else{
                    if(value<43.0){
                        result=0;
                    }else if(value>48.0){
                        result=2;
                    }
                }
                break;
            }
            default:{
                if (age<16){
                    if (value<44.0) {
                        result =0;
                    }else if(value>49.0){
                        result=2;
                    }
                }else if(age>15&&age<31){
                    if (value<43.0){
                        result=0;
                    }else if(value>48.0){
                        result=2;
                    }
                }else if(age>30&&age<61){
                    if (value<42.0){
                        result=0;
                    }else if(value>47.0){
                        result=2;
                    }
                }else{
                    if(value<41.0){
                        result=0;
                    }else if(value>46.0){
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
     * @param age 年龄
     * @param sex
     * @param value
     * @return
     */
    public String getMuscleTitle(int age,int sex,double value){
        String result="标准";
        switch (sex){
            case 0:
            {
                if (age<16){
                    if (value<46.0) {
                        result = "偏低";
                    }else if(value>51.0){
                        result="偏高";
                    }
                }else if(age>15&&age<31){
                    if (value<45.0){
                        result="偏低";
                    }else if(value>50.0){
                        result="偏高";
                    }
                }else if(age>30&&age<61){
                    if (value<44.0){
                        result="偏低";
                    }else if(value>49.0){
                        result="偏高";
                    }
                }else{
                    if(value<43.0){
                        result="偏低";
                    }else if(value>48.0){
                        result="偏高";
                    }
                }
                break;
            }
            default:{
                if (age<16){
                    if (value<44.0) {
                        result = "偏低";
                    }else if(value>49.0){
                        result="偏高";
                    }
                }else if(age>15&&age<31){
                    if (value<43.0){
                        result="偏低";
                    }else if(value>48.0){
                        result="偏高";
                    }
                }else if(age>30&&age<61){
                    if (value<42.0){
                        result="偏低";
                    }else if(value>47.0){
                        result="偏高";
                    }
                }else{
                    if(value<41.0){
                        result="偏低";
                    }else if(value>46.0){
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
            case 1:{
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

    public int getBoneStatus(double weight,int nSex,double value){
        int result=1;
        if (nSex==0){
            if (weight<60.0){
                if (value>4.5){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }else if(weight>59&&weight<76){
                if (value>6.0){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }else if (weight>75.0){
                if (value>7.5){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }
        }else{
            if (weight<45.0){
                if (value>3.0){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }else if(weight>44&&weight<61){
                if (value>4.2){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }else if (weight>60.0){
                if (value>3.0){
                    result=2;
                }else if(value<0.5) {
                    result=0;
                }
            }
        }
        return result;
    }

    /***
     * 骨量
     * @param weight 体重
     * @param nSex
     * @param value
     * @return
     */
    public String getBoneTitle(double weight,int nSex,double value){
        String result="标准";
        if (nSex==0){
            if (weight<60.0){
                if (value>4.5){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }else if(weight>59&&weight<76){
                if (value>6.0){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }else if (weight>75.0){
                if (value>7.5){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }
        }else{
            if (weight<45.0){
                if (value>3.0){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }else if(weight>44&&weight<61){
                if (value>4.2){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }else if (weight>60.0){
                if (value>3.0){
                    result="偏高";
                }else if(value<0.5) {
                    result="偏低";
                }
            }
        }
        return result;
    }


    public float getBoneRefer(int sex,double weight){
        float result=60.0f;
        switch (sex){
            case 0:{
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
        int result=1;
        if (value>23.0) {
            result=2;
        }else if(value<18.5){
            result=0;
        }
        return result;
    }

    public String getBMITitle(double value){
        String result="标准";
        if (value>23.0) {
            result="偏高";
        }else if(value<18.5){
            result="偏低";
        }
        return result;
    }

    public float getBMIRefer(){
        return 21.0f;
    }

    public int getWeightValue(int height,int sex,double value){
        int result=50;
        int w=height-105;
        if (sex==1){
            w=height-100;
        }
        if (value>(w*1.1)) {
            result=85;
        }else if (value<(w*0.9)){
            result=25;
        }
        return result;
    }

    public int getFatValue(int nAge,int nSex,double value){
        int result=50;
        switch (nSex){
            case 0:{
                if (nAge>17&&nAge<40){
                    if (value>16){
                        result=85;
                    }else if(value<11) {
                        result=25;
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>17){
                        result=85;
                    }else if(value<12) {
                        result=25;
                    }
                }else if(nAge>59){
                    if (value>19){
                        result=85;
                    }else if(value<14) {
                        result=25;
                    }
                }
                break;
            }
            case 1:{
                if (nAge>17&&nAge<40){
                    if (value>27){
                        result=85;
                    }else if(value<21) {
                        result=25;
                    }
                }else if(nAge>39&&nAge<60){
                    if (value>28){
                        result=85;
                    }else if(value<22) {
                        result=25;
                    }
                }else if(nAge>59){
                    if (value>29){
                        result=85;
                    }else if(value<23) {
                        result=25;
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
            case 1:{
                if (value<18.5){
                    result=25;
                }else if(value>26.7){
                    result=85;
                }
                break;
            }
            default:{
                if (value<8.6){
                    result=25;
                }else if(value>16.7){
                    result=85;
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
        int result=50;
        if (value<1){
            result=25;
        }else if(value>9){
            result=85;
        }
        return result;
    }

    /**
     * 水分含量
     * @param nSex
     * @param value
     * @return
     */
    public int getWaterValue(int nSex,double value){
        int result=50;
        if (nSex==0) {
            if (value>65.0){
                result=85;
            }else if(value<55.0) {
                result=25;
            }
        }else {
            if (value>60){
                result=85;
            }else  if (value<45) {
                result=25;
            }
        }
        return result;
    }


    /***
     * 基础代谢率
     * @param nAge 年龄
     * @param nSex  性别
     * @param value 值
     * @return
     */
    public int getBMRValue(int nAge,int nSex,double value){
        int result=50;
        switch (nSex){
            case 0:{
                if(nAge<=17){
                    if (value>1386){
                        result=85;
                    }else if(value<1134) {
                        result=25;
                    }
                }else if(nAge>17&&nAge<30){
                    if (value>1716){
                        result=85;
                    }else if(value<1404) {
                        result=25;
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1717){
                        result=85;
                    }else if(value<1405) {
                        result=25;
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1656){
                        result=85;
                    }else if(value<1355) {
                        result=25;
                    }
                }else{
                    if (value>1538){
                        result=85;
                    }else if(value<1405) {
                        result=25;
                    }
                }
                break;
            }
            case 1:{
                if(nAge<=17){
                    if (value>1392){
                        result=85;
                    }else if(value<1139) {
                        result=25;
                    }
                }else if (nAge>17&&nAge<30){
                    if (value>1428){
                        result=85;
                    }else if(value<1168) {
                        result=25;
                    }
                }else if(nAge>29&&nAge<50){
                    if (value>1432){
                        result=85;
                    }else if(value<1172) {
                        result=25;
                    }
                }else if(nAge>49&&nAge<70){
                    if (value>1366){
                        result=85;
                    }else if(value<1118) {
                        result=25;
                    }
                }else{
                    if (value>1139){
                        result=85;
                    }else if(value<932) {
                        result=25;
                    }
                }
                break;
            }
        }
        return result;
    }

    /***
     * 肌肉比例
     * @param age 年龄
     * @param sex
     * @param value
     * @return
     */
    public int getMuscleValue(int age,int sex,double value){
        int result=50;
        switch (sex){
            case 0:
            {
                if (age<16){
                    if (value<46.0) {
                        result = 25;
                    }else if(value>51.0){
                        result=85;
                    }
                }else if(age>15&&age<31){
                    if (value<45.0){
                        result=25;
                    }else if(value>50.0){
                        result=85;
                    }
                }else if(age>30&&age<61){
                    if (value<44.0){
                        result=25;
                    }else if(value>49.0){
                        result=85;
                    }
                }else{
                    if(value<43.0){
                        result=25;
                    }else if(value>48.0){
                        result=85;
                    }
                }
                break;
            }
            default:{
                if (age<16){
                    if (value<44.0) {
                        result =25;
                    }else if(value>49.0){
                        result=85;
                    }
                }else if(age>15&&age<31){
                    if (value<43.0){
                        result=25;
                    }else if(value>48.0){
                        result=85;
                    }
                }else if(age>30&&age<61){
                    if (value<42.0){
                        result=25;
                    }else if(value>47.0){
                        result=85;
                    }
                }else{
                    if(value<41.0){
                        result=25;
                    }else if(value>46.0){
                        result=85;
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
     * @param nSex
     * @param value
     * @return
     */
    public int getBoneValue(double weight,int nSex,double value){
        int result=50;
        if (nSex==0){
            if (weight<60.0){
                if (value>4.5){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }else if(weight>59&&weight<76){
                if (value>6.0){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }else if (weight>75.0){
                if (value>7.5){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }
        }else{
            if (weight<45.0){
                if (value>3.0){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }else if(weight>44&&weight<61){
                if (value>4.2){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }else if (weight>60.0){
                if (value>3.0){
                    result=85;
                }else if(value<0.5) {
                    result=25;
                }
            }
        }
        return result;
    }


    public int getBMIValue(double value){
        int result=50;
        if (value>23){
            result=85;
        }else if(value<18.5){
            result=25;
        }
        return result;
    }

    public int getSholaiValue(AccountEntity entity,WeightHisEntity hisEntity){
        int suc=0;
        int w=entity.getHeight()-105;
        int nHeight=entity.getHeight();
        int nSex=entity.getSex();
        int nAge=entity.getAge();
        if (nSex==1){
            w=nHeight-100;
        }
        double weightVal=100.0;
        if (hisEntity.getWeight()>(w*1.1)||hisEntity.getWeight()<(w*0.9)) {
            if ((hisEntity.getWeight()-(w*1.1))<(hisEntity.getWeight()-(w*0.9))) {
                weightVal=100-Math.abs(hisEntity.getWeight()-(w*1.1))/(w*1.1)*100.0;
            }else{
                weightVal=100-Math.abs(hisEntity.getWeight()-(w*0.9))/(w*0.9)*100.0;
            }
        }
        if (weightVal>80.0){
            suc++;
        }
        System.out.println("Weight="+weightVal);

        double bmiVal=100.0;
        if (hisEntity.getBmi()>23.0||hisEntity.getBmi()<18.5) {
            if (Math.abs(hisEntity.getBmi()-23.0)<Math.abs(hisEntity.getBmi()-18.5)){
                bmiVal=100-Math.abs(hisEntity.getBmi()-23.0)/23.0*100.0;
            }else{
                bmiVal=100-Math.abs(hisEntity.getBmi()-18.5)/18.5*100.0;
            }
        }
        if (bmiVal>80.0){
            suc++;
        }
        System.out.println("Bmi="+bmiVal);

        double fatVal=100.0;
        switch (nSex){
            case 0:{
                if (nAge>17&&nAge<40){
                    if (hisEntity.getFat()>16||hisEntity.getFat()<11) {
                        if (Math.abs(hisEntity.getFat()-16)<Math.abs(hisEntity.getFat()-11)){
                            fatVal=100-Math.abs(hisEntity.getFat()-16)/16.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat()-11)/11.0*100.0;
                        }
                    }
                }else if(nAge>39&&nAge<60){
                    if (hisEntity.getFat()>17||hisEntity.getFat()<12) {
                        if (Math.abs(hisEntity.getFat()-17)<Math.abs(hisEntity.getFat()-12)){
                            fatVal=100-Math.abs(hisEntity.getFat()-17)/17.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat()-12)/12.0*100.0;
                        }
                    }
                }else if(nAge>59){
                    if (hisEntity.getFat()>19||hisEntity.getFat()<14) {
                        if (Math.abs(hisEntity.getFat()-19)<Math.abs(hisEntity.getFat()-14)){
                            fatVal=100-Math.abs(hisEntity.getFat()-19)/19.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat()-14)/14.0*100.0;
                        }
                    }
                }
                break;
            }
            case 1:{
                if (nAge>17&&nAge<40){
                    if (hisEntity.getFat()>27||hisEntity.getFat()<21) {
                        if (Math.abs(hisEntity.getFat()-27)<Math.abs(hisEntity.getFat()-21)){
                            fatVal=100-Math.abs(hisEntity.getFat()-27)/27.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat() - 21)/21.0*100.0;
                        }
                    }
                }else if(nAge>39&&nAge<60){
                    if (hisEntity.getFat()>28||hisEntity.getFat()<22) {
                        if (Math.abs(hisEntity.getFat()-28)<Math.abs(hisEntity.getFat()-22)){
                            fatVal=100-Math.abs(hisEntity.getFat()-28)/28.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat()-22)/22.0*100.0;
                        }
                    }
                }else if(nAge>59){
                    if (hisEntity.getFat()>29||hisEntity.getFat()<23) {
                        if (Math.abs(hisEntity.getFat()-29)<Math.abs(hisEntity.getFat()-23)){
                            fatVal=100-Math.abs(hisEntity.getFat()-29)/29.0*100.0;
                        }else{
                            fatVal=100-Math.abs(hisEntity.getFat()-23)/23.0*100.0;
                        }
                    }
                }
                break;
            }
        }
        if (fatVal>80.0){
            suc++;
        }
        System.out.println("Fat="+fatVal);


        double subFatVal=100.0;
        switch (nSex){
            case 0: {
                if (hisEntity.getSubFat()>16.7||hisEntity.getSubFat()<8.6) {
                    if (Math.abs(hisEntity.getSubFat()-16.7)<Math.abs(hisEntity.getSubFat()-8.6)){
                        subFatVal=100-Math.abs(hisEntity.getSubFat()-16.7)/16.7*100.0;
                    }else{
                        subFatVal=100-Math.abs(hisEntity.getSubFat()-8.6)/8.6*100.0;
                    }
                }
                break;
            }
            case 1:{
                if (hisEntity.getSubFat()>26.7||hisEntity.getSubFat()<18.5) {
                    if (Math.abs(hisEntity.getSubFat()-26.7)<Math.abs(hisEntity.getSubFat()-18.5)){
                        subFatVal=100-Math.abs(hisEntity.getSubFat()-26.7)/26.7*100.0;
                    }else{
                        subFatVal=100-Math.abs(hisEntity.getSubFat()-18.5)/18.5*100.0;
                    }
                }
                break;
            }
        }
        if (subFatVal>80.0){
            suc++;
        }
        System.out.println("SubFat="+subFatVal);


        double visFatVal=100.0;
        if (hisEntity.getVisFat()>9||hisEntity.getVisFat()<1) {
            if (Math.abs(hisEntity.getVisFat()-9)<Math.abs(hisEntity.getVisFat()-1)){
                visFatVal=100-Math.abs(hisEntity.getVisFat()-9.0)/9.0*100.0;
            }else{
                visFatVal=100-Math.abs(hisEntity.getVisFat()-1.0)/1.0*100.0;
            }
        }
        if (visFatVal>80.0){
            suc++;
        }
        System.out.println("VisFat="+visFatVal);

        double bmrVal=100.0;
        switch (nSex){
            case 0:{
                if(nAge<=17){
                    if (hisEntity.getBMR()>1386||hisEntity.getBMR()<1134) {
                        if (Math.abs(hisEntity.getBMR()-1386)<Math.abs(hisEntity.getBMR()-1134)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1386)/1386*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1134)/1134*100.0;
                        }
                    }

                }else if(nAge>17&&nAge<30){
                    if (hisEntity.getBMR()>1716||hisEntity.getBMR()<1404) {
                        if (Math.abs(hisEntity.getBMR()-1716)<Math.abs(hisEntity.getBMR()-1404)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1716)/1716*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1404)/1404*100.0;
                        }
                    }
                }else if(nAge>29&&nAge<50){
                    if (hisEntity.getBMR()>1717||hisEntity.getBMR()<1405) {
                        if (Math.abs(hisEntity.getBMR()-1717)<Math.abs(hisEntity.getBMR()-1405)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1717)/1717*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1405)/1405*100.0;
                        }
                    }
                }else if(nAge>49&&nAge<70){
                    if (hisEntity.getBMR()>1656||hisEntity.getBMR()<1355) {
                        if (Math.abs(hisEntity.getBMR()-1656)<Math.abs(hisEntity.getBMR()-1355)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1656)/1656*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1355)/1355*100.0;
                        }
                    }
                }else{
                    if (hisEntity.getBMR()>1538||hisEntity.getBMR()<1405) {
                        if (Math.abs(hisEntity.getBMR()-1538)<Math.abs(hisEntity.getBMR()-1258)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1538)/1538*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1258)/1258*100.0;
                        }
                    }
                }
                break;
            }
            case 1:{
                if(nAge<=17){
                    if (hisEntity.getBMR()>1392||hisEntity.getBMR()<1139) {
                        if (Math.abs(hisEntity.getBMR()-1392)<Math.abs(hisEntity.getBMR()-1139)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1392)/1392*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1139)/1139*100.0;
                        }
                    }
                }else if (nAge>17&&nAge<30){
                    if (hisEntity.getBMR()>1428||hisEntity.getBMR()<1168) {
                        if (Math.abs(hisEntity.getBMR()-1428)<Math.abs(hisEntity.getBMR()-1168)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1428)/1428*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1168)/1168*100.0;
                        }
                    }
                }else if(nAge>29&&nAge<50){
                    if (hisEntity.getBMR()>1432||hisEntity.getBMR()<1172) {
                        if (Math.abs(hisEntity.getBMR()-1432)<Math.abs(hisEntity.getBMR()-1172)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1432)/1432*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1172)/1172*100.0;
                        }
                    }
                }else if(nAge>49&&nAge<70){
                    if (hisEntity.getBMR()>1366||hisEntity.getBMR()<1118) {
                        if (Math.abs(hisEntity.getBMR()-1366)<Math.abs(hisEntity.getBMR()-1118)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1366)/1366*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1118)/1118*100.0;
                        }
                    }
                }else{
                    if (hisEntity.getBMR()>1139||hisEntity.getBMR()<932) {
                        if (Math.abs(hisEntity.getBMR()-1139)<Math.abs(hisEntity.getBMR()-932)) {
                            bmrVal=100-Math.abs(hisEntity.getBMR()-1139)/1139*100.0;
                        }else{
                            bmrVal=100-Math.abs(hisEntity.getBMR()-932)/932*100.0;
                        }
                    }
                }
                break;
            }
        }
        if (bmrVal>80.0){
            suc++;
        }
        System.out.println("BMR="+bmrVal);

        double waterVal=100.0;
        if (nSex==0) {
            if (hisEntity.getWater()>65.0||hisEntity.getWater()<55.0) {
                if (Math.abs(hisEntity.getWater() - 65.0) < Math.abs(hisEntity.getWater() - 55.0)) {
                    waterVal=100-Math.abs(hisEntity.getWater()-65.0)/65.0*100.0;
                }else{
                    waterVal=100-Math.abs(hisEntity.getWater()-55.0)/55.0*100.0;
                }
            }
        }else {
            if (hisEntity.getWater()>60||hisEntity.getWater()<45) {
                if (Math.abs(hisEntity.getWater()-60.0)<Math.abs(hisEntity.getWater()-45.0)){
                    waterVal=100-Math.abs(hisEntity.getWater()-60.0)/60.0*100.0;
                }else{
                    waterVal=100-Math.abs(hisEntity.getWater()-45.0)/45.0*100.0;
                }
            }
        }
        if (weightVal>80.0){
            suc++;
        }
        System.out.println("Water="+waterVal);

        double boneVal=100.0;
        if (nSex==0){
            if (hisEntity.getWeight()<60.0){
                if (hisEntity.getBone()>4.5||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-4.5)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-4.5)/4.5*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }else if(hisEntity.getWeight()>59&&hisEntity.getWeight()<76){
                if (hisEntity.getBone()>6.0||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-6.0)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-6.0)/6.0*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }else if (hisEntity.getWeight()>75.0){
                if (hisEntity.getBone()>7.5||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-7.5)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-7.5)/7.5*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }
        }else{
            if (hisEntity.getWeight()<45.0){
                if (hisEntity.getBone()>3.0||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-3.0)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-3.0)/3.0*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }else if(hisEntity.getWeight()>44&&hisEntity.getWeight()<61){
                if (hisEntity.getBone()>4.2||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-4.2)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-4.2)/4.2*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }else if (hisEntity.getWeight()>60.0){
                if (hisEntity.getBone()>3.0||hisEntity.getBone()<0.5) {
                    if (Math.abs(hisEntity.getBone()-3.0)<Math.abs(hisEntity.getBone()-0.5)){
                        boneVal=100-Math.abs(hisEntity.getBone()-3.0)/3.0*100.0;
                    }else{
                        boneVal=100-Math.abs(hisEntity.getBone()-0.5)/0.5*100.0;
                    }
                }
            }
        }
        if (boneVal>80.0){
            suc++;
        }
        System.out.println("Bone="+boneVal);

        double total=(20*weightVal+20*bmiVal+20*fatVal+5*subFatVal+5*visFatVal+10*bmrVal+10*waterVal+10*boneVal)/100.0;
        System.out.println("Totla="+total);

        return (int)total;
    }

    public int getSholaiSuc(AccountEntity entity,WeightHisEntity hisEntity){
        int suc=0;
        int wState=getWeightStatus(entity.getHeight(),entity.getSex(),hisEntity.getWeight());
        if (wState==1){
            suc++;
        }
        int  bmiState=getBMIStatus(hisEntity.getBmi());
        if (bmiState==1){
            suc++;
        }
        int fatState=getFatStatus(entity.getAge(),entity.getSex(),hisEntity.getFat());
        if (fatState==1){
            suc++;
        }
        int subFatState=getSubFatStatus(entity.getSex(),hisEntity.getSubFat());
        if (subFatState==1){
            suc++;
        }
        int visFatState=getVisFatStatus(hisEntity.getVisFat());
        if (visFatState==1){
            suc++;
        }
        int bmrState=getBMRStatus(entity.getAge(),entity.getSex(),hisEntity.getBMR());
        if (bmrState==1){
            suc++;
        }
        int waterState=getWaterStatus(entity.getSex(),hisEntity.getWater());
        if (waterState==1){
            suc++;
        }
        int muscleState=getMuscleStatus(entity.getAge(),entity.getSex(),hisEntity.getMuscle());
        if (muscleState==1){
            suc++;
        }
        int boneState=getBoneStatus(hisEntity.getWeight(),entity.getSex(),hisEntity.getBone());
        if (boneState==1){
            suc++;
        }

        return suc;
    }

    public  int countBodyAge(double weight,double fat,int height,int age,int sex){
        double bsa=(Math.pow(weight,0.444)*Math.pow(height,0.663))*88.83/10000;
        System.out.println("bsa====>"+bsa);
        double k=0.2;
        if (sex==1){
            k=0.1;
        }
        double wlbm=(1-fat/100.0)*weight;

        System.out.print("wlbm ====>"+wlbm);
        double ta=(fat-10.74)/0.2589;
        if (sex==1){
            ta=(fat-21.99)/0.097;
        }
        System.out.print("ta ====>"+ta);
        double wb=(34.668-(wlbm/bsa))/0.1;
        if (sex==1){
            wb=(28.746-(wlbm/bsa))/0.032;
        }
        System.out.print("wlbm/bsa ===>"+wb);
        double bodyage=(1-k)*age+k*(wb+ta)/2;
        System.out.print("bodyage "+bodyage);
        return (int)bodyage;
    }

}
