// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate_utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.manager.BleManager;
import com.ideabus.mylibrary.code.manager.UsbManager;

@SuppressLint({ "NewApi" })
public class ManagerGetter
{
    
    public static BleManager manager(final Context context, final BluetoothDevice bluetoothDevice, final CommunicateBase communicateBase) {
        BleManager bleManager = null;
        if (bluetoothDevice.getType() == 2) {
            bleManager = new BleManager(context, bluetoothDevice, communicateBase);
        }
        return bleManager;
    }
    
    public static UsbManager manager(final android.hardware.usb.UsbManager usbManager, final UsbDevice usbDevice, final CommunicateBase communicateBase) {
        UsbManager usbManager1 = null;
        if (usbDevice.getProductId() == 650) {
            usbManager1 = new UsbManager(usbManager, usbDevice, communicateBase);
        }
        return usbManager1;
    }
}
