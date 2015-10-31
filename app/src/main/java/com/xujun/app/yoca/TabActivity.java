package com.xujun.app.yoca;

import android.app.ActivityManager;
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
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.j256.ormlite.stmt.Where;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;
import com.xujun.app.yoca.fragment.ChartLFragment;
import com.xujun.app.yoca.fragment.ContentFragment;
import com.xujun.app.yoca.fragment.InfoFragment;
import com.xujun.app.yoca.fragment.MyFragment;
import com.xujun.app.yoca.widget.PopAccount;
import com.xujun.model.BaseResp;
import com.xujun.model.WeightHisResp;
import com.xujun.model.WeightResp;
import com.xujun.sqlite.AccountEntity;
import com.xujun.sqlite.DatabaseHelper;
import com.xujun.sqlite.HealthEntity;
import com.xujun.sqlite.SendRecord;
import com.xujun.sqlite.WeightEntity;
import com.xujun.sqlite.WeightHisEntity;
import com.xujun.util.DateUtil;
import com.xujun.util.ImageUtils;
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
    private LinearLayout mTabChart;
//    private LinearLayout mTabSettings;

    private ImageButton mImgMy;
    private ImageButton mImgInfo;
    private ImageButton mImgWeight;
    private ImageButton mImgChart;
//    private ImageButton mImgSettings;

    private TextView    mTvMy;
    private TextView    mTvWeight;
    private TextView    mTvChart;
    private TextView    mTvInfo;

    private SherlockFragment mTab01;
    private SherlockFragment mTab02;
    private SherlockFragment mTab03;
    private SherlockFragment mTab04;
//    private SherlockFragment mTab05;

    private TextView mHeadTitle;
    private ImageButton mHeadIcon;
    private Button mHeadButton;

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

    private byte[]     mCurrentData;
    List<BluetoothGattService>      gattServices=new ArrayList<BluetoothGattService>();

//    private ListView            accountListView;
    private List<AccountEntity>  accountEntities=new ArrayList<AccountEntity>();
    private PopAccount           popAccount;
