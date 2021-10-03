// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.base;

import com.ideabus.mylibrary.code.callback.ConnectCallback;

public abstract class ConnectBase
{
    public static boolean curOperationSucceed;
    
    public abstract void connect(final ConnectCallback connectCallback);
    
    public abstract void disconnect();
    
    public abstract void writeBytes(final byte[] array);
    
    static {
        curOperationSucceed = false;
    }
}
