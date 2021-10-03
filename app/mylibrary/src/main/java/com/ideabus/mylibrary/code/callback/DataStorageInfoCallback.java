// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

import com.ideabus.mylibrary.code.bean.SystemParameter;

public interface DataStorageInfoCallback extends CommunicateFailCallback
{
    void onSuccess(final SystemParameter.DataStorageInfo storageInfo, final int p1);
}