//    private AccountAdapter       accountAdapter;

    private Handler             mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_default);
        appContext=(AppContext)getApplication();
        mContext=getApplicationContext();
        mHandler=new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            mBleSupported=false;
        }
        mBluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if (mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startBluetoothLeService();
                }
            }, 2);
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, AppConfig.REQUEST_ENABLE_BT);
        }
        else{
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
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);

        PushAgent mPushAgent=PushAgent.getInstance(mContext);
        mPushAgent.enable();

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout= LayoutInflater.from(this).inflate(R.layout.custom_actionbar,null);
        mHeadTitle=(TextView)actionbarLayout.findViewById(R.id.tvHeadTitle);
        mHeadIcon=(ImageButton)actionbarLayout.findViewById(R.id.ibHeadBack);
        mHeadButton=(Button)actionbarLayout.findViewById(R.id.btnHeadEdit);
        mHeadButton.setOnClickListener(this);
        mHeadIcon.setOnClickListener(this);
        getActionBar().setCustomView(actionbarLayout);

        loadAccountEntitys();
        initView();
        initEvent();
        setSelect(1);
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

    private boolean isServiceRunning(){
        ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo:am.getRunningServices(Integer.MAX_VALUE)){
            if ("com.xujun.app.yoca.NotifyService".equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private Messenger messenger=null;
    private boolean isBounded=false;

    private ServiceConnection  notifyServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           messenger=new Messenger(iBinder);
            isBounded=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messenger=null;
            isBounded=false;
        }
    };

    public void onStart(){
        super.onStart();
        if (!isServiceRunning()){
            Intent intent=new Intent(this,NotifyService.class);
            startService(intent);
        }
        bindService(new Intent(this,NotifyService.class),notifyServiceConnection,BIND_AUTO_CREATE);
    }

    public void onStop(){
        super.onStop();
        if (isBounded){
            unbindService(notifyServiceConnection);
            isBounded=false;
        }
    }

    private void sendNotifyService(int actionType){
        Message msg=Message.obtain(null,actionType,0,0);
        try{
            if (messenger!=null) {
                messenger.send(msg);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        startScan();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(mContext);
        Log.e(TAG, " ===>" + UmengRegistrar.getRegistrationId(mContext) + "   " + System.currentTimeMillis());
        checkUserInfo();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause()");
        scanLeDevice(false);
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(mContext);
    }

    /**
     * 验证用户信息
     */
    private void checkUserInfo() {
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
            Where<AccountEntity,Integer> where=queryBuilder.where().eq("type", 1);
            queryBuilder.orderBy("type",true);
            PreparedQuery<AccountEntity> preparedQuery=queryBuilder.prepare();
            List<AccountEntity> lists=dao.query(preparedQuery);
            Log.e(TAG, "checkUserInfo list:" + lists.size());
            if (lists.size()>0){
                Log.e(TAG,""+lists.toString());
                for (int i=0;i<lists.size();i++){
                    AccountEntity entity=lists.get(i);
                    if (entity.getType()==1){
                        localAccountEntity=entity;
                    }
                }
            }else{
                Intent intent=new Intent(TabActivity.this,AccountActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt(AppConfig.PARAM_SOURCE_TYPE,1);
                bundle.putInt(AppConfig.PARAM_ACCOUNT_DATA_TYPE,AppConfig.REQUEST_ACCOUNT_ADD);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConfig.REQUEST_ACCOUNT_ADD);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadAccountEntitys();
        querySyncWeight();
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("type", "1");
        map.put("result", "2");
        MobclickAgent.onEvent(mContext, "2000", map);
    }

    private void loadAccountEntitys(){
        accountEntities.clear();
        try{
            Dao<AccountEntity,Integer> dao=getDatabaseHelper().getAccountEntityDao();
            QueryBuilder<AccountEntity,Integer> queryBuilder=dao.queryBuilder();
            Where<AccountEntity,Integer> where=queryBuilder.where();
            where.or(where.eq("type",0),where.eq("type",1));
            queryBuilder.orderBy("type", true);
            PreparedQuery<AccountEntity> preparedQuery=queryBuilder.prepare();
            List<AccountEntity> lists=dao.query(preparedQuery);
            if (lists.size()>0){
                accountEntities.addAll(lists);
                boolean  isSync=false;
                for (AccountEntity entity:lists){
                    if (entity.getIsSync()==0){
                        isSync=true;
                    }
                }
                if (isSync){
                    sendNotifyService(AppConfig.ACTION_ACCOUNT_SYNC);
                }
                Log.e(TAG,"loadAccountEntitys ...."+accountEntities.size());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void querySyncWeight(){
        try {
            Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("isSync", 0);
            List<WeightHisEntity> list = weightHisQueryBuilder.query();
            if (list.size() > 0) {
                sendNotifyService(AppConfig.ACTION_WEIGH_DATA_AVATAR);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    AdapterView.OnItemClickListener popAccountItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (popAccount!=null){
                popAccount.dismiss();
                Log.d(TAG,"popAccountItemClickListener  "+i);
                localAccountEntity=accountEntities.get(i);
                if (localAccountEntity!=null){
                    mHeadTitle.setText(localAccountEntity.getUserNick());
                    SherlockFragment sherlockFragment=(SherlockFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (sherlockFragment instanceof ContentFragment) {
                        ((ContentFragment)sherlockFragment).loadData(localAccountEntity);
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.d(TAG,"onCreateOptionsMenu()");
        mHeadButton.setVisibility(View.VISIBLE);
        mHeadIcon.setVisibility(View.INVISIBLE);
        SherlockFragment sherlockFragment=(SherlockFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (sherlockFragment instanceof ContentFragment) {
//            getSupportMenuInflater().inflate(R.menu.main, menu);
            mHeadButton.setText(getText(R.string.btn_shared));
            mHeadIcon.setVisibility(View.VISIBLE);
            localAccountEntity=((ContentFragment)sherlockFragment).getLocalAccountEntity();
            if (localAccountEntity!=null){
                mHeadTitle.setText(localAccountEntity.getUserNick());
                if (!StringUtil.isEmpty(localAccountEntity.getAvatar())){
                    Log.d(TAG, localAccountEntity.getAvatar());
                    mHeadIcon.setImageBitmap(ImageUtils.getBitmapByPath(appContext.getCameraPath() + "/crop_" + localAccountEntity.getAvatar()));
                }
            }

        }else if(sherlockFragment instanceof  MyFragment){
//            getSupportMenuInflater().inflate(R.menu.setting,menu);
            mHeadButton.setText(getText(R.string.btn_Logout));
            mHeadTitle.setText("我的");
        }else if (sherlockFragment instanceof ChartLFragment){
//            getSupportMenuInflater().inflate(R.menu.category,menu);
            mHeadTitle.setText("数据分析");
            mHeadButton.setVisibility(View.INVISIBLE);
        }else if(sherlockFragment instanceof InfoFragment){
            mHeadTitle.setText("资讯信息");
            mHeadButton.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                Log.d(TAG, "---------------------------->...............");
                break;
            }
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void startScan(){
        if (mBleSupported){
            scanLeDevice(true);
            if (!mScanning){
                updateUIStatus("未连接", -1);
            }
        }else{
            updateUIStatus("BLE不支持", -1);
        }
    }

    private void scanLeDevice(boolean enable){
        if (enable){
            Log.e(TAG,"scanLeDevice... Start...");
            if (!mScanning) {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }else{
                mScanning=false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning=true;
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                },500);
            }
        }else{
            mScanning=false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
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
            scanLeDevice(false);
        }
        HashMap<String,String> map=new HashMap<String, String>();
        map.put("devAddr",device.getAddress());
        map.put("devName", device.getName());
        MobclickAgent.onEvent(mContext, "2000", map);
        if (mConnIndex==NO_DEVICE){
//            updateUIStatus(getResources().getString(R.string.main_connecting),0);
            int connState=mBluetoothManager.getConnectionState(device,BluetoothGatt.GATT);
            switch (connState){
                case BluetoothGatt.STATE_CONNECTED:{
                    Log.e(TAG,"connected .....");
//                    mBluetoothLeService.disconnect();
                    break;
                }
                case BluetoothGatt.STATE_DISCONNECTED:{
                    Log.e(TAG,"connecting....");
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
                mBluetoothLeService.disconnect();
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
            case AppConfig.REQUEST_SWITCH_ACCOUNT:
            {

                break;
            }
            case AppConfig.REQUEST_ACCOUNT_ADD:{
                if (resultCode==AppConfig.SUCCESS){
                    Log.e(TAG,"account add success");
                    AccountEntity accountEntity=(AccountEntity)data.getSerializableExtra(AppConfig.PARAM_ACCOUNT);
                    if (accountEntity!=null){
                        Log.e(TAG, accountEntity.getId() + " " + accountEntity.getType() + "  " + accountEntity.getUserNick() + "  " + accountEntity.getBirthday());
                    }
                    loadAccountEntitys();
                }
                break;
            }
        }
    }


    private void startBluetoothLeService(){
        boolean f;
        Intent bindIntent=new Intent(this,BluetoothLeService.class);
        f=bindService(bindIntent,mServiceConnection,Context.BIND_AUTO_CREATE);
        if (f){
            Log.e(TAG,"BluetoothLeService - success");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScan();
                }
            },500);
        }else{
            Log.e(TAG,"BluetoothLeService bind Failed");
        }
    }

    private void writeSetting(byte[]  data){
        String sendPacket=null;
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e(TAG,"- write:--->"+stringBuilder.toString());
            sendPacket=stringBuilder.toString();
        }

        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)){
                    gattCharacteristic.setValue(data);
                    sendDataToBLE(gattCharacteristic,sendPacket);
                }
            }
        }
    }

    /**
     * 连接成功，写入设置信息
     */
    private void writeSettingInfo(int dataType){
        if (dataType==AppConfig.WRITE_DEVICE_SET_MODE){
            String val=appContext.getProperty(AppConfig.DEVICE_SET_SHOW_MODEL);
            if (StringUtil.isEmpty(val)){
                byte[]  data={9,8,18,21,5,1,(byte)(1&0xFF),(byte)(28&0xFF)};  //普通模式
                writeData(data);
            }else{
                if (val.equals("1")){
                    byte[]  data={9,8,18,21,5,1,(byte)(1&0xFF),(byte)(28&0xFF)};  //普通模式
                    writeData(data);
                }else if(val.equals("0")){
                    byte[]  data={9,8,18,21,5,1,(byte)(0&0xFF),(byte)(27&0xFF)};  //跑马模式
                    writeSetting(data);
                }else{
                    byte[]  data={9,8,18,21,5,1,(byte)(2&0xFF),(byte)(29&0xFF)};  //目标值
                    writeSetting(data);
                }
            }
        }else if(dataType==AppConfig.WRITE_DEVICE_SET_LEDLIGHT){
            String val=appContext.getProperty(AppConfig.DEVICE_SET_LED_LEVEL);
            if (StringUtil.isEmpty(val)){
                byte[]  data={9,8,18,26,5,1,(byte)(1&0xFF),(byte)(33&0xFF)};
                writeData(data);
            }else {
                if (val.equals("1")){
                    byte[]  data={9,8,18,26,5,1,(byte)(1&0xFF),(byte)(33&0xFF)};
                    writeData(data);
                }else if(val.equals("2")){
                    byte[]  data={9,8,18,26,5,1,(byte)(2&0xFF),(byte)(34&0xFF)};
                    writeData(data);
                }else if(val.equals("3")){
                    byte[]  data={9,8,18,26,5,1,(byte)(4&0xFF),(byte)(36&0xFF)};
                    writeData(data);
                }else if(val.equals("4")){
                    byte[]  data={9,8,18,26,5,1,(byte)(8&0xFF),(byte)(40&0xFF)};
                    writeData(data);
                }else if(val.equals("5")){
                    byte[]  data={9,8,18,26,5,1,(byte)(16&0xFF),(byte)(48&0xFF)};
                    writeData(data);
                }else if(val.equals("6")){
                    byte[]  data={9,8,18,26,5,1,(byte)(32&0xFF),(byte)(64&0xFF)};
                    writeData(data);
                }else if(val.equals("7")){
                    byte[]  data={9,8,18,26,5,1,(byte)(64&0xFF),(byte)(96&0xFF)};
                    writeData(data);
                }else if(val.equals("8")){
                    byte[]  data={9,8,18,26,5,1,(byte)(128&0xFF),(byte)(160&0xFF)};
                    writeData(data);
                }

            }

        }
//        byte[] d1={9,8,18,23,5,1,0,29};
//        writeSetting(d1);
//        byte[]  data={9,8,18,25,5,1,0,31};
//
//        byte[]  data={9,8,18,21,5,1,(byte)(3&0xFF),(byte)(30&0xFF)};  //
//        byte[]  data={9,8,18,21,5,1,(byte)(4&0xFF),(byte)(31&0xFF)};  // 跑马，目标
//        byte[]  data={9,8,18,21,5,1,(byte)(5&0xFF),(byte)(32&0xFF)};  //
//        byte[]  data={9,8,18,21,5,1,(byte)(6&0xFF),(byte)(33&0xFF)};  //
//        byte[]  data={9,8,18,21,5,1,(byte)(7&0xFF),(byte)(34&0xFF)};  //
//        byte[]  data={9,8,18,21,5,1,(byte)(7&0xFF),(byte)(35&0xFF)};  //
    }


    private void writeData(byte[] data){
        String sendPacket="";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e(TAG,"- write:--->"+stringBuilder.toString());
            sendPacket=stringBuilder.toString();
        }
        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)){
                    gattCharacteristic.setValue(data);
                    sendDataToBLE(gattCharacteristic,sendPacket);
                }
            }
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
                    updateUIStatus(getResources().getString(R.string.main_disconnected),3);
                }else{
                    updateUIStatus(getResources().getString(R.string.main_connect_err),-1);
                }
                mConnIndex=NO_DEVICE;
                mBluetoothLeService.close();
                startScan();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                Log.d(TAG,"receive broadcast from BLEService ACTION_GATT_SERVICES_DISCOVERED");
                if (!mCurrentAddress.equals(intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS))){
                    return;
                }
                gattServices=mBluetoothLeService.getSupportedGattServices(mCurrentAddress);
                dealGattService();
            }else if(BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)){
                Log.d(TAG, "receive broadcast from BLEService ACTION_DATA_NOTIFY");
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    String text =stringBuilder.toString();
                    Log.e(TAG, "====>ACTION_DATA_NOTIFY---->" + text);
                }
                dealRecvData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));

            }else if(BluetoothLeService.ACTION_DATA_READ.equals(action)){

                Log.d(TAG,"receive broadcast from BLEService ACTION_DATA_READ");
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    String text =stringBuilder.toString();
                    Log.e(TAG,"=====>ACTION_DATA_READ---->"+text);
                }
            }else if(BluetoothLeService.ACTION_DATA_WRITE.equals(action)){
                int status=intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,-1);
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    String text =stringBuilder.toString();
                    Log.e(TAG,"=====>ACTION_DATA_WRITE---->"+text);
                }
                Log.d(TAG,"receive broadcast from BLEService ACTION_DATA_WRITE status=>"+status);
            }
            else if (AppConfig.ACTION_START_WEIGH.equals(action)){
                Log.e(TAG," action:"+action);
                if (mCurrentAddress!=null) {
                    Log.i(TAG," ===> "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_HEIGHT,0))+"  "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_AGE,0))+"  "+Integer.toHexString(intent.getIntExtra(AppConfig.EXTRA_DATA_SEX,0)));
                    Log.i(TAG," ===> "+Integer.parseInt("aa",16)+"  "+Integer.parseInt("23",16)+"  "+Integer.parseInt("0",16));
                    BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb"), BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, BluetoothGattCharacteristic.PROPERTY_WRITE);
                    gattCharacteristic.setValue(new byte[]{0x2});
                    mBluetoothLeService.writeCharacteristic(mCurrentAddress, gattCharacteristic);
                    Log.d(TAG, "writeCharacteristic ALERT_LEVELE");
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
            Log.e(TAG,"c..........");
            startScan();
