package com.xujun.app.yoca;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xujun.app.yoca.fragment.AccountFragment;
import com.xujun.app.yoca.fragment.ContentFragment;
import com.xujun.app.yoca.fragment.InfoFragment;
import com.xujun.app.yoca.fragment.MemberMFragment;
import com.xujun.app.yoca.fragment.MyFragment;
import com.xujun.app.yoca.fragment.WarnSetFragment;
import com.xujun.model.BaseResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.JsonUtil;
import com.xujun.util.StringUtil;
import com.xujun.util.URLs;

import org.json.JSONException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xujunwu on 15/4/7.
 */
public class TabActivity extends SherlockFragmentActivity implements View.OnClickListener{

    private static final String TAG="TabActivity";


    private Context mContext;
    private AppContext              appContext;

    private DatabaseHelper databaseHelper;

    public DatabaseHelper getDatabaseHelper(){
        if (databaseHelper==null){
            databaseHelper=DatabaseHelper.getDatabaseHelper(appContext);
        }
        return databaseHelper;
    }


    private LinearLayout mTabMy;
    private LinearLayout mTabInfo;
    private LinearLayout mTabWeight;
//    private LinearLayout mTabGroup;
//    private LinearLayout mTabSettings;

    private ImageButton mImgMy;
    private ImageButton mImgInfo;
    private ImageButton mImgWeight;
//    private ImageButton mImgGroup;
//    private ImageButton mImgSettings;

    private SherlockFragment mTab01;
    private SherlockFragment mTab02;
    private SherlockFragment mTab03;
//    private SherlockFragment mTab04;
//    private SherlockFragment mTab05;

    private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    private String          strToday=df.format(new Date());

    private static final int NO_DEVICE=-1;
    private int mConnIndex=NO_DEVICE;

    private boolean mBleSupported=true;
    private boolean mScanning=false;

    private AccountEntity            localAccountEntity;
    private static BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService  mBluetoothLeService;

    private String      mCurrentAddress=null;

    IntentFilter mFilter;

