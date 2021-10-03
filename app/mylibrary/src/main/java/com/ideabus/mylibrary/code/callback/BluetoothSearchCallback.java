// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

import android.bluetooth.BluetoothDevice;

public interface BluetoothSearchCallback
{
    void onDeviceFound(final BluetoothDevice bluetoothDevice, final int rssi, final byte[] record);
    
    void onSearchError(final int errorCode);
    
    void onSearchComplete();
}
