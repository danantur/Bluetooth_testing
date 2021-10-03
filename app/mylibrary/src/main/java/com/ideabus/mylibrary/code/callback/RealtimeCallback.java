// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

public interface RealtimeCallback extends CommunicateFailCallback
{
    void onRealtimeWaveData(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    void onSpo2Data(final int p0, final int p1, final int p2, final int p3);
    
    void onRealtimeEnd();
}
