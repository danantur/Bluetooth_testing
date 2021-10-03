// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

public interface RealtimeSpO2Callback extends CommunicateFailCallback
{
    void onRealtimeSpo2Data(final int p0, final int p1, final int p2);
    
    void onRealtimeSpo2End();
}
