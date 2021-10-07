// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

public interface RealtimeCallback extends CommunicateFailCallback
{
    void onRealtimeWaveData(final int signal, final int prSound, final int waveData, final int barData, final int fingerOut);
    
    void onSpo2Data(final int piError, final int spo2, final int pr, final int pi);
    
    void onRealtimeEnd();
}
