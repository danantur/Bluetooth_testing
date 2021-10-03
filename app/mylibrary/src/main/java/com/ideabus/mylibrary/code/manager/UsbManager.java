// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.manager;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.base.ConnectBase;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.callback.ConnectCallback;

import java.io.IOException;
import java.util.Arrays;

public class UsbManager extends ConnectBase
{
    private final String TAG = this.getClass().getName();

    private final android.hardware.usb.UsbManager usbManager;
    private final UsbDevice usbDevice;
    private final CommunicateBase communicateBase;
    private ConnectCallback connectCallback;
    private UsbEndpoint usbEndpoint;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbWriteThread writeThread;

    public UsbManager(final android.hardware.usb.UsbManager usbManager, final UsbDevice usbDevice, final CommunicateBase communicateBase) {
        this.writeThread = null;
        this.usbManager = usbManager;
        this.usbDevice = usbDevice;
        this.communicateBase = communicateBase;
    }
    
    @Override
    public void connect(final ConnectCallback connectCallback) {
        if (connectCallback == null) {
            return;
        }
        this.connectCallback = connectCallback;
        if (UsbManager.curOperationSucceed) {
            connectCallback.onOpenStatus(SdkConstants.OPENED);
        }
        if (this.writeThread != null) {
            this.writeThread.end();
            this.writeThread = null;
        }
        final UsbInterface interface1 = this.usbDevice.getInterface(0);
        if (interface1 == null) {
            connectCallback.onOpenStatus(SdkConstants.OPEN_FAIL);
        }
        assert interface1 != null;
        interface1.getEndpoint(0);
        this.usbEndpoint = interface1.getEndpoint(1);
        this.usbDeviceConnection = this.usbManager.openDevice(this.usbDevice);
        if (this.usbDeviceConnection != null && this.usbDeviceConnection.claimInterface(interface1, true)) {
            UsbManager.curOperationSucceed = true;
            if (this.communicateBase != null) {
                this.communicateBase.setConnected(true);
            }
            Log.i(this.TAG, "usb successfully connected");
            (this.writeThread = new UsbWriteThread()).start();
            connectCallback.onOpenStatus(SdkConstants.OPEN_SUCCESS);
        }
        else {
            UsbManager.curOperationSucceed = false;
            connectCallback.onOpenStatus(SdkConstants.OPEN_FAIL);
        }
    }
    
    @Override
    public void disconnect() {
        if (this.writeThread != null) {
            this.writeThread.end();
            this.writeThread = null;
        }
        if (this.usbDeviceConnection != null && UsbManager.curOperationSucceed) {
            Log.i(this.TAG, "usb disconnected");
            this.usbDeviceConnection.close();
            this.usbDeviceConnection = null;
            UsbManager.curOperationSucceed = false;
            if (this.connectCallback != null) {
                this.connectCallback.onOpenStatus(7);
            }
            if (this.communicateBase != null) {
                this.communicateBase.setConnected(false);
            }
        }
    }
    
    @Override
    public void writeBytes(final byte[] array) {
        if (null == array) {
            return;
        }
        if (array.length <= 0) {
            return;
        }
        if (null == this.writeThread) {
            return;
        }
        try {
            this.writeThread.writeUsbBytes(array);
        }
        catch (Exception ex) {
            if (this.communicateBase != null) {
                Log.e(this.TAG, "error while writing bytes" + Arrays.toString(array));
            }
        }
    }
    
    public class UsbWriteThread extends Thread
    {
        private boolean writeUsb;
        private final byte[] byteArray1;
        private final byte[] byteArray2;
        private final Object obj;
        
        public UsbWriteThread() {
            this.writeUsb = false;
            this.byteArray1 = new byte[64];
            this.byteArray2 = new byte[64];
            this.obj = new Object();
            this.writeUsb = true;
        }
        
        @Override
        public void run() {
            while (this.writeUsb && ConnectBase.curOperationSucceed) {
                if (this.writeUsbBytes(this.byteArray1) > 0) {
                    if (UsbManager.this.communicateBase == null) {
                        continue;
                    }
                    UsbManager.this.communicateBase.addBytesToParse(this.byteArray1);
                }
            }
        }
        
        public int writeUsbBytes(final byte[] array) {
            if (!ConnectBase.curOperationSucceed) {
                return 0;
            }
            int i = 0;
            int min;
            while (i < array.length) {
                int bulkTransfer = 0;
                synchronized (this.obj) {
                    min = Math.min(array.length - i, this.byteArray2.length);
                    byte[] e;
                    if (i == 0) {
                        e = array;
                    }
                    else {
                        System.arraycopy(array, i, this.byteArray2, 0, min);
                        e = this.byteArray2;
                    }
                    if (UsbManager.this.usbDeviceConnection != null) {
                        bulkTransfer = UsbManager.this.usbDeviceConnection.bulkTransfer(UsbManager.this.usbEndpoint, e, min, 1000);
                    }
                }
                if (bulkTransfer <= 0) {
                    try {
                        throw new IOException("Error writing " + min + " bytes at offset " + i + " length=" + array.length);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                i += bulkTransfer;
            }
            return i;
        }
        
        public void end() {
            this.writeUsb = false;
        }
    }
}
