// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;

import com.ideabus.mylibrary.code.base.ConnectBase;
import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.bean.DeviceType;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.communicate_utils.ManagerGetter;
import com.ideabus.mylibrary.code.communicate_utils.CommunicateGetter;
import com.ideabus.mylibrary.code.callback.BluetoothSearchCallback;
import com.ideabus.mylibrary.code.callback.CommunicateCallback;
import com.ideabus.mylibrary.code.callback.ConnectCallback;
import com.ideabus.mylibrary.code.callback.DataStorageInfoCallback;
import com.ideabus.mylibrary.code.callback.DeleteDataCallback;
import com.ideabus.mylibrary.code.callback.GetStorageModeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeSpO2Callback;
import com.ideabus.mylibrary.code.callback.SetCalorieCallback;
import com.ideabus.mylibrary.code.callback.SetHeightCallback;
import com.ideabus.mylibrary.code.callback.SetStepsTimeCallback;
import com.ideabus.mylibrary.code.callback.SetWeightCallback;
import com.ideabus.mylibrary.code.callback.StorageModeCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class ContecSdk
{
    private final String TAG = this.getClass().getName();
    
    private final Context ctx;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSearchCallback sdkScanCallback;
    private boolean isSearching;
    private Timer timer;
    public static int connectOverTime;
    private CommunicateBase communicateBase;
    private ConnectBase connectBase;
    public static boolean isDelete;
    private SystemParameter.DataType dataType;
    private static String rangeID;
    private Map<String, DeviceType> supportedDevices;
    private final BluetoothAdapter.LeScanCallback scanCallback;
    
    public ContecSdk(final Context ctx) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isSearching = false;
        scanCallback = (bluetoothDevice, rssi, record) -> {
            if (bluetoothDevice.getName() == null) {
                return;
            }
            if (sdkScanCallback != null) {
                sdkScanCallback.onDeviceFound(bluetoothDevice, rssi, record);
            }
        };
        this.ctx = ctx;
        this.initSupportDevices();
    }
    
    public ContecSdk(final Context ctx, final String rangeID) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isSearching = false;
        scanCallback = (bluetoothDevice, rssi, record) -> {
            if (bluetoothDevice.getName() == null) {
                return;
            }
            if (sdkScanCallback != null) {
                sdkScanCallback.onDeviceFound(bluetoothDevice, rssi, record);
            }
        };
        this.ctx = ctx;
        ContecSdk.rangeID = rangeID;
        this.initSupportDevices();
    }
    
    private void initSupportDevices() {
        (supportedDevices = new HashMap<>()).put("SpO201", DeviceType.CMS50E);
        supportedDevices.put("SpO202", DeviceType.CMS50F);
        supportedDevices.put("SpO206", DeviceType.CMS50I);
        supportedDevices.put("SpO208", DeviceType.CMS50D);
        supportedDevices.put("SpO209", DeviceType.CMS50K);
        supportedDevices.put("SpO210", DeviceType.CMS50K);
    }
    
    public static boolean isRangeIDEmpty() {
        return ContecSdk.rangeID.isEmpty();
    }
    
    public static String getRangID() {
        return ContecSdk.rangeID;
    }
    
    public void init(final boolean isDelete) {
        ContecSdk.isDelete = isDelete;
    }
    
    public void stopBluetoothSearch() {
        if (bluetoothAdapter == null) {
            if (sdkScanCallback != null) {
                sdkScanCallback.onSearchError(SdkConstants.ERRORCODE_UNCONNECT);
            }
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            if (sdkScanCallback != null) {
                sdkScanCallback.onSearchError(SdkConstants.ERRORCODE_FAIL);
            }
            return;
        }
        bluetoothAdapter.stopLeScan(scanCallback);
        isSearching = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (null != sdkScanCallback) {
            sdkScanCallback.onSearchComplete();
        }
    }
    
    public void startBluetoothSearch(final BluetoothSearchCallback sdkScanCallback, final int searchTimeout) {
        this.sdkScanCallback = sdkScanCallback;
        if (bluetoothAdapter == null) {
            sdkScanCallback.onSearchError(SdkConstants.ERRORCODE_UNCONNECT);
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            sdkScanCallback.onSearchError(SdkConstants.ERRORCODE_FAIL);
            return;
        }
        if (isSearching) {
            return;
        }
        Log.e(TAG, "ble scan started!");
        bluetoothAdapter.startLeScan(scanCallback);
        isSearching = true;
        if (searchTimeout > 0) {
            (timer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    ContecSdk.this.stopBluetoothSearch();
                }
            }, searchTimeout);
        }
    }
    
    public void setConnectTimeout(final int connectOverTime) {
        if (connectOverTime > 0) {
            ContecSdk.connectOverTime = connectOverTime;
        }
        else {
            ContecSdk.connectOverTime = 20000;
        }
    }
    
    public void connect(final BluetoothDevice bluetoothDevice, final ConnectCallback sdkConnectCallback) {
        if (null != this.connectBase) {
            if (ConnectBase.curOperationSucceed) {
                if (null != this.communicateBase && this.communicateBase.isConnected()) {
                    if (null != sdkConnectCallback) {
                        sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_CONNECTED);
                    }
                }
                else if (null != sdkConnectCallback) {
                    sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_CONNECTING);
                }
                return;
            }
        }
        if (null == bluetoothDevice) {
            Log.e(TAG, "null bluetooth device provided");
            return;
        }
        if (sdkConnectCallback == null) {
            Log.e(TAG, "null callback was provided");
            return;
        }
        this.communicateBase = CommunicateGetter.getCommunicateByType(this.deviceTypeFromName(bluetoothDevice.getName()));
        if (null == this.communicateBase) {
            Log.e(TAG, "failed to init deviceCommunicate");
            sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_UNSUPPORT_DEVICETYPE);
            return;
        }
        this.connectBase = ManagerGetter.manager(ctx, bluetoothDevice, this.communicateBase);
        if (null == this.connectBase) {
            Log.e(TAG, "failed to init K");
            sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_UNSUPPORT_BLUETOOTHTYPE);
            return;
        }
        Log.e(TAG, "connection successfully initialized!");
        this.communicateBase.setConnectBase(this.connectBase);
        this.communicateBase.startRealtime(sdkConnectCallback);
    }
    
    public void connect(final String macAddress, final ConnectCallback sdkConnectCallback) {
        if (null != this.connectBase) {
            if (ConnectBase.curOperationSucceed) {
                if (null != this.communicateBase && this.communicateBase.isConnected()) {
                    if (null != sdkConnectCallback) {
                        sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_CONNECTED);
                    }
                }
                else if (null != sdkConnectCallback) {
                    sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_CONNECTING);
                }
                return;
            }
        }
        if (TextUtils.isEmpty((CharSequence)macAddress)) {
            Log.e(TAG, "empty bluetooth address");
            return;
        }
        if (sdkConnectCallback == null) {
            Log.e(TAG, "null callback was provided");
            return;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "bluetoothAdapter isn't ready");
            return;
        }
        final BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        if (remoteDevice == null) {
            Log.e(TAG, "failed to get remote bluetooth device");
            return;
        }
        communicateBase = CommunicateGetter.getCommunicateByType(this.deviceTypeFromName(remoteDevice.getName()));
        if (null == communicateBase) {
            Log.e(TAG, "failed to init deviceCommunicate");
            sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_UNSUPPORT_DEVICETYPE);
            return;
        }
        this.connectBase = ManagerGetter.manager(ctx, remoteDevice, communicateBase);
        if (null == this.connectBase) {
            Log.e(TAG, "failed to init BleManager");
            sdkConnectCallback.onConnectStatus(SdkConstants.CONNECT_UNSUPPORT_BLUETOOTHTYPE);
            return;
        }
        Log.e(TAG, "connection successfully initialized!");
        communicateBase.setConnectBase(this.connectBase);
        communicateBase.startRealtime(sdkConnectCallback);
    }
    
    public void disconnect() {
        if (communicateBase != null) {
            communicateBase.disconnect();
            communicateBase = null;
        }
    }
    
    public void open(final UsbManager usbManager, final UsbDevice usbDevice, final ConnectCallback sdkConnectCallback) {
        if (this.connectBase != null) {
            if (ConnectBase.curOperationSucceed) {
                Log.e(TAG, "already opened");
                sdkConnectCallback.onOpenStatus(SdkConstants.OPENED);
                return;
            }
        }
        if (usbManager == null) {
            sdkConnectCallback.onOpenStatus(1);
            return;
        }
        if (usbDevice == null) {
            sdkConnectCallback.onOpenStatus(2);
            return;
        }
        if (sdkConnectCallback == null) {
            return;
        }
        communicateBase = CommunicateGetter.getCommunicateByType(usbDevice);
        if (communicateBase == null) {
            Log.i(TAG, "failed to initialize deviceCommunicate");
            sdkConnectCallback.onOpenStatus(4);
            return;
        }
        this.connectBase = ManagerGetter.manager(usbManager, usbDevice, communicateBase);
        if (this.connectBase == null) {
            Log.i(TAG, "failed to initialize UsbManager");
            sdkConnectCallback.onOpenStatus(4);
            return;
        }
        communicateBase.setConnectBase(this.connectBase);
        communicateBase.startRealtime(sdkConnectCallback);
    }
    
    public void communicate(final CommunicateCallback communicateCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setDataType(dataType);
            communicateBase.startCommunicate(communicateCallback);
        }
        else if (null != communicateCallback) {
            communicateCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void startRealtime(final RealtimeCallback realtimeCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.startRealtime(realtimeCallback);
        }
        else if (null != realtimeCallback) {
            realtimeCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void stopRealtime() {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.startRealtime();
        }
    }
    
    public void setDeviceStorageMode(final SystemParameter.StorageMode storageMode, final StorageModeCallback storageModeCallback) {
        if (null != storageModeCallback) {
            if (null != communicateBase && communicateBase.isConnected()) {
                communicateBase.setStorageMode(storageMode, storageModeCallback);
            }
            else {
                storageModeCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
            }
        }
    }
    
    public void setDataType(final SystemParameter.DataType dataType) {
        this.dataType = dataType;
    }
    
    public void getDeviceStorageMode(final GetStorageModeCallback getStorageModeCallback) {
        if (null != getStorageModeCallback) {
            if (null != communicateBase && communicateBase.isConnected()) {
                communicateBase.getStorageMode(getStorageModeCallback);
            }
            else {
                getStorageModeCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
            }
        }
    }
    
    public void deleteData(final DeleteDataCallback deleteDataCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.deleteData(deleteDataCallback);
        }
        else if (null != deleteDataCallback) {
            deleteDataCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void setDataStorageInfo(final SystemParameter.DataStorageInfo dataStorageInfo, final DataStorageInfoCallback dataStorageInfoCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setDataStorageInfo(dataStorageInfo, dataStorageInfoCallback);
        }
        else if (null != dataStorageInfoCallback) {
            dataStorageInfoCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public boolean defineBTPrefix(final DeviceType deviceType, final String[] array) {
        if (array == null || deviceType == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (TextUtils.isEmpty((CharSequence)array[i]) || supportedDevices.containsKey(array[i])) {
                return false;
            }
            supportedDevices.put(array[i], deviceType);
        }
        return true;
    }
    
    public boolean defineBTPrefix(final DeviceType deviceType, final String rangeID) {
        if (deviceType == null || TextUtils.isEmpty((CharSequence)rangeID)) {
            return false;
        }
        if (supportedDevices.containsKey(rangeID)) {
            return false;
        }
        supportedDevices.put(rangeID, deviceType);
        return true;
    }
    
    private DeviceType deviceTypeFromName(final String name) {
        if (TextUtils.isEmpty((CharSequence)name)) {
            return null;
        }
        final TreeMap<String, DeviceType> treeMap = new TreeMap<>((s12, s1) -> {
            if (s12.length() > s1.length()) {
                return -1;
            }
            if (s12.length() < s1.length()) {
                return 1;
            }
            return s12.compareTo(s1);
        });
        treeMap.putAll(supportedDevices);
        DeviceType deviceType = null;
        for (final String prefix : treeMap.keySet()) {
            if (name.startsWith(prefix)) {
                deviceType = treeMap.get(prefix);
                break;
            }
        }
        return deviceType;
    }
    
    public void startRealtimeSpO2(final RealtimeSpO2Callback realtimeSpO2Callback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.startRealtimeSpo2(realtimeSpO2Callback);
        }
        else if (null != realtimeSpO2Callback) {
            realtimeSpO2Callback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void setStepsTime(final int n, final int n2, final SetStepsTimeCallback setStepsTimeCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setStepsTime(n, n2, setStepsTimeCallback);
        }
        else if (null != setStepsTimeCallback) {
            setStepsTimeCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void setWeight(final int weight, final SetWeightCallback setWeightCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setWeight(weight, setWeightCallback);
        }
        else if (null != setWeightCallback) {
            setWeightCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void setHeight(final int height, final SetHeightCallback setHeightCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setHeight(height, setHeightCallback);
        }
        else if (null != setHeightCallback) {
            setHeightCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    public void setCalorie(final int n, final int n2, final SystemParameter.StepsSensitivity stepsSensitivity, final SetCalorieCallback setCalorieCallback) {
        if (null != communicateBase && communicateBase.isConnected()) {
            communicateBase.setCalorie(n, n2, stepsSensitivity, setCalorieCallback);
        }
        else if (null != setCalorieCallback) {
            setCalorieCallback.onFail(SdkConstants.ERRORCODE_UNCONNECT);
        }
    }
    
    static {
        ContecSdk.connectOverTime = 20000;
        ContecSdk.rangeID = "";
    }
}
