package com.xujun.app.yoca;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by xujunwu on 15/1/14.
 */
public class BluetoothLeService extends Service{

    private final static String TAG=BluetoothLeService.class.getSimpleName();

    public final static String ACTION_GATT_CONNECTED="com.xujun.yoca.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED="com.xujun.yoca.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED="com.xujun.yoca.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_READ="com.xujun.yoca.ACTION_DATA_READ";
    public final static String ACTION_DATA_NOTIFY="com.xujun.yoca.ACTION_DATA_NOTIFY";
    public final static String ACTION_DATA_WRITE="com.xujun.yoca.ACTION_DATA_WRITE";

    public final static String EXTRA_DATA="com.xujun.yoca.EXTRA_DATA";
    public final static String EXTRA_UUID="com.xujun.yoca.EXTRA_UUID";
    public final static String EXTRA_STATUS="com.xujun.yoca.EXTRA_STATUS";
    public final static String EXTRA_ADDRESS="com.xujun.yoca.EXTRA_ADDRESS";


    public final static String SERVICE_UUID="0000fff0-0000-1000-8000-00805f9b34fb";
    public final static String SERVICE_UUID1="0000fff1-0000-1000-8000-00805f9b34fb";
    public final static String SERVICE_UUID2="0000fff2-0000-1000-8000-00805f9b34fb";

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private BluetoothManager   mBluetoothManager=null;
    private BluetoothAdapter   mBluetoothAdapter=null;
    private BluetoothGatt      mBluetoothGatt=null;

    private static BluetoothLeService       mThis=null;
    private volatile boolean mBusy=false;
    private String mBluetoothDeviceAddress;

    private  Handler handler;
    private static  boolean stopRead;
    @Override
    public void onCreate(){
        super.onCreate();
        handler=new Handler();
        stopRead=false;
        handler.postDelayed(reconRunnable,3000);

    }

    public IBinder onBind(Intent intent){
        return mBinder;
    }

    public boolean onUnbind(Intent intent){
        close();
        return super.onUnbind(intent);
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        Log.i(TAG,"Received start id "+startId+" : "+intent);
        return START_STICKY;
    }
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy () called");
        if (mBluetoothGatt!=null){
            mBluetoothGatt.close();
            mBluetoothGatt=null;
        }

