// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate_utils;

import java.util.UUID;

public class Uuids
{
    public static UUID writeChar() {
        return UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    }
    
    public static UUID notifyChar() {
        return UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
    }
    
    public static UUID serviceChar() {
        return UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
    }

    public static UUID notifyDescriptor() {
        return UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }
}