//            mBluetoothLeService.connect(mCurrentAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService=null;
            Log.i(TAG,"BluetoothLeService disconnected.");
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!StringUtil.isEmpty(bluetoothDevice.getName())){
                        String strName=bluetoothDevice.getName();
                        Log.e(TAG,"|"+strName+"|"+bluetoothDevice.getAddress()+" "+rssi);
                        if (strName.equals(AppConfig.APP_DEVICE_UUID)){
                            onConnect(bluetoothDevice);
                        }
                    }
                }
            });
        }
    };


    /**
     * 解包
     * @param data
     */
    private void dealRecvData(byte[]  data){
        if (data != null && data.length > 0&&data.length==20) {
            String cmd=String.format("%02X",data[0]);
            String len = String.format("%02x", data[1]);
            String dType = String.format("%02x", data[2]);
            if(cmd.equals("37")&&dType.equals("01")){
                Log.e(TAG, "dealRecvData  Disconnect ......" + mCurrentAddress);
                mBluetoothLeService.disconnect();
                mConnIndex=NO_DEVICE;
                mBluetoothLeService.close();
                updateUIStatus(getResources().getString(R.string.main_disconnected), 3);
                startScan();
                return;
            }else if(cmd.equals("37")&&dType.equals("00")){
                writeSettingInfo(AppConfig.WRITE_DEVICE_SET_MODE);
                writeSettingInfo(AppConfig.WRITE_DEVICE_SET_LEDLIGHT);
                sendAccountInfo();
                return;
            }
            byte total=0;
            for (int i=0;i<19;i++){
                total+=data[i];
            }
            if (total!=data[19]){
                return;
            }
            if (cmd.equals("10")) {
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
                Log.e(TAG,"=====>dealRecvData  requestID:"+requestID+"   ============> "+((h*256+l)/5.0)/20.0+"    "+StringUtil.doubleToStringOne(((h*256+l)/5.0)/20.0));
                String weight=String.format("%.1f",((h*256+l)/5.0)/2.0/10.0);
                if (requestID.equals("00")||requestID.equals("80")){
                    updateUIStatus(weight, 2);
                }else if (requestID.equals("01")){
                    //传参数
                    updateUIStatus(weight,2);
                    sendStartWeight();
                }else if(requestID.equals("02")){
                    //计算完成
                    respCmd(16);
                    updateUIStatus(weight,2);
                    saveWeightData(weight, StringUtil.doubleToStringOne((fatH * 256 + fatL) / 10.0),
                            StringUtil.doubleToStringOne((subFatH * 256 + subFatL) / 10.0), StringUtil.doubleToStringOne(visFat), StringUtil.doubleToStringOne((waterH * 256 + waterL) / 10.0),
                            StringUtil.doubleToString((bmrH * 256 + bmrL)), String.format("%d", bodyAge), StringUtil.doubleToStringOne((muscleH * 256 + muscleL) / 10.0), StringUtil.doubleToStringOne(bone));
                    updateUIResult();
                }
            }
        }
    }

    private void dealGattService()
    {
        if(gattServices!=null&&gattServices.size()>0) {
            for (BluetoothGattService gattService : gattServices) {
                int type=gattService.getType();
                Log.i(TAG,"---->service type:"+type);
                Log.i(TAG,"---->includedServices size:"+gattService.getIncludedServices().size());
                Log.i(TAG,"---->service uuid:"+gattService.getUuid());

                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    Log.i(TAG, gattCharacteristic.getProperties() + "  " + gattCharacteristic.getUuid()+" "+gattCharacteristic.getPermissions());
                    UUID uuid = gattCharacteristic.getUuid();
                    if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID1)) {
                        mBluetoothLeService.setCharacteristicNotification(mCurrentAddress, gattCharacteristic, true);
                    }
                }
            }
        }
    }

    private void sendDataToBLE(BluetoothGattCharacteristic gattCharacteristic,String strData){
        SendRecord  record=new SendRecord();
        record.setData(strData);
        record.setDevAddress(mCurrentAddress);
        record.setAddTime(DateUtil.getCurrentTimeString());

        byte[] data =gattCharacteristic.getValue();
        String text ="";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            text=stringBuilder.toString();
        }
        if (mBluetoothLeService.writeCharacteristic(mCurrentAddress,gattCharacteristic)){
            Log.e(TAG, "write data Success..." + text);
            record.setStatus(1);
        }else {
            Log.e(TAG, "write data Failed..." + text);
            record.setStatus(0);
        }
        addSendRecord(record);
    }


    private void respCmd(int type){
        byte[]  data={9,8,18,31,5,1,(byte)(type & 0xFF),53};
        String sendPacket="";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e(TAG,"- write:--->"+stringBuilder.toString());
            sendPacket=stringBuilder.toString();
        }

        byte total=0;
        for (int i=3;i<7;i++){
            total+=data[i];
        }
        if (total!=data[7]){
            Log.e(TAG, "checknumber is not ...");
        }
        Log.e(TAG, "checknumber =====" + total + "  ===== " + data[7]);


        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)){
                    gattCharacteristic.setValue(data);
//                    mBluetoothLeService.writeCharacteristic(mCurrentAddress,gattCharacteristic);
                    sendDataToBLE(gattCharacteristic,sendPacket);
                }
            }
        }
    }

    /***
     * 11 包
     */
    private void sendStartWeight(){
        if (localAccountEntity==null){
            Log.e(TAG,"sendStartWeight  localAccountEntity is null");
            return;
        }
        int h=localAccountEntity.getHeight();
        int age=localAccountEntity.getAge();
        int sex=localAccountEntity.getSex();
        int target=5000;
        if (StringUtil.isEmpty(localAccountEntity.getTargetWeight())){
            if(localAccountEntity.getSex()==0){
                target=(localAccountEntity.getHeight()-100)*100;
            }else{
                target=(localAccountEntity.getHeight()-105)*100;
            }
        }else{
            target=Integer.parseInt(localAccountEntity.getTargetWeight())*100;
        }
        int fat=200;
        if (!StringUtil.isEmpty(localAccountEntity.getTargetFat())){
            fat=(int)Double.parseDouble(localAccountEntity.getTargetFat())*100;
        }
        int fH=fat/256;
        int fL=fat-fH*256;
        int wH=target/256;
        int wL=target-wH*256;
        int checkNum=17+13+1+h+age+sex+1+0+wH+wL+fH+fL;
        Log.e(TAG, "" + h + "  " + age + "  " + sex+  "  "+target+"  "+wH+"  "+wL+ ""+fat+"  "+fH+"  "+fL);
//        byte[]  data={9,11,18,18,13,1,(byte)(h & 0xFF),(byte)(age & 0xFF),(byte)(sex & 0xFF),1,0,(byte)(wH&0xFF),(byte)(wL&0xFF),(byte)(fH&0xff),(byte)(fL&0xff),(byte)(checkNum & 0xFF)};
        byte[]  data={9,16,18,17,13,1,(byte)(h & 0xFF),(byte)(age & 0xFF),(byte)(sex & 0xFF),1,0,(byte)(wH&0xFF),(byte)(wL&0xFF),(byte)(fH&0xff),(byte)(fL&0xff),(byte)(checkNum & 0xFF)};

        //09 10 12
        //09 0B 12 12 08 1 height age sex 1 0 0 0 0 0
        String sendPacket="";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e(TAG,"write :--->"+stringBuilder.toString());
            sendPacket=stringBuilder.toString();
        }
        byte total=0;
        for (int i=3;i<15;i++){
            total+=data[i];
        }
        if (total!=data[15]){
            Log.e(TAG,"checknumber is not ...");
        }
        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)){
                    gattCharacteristic.setValue(data);
                    sendDataToBLE(gattCharacteristic,sendPacket);
                }
            }
        }
    }

    /***
     * 12 包
     */
    private void sendAccountInfo(){
        if (localAccountEntity==null){
            Log.e(TAG,"sendStartWeight  localAccountEntity is null");
            return;
        }
        int h=localAccountEntity.getHeight();
        int age=localAccountEntity.getAge();
        int sex=localAccountEntity.getSex();
        int target=5000;
        if (StringUtil.isEmpty(localAccountEntity.getTargetWeight())){
            if(localAccountEntity.getSex()==0){
                target=(localAccountEntity.getHeight()-100)*100;
            }else{
                target=(localAccountEntity.getHeight()-105)*100;
            }
        }else{
            target=Integer.parseInt(localAccountEntity.getTargetWeight())*100;
        }
        int fat=200;
        int fH=200/256;
        int fL=200-fH*256;
        int wH=target/256;
        int wL=target-wH*256;
        int checkNum=18+13+1+h+age+sex+1+0+wH+wL+fH+fL;
        Log.e(TAG, "" + h + "  " + age + "  " + sex+  "  "+target+"  "+wH+"  "+wL+ ""+fat+"  "+fH+"  "+fL);
        byte[]  data={9,16,18,18,13,1,(byte)(h & 0xFF),(byte)(age & 0xFF),(byte)(sex & 0xFF),1,0,(byte)(wH&0xFF),(byte)(wL&0xFF),(byte)(fH&0xff),(byte)(fL&0xff),(byte)(checkNum & 0xFF)};
        //09 10 12
        //09 0B 12 12 08 1 height age sex 1 0 0 0 0 0
        String sendPacket="";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.e(TAG,"write :--->"+stringBuilder.toString());
            sendPacket=stringBuilder.toString();
        }
        byte total=0;
        for (int i=3;i<15;i++){
            total+=data[i];
        }
        if (total!=data[15]){
            Log.e(TAG,"checknumber is not ...");
        }
        Log.e(TAG,"checknumber ====="+total+"  ===== "+data[15]);

        for (BluetoothGattService gattService : gattServices) {
            if (!gattService.getUuid().toString().equals(BluetoothLeService.SERVICE_UUID)){
                continue;
            }
            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                UUID uuid=gattCharacteristic.getUuid();
                if (uuid.toString().equals(BluetoothLeService.SERVICE_UUID2)){
                    gattCharacteristic.setValue(data);
                    sendDataToBLE(gattCharacteristic,sendPacket);
                }
            }
        }
    }

    private void saveWeightData(String weight,String fat,String subFat,String visFat,String water,String bmr,String bodyAge,String muscle,String bone)
    {
        SherlockFragment fragment = (SherlockFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        WeightHisEntity entity=new WeightHisEntity();
        entity.setWid(System.currentTimeMillis());
        long aid=1;
        if (localAccountEntity!=null) {
            aid=localAccountEntity.getId();
            entity.setAid(localAccountEntity.getId());
            double w=Double.parseDouble(weight);
            double h=Math.pow(localAccountEntity.getHeight() / 100.0, 2.0);
            String v=StringUtil.doubleToStringOne(w/h);
            entity.setBmi(StringUtil.toDouble(v));
        }
        entity.setPickTime(strToday);
        entity.setWeight(StringUtil.toDouble(weight));
        entity.setFat(StringUtil.toDouble(fat));
        entity.setSubFat(StringUtil.toDouble(subFat));
        entity.setVisFat(StringUtil.toDouble(visFat));
        entity.setWater(StringUtil.toDouble(water));
        entity.setBMR(StringUtil.toDouble(bmr));
        entity.setBodyAge(Integer.parseInt(bodyAge));
        entity.setMuscle(StringUtil.toDouble(muscle));
        entity.setBone(StringUtil.toDouble(bone));
        entity.setIsSync(0);
        entity.setAddtime(System.currentTimeMillis());
        try{
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(), false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());

            GenericRawResults<String[]> rawResults=dao.queryRaw("select pickTime,count(*),sum(weight),sum(fat),sum(subFat),sum(visFat)," +
                    "sum(water),sum(BMR),sum(bodyAge),sum(muscle),sum(bone),sum(bmi) from t_weight_his where aid="+aid+" and pickTime='"+strToday+"' group by pickTime");
            List<String[]> results=rawResults.getResults();
            if (results.size()>0) {

                Log.e(TAG,"++++++++++++++++===============> "+results.size());
                String[] resultArray = results.get(0);
                if (localAccountEntity != null) {
                    int count = Integer.parseInt(resultArray[1]);

                    localAccountEntity.setWeight(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[2]) / count));
                    localAccountEntity.setFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[3]) / count));
                    localAccountEntity.setSubFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[4]) / count));
                    localAccountEntity.setVisFat(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[5]) / count));
                    localAccountEntity.setWater(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[6]) / count));
                    localAccountEntity.setBmr(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[7]) / count));
                    localAccountEntity.setBodyAge(StringUtil.doubleToStringOne(StringUtil.toInt(resultArray[8]) / count));
                    localAccountEntity.setMuscle(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[9]) / count));
                    localAccountEntity.setBone(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[10]) / count));;
                    localAccountEntity.setBmi(StringUtil.doubleToStringOne(StringUtil.toDouble(resultArray[11]) / count));
                    localAccountEntity.setIsSync(0);
                    Dao<AccountEntity, Integer> accountEntityDao = getDatabaseHelper().getAccountEntityDao();
                    accountEntityDao.setAutoCommit(accountEntityDao.startThreadConnection(), false);
                    accountEntityDao.createOrUpdate(localAccountEntity);
                    accountEntityDao.commit(dao.startThreadConnection());

                    AddHealthForDay(aid,strToday,0,localAccountEntity.getBmi());
                    AddHealthForDay(aid,strToday,1,localAccountEntity.getWeight());
                    AddHealthForDay(aid,strToday,2,localAccountEntity.getFat());
                    AddHealthForDay(aid,strToday,3,localAccountEntity.getSubFat());
                    AddHealthForDay(aid,strToday,4,localAccountEntity.getVisFat());
                    AddHealthForDay(aid,strToday,5,localAccountEntity.getWater());
                    AddHealthForDay(aid,strToday,6,localAccountEntity.getBmr());
                    AddHealthForDay(aid,strToday,7,localAccountEntity.getBodyAge());
                    AddHealthForDay(aid,strToday,8,localAccountEntity.getMuscle());
                    AddHealthForDay(aid,strToday,9,localAccountEntity.getBone());
                    AddHealthForDay(aid,strToday,10,localAccountEntity.getProtein());

                    AddWeightForDay(aid,strToday,localAccountEntity.getWeight(),localAccountEntity.getFat(),localAccountEntity.getSubFat(),localAccountEntity.getVisFat(),localAccountEntity.getWater(),localAccountEntity.getBmr(),localAccountEntity.getBodyAge(),localAccountEntity.getMuscle(),localAccountEntity.getBone(),localAccountEntity.getBmi());
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        sendNotifyService(AppConfig.ACTION_WEIGH_DATA_UPLOAD);
    }

    private void AddHealthForDay(long accountId,String pickTime,int targetType,String targetValue){
        try{
            HealthEntity entity=searchForHealth(accountId, pickTime, targetType);
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

    private HealthEntity searchForHealth(long accountId,String pickTime,int targetType){
        try{
            List<HealthEntity> healths=getDatabaseHelper().getHealthDao().queryBuilder().where().eq("accountId",accountId).and().eq("pickTime",pickTime).and().eq("targetType", targetType).query();
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

    private void AddWeightForDay(long aId,String pickTime,String weight,String fat,String subFat,String visFat,String water
            ,String bmr,String bodyAge,String muscle,String bone,String bmi){
        try{
            WeightEntity entity=searchForWeight(aId, pickTime);
            if (entity==null){
                Log.e(TAG,"=================> is null");
                entity=new WeightEntity();
                entity.setWid(System.currentTimeMillis());
                entity.setAid(aId);
                entity.setPickTime(pickTime);
                entity.setAddtime(System.currentTimeMillis());
            }
            Log.e(TAG,"----------->"+entity.getWid());
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

    private WeightEntity searchForWeight(long accountId,String pickTime){
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
        if (appContext.getNetworkType()<1){
            Log.e(TAG,"无网络连接，暂不同步。");
            return;
        }
        try {
            Dao<WeightHisEntity, Integer> weightHisEntityDao = getDatabaseHelper().getWeightHisEntityDao();
            QueryBuilder<WeightHisEntity, Integer> weightHisQueryBuilder = weightHisEntityDao.queryBuilder();
            weightHisQueryBuilder.where().eq("isSync", 0);
            List<WeightHisEntity> list=weightHisQueryBuilder.query();
            if (list.size()>0){
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("root",list);
                if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", appContext.getProperty(AppConfig.CONF_USER_UID));
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
                if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", appContext.getProperty(AppConfig.CONF_USER_UID).toString());
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
                if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", appContext.getProperty(AppConfig.CONF_USER_UID).toString());
                }
                params.put("imei",appContext.getIMSI());

                request(URLs.ACCOUNT_SYNC_URL,JsonUtil.toJson(params).toString());
            }

            {
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("syncid",getMaxWeightForSyncId());
                if (!StringUtil.isEmpty(appContext.getProperty(AppConfig.CONF_USER_UID))) {
                    params.put("uid", appContext.getProperty(AppConfig.CONF_USER_UID).toString());
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
        Log.d(TAG, "parserResp:" + resp);
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
                        Log.d(TAG,"===============> weight list------------> size "+list.size());
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
            Log.d(TAG, " select result size:" + results.size());
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
            Log.d(TAG, " select result size:" + results.size());
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
                Log.d(TAG,"------------>"+entity.getPickTime()+" "+entity.getWeight()+"  "+entity.getIsSync());
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
                Log.d(TAG,"addWeightHisEntity "+entity.getPickTime()+" "+entity.getWeight());
            }
            Dao<WeightHisEntity,Integer> dao=getDatabaseHelper().getWeightHisEntityDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(entity);
            dao.commit(dao.startThreadConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void addSendRecord(SendRecord record){
        try{
            Dao<SendRecord,Integer> dao=getDatabaseHelper().getSendRecordDao();
            dao.setAutoCommit(dao.startThreadConnection(),false);
            dao.createOrUpdate(record);
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
            queryBuilder.orderBy("id", true);
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
        mTabChart.setOnClickListener(this);
//        mTabSettings.setOnClickListener(this);
    }

    private void initView()
    {
        mTabMy = (LinearLayout) findViewById(R.id.id_tab_my);
        mTabInfo = (LinearLayout) findViewById(R.id.id_tab_info);
        mTabWeight = (LinearLayout) findViewById(R.id.id_tab_weight);
        mTabChart = (LinearLayout) findViewById(R.id.id_tab_chart);
//        mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

        mImgMy = (ImageButton) findViewById(R.id.id_tab_my_img);
        mImgInfo = (ImageButton) findViewById(R.id.id_tab_info_img);
        mImgWeight = (ImageButton) findViewById(R.id.id_tab_weight_img);
        mImgChart = (ImageButton) findViewById(R.id.id_tab_chart_img);
//        mImgSettings = (ImageButton) findViewById(R.id.id_tab_settings_img);

        mTvMy=(TextView)findViewById(R.id.tv_tab_my);
        mTvWeight=(TextView)findViewById(R.id.tv_tab_weight);
        mTvChart=(TextView)findViewById(R.id.tv_tab_chart);
        mTvInfo=(TextView)findViewById(R.id.tv_tab_info);

        popAccount=new PopAccount(mContext,appContext,accountEntities);
        popAccount.setOnItemClickListener(popAccountItemClickListener);

//        accountAdapter=new AccountAdapter(mContext,accountEntities,appContext,R.layout.main_account_item);
//        accountListView=(ListView)findViewById(R.id.accountList);
//        accountListView.setAdapter(accountAdapter);
//        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                accountListView.setVisibility(View.GONE);
//            }
//        });

    }

    public void setSelect(int i){
        resetImgs();
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
                mTvMy.setTextColor(getResources().getColor(R.color.btn_color_selected));
                mImgMy.setImageResource(R.drawable.img_tab_my_sel);
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
                mTvWeight.setTextColor(getResources().getColor(R.color.btn_color_selected));
                mImgWeight.setImageResource(R.drawable.img_tab_weight_sel);
                break;
            case 2:
                if (mTab03 == null)
                {
                    mTab03 = new ChartLFragment();
                    ((ChartLFragment)mTab03).setLocalAccountEntity(getAccountEntity());
                    transaction.add(R.id.content_frame, mTab03);
                } else
                {
                    transaction.attach(mTab03);
                }
                mImgChart.setImageResource(R.drawable.img_tab_chart_sel);
                mTvChart.setTextColor(getResources().getColor(R.color.btn_color_selected));
                break;
            case 3:
                if (mTab04 == null)
                {
                    mTab04 = new InfoFragment();
                    transaction.add(R.id.content_frame, mTab04);
                } else
                {
                    transaction.attach(mTab04);
                }
                mTvInfo.setTextColor(getResources().getColor(R.color.btn_color_selected));
                mImgInfo.setImageResource(R.drawable.img_tab_info_sel);
                break;
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
        if (mTab04 != null)
        {
            transaction.detach(mTab04);
        }
//        if (mTab05 != null)
//        {
//            transaction.hide(mTab05);
//        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.id_tab_my:
                setSelect(0);
                break;
            case R.id.id_tab_weight:
                setSelect(1);
                break;
            case R.id.id_tab_chart:
                setSelect(2);
                break;
            case R.id.id_tab_info:
                setSelect(3);
                break;
//            case R.id.id_tab_settings:
//                setSelect(4);
//                break;
            case R.id.btnHeadEdit:{
                SherlockFragment sherlockFragment=(SherlockFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (sherlockFragment instanceof ContentFragment) {
                    ((ContentFragment)sherlockFragment).openShare();

                }else if(sherlockFragment instanceof MyFragment){
                    Intent intent=new Intent(TabActivity.this,HomeActivity.class);
                    appContext.setProperty(AppConfig.CONF_LOGIN_FLAG,"0");
                    startActivity(intent);
                    finish();
                }
                break;
            }
            case R.id.ibHeadBack:{
                popAccount.showAsDropDown(v);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 切换图片至暗色
     */
    private void resetImgs()
    {
        mImgWeight.setImageResource(R.drawable.img_tab_weight);
        mImgChart.setImageResource(R.drawable.img_tab_chart);
        mImgInfo.setImageResource(R.drawable.img_tab_info);
        mImgMy.setImageResource(R.drawable.img_tab_my);

        mTvMy.setTextColor(getResources().getColor(R.color.btn_color));
        mTvWeight.setTextColor(getResources().getColor(R.color.btn_color));
        mTvChart.setTextColor(getResources().getColor(R.color.btn_color));
        mTvInfo.setTextColor(getResources().getColor(R.color.btn_color));

    }


}