        stopRead=true;
    }

    public class LocalBinder extends Binder{
        BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder=new LocalBinder();


    private final BluetoothGattCallback mGattCallbacks=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mBluetoothGatt==null){
                Log.e(TAG,"mBluetoothGatt no Created");
                return;
            }
            BluetoothDevice device=gatt.getDevice();

            String address=device.getAddress();
            if (device.getUuids()!=null) {
                Log.e(TAG, "uuid=" + device.getUuids());
            }
            Log.e(TAG,"name is null"+device.getName()+".........");
            Log.e(TAG,"onConnectionStateChange( "+address+" ) "+newState+"  status :"+status+"  "+device.getName());
            try{
                switch (newState){
                    case BluetoothProfile.STATE_CONNECTED:{
                        MyGattList.insertGatt(gatt);
                        broadcaseUpdate(ACTION_GATT_CONNECTED, address, status);
                        break;
                    }
                    case BluetoothProfile.STATE_DISCONNECTED:{
                        broadcaseUpdate(ACTION_GATT_DISCONNECTED,address,status);
                        break;
                    }
                    default:
                        Log.e(TAG,"New State no processed: "+newState);
                        break;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothDevice device=gatt.getDevice();
            broadcaseUpdate(ACTION_GATT_SERVICES_DISCOVERED,device.getAddress(),status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG,"onCharacteristicRead  "+characteristic.getUuid()+"  "+characteristic.getValue());
            broadcaseUpdate(ACTION_DATA_READ,characteristic,status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG,"onCharacteristicWrite "+characteristic.getUuid()+"  "+characteristic.getValue());
            broadcaseUpdate(ACTION_DATA_WRITE,characteristic,status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG,"onCharacteristicChanged "+characteristic.getUuid()+"  "+characteristic.getValue());
            broadcaseUpdate(ACTION_DATA_NOTIFY,characteristic,BluetoothGatt.GATT_SUCCESS);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mBusy=false;
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mBusy=false;
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    private void broadcaseUpdate(final String action,final String address,final int status){
        final Intent intent=new Intent(action);
        intent.putExtra(EXTRA_ADDRESS,address);
        intent.putExtra(EXTRA_STATUS,status);
        sendBroadcast(intent);
        mBusy=false;
    }

    private void broadcaseUpdate(final String action,final BluetoothGattCharacteristic characteristic,final int status){
        final Intent intent=new Intent(action);
        intent.putExtra(EXTRA_UUID,characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA,characteristic.getValue());
        intent.putExtra(EXTRA_STATUS,status);
        sendBroadcast(intent);
        mBusy=false;
    }

    private boolean checkGatt(){
        if (mBluetoothAdapter==null){
            Log.w(TAG,"BluetoothAdapter not initialized");
            return false;
        }
        if (mBluetoothGatt==null){
            Log.w(TAG,"BluetoothGatt not initialized");
            return false;
        }
        if (mBusy){
            Log.w(TAG,"LeService busy.");
            return false;
        }
        return true;
    }

    public boolean initialize(){
        Log.e(TAG,"initialize");
        mThis=this;
        if (mBluetoothManager==null) {
            mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG,"Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if (mBluetoothAdapter==null){
            Log.e(TAG,"Unable to obtain a BluetoothAdapter");
            return false;
        }
        return true;
    }

    public boolean connect(final String address){
        if (mBluetoothAdapter==null||address==null){
            Log.e(TAG,"BluetoothAdater not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(address);
        int connectionState=mBluetoothManager.getConnectionState(device,BluetoothProfile.GATT);
        if (connectionState==BluetoothProfile.STATE_DISCONNECTED){
            if (mBluetoothDeviceAddress!=null&&address.equals(mBluetoothDeviceAddress)&&mBluetoothGatt!=null){
                Log.d(TAG,"Re-use GATT connection.");
                if (mBluetoothGatt.connect()){
                    return true;
                }else {
                    Log.w(TAG,"Gatt Re-Connrection failed");
                    return false;
                }
            }

            if (device==null){
                Log.w(TAG,"Device not found Unabled to connect");
                return false;
            }

            Log.d(TAG,"Create a new GATT connection.");
            mBluetoothGatt=device.connectGatt(this,false,mGattCallbacks);
            mBluetoothDeviceAddress=address;
        }else{
            Log.w(TAG,"Attempt to connect in state :"+connectionState);
            return false;
        }
        return true;
    }

    public void disconnect(final String address){
        if (mBluetoothAdapter==null){
            Log.w(TAG,"disconnect :BluetoothAdapter not initialized.");
            return;
        }
        if (address!=null) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            int connectionState = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if (mBluetoothGatt != null) {
                Log.i(TAG, "disconnect");
                if (connectionState != BluetoothProfile.STATE_DISCONNECTED) {
                    mBluetoothGatt.disconnect();
                } else {
                    Log.w(TAG, "Attempt to disconnect in state: " + connectionState);
                }
            }
        }
    }

    Runnable reconRunnable=new Runnable() {
        @Override
        public void run() {

            if (stopRead){
                return;
            }
            handler.postDelayed(this,3000);
        }
    };



    public void close(){
        if (mBluetoothGatt!=null){
            Log.i(TAG,"close");
            mBluetoothGatt.close();
            mBluetoothGatt=null;
        }
    }

    public int numConnectedDevices(){
        int n=0;
        if (mBluetoothGatt!=null){
            List<BluetoothDevice > deviceList;
            deviceList=mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            n=deviceList.size();
        }
        return n;
    }

    public void disconnectAll(){
        if (mBluetoothAdapter==null){
            return;
        }

    }

    public boolean discoverService(String devAddr){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
           BluetoothGatt gatt=MyGattList.getGatt(devAddr);
           if (gatt!=null){
                return  gatt.discoverServices();
           }
        }
        Log.e(TAG,"Disconnected can't discover services");
        return false;
    }

    public List<BluetoothGattService> getSupportedGattServices(String devAddr){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            BluetoothGatt gatt=MyGattList.getGatt(devAddr);
            if (gatt!=null){
                return gatt.getServices();
            }else{
                return null;
            }
        }
        return null;
    }

    public BluetoothGattService getService(String devAddr,String uuid){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            BluetoothGatt gatt=MyGattList.getGatt(devAddr);
            if (gatt!=null){
                return gatt.getService(UUID.fromString(uuid));
            }else{
                Log.w(TAG,"gatt ==null getService failed.");
                return null;
            }
        }else{
            Log.w(TAG,"disconnected getService failed");
        }
        return null;
    }


    public int getConnectionState(final String address){
        final BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(address);
        if (device!=null){
            return mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        }else
        {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
    }

    public boolean setCharacteristicNotification(String devAddr,BluetoothGattCharacteristic characteristic,boolean enable){
        if (mBluetoothAdapter==null){
            return  false;
        }
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            BluetoothGatt gatt=MyGattList.getGatt(devAddr);
            if (gatt!=null){
                if (!gatt.setCharacteristicNotification(characteristic,enable)){
                    Log.w(TAG,"setCharacteristicNotification failed.");
                    return false;
                }
                BluetoothGattDescriptor clientConfig=characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                if (clientConfig == null)
                    return false;
                if (enable) {
                    Log.i(TAG, "enable notification");
                    clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    Log.i(TAG, "disable notification");
                    clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                return gatt.writeDescriptor(clientConfig);
            }else{
                Log.e(TAG,"gatt ==null set character failed.");
                return false;
            }
        }
        Log.e(TAG,"connnect  ==null set character failed.");
        return false;
    }

    public static BluetoothGatt getBtGatt(){
        return mThis.mBluetoothGatt;
    }

    public static BluetoothManager getBtManager(){
        return mThis.mBluetoothManager;
    }

    public static BluetoothLeService getInstance(){
        return mThis;
    }

    public boolean waitIdle(int i){
        i/=10;
        while (--i>0){
            if (mBusy){
                try {
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }else {
                break;
            }
        }
        return i>0;
    }

    public boolean readCharacteristic(String address,BluetoothGattCharacteristic characteristic)
    {
        if (mBluetoothAdapter==null){
            return false;
        }
        if (getConnectionState(address)==BluetoothProfile.STATE_CONNECTED){
            BluetoothGatt gatt=MyGattList.getGatt(address);
            if (gatt!=null){
                return gatt.readCharacteristic(characteristic);
            }else{
                Log.e(TAG,"gatt=null read Characteristic failed.");
                return false;
            }
        }else{
            Log.e(TAG,"disconnected read Characteristic failed.");
        }
        return false;
    }

    public boolean writeCharacteristic(String address,BluetoothGattCharacteristic characteristic){
        if (getConnectionState(address)==BluetoothProfile.STATE_CONNECTED) {
            BluetoothGatt gatt=MyGattList.getGatt(address);
            if (gatt!=null){
                return gatt.writeCharacteristic(characteristic);
            }else{
                Log.e(TAG,"gatt =null writeCharacteristic failed.");
            }
        }else{
            Log.e(TAG,"disconnected writeCharacteristic failed.");
        }
        return false;
    }
}
