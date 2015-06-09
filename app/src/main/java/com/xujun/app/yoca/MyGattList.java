package com.xujun.app.yoca;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xujunwu on 15/2/6.
 */
public class MyGattList {
    private final static String TAG = "MyGattList";
    private static List<BluetoothGatt> glist = new ArrayList<BluetoothGatt>();
    private static HashMap<String, Boolean> lostMap = new HashMap<String, Boolean>();

    public static int  size() {
        if (glist == null) {
            Log.e(TAG, "findGatt: glist is null");
            return 0;
        }
        return glist.size();
    }
    public static boolean deleteAll() {
        if (glist == null) {
            Log.e(TAG, "findGatt: glist is null");
            return false;
        }
        for(BluetoothGatt gatt: glist)
        {
            gatt.close();
        }
        glist.clear();
        lostMap.clear();
        return true;
    }

    public static boolean deleteGatt(String address) {
        if ((glist == null) || address == null) {
            Log.e(TAG, "deleteGatt: glist is null or address is empty.");
            return false;
        }

        if (glist.size() == 0) {
            Log.e(TAG, "deleteGatt: glist is empty.");
            return false;
        }
        lostMap.remove(address);

        try {
            for (BluetoothGatt gatt: glist) {
                if (gatt.getDevice().getAddress().equals(address)) {

                    Log.d(TAG, "delete Gatt List device->"+ gatt.getDevice().getAddress());
                    gatt.close();
                    glist.remove(gatt);
                    return true;
                }
            }
            return false;
        } catch (Exception localException) {
            Log.e(TAG, "deleteGatt exception.");
            // LoggerManager.getExceptionMessage(localException);
        }
        return false;
    }

    public static void echoAll() {
        for (BluetoothGatt gatt:glist) {

            Log.i(TAG, "echoAll->" + gatt.getDevice().getAddress());
        }
    }

    public static BluetoothGatt getGatt(String address) {
        if ((glist == null) || (address == null)) {
            Log.e(TAG, "findGatt: glist is null OR address is empty.");
            return null;
        }
        if (glist.size() == 0) {
            Log.e(TAG, "findGatt: glist is empty.");
            return null;
        }
        for(BluetoothGatt gatt: glist)
        {
            if(gatt.getDevice().getAddress().equals(address))
            {
                return gatt;
            }
        }
        return null;

    }
    public static Boolean isLost(final String address) {

        return lostMap.get(address);

    }
    public static boolean setLost(final String address, Boolean lost) {

        return lostMap.put(address, lost);

    }
    public static List<BluetoothGatt> getGattList() {
        if (glist == null) {
            Log.e(TAG, "getGattList: glist is null");
            return null;
        }
        return glist;
    }

    public static boolean insertGatt(BluetoothGatt newGatt) {
        if (glist == null) {
            Log.e(TAG, "insertGatt: glist is null");
            return false;
        }
        lostMap.put(newGatt.getDevice().getAddress(), false);

        for(BluetoothGatt gatt:glist)
        {
            if(gatt.getDevice().getAddress().equals(
                    newGatt.getDevice().getAddress()))
            {
                //gatt.disconnect();
                //gatt.close();
                glist.remove(gatt);
                glist.add(newGatt);
                return true;
            }
        }
        glist.add(newGatt);
        return true;
    }
}