    List<BluetoothGattService>      gattServices=new ArrayList<BluetoothGattService>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_default);
        appContext=(AppContext)getApplication();
        mContext=getApplicationContext();


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            mBleSupported=false;
        }
        mBluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if (mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, AppConfig.REQUEST_ENABLE_BT);
        }else{
            startBluetoothLeService();
        }


        mFilter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        mFilter.addAction(AppConfig.ACTION_START_WEIGH);

        registerReceiver(mReceiver, mFilter);

        Intent notify=new Intent(this,NotifyService.class);
        startService(notify);


        UmengUpdateAgent.update(this);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);

        PushAgent mPushAgent=PushAgent.getInstance(mContext);
        mPushAgent.enable();


        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Yoca");
        initView();
        initEvent();
        setSelect(0);
    }

    @Override
    public void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
        super.onDestroy();
        if (mBluetoothLeService!=null){
            scanLeDevice(false);
            mBluetoothLeService.close();
            unregisterReceiver(mReceiver);
            unbindService(mServiceConnection);
            mBluetoothLeService=null;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        startScan();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(mContext);
//        Log.e(TAG, " ===>"+ UmengRegistrar.getRegistrationId(mContext));
        synchWeightData();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e(TAG,"onPause()");
        scanLeDevice(false);
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(mContext);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.e(TAG,"onCreateOptionsMenu()");
        SherlockFragment sherlockFragment=(SherlockFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (sherlockFragment instanceof ContentFragment) {
            getSupportMenuInflater().inflate(R.menu.main, menu);
        }else if(sherlockFragment instanceof  MyFragment){
            getSupportMenuInflater().inflate(R.menu.my,menu);
        }else{
            getSupportMenuInflater().inflate(R.menu.category,menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_main_edit:{
                Intent intent=new Intent(TabActivity.this,ContentEActivity.class);
                SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment instanceof ContentFragment) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("account",((ContentFragment)fragment).getLocalAccountEntity());
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                break;
            }
            case R.id.item_menu_setting:{

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startScan(){
        if (mBleSupported){
            scanLeDevice(true);
            if (!mScanning){
                updateUIStatus("未连接",-1);
            }
        }else{
            updateUIStatus("BLE不支持",-1);
        }
    }

    private boolean scanLeDevice(boolean enable){
        if (enable){
            mScanning=mBluetoothAdapter.startLeScan(mLeScanCallback);
        }else{
            mScanning=false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        return mScanning;
    }

    /**
     * type 0 scan  1 val  -1 disconnect  2
     * @param msg
     * @param type
     */
    private void updateUIStatus(String msg,int type){
        SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ScanFragment) {
            ((ScanFragment)fragment).updateStatus(msg,type);
        }
        if (fragment instanceof ContentFragment){
            ((ContentFragment)fragment).updateWeightValue(msg,type);
            localAccountEntity=((ContentFragment)fragment).getLocalAccountEntity();
        }
        Log.e(TAG, "updateUIStatus " + msg);
    }

    private void updateUIResult()
    {
        SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ContentFragment){
            ((ContentFragment)fragment).updateResult();
        }
    }

    private void onConnect(BluetoothDevice device){
        if (mBluetoothLeService==null||device==null){
            Log.e(TAG,"onConnect service or device is null");
            return;
        }
        if (mScanning){
            mScanning=false;
            scanLeDevice(false);
        }
        MobclickAgent.onEvent(mContext,"Connect BLE");
        Log.e(TAG,device.getName()+"-------->"+device.getAddress()+"  ="+device.getName().indexOf("7-11J/BT0721"));
        if (mConnIndex==NO_DEVICE){
//            updateUIStatus(getResources().getString(R.string.main_connecting),0);
            int connState=mBluetoothManager.getConnectionState(device,BluetoothGatt.GATT);
            switch (connState){
                case BluetoothGatt.STATE_CONNECTED:{
                    mBluetoothLeService.disconnect(mCurrentAddress);
                    break;
                }
                case BluetoothGatt.STATE_DISCONNECTED:{
                    boolean ok=mBluetoothLeService.connect(device.getAddress());
                    if (!ok){
                        updateUIStatus(getResources().getString(R.string.main_connect_err),-1);
                    }
                    break;
                }
                default:{
                    updateUIStatus(getResources().getString(R.string.main_connect_err),-1);
                    break;
                }
            }

        }else{
            updateUIStatus("断开连接",-1);
            if (mConnIndex!=NO_DEVICE){
                mBluetoothLeService.disconnect(device.getAddress());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK){

        }
        return  super.onKeyDown(keyCode,event);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }


    //返回提示，退出应用
    private long exitTime=0;
    public boolean dispatchKeyEvent(KeyEvent event){
        int keyCode=event.getKeyCode();
        if (event.getAction()==KeyEvent.ACTION_DOWN&&keyCode==KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(mContext, getResources().getString(R.string.exit_tips), Toast.LENGTH_LONG).show();
                exitTime=System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case AppConfig.REQUEST_ENABLE_BT:
            {
                if (resultCode==RESULT_CANCELED){
                    Log.e(TAG,"BluetoothLeService is not null.");
                }else{
                    startBluetoothLeService();
                }
                break;
            }
        }
    }


    private void startBluetoothLeService(){
        boolean f;
        Intent bindIntent=new Intent(this,BluetoothLeService.class);
        startService(bindIntent);
        f=bindService(bindIntent,mServiceConnection,Context.BIND_AUTO_CREATE);
        if (f){
            Log.e(TAG,"BluetoothLeService - success");
        }else{
            Log.e(TAG,"BluetoothLeService bind Failed");
        }
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action=intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                switch (mBluetoothAdapter.getState()){
                    case BluetoothAdapter.STATE_ON:{
                        mConnIndex=NO_DEVICE;
                        startBluetoothLeService();
                        break;

                    }
                    case BluetoothAdapter.STATE_OFF:
                    {

                        break;
                    }
                    default:
                        break;
                }
            }else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){

                int status=intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
                if (status==BluetoothGatt.GATT_SUCCESS){
                    mCurrentAddress=intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
                    mBluetoothLeService.discoverService(mCurrentAddress);
                    updateUIStatus(getResources().getString(R.string.main_connected), 0);
                }else{
                    updateUIStatus(getResources().getString(R.string.main_connect_err),-1);
                }

            }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                int status=intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
                if (status==BluetoothGatt.GATT_SUCCESS){
                    updateUIStatus(getResources().getString(R.string.main_disconnected),0);
                }else{
                    updateUIStatus(getResources().getString(R.string.main_connect_err),-1);
                }
                mConnIndex=NO_DEVICE;
                mBluetoothLeService.close();
                startScan();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                Log.e(TAG,"receive broadcast from BLEService ACTION_GATT_SERVICES_DISCOVERED");
                if (!mCurrentAddress.equals(intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS))){
                    return;
                }
                gattServices=mBluetoothLeService.getSupportedGattServices(mCurrentAddress);
                dealGattService();
//                BluetoothGattService gattService=mBluetoothLeService.getService(mCurrentAddress,BluetoothLeService.SERVICE_UUID);
//                if (gattService!=null) {
//                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(BluetoothLeService.SERVICE_UUID1));
//                    characteristic.setValue("1");
//                    mBluetoothLeService.writeCharacteristic(mCurrentAddress, characteristic);
//                    BluetoothGattCharacteristic characteristic1=gattService.getCharacteristic(UUID.fromString(BluetoothLeService.SERVICE_UUID2));
//                    mBluetoothLeService.setCharacteristicNotification(mCurrentAddress,characteristic1,true);
//                    mBluetoothLeService.readCharacteristic(mCurrentAddress,characteristic);
//                }
            }else if(BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)){

                Log.e(TAG,"receive broadcast from BLEService ACTION_DATA_NOTIFY");

                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                dealRecvData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));

                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    String text = new String(data) + "\n" + stringBuilder.toString();
                    Log.e(TAG,"---->"+text);
                }

            }else if(BluetoothLeService.ACTION_DATA_READ.equals(action)){

                Log.e(TAG,"receive broadcast from BLEService ACTION_DATA_READ");
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    String text = new String(data) + "\n" + stringBuilder.toString();
                    Log.e(TAG,"---->"+text);
                }
            }else if(BluetoothLeService.ACTION_DATA_WRITE.equals(action)){

                Log.e(TAG,"receive broadcast from BLEService ACTION_DATA_WRITE");
            }
            else if (AppConfig.ACTION_START_WEIGH.equals(action)){
                Log.e(TAG," action:"+action);
                if (mCurrentAddress!=null) {
                    Log.i(TAG," ===> "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_HEIGHT,0))+"  "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_AGE,0))+"  "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_SEX,0)));
                    Log.i(TAG," ===> "+Integer.parseInt("aa",16)+"  "+Integer.parseInt("23",16)+"  "+Integer.parseInt("0",16));
                    BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb"), BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, BluetoothGattCharacteristic.PROPERTY_WRITE);
                    gattCharacteristic.setValue(new byte[]{0x2});
                    mBluetoothLeService.writeCharacteristic(mCurrentAddress, gattCharacteristic);
                    Log.e(TAG, "writeCharacteristic ALERT_LEVELE");
                }
            }else{
                Log.w(TAG, "Unknown action:" + action);
            }

        }
    };

    private final ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothLeService=((BluetoothLeService.LocalBinder)iBinder).getService();
            if (!mBluetoothLeService.initialize()){
                Log.e(TAG,"Unable to initialize BluetoothLeService");
                return;
            }

            final int n=mBluetoothLeService.numConnectedDevices();
            if (n>0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }else {

                Log.i(TAG,"BluetoothLeService connected");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService=null;
            Log.i(TAG,"BluetoothLeService disconnected.");
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!StringUtil.isEmpty(bluetoothDevice.getName())){
                        Log.e(TAG,"===>"+Math.pow(10.0,2.0));
                        Log.e(TAG,""+bluetoothDevice.getName()+"  "+bluetoothDevice.getAddress());
                        if (bluetoothDevice.getName().indexOf("7-11J/BT0721")>0){
                            onConnect(bluetoothDevice);
                        }
                    }
                }
            });
        }
    };


    private void dealRecvData(byte[]  data){
        if (data != null && data.length > 0) {

            byte total=0;
            for (int i=0;i<19;i++){
                total+=data[i];
            }
            if (total!=data[19]){
                return;
            }
            Log.e(TAG,"checknumber ====="+total+"  ===== "+data[19]);
            String cmd=String.format("%02X",data[0]);
            if (cmd.equals("10")) {
                String len = String.format("%02x", data[1]);
                String dType = String.format("%02x", data[2]);
                int h=Integer.parseInt(String.format("%02x", data[3]), 16);
                int l=Integer.parseInt(String.format("%02x", data[4]), 16);
                String requestID=String.format("%02x", data[5]);
                int fatH=Integer.parseInt(String.format("%02x", data[6]), 16);
                int fatL=Integer.parseInt(String.format("%02x", data[7]), 16);
                int subFatH=Integer.parseInt(String.format("%02x", data[8]), 16);
                int subFatL=Integer.parseInt(String.format("%02x",data[9]),16);
                int visFat=Integer.parseInt(String.format("%02x",data[10]),16);
                int waterH=Integer.parseInt(String.format("%02x",data[11]),16);
                int waterL=Integer.parseInt(String.format("%02x",data[12]),16);
                int bmrH=Integer.parseInt(String.format("%02x",data[13]),16);
                int bmrL=Integer.parseInt(String.format("%02x",data[14]),16);
                int bodyAge=Integer.parseInt(String.format("%02x",data[15]),16);
                int muscleH=Integer.parseInt(String.format("%02x",data[16]),16);
                int muscleL=Integer.parseInt(String.format("%02x",data[17]),16);
                int bone=Integer.parseInt(String.format("%02x",data[18]),16);

                if (requestID.equals("00")){
                    String weight=String.format("%.1f",(h*256+l)/10.0);
                    updateUIStatus(weight,1);
                }else if (requestID.equals("01")){
                    //传参数
                    String weight=String.format("%.1f",(h*256+l)/10.0);
                    updateUIStatus(weight,1);
                    sendStartWeight();
                }else if(requestID.equals("02")){
                    //计算完成
                    respCmd(16);
                    updateUIStatus(StringUtil.doubleToStringOne((h*256+l)/10.0),2);
                    saveWeightData(StringUtil.doubleToStringOne((h*256+l)/10.0),StringUtil.doubleToStringOne((fatH*256+fatL)/10.0),
                            StringUtil.doubleToStringOne((subFatH*256+subFatL)/10.0),StringUtil.doubleToStringOne(visFat/10.0),StringUtil.doubleToStringOne((waterH*256+waterL)/10.0),
                            StringUtil.doubleToString(bmrH*256+bmrL),String.format("%d",bodyAge),StringUtil.doubleToStringOne((muscleH*256+muscleL)/10.0),StringUtil.doubleToStringOne(bone/10.0));
                    updateUIResult();
                }
            }
        }
    }

    private void dealGattService()
    {
        if(gattServices!=null&&gattServices.size()>0) {
            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    Log.e(TAG, gattCharacteristic.getProperties() + "  " + gattCharacteristic.getUuid());
                    UUID uuid = gattCharacteristic.getUuid();
                    if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)) {
                        mBluetoothLeService.setCharacteristicNotification(mCurrentAddress, gattCharacteristic, true);
                    }
                }
            }
        }
    }

    private void respCmd(int type){
        byte[]  data={9,11,18,31,5,1,(byte)(type & 0xFF),53};
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            String text = new String(data) + "\n" + stringBuilder.toString();
            Log.e(TAG,"- write:--->"+text);
        }

        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG,gattCharacteristic.getProperties()+"  "+gattCharacteristic.getUuid());
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID1)){
                    gattCharacteristic.setValue(data);
                    mBluetoothLeService.writeCharacteristic(mCurrentAddress,gattCharacteristic);
                }
            }
        }
    }

    private void sendStartWeight(){
        if (localAccountEntity==null){
            return;
        }
        int h=localAccountEntity.getHeight();
        int age=localAccountEntity.getAge();
        int sex=localAccountEntity.getSex();
        int checkNum=17+8+1+h+age+sex+1;
        Log.e(TAG,""+h+"  "+age+"  "+sex);
        byte[]  data={9,11,18,17,8,1,(byte)(h & 0xFF),(byte)(age & 0xFF),(byte)(sex & 0xFF),1,(byte)(checkNum & 0xFF)};
        //09 0B 12 11 08 1 height age sex 1

        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            String text = new String(data) + "\n" + stringBuilder.toString();
            Log.e(TAG,"- write:--->"+text);
        }

        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            Log.e(TAG,"UUID:"+gattService.getUuid());
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG,gattCharacteristic.getProperties()+"  "+gattCharacteristic.getUuid());
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID1)){
                    Log.e(TAG,"------------------------>"+uuid.toString()+"  "+data);
                    gattCharacteristic.setValue(data);
                    mBluetoothLeService.writeCharacteristic(mCurrentAddress,gattCharacteristic);
                }
            }
        }
    }

    private void saveWeightData(String weight,String fat,String subFat,String visFat,String water,String bmr,String bodyAge,String muscle,String bone)
    {
        SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        WeightHisEntity entity=new WeightHisEntity();
        int aid=1;
        AccountEntity accountEntity=null;
        if (fragment instanceof ContentFragment){
            accountEntity=((ContentFragment)fragment).getLocalAccountEntity();
            if (accountEntity!=null) {
                aid=accountEntity.getId();
                entity.setAid(accountEntity.getId());
                double w=Double.parseDouble(weight);
                double h=Math.pow(accountEntity.getHeight()/100.0,2.0);
                entity.setBmi(w/h);
            }
        }
        entity.setPickTime(strToday);
        entity.setWeight(Double.parseDouble(weight));
        entity.setFat(Double.parseDouble(fat));
        entity.setSubFat(Double.parseDouble(subFat));
        entity.setVisFat(Double.parseDouble(visFat));
        entity.setWater(Double.parseDouble(water));
        entity.setBMR(Double.parseDouble(bmr));
        entity.setBodyAge(Integer.parseInt(bodyAge));
        entity.setMuscle(Double.parseDouble(muscle));
        entity.setBone(Double.parseDouble(bone));
        entity.setIsSync(0);
        entity.setAddtime(System.currentTimeMillis());
        try{
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());

            Log.e(TAG," insert to health hist:"+dao.queryForAll().size());
            GenericRawResults<String[]> rawResults=dao.queryRaw("select pickTime,count(*),sum(weight),sum(fat),sum(subFat),sum(visFat)," +
                    "sum(water),sum(BMR),sum(bodyAge),sum(muscle),sum(bone),sum(bmi) from t_weight_his where aid="+aid+" and pickTime='"+strToday+"' group by pickTime");
            List<String[]> results=rawResults.getResults();
            Log.e(TAG," select result size:"+results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                Log.e(TAG, "pickTime " + resultArray[0] + "  " + resultArray[1] + "  " + resultArray[2] + "  " + resultArray[3] + "  " + resultArray[4] + "  " + resultArray[5] + "  " + resultArray[6] + "  " + resultArray[7] + "  " + resultArray[8] + "  " + resultArray[9] + "  " + resultArray[10]);
                if (accountEntity != null) {
                    int count = Integer.parseInt(resultArray[1]);
                    accountEntity.setWeight(String.valueOf(Double.parseDouble(resultArray[2]) / count));
                    accountEntity.setFat(String.valueOf(Double.parseDouble(resultArray[3]) / count));
                    accountEntity.setSubFat(String.valueOf(Double.parseDouble(resultArray[4]) / count));
                    accountEntity.setVisFat(String.valueOf(Double.parseDouble(resultArray[5]) / count));
                    accountEntity.setWater(String.valueOf(Double.parseDouble(resultArray[6]) / count));
                    accountEntity.setBmr(String.valueOf(Double.parseDouble(resultArray[7]) / count));
                    accountEntity.setBodyAge(String.valueOf(Integer.parseInt(resultArray[8]) / count));
                    accountEntity.setMuscle(String.valueOf(Double.parseDouble(resultArray[9]) / count));
                    accountEntity.setBone(String.valueOf(Double.parseDouble(resultArray[10]) / count));;
                    accountEntity.setBmi(String.valueOf(Double.parseDouble(resultArray[11]) / count));
                    accountEntity.setIsSync(0);
                    Dao<AccountEntity, Integer> accountEntityDao = getDatabaseHelper().getAccountEntityDao();
                    accountEntityDao.setAutoCommit(accountEntityDao.startThreadConnection(), false);
                    accountEntityDao.createOrUpdate(accountEntity);
                    accountEntityDao.commit(dao.startThreadConnection());

                    AddHealthForDay(accountEntity.getId(),strToday,0,accountEntity.getBmi());
                    AddHealthForDay(accountEntity.getId(),strToday,1,accountEntity.getWeight());
                    AddHealthForDay(accountEntity.getId(),strToday,2,accountEntity.getFat());
                    AddHealthForDay(accountEntity.getId(),strToday,3,accountEntity.getSubFat());
                    AddHealthForDay(accountEntity.getId(),strToday,4,accountEntity.getVisFat());
                    AddHealthForDay(accountEntity.getId(),strToday,5,accountEntity.getWater());
                    AddHealthForDay(accountEntity.getId(),strToday,6,accountEntity.getBmr());
                    AddHealthForDay(accountEntity.getId(),strToday,7,accountEntity.getBodyAge());
                    AddHealthForDay(accountEntity.getId(),strToday,8,accountEntity.getMuscle());
                    AddHealthForDay(accountEntity.getId(),strToday,9,accountEntity.getBone());
                    AddHealthForDay(accountEntity.getId(),strToday,10,accountEntity.getProtein());

                    AddWeightForDay(accountEntity.getId(),strToday,accountEntity.getWeight(),accountEntity.getFat(),accountEntity.getSubFat(),accountEntity.getVisFat(),accountEntity.getWater(),accountEntity.getBmr(),accountEntity.getBodyAge(),accountEntity.getMuscle(),accountEntity.getBone(),accountEntity.getBmi());
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        synchWeightData();
    }

    private void AddHealthForDay(int accountId,String pickTime,int targetType,String targetValue){
        try{
            HealthEntity entity=searchForHealth(accountId,pickTime,targetType);
            if (entity==null){
                entity=new HealthEntity();
                entity.setAccountId(accountId);
                entity.setPickTime(pickTime);
                entity.setCreateTime(System.currentTimeMillis());
            }
            entity.setTargetType(targetType);
            entity.setTargetValue(targetValue);
            entity.setIsSync(0);
            Dao<HealthEntity,Integer> dao=getDatabaseHelper().getHealthDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private HealthEntity searchForHealth(int accountId,String pickTime,int targetType){
        try{
            List<HealthEntity> healths=getDatabaseHelper().getHealthDao().queryBuilder().where().eq("accountId",accountId).and().eq("pickTime",pickTime).and().eq("targetType",targetType).query();
            if (healths.size()>0){
                return healths.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private int deleteForHealth(String pickTime){
        try {
            DeleteBuilder<HealthEntity,Integer> deleteBuilder=getDatabaseHelper().getHealthDao().deleteBuilder();
            deleteBuilder.where().eq("pickTime",pickTime);
            return deleteBuilder.delete();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private void AddWeightForDay(int aId,String pickTime,String weight,String fat,String subFat,String visFat,String water
            ,String bmr,String bodyAge,String muscle,String bone,String bmi){
        try{
            WeightEntity entity=searchForWeight(aId, pickTime);
            if (entity==null){
                entity=new WeightEntity();
                entity.setAid(aId);
                entity.setPickTime(pickTime);
                entity.setAddtime(System.currentTimeMillis());
            }
            entity.setWeight(weight);
            entity.setFat(fat);
            entity.setSubFat(subFat);
            entity.setVisFat(visFat);
            entity.setWater(water);
            entity.setBMR(bmr);
            entity.setBodyAge(bodyAge);
            entity.setMuscle(muscle);
            entity.setBone(bone);
            entity.setBmi(bmi);
            entity.setIsSync(0);
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private WeightEntity searchForWeight(int accountId,String pickTime){
        try{
            List<WeightEntity> weights=getDatabaseHelper().getWeightEntityDao().queryBuilder().where().eq("aid",accountId).and().eq("pickTime",pickTime).query();
            if (weights.size()>0){
                return weights.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private void synchWeightData()
    {
        try {
            Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("isSync", 0);
            List<WeightHisEntity> list=weightHisQueryBuilder.query();
            if (list.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list);
                if (!StringUtil.isEmpty(appContext.getProperty("uid"))) {
                    params.put("uid", appContext.getProperty("uid"));
                }
                params.put("imei",appContext.getIMSI());
                request(URLs.WEIGHT_HIS_SYNC_URL, JsonUtil.toJson(params).toString());
            }

            Dao<WeightEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightEntityDao();
            QueryBuilder<WeightEntity, Integer> weightQueryBuilder = weightEntityDao.queryBuilder();
            weightQueryBuilder.where().eq("isSync", 0);
            List<WeightEntity> weightEntityList=weightQueryBuilder.query();
            if (weightEntityList.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",weightEntityList);
                if (!StringUtil.isEmpty(appContext.getProperty("uid"))) {
                    params.put("uid", appContext.getProperty("uid").toString());
                }
                params.put("imei",appContext.getIMSI());
                request(URLs.WEIGHT_SYNC_URL, JsonUtil.toJson(params).toString());
            }

            Dao<AccountEntity,Integer> accountEntities=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> accountEntityIntegerQueryBuilder=accountEntities.queryBuilder();
            accountEntityIntegerQueryBuilder.where().eq("isSync",0).and().notIn("type",2);
            List<AccountEntity> list1=accountEntityIntegerQueryBuilder.query();
            if (list1.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list1);
                if (!StringUtil.isEmpty(appContext.getProperty("uid"))) {
                    params.put("uid", appContext.getProperty("uid").toString());
                }
                params.put("imei",appContext.getIMSI());

                request(URLs.ACCOUNT_SYNC_URL,JsonUtil.toJson(params).toString());
            }

            {
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("syncid",getMaxWeightForSyncId());
                if (!StringUtil.isEmpty(appContext.getProperty("uid"))) {
                    params.put("uid", appContext.getProperty("uid").toString());
                }
                params.put("imei",appContext.getIMSI());
                request(URLs.SYNC_WEIGHT_URL,JsonUtil.toJson(params).toString());

                params.put("syncid",getMaxWeightHisForSyncId());
                request(URLs.SYNC_WEIGHT_HIS_URL, JsonUtil.toJson(params).toString());
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        catch (JSONException je){
            je.printStackTrace();
        }
    }


    private void request(final String url,final String params){
        final Handler handler=new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==1){
                    parserResp(msg.obj.toString());
                    MobclickAgent.onEvent(mContext,"SyncData");
                }else {
                    MobclickAgent.onEvent(mContext,"SyncDataFaild");
                }
            }
        };

        new Thread(){
            public void run(){
                Message msg=new Message();
                try{
                    String resp=appContext.sendRequestData(url, params,null);
                    if (resp!=null){
                        msg.what=1;
                        msg.obj=resp;
                    }else{
                        msg.what=0;
                        msg.obj="登录失败";
                    }
                }catch (AppException e){
                    e.printStackTrace();
                    msg.what=-1;
                    msg.obj=e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


    private void parserResp(String resp){
        Log.e(TAG,"parserResp:"+resp);
        try{
            BaseResp baseResp=(BaseResp)JsonUtil.ObjFromJson(resp,BaseResp.class);
            if (baseResp.getDataType().equals("syncweighthis")){
                if (baseResp.getStatus()==1){
                    Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                    weightHisEntityDao.updateRaw("UPDATE `t_weight_his` SET isSync = 1 WHERE isSync=0;");
                }
            }else if(baseResp.getDataType().equals("syncweight")){
                if (baseResp.getStatus()==1) {
                    Dao<WeightEntity, Integer> weightEngityDao = getDatabaseHelper().getWeightEntityDao();
                    weightEngityDao.updateRaw("UPDATE `t_weight` SET isSync=1 where isSync=0;");
                }
            }else if(baseResp.getDataType().equals("getWeights")){
                WeightResp weightResp=(WeightResp)JsonUtil.ObjFromJson(resp,WeightResp.class);
                if (weightResp.getRoot()!=null&&weightResp.getRoot().size()>0){
                    for (int i=0;i<weightResp.getRoot().size();i++){
                        addWeightEntity(weightResp.getRoot().get(i));
                    }
                    SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (fragment instanceof ContentFragment){
                        ((ContentFragment)fragment).RefreshHistoryData();
                    }

                }
            }else if(baseResp.getDataType().equals("getWeightHiss")){
                WeightHisResp weightHisResp=(WeightHisResp)JsonUtil.ObjFromJson(resp,WeightHisResp.class);
                if (weightHisResp.getRoot()!=null&&weightHisResp.getRoot().size()>0){
                    for (int j=0;j<weightHisResp.getRoot().size();j++){
                        addWeightHisEntity(weightHisResp.getRoot().get(j));
                    }
                    Dao<WeightHisEntity, Integer> weightEntityDao = getDatabaseHelper().getWeightHisEntityDao();
                    QueryBuilder<WeightHisEntity, Integer> weightQueryBuilder = weightEntityDao.queryBuilder();
                    List<WeightHisEntity> list=weightQueryBuilder.query();
                    if (list!=null){
                        Log.e(TAG,"===============> weight list------------> size "+list.size());
                    }
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int  getMaxWeightForSyncId(){
        try{
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select max(syncid) from t_weight ");
            List<String[]> results=rawResults.getResults();
            Log.e(TAG," select result size:"+results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                if (!StringUtil.isEmpty(resultArray[0])) {
                    return Integer.parseInt(resultArray[0]);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private int  getMaxWeightHisForSyncId(){
        try{
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            GenericRawResults<String[]> rawResults=dao.queryRaw("select max(syncid) from t_weight_his ");
            List<String[]> results=rawResults.getResults();
            Log.e(TAG," select result size:"+results.size());
            if (results.size()>0) {
                String[] resultArray = results.get(0);
                if (!StringUtil.isEmpty(resultArray[0])) {
                    return Integer.parseInt(resultArray[0]);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private void addWeightEntity(WeightEntity entity){
        try{
            if (entity!=null){
                Log.e(TAG,"------------>"+entity.getPickTime()+" "+entity.getWeight()+"  "+entity.getIsSync());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),0,entity.getBmi());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),1,entity.getWeight());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),2,entity.getFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),3,entity.getSubFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),4,entity.getVisFat());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),5,entity.getWater());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),6,entity.getBMR());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),7,entity.getBodyAge());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),8,entity.getMuscle());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),9,entity.getBone());
                AddHealthForDay(entity.getAid(),entity.getPickTime(),10,entity.getProtein());
            }
            Dao<WeightEntity,Integer> dao=getDatabaseHelper().getWeightEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    private void addWeightHisEntity(WeightHisEntity entity){
        try{
            if (entity!=null){
                Log.e(TAG,"addWeightHisEntity "+entity.getPickTime()+" "+entity.getWeight());
            }
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private AccountEntity getAccountEntity(){
        try {
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
            queryBuilder.where().eq("type",1);
            queryBuilder.orderBy("id",true);
            PreparedQuery<AccountEntity> preparedQuery1=queryBuilder.prepare();
            List<AccountEntity> list=dao.query(preparedQuery1);
            if (list.size()>0){
                return list.get(0);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return  null;
    }

    private void initEvent()
    {
        mTabMy.setOnClickListener(this);
        mTabInfo.setOnClickListener(this);
        mTabWeight.setOnClickListener(this);
//        mTabGroup.setOnClickListener(this);
//        mTabSettings.setOnClickListener(this);
    }

    private void initView()
    {
        mTabMy = (LinearLayout) findViewById(R.id.id_tab_my);
        mTabInfo = (LinearLayout) findViewById(R.id.id_tab_info);
        mTabWeight = (LinearLayout) findViewById(R.id.id_tab_weight);
//        mTabGroup = (LinearLayout) findViewById(R.id.id_tab_group);
//        mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

        mImgMy = (ImageButton) findViewById(R.id.id_tab_my_img);
        mImgInfo = (ImageButton) findViewById(R.id.id_tab_info_img);
        mImgWeight = (ImageButton) findViewById(R.id.id_tab_weight_img);
//        mImgGroup = (ImageButton) findViewById(R.id.id_tab_group_img);
//        mImgSettings = (ImageButton) findViewById(R.id.id_tab_settings_img);
    }

    private void setSelect(int i)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (i)
        {
            case 0:
                if (mTab01 == null)
                {
                    mTab01 = new MyFragment();
                    transaction.add(R.id.content_frame, mTab01);
                } else
                {
                    transaction.attach(mTab01);
                }
//                mImgWeixin.setImageResource(R.drawable.tab_weixin_pressed);
                break;
            case 1:
                if (mTab02 == null)
                {
                    mTab02 = new ContentFragment();
                    ((ContentFragment)mTab02).loadData(getAccountEntity());
                    transaction.add(R.id.content_frame, mTab02);
                } else
                {
                    transaction.attach(mTab02);

                }
//                mImgFrd.setImageResource(R.drawable.tab_find_frd_pressed);
                break;
            case 2:
                if (mTab03 == null)
                {
                    mTab03 = new InfoFragment();
                    transaction.add(R.id.content_frame, mTab03);
                } else
                {
                    transaction.attach(mTab03);
                }
//                mImgAddress.setImageResource(R.drawable.tab_address_pressed);
                break;
//            case 3:
//                if (mTab04 == null)
//                {
//                    mTab04 = new GroupFragment();
//                    transaction.add(R.id.content_frame, mTab04);
//                } else
//                {
//                    transaction.show(mTab04);
//                }
////                mImgSettings.setImageResource(R.drawable.tab_settings_pressed);
//                break;
//            case 4:
//                if (mTab05 == null)
//                {
//                    mTab05 = new SettingFragment();
//                    transaction.add(R.id.content_frame, mTab05);
//                } else
//                {
//                    transaction.show(mTab05);
//                }
////                mImgSettings.setImageResource(R.drawable.tab_settings_pressed);
//                break;

            default:
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction)
    {
        if (mTab01 != null)
        {
            transaction.detach(mTab01);
        }
        if (mTab02 != null)
        {
            transaction.detach(mTab02);
        }
        if (mTab03 != null)
        {
            transaction.detach(mTab03);
        }
//        if (mTab04 != null)
//        {
//            transaction.hide(mTab04);
//        }
//        if (mTab05 != null)
//        {
//            transaction.hide(mTab05);
//        }
    }

    @Override
    public void onClick(View v)
    {
        resetImgs();
        switch (v.getId())
        {
            case R.id.id_tab_my:
                setSelect(0);
                break;
            case R.id.id_tab_info:
                setSelect(2);
                break;
            case R.id.id_tab_weight:
                setSelect(1);
                break;
//            case R.id.id_tab_group:
//                setSelect(3);
//                break;
//            case R.id.id_tab_settings:
//                setSelect(4);
//                break;

            default:
                break;
        }
    }

    /**
     * 切换图片至暗色
     */
    private void resetImgs()
    {
//        mImgWeixin.setImageResource(R.drawable.tab_weixin_normal);
//        mImgFrd.setImageResource(R.drawable.tab_find_frd_normal);
//        mImgAddress.setImageResource(R.drawable.tab_address_normal);
//        mImgSettings.setImageResource(R.drawable.tab_settings_normal);
    }


}
