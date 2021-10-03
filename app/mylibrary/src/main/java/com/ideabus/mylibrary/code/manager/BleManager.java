// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.base.ConnectBase;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.communicate_utils.Uuids;
import com.ideabus.mylibrary.code.callback.ConnectCallback;
import com.ideabus.mylibrary.code.connect.ContecSdk;
import com.ideabus.mylibrary.code.tools.Utils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class BleManager extends ConnectBase
{
    private final String TAG = this.getClass().getName();
    private final Context ctx;
    private final BluetoothDevice device;
    private BluetoothGatt gatt;
    private ConnectCallback connectCallback;
    private Timer connectionTimer;
    private int sdkConnectStatus;
    private boolean disconnected;
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private final BluetoothGattCallback gattCallback;
    
    public BleManager(final Context ctx, final BluetoothDevice device, final CommunicateBase communicateBase) {
        this.sdkConnectStatus = SdkConstants.CONNECT_DISCONNECT_EXCEPTION;
        this.disconnected = true;
        this.writeCharacteristic = null;

        this.gattCallback = new BluetoothGattCallback() {
            public void onConnectionStateChange(final BluetoothGatt bluetoothGatt, final int status, final int newState) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    Log.e(TAG, "gatt connected");

                    if (connectCallback != null) {
                        connectCallback.onConnectStatus(SdkConstants.CONNECT_CONNECTING);
                    }

                    sleep(800);

                    Log.e(TAG, "gatt started discovering services");
                    if (gatt != null) {
                        gatt.discoverServices();
                    }

                    sdkConnectStatus = SdkConstants.CONNECT_DISCONNECT_SERVICE_UNFOUND;
                }
                if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                    if (gatt != null) {
                        gatt.close();
                        gatt = null;
                    }

                    if (connectionTimer != null) {
                        connectionTimer.cancel();
                        connectionTimer = null;
                    }

                    com.ideabus.mylibrary.code.base.ConnectBase.curOperationSucceed = false;

                    if (null != communicateBase) {
                        communicateBase.setConnected(false);
                        communicateBase.resetCommunicateErrorTimer();
                        communicateBase.resetWaveTimeoutTimer();
                        communicateBase.resetCommunicateTimer();
                    }

                    if (!disconnected) {
                        Log.e(TAG, "gatt disconnected");
                        if (connectCallback != null) {
                            connectCallback.onConnectStatus(SdkConstants.CLOSED);
                        }
                    }

                    else if (connectCallback != null) {
                        connectCallback.onConnectStatus(SdkConstants.CONNECT_DISCONNECTED);
                    }
                }
            }
            
            public void onCharacteristicWrite(final BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final int status) {
                super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, status);
            }
            
            public void onDescriptorWrite(final BluetoothGatt bluetoothGatt, final BluetoothGattDescriptor bluetoothGattDescriptor, final int status) {
                if (status == 0 && Utils.bytesToHexString(bluetoothGattDescriptor.getValue()).equals(Utils.bytesToHexString(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE))) {
                    Log.e(TAG, "gatt disconnected");
                    if (connectionTimer != null) {
                        connectionTimer.cancel();
                        connectionTimer = null;
                    }
                    if (null != communicateBase) {
                        communicateBase.resetCommunicateErrorTimer();
                        communicateBase.resetCommunicateTimer();
                        communicateBase.resetWaveTimeoutTimer();
                        communicateBase.setConnected(true);
                    }
                    if (null != connectCallback) {
                        connectCallback.onConnectStatus(3);
                    }
                }
            }
            
            public void onServicesDiscovered(final BluetoothGatt bluetoothGatt, final int status) {
                gattService = gatt.getService(Uuids.serviceChar());
                writeCharacteristic = gattService.getCharacteristic(Uuids.writeChar());
                notifyCharacteristic = gattService.getCharacteristic(Uuids.notifyChar());
                sdkConnectStatus = SdkConstants.CONNECT_DISCONNECT_NOTIFY_FAIL;
                setNotify(notifyCharacteristic);
            }
            
            public void onCharacteristicChanged(final BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                if (null != communicateBase) {
                    communicateBase.addBytesToParse(bluetoothGattCharacteristic.getValue());
                }
            }
        };
        this.ctx = ctx;
        this.device = device;
    }
    
    @Override
    public void connect(final ConnectCallback connectCallback) {
        if (null != connectCallback) {
            this.connectCallback = new WeakReference<>(connectCallback).get();
        }
        if (curOperationSucceed) {
            return;
        }
        curOperationSucceed = true;
        if (null != this.gatt) {
            this.gatt.disconnect();
        }
        if (null != this.connectionTimer) {
            this.connectionTimer.cancel();
            this.connectionTimer = null;
        }
        this.disconnected = false;
        this.gatt = this.device.connectGatt(this.ctx, false, this.gattCallback);
        int connectOverTime = ContecSdk.connectOverTime;
        if (connectOverTime <= 0) {
            connectOverTime = 20000;
        }
        if (this.connectionTimer == null) {
            (this.connectionTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (gatt != null) {
                        if (BleManager.this.connectCallback != null) {
                            BleManager.this.connectCallback.onConnectStatus(sdkConnectStatus);
                        }
                        disconnect();
                    }
                }
            }, connectOverTime);
        }
    }
    
    public void setNotify(final BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (!this.gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
            return;
        }
        final BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(Uuids.notifyDescriptor());
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.gatt.writeDescriptor(descriptor);
        }
    }
    
    @Override
    public void disconnect() {
        curOperationSucceed = false;
        if (this.gatt != null) {
            this.gatt.disconnect();
            this.disconnected = true;
            Log.e(this.TAG, "gatt manually disconnected");
        }
        if (this.connectionTimer != null) {
            this.connectionTimer.cancel();
            this.connectionTimer = null;
        }
    }
    
    @Override
    public void writeBytes(final byte[] array) {
        this.writeCharacter(array);
    }
    
    private void writeCharacter(final byte[] value) {
        if (null == value) {
            return;
        }
        if (this.writeCharacteristic != null) {
            this.writeCharacteristic.setValue(value);
        }
        if (this.gatt != null) {
            this.gatt.writeCharacteristic(this.writeCharacteristic);
        }
    }
    
    private void sleep(final int secs) {
        try {
            Thread.sleep(secs);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
