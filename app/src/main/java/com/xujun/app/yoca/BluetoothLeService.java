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

import com.xujun.util.StringUtil;

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


    private static BluetoothLeService       mThis=null;
    private volatile boolean mBusy=false;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt   mBluetoothGatt;


    private  Handler handler;
    private static  boolean stopRead;
    private Context     mContext;

    @Override
    public void onCreate(){
        super.onCreate();
        handler=new Handler();
        stopRead=false;
        mContext=this;
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
        return super.onStartCommand(intent,flags,startId);
    }
    public void onDestroy(){
        Log.i(TAG,"onDestroy () called");
        stopRead=true;
        super.onDestroy();
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
            BluetoothDevice device=gatt.getDevice();
            String devname=device.getName();
            String address=device.getAddress();
            if (device.getUuids()!=null) {
                Log.i(TAG, "uuid=" + device.getUuids());
            }
            Log.i(TAG,"name is "+device.getName()+".........");
            Log.i(TAG,"onConnectionStateChange( "+address+" ) "+newState+"  status :"+status+"  "+device.getName());
            try{
                switch (newState){
                    case BluetoothProfile.STATE_CONNECTED:{
                        broadcaseUpdate(ACTION_GATT_CONNECTED, address, status);
                        break;
                    }
                    case BluetoothProfile.STATE_DISCONNECTED:{

                        broadcaseUpdate(ACTION_GATT_DISCONNECTED, address, status);
                        break;
                    }
                    default:
                        Log.i(TAG,"New State no processed: "+newState);
                        break;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status==BluetoothGatt.GATT_SUCCESS) {
                BluetoothDevice device = gatt.getDevice();
                broadcaseUpdate(ACTION_GATT_SERVICES_DISCOVERED, device.getAddress(), status);
            }else{
                Log.w(TAG,"onServicesDiscovered received:"+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead  " + characteristic.getUuid() + "  " + characteristic.getValue() + "  " + status);
            if (status==BluetoothGatt.GATT_SUCCESS) {
                broadcaseUpdate(ACTION_DATA_READ, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG,"onCharacteristicWrite "+characteristic.getUuid()+"  "+characteristic.getValue()+"  "+status);
            broadcaseUpdate(ACTION_DATA_WRITE,characteristic,status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG,"onCharacteristicChanged "+characteristic.getUuid()+"  "+characteristic.getValue());
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
            Log.e(TAG,""+gatt.getDevice()+"  "+rssi+"   "+status);
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    private void broadcaseUpdate(final String action,final String address,final int status){
        final Intent intent=new Intent(action);
        intent.putExtra(EXTRA_ADDRESS,address);
        intent.putExtra(EXTRA_STATUS, status);
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

    public boolean initialize(){
        Log.e(TAG, "initialize");
        mThis=this;
        if (mBluetoothManager==null) {
            mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.i(TAG,"Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if (mBluetoothAdapter==null){
            Log.i(TAG, "Unable to obtain a BluetoothAdapter");
            return false;
        }
        return true;
    }

    public boolean connect(final String address){
        if (mBluetoothAdapter==null||address==null){
            Log.i(TAG, "BluetoothAdater not initialized or unspecified address.");
            return false;
        }
        if (mBluetoothDeviceAddress!=null&&address.equals(mBluetoothDeviceAddress)&&mBluetoothGatt!=null){
            Log.i(TAG,"BluetoothGatt close");
            mBluetoothGatt.close();
        }
        final BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(address);
        if (device==null){
            Log.w(TAG,"Device not found Unabled to connect");
            return false;
        }
        Log.d(TAG, "Create a new GATT connection.");
        mBluetoothGatt=device.connectGatt(this,false,mGattCallbacks);
        mBluetoothDeviceAddress=address;
        return true;
    }

    public void disconnect(){
        if (mBluetoothAdapter==null||mBluetoothGatt==null){
            Log.w(TAG, "disconnect :BluetoothAdapter not initialized.");
            return;
        }
        Log.i(TAG,"disconnect()....");
        mBluetoothGatt.disconnect();
    }


    public void close(){
        if (mBluetoothGatt==null){
            return;
        }
        Log.i(TAG,"close()....");
        mBluetoothGatt.close();
        mBluetoothGatt=null;
    }

    public boolean discoverService(String devAddr){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
           if (mBluetoothGatt!=null){
                return  mBluetoothGatt.discoverServices();
           }
        }
        Log.i(TAG,"Disconnected can't discover services");
        return false;
    }

    public List<BluetoothGattService> getSupportedGattServices(String devAddr){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            if (mBluetoothGatt!=null){
                return mBluetoothGatt.getServices();
            }else{
                return null;
            }
        }
        return null;
    }

    public List<BluetoothGattService> getSuppertedGattServices(){
        if (mBluetoothGatt==null){
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    public BluetoothGattService getService(String devAddr,String uuid){
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            if (mBluetoothGatt!=null){
                return mBluetoothGatt.getService(UUID.fromString(uuid));
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
        if (mBluetoothAdapter==null||mBluetoothGatt==null){
            Log.w(TAG,"mBluetoothAdapter not initialized.");
            return  false;
        }
        if (getConnectionState(devAddr)==BluetoothProfile.STATE_CONNECTED){
            if (mBluetoothGatt!=null){
                if (!mBluetoothGatt.setCharacteristicNotification(characteristic,enable)){
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
                return mBluetoothGatt.writeDescriptor(clientConfig);
            }else{
                Log.i(TAG,"gatt ==null set character failed.");
                return false;
            }
        }
        Log.i(TAG,"connnect  ==null set character failed.");
        return false;
    }

    public static BluetoothManager getBtManager(){
        return mThis.mBluetoothManager;
    }

    public static BluetoothLeService getInstance(){
        return mThis;
    }

    public boolean readCharacteristic(String address,BluetoothGattCharacteristic characteristic)
    {
        if (mBluetoothAdapter==null||mBluetoothGatt==null){
            Log.w(TAG,"BluetoothAdapter not initialized..");
            return false;
        }
        if (getConnectionState(address)==BluetoothProfile.STATE_CONNECTED){
            if (mBluetoothGatt!=null){
                Log.i(TAG,"gatt.readCharacteristic(characteristic) .....");
                return mBluetoothGatt.readCharacteristic(characteristic);
            }else{
                Log.i(TAG,"gatt=null read Characteristic failed.");
                return false;
            }
        }else{
            Log.i(TAG,"disconnected read Characteristic failed.");
        }
        return false;
    }

    public boolean writeCharacteristic(String address,BluetoothGattCharacteristic characteristic){
        if (mBluetoothAdapter==null||mBluetoothGatt==null){
            Log.w(TAG,"BluetoothAdapter not initialzed");
            return false;
        }
        if (getConnectionState(address)==BluetoothProfile.STATE_CONNECTED) {
            if (mBluetoothGatt!=null){
                Log.i(TAG, "gatt.writeCharacteristic(characteristic) .....");
                return mBluetoothGatt.writeCharacteristic(characteristic);
            }else{
                Log.i(TAG,"gatt =null writeCharacteristic failed.");
            }
        }else{
            Log.i(TAG,"disconnected writeCharacteristic failed.");
        }
        return false;
    }
}
