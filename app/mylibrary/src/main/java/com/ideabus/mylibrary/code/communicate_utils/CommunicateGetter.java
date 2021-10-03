// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate_utils;

import android.hardware.usb.UsbDevice;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.communicate.CommunicateWatches;
import com.ideabus.mylibrary.code.communicate.CommunicateBasic;
import com.ideabus.mylibrary.code.bean.DeviceType;

public class CommunicateGetter
{
    
    public static CommunicateBase getCommunicateByType(final DeviceType deviceType) {
        CommunicateBase communicateBase = null;
        if (null == deviceType) {
            return null;
        }
        switch (deviceType) {
            case CMS50I:
            case CMS50E:
            case CMS50F:
            case CMS50D:
            case CMSBleStandardDevice: {
                communicateBase = new CommunicateBasic(deviceType.name());
                break;
            }
            case CMS50K: {
                communicateBase = new CommunicateWatches(deviceType.name());
                break;
            }
        }
        return communicateBase;
    }
    
    public static CommunicateBase getCommunicateByType(final UsbDevice usbDevice) {
        CommunicateBase communicateBase = null;
        if (usbDevice.getProductId() == 650) {
            communicateBase = new CommunicateBasic("CMS60D1");
        }
        return communicateBase;
    }
}
