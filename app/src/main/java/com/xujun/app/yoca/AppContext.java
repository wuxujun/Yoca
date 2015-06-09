package com.xujun.app.yoca;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xujun.model.TargetInfoResp;
import com.xujun.util.MHttpClient;
import com.xujun.util.StringUtil;


public class AppContext extends  Application{

	private static final String TAG = "YocaApplication";

    public static  final  int NETTYPE_WIFI=1;
    public static  final  int NETTYPE_MOBILE=2;

	public static String homePath;

    private static AppContext  mInstance=null;

    public static AppContext getInstance(){
        return mInstance;
    }

    public static float scaledDensity;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance=this;

        DisplayMetrics dm=new DisplayMetrics();
        WindowManager windowManager=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        scaledDensity=dm.scaledDensity;
        checkSoftStage();
        File cacheDir= StorageUtils.getOwnCacheDirectory(getApplicationContext(),"YOCA/ImageCache");
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPriority(Thread.NORM_PRIORITY-2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).diskCache(new UnlimitedDiskCache(cacheDir)).writeDebugLogs().denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2*1024*1024)).memoryCacheSize(2*1024*1024).build();
        ImageLoader.getInstance().init(config);

	}

    public String getIMSI(){
        final TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice,tmSerial,androidId;
        tmDevice=""+tm.getDeviceId();
        tmSerial=""+tm.getSimSerialNumber();
        androidId=""+ Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        UUID deviceUuid=new UUID(androidId.hashCode(),((long)tmDevice.hashCode()<<32)|tmSerial.hashCode());
        String uniqueId=deviceUuid.toString();
        uniqueId=uniqueId.replace("-","");
        return uniqueId;
    }

	private boolean checkBLE(){
       return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

	/** 
     * 检测手机是否存在SD卡
     */  
    private boolean checkSoftStage(){  
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //判断是否存在SD卡  
            final String rootPath = Environment.getExternalStorageDirectory().getPath();  //获取SD卡的根目录  
            homePath = rootPath + "/"+this.getString(R.string.app_name);
            File file = new File(homePath);  
            if(!file.exists()){  
                file.mkdir(); 
//                Toast.makeText(getApplicationContext(), "创建路径"+homePath, Toast.LENGTH_SHORT).show();
            }  
            return true;
        }else{  
            Toast.makeText(getApplicationContext(), "您的手机没有SD卡！请插入SD卡再拍照", Toast.LENGTH_SHORT).show();
            return false;
        }  
    }


    public int getVersionCode(){
        int verCode=-1;
        try{
            verCode=getPackageManager().getPackageInfo("com.xujun.app.yoca",0).versionCode;
        }catch (PackageManager.NameNotFoundException e){

        }
        return verCode;
    }

    public String getVersionName(){
        String verName="";
        try{
            verName=getPackageManager().getPackageInfo("com.xujun.app.yoca",0).versionName;
        }catch (PackageManager.NameNotFoundException e){

        }
        return verName;
    }

    public boolean isDate(String birthday){
        String str=birthday.substring(0,4)+"-"+birthday.substring(4,6)+"-"+birthday.substring(6);
        String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(str);
        boolean dateType = mat.matches();
        return dateType;
    }

    public String sendRequestData(String url,String para,Map<String,File> files)throws AppException{
        return MHttpClient.sendRequestData(this, url, para, files);
    }

    public String requestData(String url,Map<String,String> params)throws AppException{
        return MHttpClient.requestData(this,url,params);
    }

    public String requestPostData(String url,Map<String,String> params)throws AppException{
        return MHttpClient.requestPostData(this,url,params);
    }
    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtil.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    public int getNetworkType(){
        int netType=0;
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null){
            return netType;
        }
        int nType=networkInfo.getType();
        if (nType==ConnectivityManager.TYPE_MOBILE){
            netType=2;
        }else if(nType==ConnectivityManager.TYPE_WIFI){
            netType=1;
        }
        return netType;
    }

    /**
    * 读取Assets文件夹中的图片资源 
    * @param context 
    * @param fileName 图片名称 
    * @return 
    */ 
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {   
       
        Bitmap image = null;   
        AssetManager am = context.getResources().getAssets();   
        try {   
            InputStream is = am.open(fileName);   
            image = BitmapFactory.decodeStream(is);   
            is.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        return image;   
    }


    public String getDowloadPath(){
        String downPath=homePath+"/Download";
        File path=new File(downPath);
        if (!path.exists()){
            path.mkdir();
        }
        return downPath;
    }

    public File getImageCachePath(){
        String imagePath=homePath+"/ImageCache";
        File path=new File(imagePath);
        if (!path.exists()){
            path.mkdir();
        }
        return path;
    }

    public String getCameraPath(){
        String imagePath=homePath+"/Camera";
        File path=new File(imagePath);
        if (!path.exists()){
            path.mkdir();
        }
        return imagePath;
    }

    public boolean isAudioNormal(){
        AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
        return am.getRingerMode()==AudioManager.RINGER_MODE_NORMAL;
    }

    public boolean isVoice(){
        return false;
    }
    public boolean isAppSound(){
        return isAudioNormal()&&isVoice();
    }

    public boolean containsProperty(String key){
        Properties ps=getProperties();
        return ps.containsKey(key);
    }

    public void setProperties(Properties ps){
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties(){
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key,String value){
        AppConfig.getAppConfig(this).set(key,value);
    }

    public String getProperty(String key){
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key){
        AppConfig.getAppConfig(this).remove(key);
    }


    private boolean isExistDataCache(String cacheFile){
        boolean exist=false;
        File data=getFileStreamPath(cacheFile);
        if (data.exists()){
            exist=true;
        }
        return exist;
    }
    public boolean saveObject(Serializable ser,String fileName){
        FileOutputStream fos=null;
        ObjectOutputStream oss=null;
        try{
            fos=openFileOutput(fileName,MODE_PRIVATE);
            oss=new ObjectOutputStream(fos);
            oss.writeObject(ser);
            oss.flush();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                oss.close();
            }
            catch (Exception e){
            }
            try{
                fos.close();
            }catch (Exception e){

            }
        }
    }

    public Serializable readObject(String fileName){
        if (!isExistDataCache(fileName)){
            return null;
        }
        FileInputStream fis=null;
        ObjectInputStream ois=null;
        try{
            fis=openFileInput(fileName);
            ois=new ObjectInputStream(fis);
            return (Serializable)ois.readObject();
        }catch (FileNotFoundException e){

        }catch (Exception e){
            e.printStackTrace();
            if (e instanceof InvalidClassException){
                File data=getFileStreamPath(fileName);
                data.delete();
            }
        }finally {
            try{
                ois.close();
            }catch (Exception e){

            }
            try{
                fis.close();
            }catch (Exception e){

            }
        }
        return null;
    }
}
