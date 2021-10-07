// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

public interface RealtimeSpO2Callback extends CommunicateFailCallback
{
    void onRealtimeSpo2Data(final int pr, final int spo2, final int pi);
    
    void onRealtimeSpo2End();
}
