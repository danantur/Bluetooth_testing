// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate;

import android.util.Log;

import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.bean.g;
import com.ideabus.mylibrary.code.callback.CommunicateFailCallback;
import com.ideabus.mylibrary.code.callback.DeleteDataCallback;
import com.ideabus.mylibrary.code.callback.GetStorageModeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeSpO2Callback;
import com.ideabus.mylibrary.code.callback.SetCalorieCallback;
import com.ideabus.mylibrary.code.callback.SetHeightCallback;
import com.ideabus.mylibrary.code.callback.SetStepsTimeCallback;
import com.ideabus.mylibrary.code.callback.SetWeightCallback;
import com.ideabus.mylibrary.code.callback.StorageModeCallback;
import com.ideabus.mylibrary.code.connect.ContecSdk;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

// https://www.contecmed.com/ProductInfoSearch?searchKey=CMS50K

public class CommunicateWatches extends CommunicateBasic
{
    public ParseThread parseThread;
    
    public CommunicateWatches(String deviceName) {
        super(deviceName);
        this.parseThread = null;
    }
    
    @Override
    public void startRealtime(final RealtimeCallback realtimeCallback) {
        if (this.communicating) {
            return;
        }
        if (this.realtimeStarted) {
            return;
        }
        if (realtimeCallback != null) {
            this.realtimeCallback = new WeakReference<>(realtimeCallback).get();
        }
        this.realtimeStarted = true;
        this.writeBytes(ParseUtils.realtimeBytes(0));
        this.setWaveTimeout(this.realtimeCallback);
        this.sleep(1000);
        this.currentOperationCode = 4;
        Log.e("h", Integer.toString(1));
        this.writeBytes(ParseUtils.realtimeBytes(1));
        this.setSpo2Timeout(this.realtimeCallback);
        if (this.ak == null) {
            (this.ak = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.realtimeBytes());
                }
            }, 5500L, 4500L);
        }
    }
    
    @Override
    public void startRealtime() {
        if (this.communicating) {
            return;
        }
        this.writeBytes(ParseUtils.realtimeBytes(127));
        this.errorCode = 10158463;
        if (this.currentOperationCode == 4) {
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.realtimeCallback);
        }
        else if (this.currentOperationCode == 7) {
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.realtimeSpO2Callback);
        }
    }
    
    @Override
    public void getStorageMode(final GetStorageModeCallback getStorageModeCallback) {
        if (getStorageModeCallback != null) {
            getStorageModeCallback.onFail(255);
        }
    }
    
    @Override
    public void setStorageMode(final SystemParameter.StorageMode storageMode, final StorageModeCallback storageModeCallback) {
        if (storageModeCallback != null) {
            storageModeCallback.onFail(255);
        }
    }
    
    @Override
    public void deleteData(final DeleteDataCallback referent) {
        this.deleteDataCallback = new WeakReference<>(referent).get();
        this.currentOperationCode = 6;
        this.writeBytes(ParseUtils.k(0));
    }
    
    @Override
    public void startRealtimeSpo2(final RealtimeSpO2Callback spO2Callback) {
        if (this.communicating) {
            return;
        }
        if (null != spO2Callback) {
            this.realtimeSpO2Callback = new WeakReference<>(spO2Callback).get();
        }
        this.currentOperationCode = 7;
        this.writeBytes(ParseUtils.realtimeBytes(1));
        this.m();
        if (this.ak == null) {
            (this.ak = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.realtimeBytes());
                }
            }, 5500L, 4500L);
        }
    }
    
    @Override
    public void setCalorie(final int n, final int n2, final SystemParameter.StepsSensitivity stepsSensitivity, final SetCalorieCallback setCalorieCallback) {
    }
    
    @Override
    public void setWeight(final int weight, final SetWeightCallback setWeightCallback) {
    }
    
    @Override
    public void setHeight(final int height, final SetHeightCallback setHeightCallback) {
    }
    
    @Override
    public void setStepsTime(final int n, final int n2, final SetStepsTimeCallback setStepsTimeCallback) {
    }
    
    @Override
    public void addBytesToParse(final byte[] bytes) {
        if (null != this.inputBytes) {
            for (int i = 0; i < bytes.length; ++i) {
                this.inputBytes.offer(bytes[i]);
            }
        }
        if (this.parseThread == null) {
            (this.parseThread = new ParseThread(this.inputBytes)).start();
        }
    }
    
    protected void c(final byte[] array) {
        final short[] m = com.ideabus.mylibrary.code.tools.b.m(array);
        if (this.Y != null && (this.ae + 1) * 27 < this.Y.length) {
            for (int i = 27 * this.ae; i < 27 * (this.ae + 1); ++i) {
                this.Y[i] = m[i - 27 * this.ae];
            }
        }
        else if (this.Y != null) {
            for (int j = 27 * this.ae; j < this.Y.length; ++j) {
                this.Y[j] = m[j - 27 * this.ae];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.I) {
            this.ae = 0;
            this.writeBytes(ParseUtils.i(1));
            this.errorCode = 10486017;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }
    
    protected void d(final byte[] array) {
        final short[] m = com.ideabus.mylibrary.code.tools.b.m(array);
        if (this.Z != null && (this.ae + 1) * 27 < this.Z.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.Z[i] = (m[i - this.ae * 27] & 0x7F);
            }
        }
        else if (this.Z != null) {
            for (int j = this.ae * 27; j < this.Z.length; ++j) {
                this.Z[j] = (m[j - this.ae * 27] & 0x7F);
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.I) {
            this.H = 0;
            this.ae = 0;
            this.n();
        }
    }
    
    private void n1() {
        final com.ideabus.mylibrary.code.bean.b b = new com.ideabus.mylibrary.code.bean.b();
        this.b(b);
        this.onEachPieceDataResult(b);
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.k(0));
        }
        else {
            this.e();
            this.resetCommunicateErrorTimer();
            this.init();
        }
        this.onDataResultEnd();
    }
    
    private void b(final PieceData pieceData) {
        pieceData.setDataType(this.dataTypeInt);
        pieceData.setTotalNumber(this.D);
        pieceData.setCaseCount(this.N);
        pieceData.setSupportPI(0);
        pieceData.setLength(this.I);
        pieceData.setStartTime(this.ag);
        pieceData.setSpo2Data(this.Z);
        pieceData.setPrData(this.Y);
    }
    
    private void e() {
        if (null != this.inputBytes) {
            this.inputBytes.clear();
        }
        if (this.parseThread != null) {
            this.parseThread.end();
            this.parseThread = null;
        }
    }
    
    public void l() {
        if (this.ak != null) {
            this.ak.cancel();
            this.ak = null;
        }
    }
    
    protected void m() {
        this.resetCommunicateErrorTimer();
        if (this.realtimeDelayTimer == null) {
            (this.realtimeDelayTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.realtimeBytes(1));
                    CommunicateWatches.this.setSpo2Timeout(CommunicateWatches.this.realtimeSpO2Callback);
                }
            }, 5000L);
        }
    }
    
    public class ParseThread extends Thread
    {
        private final ConcurrentLinkedQueue<Byte> inputData;
        private final byte[] bytes;
        private boolean running;
        
        public ParseThread(final ConcurrentLinkedQueue<Byte> inputData) {
            running = false;
            this.bytes = new byte[128];
            this.inputData = inputData;
            running = true;
        }
        
        @Override
        public void run() {
            while (running) {
                if (null != inputData && !inputData.isEmpty()) {
                    this.isDataNull(this.bytes, 0, 1);
                    if (!running) {
                        return;
                    }
                    switch (this.bytes[0]) {
                        case -21: {
                            this.isDataNull(this.bytes, 1, 1);
                            if (!running) {
                                return;
                            }
                            if (this.bytes[1] == 0) {
                                this.isDataNull(this.bytes, 2, 4);
                                if (!running) {
                                    return;
                                }
                                CommunicateWatches.this.setWaveTimeout(CommunicateWatches.this.realtimeCallback);
                                final g b = com.ideabus.mylibrary.code.tools.b.b(this.bytes);
                                if (CommunicateWatches.this.realtimeCallback == null) {
                                    continue;
                                }
                                CommunicateWatches.this.realtimeCallback.onRealtimeWaveData(b.a(), b.b(), b.c(), b.d(), b.e());
                                continue;
                            }
                            else if (this.bytes[1] == 1) {
                                this.isDataNull(this.bytes, 2, 6);
                                if (!running) {
                                    return;
                                }
                                CommunicateWatches.this.resetRealtimeDelayTimer();
                                final com.ideabus.mylibrary.code.bean.f d = com.ideabus.mylibrary.code.tools.b.d(this.bytes);
                                if (CommunicateWatches.this.realtimeCallback != null) {
                                    CommunicateWatches.this.setSpo2Timeout(CommunicateWatches.this.realtimeCallback);
                                    CommunicateWatches.this.realtimeCallback.onSpo2Data(d.a(), d.c(), d.b(), d.d());
                                }
                                if (CommunicateWatches.this.realtimeSpO2Callback == null) {
                                    continue;
                                }
                                CommunicateWatches.this.setSpo2Timeout(CommunicateWatches.this.realtimeSpO2Callback);
                                CommunicateWatches.this.realtimeSpO2Callback.onRealtimeSpo2Data(d.c(), d.b(), d.d());
                                continue;
                            }
                            else {
                                if (this.bytes[1] != 127) {
                                    continue;
                                }
                                this.isDataNull(this.bytes, 2, 1);
                                if (!running) {
                                    return;
                                }
                                CommunicateWatches.this.realtimeStarted = false;
                                CommunicateWatches.this.e();
                                CommunicateWatches.this.l();
                                CommunicateWatches.this.resetRealtimeDelayTimer();
                                CommunicateWatches.this.resetCommunicateErrorTimer();
                                CommunicateWatches.this.resetCommunicateTimer();
                                CommunicateWatches.this.resetWaveTimeoutTimer();
                                if (CommunicateWatches.this.realtimeCallback != null) {
                                    CommunicateWatches.this.realtimeCallback.onRealtimeEnd();
                                }
                                if (CommunicateWatches.this.realtimeSpO2Callback != null) {
                                    CommunicateWatches.this.realtimeSpO2Callback.onRealtimeSpo2End();
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -15: {
                            this.isDataNull(this.bytes, 1, 9);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.errorCode = 8585475;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            CommunicateWatches.this.writeBytes(ParseUtils.d());
                            continue;
                        }
                        case -13: {
                            this.isDataNull(this.bytes, 1, 2);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.writeBytes(ParseUtils.a(0, 24));
                            CommunicateWatches.this.errorCode = 8651012;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -12: {
                            this.isDataNull(this.bytes, 1, 2);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(0));
                            CommunicateWatches.this.errorCode = 9437440;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -32: {
                            this.isDataNull(this.bytes, 1, 6);
                            if (!running) {
                                return;
                            }
                            final int n = (this.bytes[4] & 0x7F) | ((this.bytes[5] & 0x7F) << 7 & 0xFFFF);
                            if (CommunicateWatches.this.currentOperationCode == 2) {
                                CommunicateWatches.this.resetCommunicateErrorTimer();
                                if (CommunicateWatches.this.dataStorageInfoCallback != null) {
                                    CommunicateWatches.this.dataStorageInfoCallback.onSuccess(CommunicateWatches.this.storageInfo, n);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateWatches.this.currentOperationCode != 3) {
                                    continue;
                                }
                                if ((this.bytes[1] & 0x7) == 0x0) {
                                    CommunicateWatches.this.z = n;
                                    if (CommunicateWatches.this.z > 0) {
                                        CommunicateWatches.this.spo2PointDataArray = (ArrayList<SpO2PointData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.b(0));
                                        CommunicateWatches.this.errorCode = 9502976;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                    CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(1));
                                    CommunicateWatches.this.errorCode = 9437441;
                                    CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                    continue;
                                }
                                else if ((this.bytes[1] & 0x7) == 0x1) {
                                    CommunicateWatches.this.A = n;
                                    if (CommunicateWatches.this.A > 0) {
                                        CommunicateWatches.this.dayStepsData = (ArrayList<DayStepsData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.c(0));
                                        CommunicateWatches.this.errorCode = 9568512;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                    CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                    CommunicateWatches.this.errorCode = 9634048;
                                    CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                    continue;
                                }
                                else if ((this.bytes[1] & 0x7) == 0x2) {
                                    CommunicateWatches.this.B = n;
                                    if (CommunicateWatches.this.B > 0) {
                                        CommunicateWatches.this.fiveMinStepsDataArray = (ArrayList<FiveMinStepsData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.d(1));
                                        CommunicateWatches.this.errorCode = 9437442;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                    CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(3));
                                    CommunicateWatches.this.errorCode = 9437443;
                                    CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                    continue;
                                }
                                else {
                                    if ((this.bytes[1] & 0x7) != 0x3) {
                                        continue;
                                    }
                                    CommunicateWatches.this.C = n;
                                    if (CommunicateWatches.this.C > 0) {
                                        CommunicateWatches.this.writeBytes(ParseUtils.f(0));
                                        CommunicateWatches.this.errorCode = 9765120;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                    CommunicateWatches.this.writeBytes(ParseUtils.i(0));
                                    CommunicateWatches.this.errorCode = 10486016;
                                    CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                    continue;
                                }
                            }
                        }
                        case -31: {
                            this.isDataNull(this.bytes, 1, 10);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.spo2PointDataArray.add(com.ideabus.mylibrary.code.tools.b.e(this.bytes));
                            if (CommunicateWatches.this.E == 10) {
                                CommunicateWatches.this.E = 0;
                            }
                            if (CommunicateWatches.this.E != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(500);
                                if (inputData != null) {
                                    inputData.clear();
                                }
                                CommunicateWatches.this.E = 10;
                                CommunicateWatches.this.writeBytes(ParseUtils.b(2));
                                CommunicateWatches.this.errorCode = 9502976;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.E++;
                            if (CommunicateWatches.this.E == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.b(1));
                                CommunicateWatches.this.errorCode = 9502976;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) != 0x0) {
                                if (ContecSdk.isDelete) {
                                    CommunicateWatches.this.writeBytes(ParseUtils.b(127));
                                }
                                else {
                                    CommunicateWatches.this.writeBytes(ParseUtils.b(126));
                                }
                                CommunicateWatches.this.sleep(500);
                                CommunicateWatches.this.E = 0;
                                CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(1));
                                CommunicateWatches.this.errorCode = 9437441;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                CommunicateWatches.this.onPointSpO2DataResult(CommunicateWatches.this.spo2PointDataArray);
                                continue;
                            }
                            continue;
                        }
                        case -30: {
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            this.isDataNull(this.bytes, 1, 10);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.dayStepsData.add(com.ideabus.mylibrary.code.tools.b.f(this.bytes));
                            if (CommunicateWatches.this.E == 10) {
                                CommunicateWatches.this.E = 0;
                            }
                            if (CommunicateWatches.this.E != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(500);
                                if (inputData != null) {
                                    inputData.clear();
                                }
                                CommunicateWatches.this.E = 10;
                                CommunicateWatches.this.writeBytes(ParseUtils.c(2));
                                CommunicateWatches.this.errorCode = 9568512;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.E++;
                            if (CommunicateWatches.this.E == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.c(1));
                                CommunicateWatches.this.errorCode = 9568512;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) != 0x0) {
                                if (ContecSdk.isDelete) {
                                    CommunicateWatches.this.writeBytes(ParseUtils.c(127));
                                }
                                else {
                                    CommunicateWatches.this.writeBytes(ParseUtils.c(126));
                                }
                                CommunicateWatches.this.sleep(500);
                                CommunicateWatches.this.E = 0;
                                CommunicateWatches.this.errorCode = 9634048;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                CommunicateWatches.this.onDayStepsDataResult(CommunicateWatches.this.dayStepsData);
                                continue;
                            }
                            continue;
                        }
                        case -29: {
                            CommunicateWatches.this.fiveMinStepsData = new FiveMinStepsData();
                            this.isDataNull(this.bytes, 1, 8);
                            if (!running) {
                                return;
                            }
                            final int year = (this.bytes[1] & 0x7F) + 2000;
                            final int month = this.bytes[2] & 0xF;
                            final int day = this.bytes[3] & 0x1F;
                            final int length = ((this.bytes[6] & 0x7F) | (this.bytes[7] & 0x7F) << 7) & 0xFFFF;
                            CommunicateWatches.this.fiveMinStepsData.setYear(year);
                            CommunicateWatches.this.fiveMinStepsData.setMonth(month);
                            CommunicateWatches.this.fiveMinStepsData.setDay(day);
                            CommunicateWatches.this.fiveMinStepsData.setLength(length);
                            CommunicateWatches.this.ar = new short[length * 2];
                            CommunicateWatches.this.writeBytes(ParseUtils.e(0));
                            CommunicateWatches.this.errorCode = 9699584;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -28: {
                            this.isDataNull(this.bytes, 1, 16);
                            if (!running) {
                                return;
                            }
                            final short[] h = com.ideabus.mylibrary.code.tools.b.h(this.bytes);
                            if (CommunicateWatches.this.E == 10) {
                                CommunicateWatches.this.E = 0;
                            }
                            if (null != CommunicateWatches.this.ar && (CommunicateWatches.this.ae + 1) * 6 < CommunicateWatches.this.ar.length) {
                                for (int i = CommunicateWatches.this.ae * 6; i < (CommunicateWatches.this.ae + 1) * 6; ++i) {
                                    CommunicateWatches.this.ar[i] = h[i - CommunicateWatches.this.ae * 6];
                                }
                            }
                            else if (null != CommunicateWatches.this.ar) {
                                for (int j = CommunicateWatches.this.ae * 6; j < CommunicateWatches.this.ar.length; ++j) {
                                    CommunicateWatches.this.ar[j] = h[j - CommunicateWatches.this.ae * 6];
                                }
                            }
                            if (CommunicateWatches.this.E != (this.bytes[1] & 0xF)) {
                                continue;
                            }
                            CommunicateWatches.this.E++;
                            CommunicateWatches.this.ae++;
                            if (CommunicateWatches.this.E == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.e(1));
                                CommunicateWatches.this.errorCode = 9699584;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) == 0x0 || CommunicateWatches.this.ae * 6 < CommunicateWatches.this.ar.length) {
                                continue;
                            }
                            CommunicateWatches.this.E = 0;
                            CommunicateWatches.this.ae = 0;
                            CommunicateWatches.this.fiveMinStepsData.setStepFiveDataBean(CommunicateWatches.this.ar);
                            CommunicateWatches.this.fiveMinStepsDataArray.add(CommunicateWatches.this.fiveMinStepsData);
                            if (ContecSdk.isDelete) {
                                CommunicateWatches.this.writeBytes(ParseUtils.e(127));
                            }
                            else {
                                CommunicateWatches.this.writeBytes(ParseUtils.e(126));
                            }
                            CommunicateWatches.this.sleep(500);
                            if (CommunicateWatches.this.fiveMinStepsDataArray.size() < CommunicateWatches.this.B) {
                                CommunicateWatches.this.writeBytes(ParseUtils.d(1));
                                CommunicateWatches.this.errorCode = 9634048;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(3));
                            CommunicateWatches.this.errorCode = 9765120;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            CommunicateWatches.this.onFiveMinStepsDataResult(CommunicateWatches.this.fiveMinStepsDataArray);
                            continue;
                        }
                        case -27: {
                            this.isDataNull(this.bytes, 1, 17);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.as = com.ideabus.mylibrary.code.tools.b.i(this.bytes);
                            CommunicateWatches.this.at = new int[CommunicateWatches.this.as.getSize()];
                            CommunicateWatches.this.writeBytes(ParseUtils.g(0));
                            CommunicateWatches.this.errorCode = 9830656;
                            continue;
                        }
                        case -26: {
                            this.isDataNull(this.bytes, 1, 16);
                            if (!running) {
                                return;
                            }
                            final int[] k = com.ideabus.mylibrary.code.tools.b.j(this.bytes);
                            if (CommunicateWatches.this.E == 10) {
                                CommunicateWatches.this.E = 0;
                            }
                            if (null != CommunicateWatches.this.at && (CommunicateWatches.this.ae + 1) * 6 < CommunicateWatches.this.at.length) {
                                for (int l = CommunicateWatches.this.ae * 6; l < (CommunicateWatches.this.ae + 1) * 6; ++l) {
                                    CommunicateWatches.this.at[l] = k[l - CommunicateWatches.this.ae * 6];
                                }
                            }
                            else if (null != CommunicateWatches.this.at) {
                                for (int n2 = CommunicateWatches.this.ae * 6; n2 < CommunicateWatches.this.at.length; ++n2) {
                                    CommunicateWatches.this.at[n2] = k[n2 - CommunicateWatches.this.ae * 6];
                                }
                            }
                            if (CommunicateWatches.this.E != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(100);
                                CommunicateWatches.this.ae -= CommunicateWatches.this.E;
                                CommunicateWatches.this.E = 10;
                                if (null != CommunicateWatches.this.inputBytes) {
                                    CommunicateWatches.this.inputBytes.clear();
                                }
                                CommunicateWatches.this.writeBytes(ParseUtils.g(2));
                                CommunicateWatches.this.errorCode = 9830656;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.E++;
                            CommunicateWatches.this.ae++;
                            if (CommunicateWatches.this.E == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(1));
                                CommunicateWatches.this.errorCode = 9765120;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) == 0x0 || CommunicateWatches.this.ae * 6 < CommunicateWatches.this.as.getSize()) {
                                continue;
                            }
                            CommunicateWatches.this.as.setUploadCount(CommunicateWatches.this.C);
                            CommunicateWatches.this.as.setCurrentCount(CommunicateWatches.this.au);
                            CommunicateWatches.this.as.setEcgData(CommunicateWatches.this.at);
                            CommunicateWatches.this.E = 0;
                            CommunicateWatches.this.ae = 0;
                            if (ContecSdk.isDelete) {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(127));
                            }
                            else {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(126));
                            }
                            CommunicateWatches.this.sleep(500);
                            CommunicateWatches.this.onEachEcgDataResult(CommunicateWatches.this.as);
                            if (CommunicateWatches.this.au < CommunicateWatches.this.C) {
                                CommunicateWatches.this.au++;
                                CommunicateWatches.this.writeBytes(ParseUtils.f(1));
                                CommunicateWatches.this.errorCode = 9765120;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.au = 0;
                            CommunicateWatches.this.writeBytes(ParseUtils.i(0));
                            CommunicateWatches.this.errorCode = 10486016;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -48: {
                            this.isDataNull(this.bytes, 1, 13);
                            if (!running) {
                                return;
                            }
                            final int n3 = this.bytes[1] & 0x7;
                            CommunicateWatches.this.ag = com.ideabus.mylibrary.code.tools.b.k(this.bytes);
                            CommunicateWatches.this.I = (((this.bytes[10] & 0x7F) | (this.bytes[11] & 0x7F) << 7 | (this.bytes[12] & 0x7F) << 14) & -1);
                            if (CommunicateWatches.this.I > 0) {
                                switch (n3) {
                                    case 0: {
                                        CommunicateWatches.this.Y = new int[CommunicateWatches.this.I];
                                        CommunicateWatches.this.writeBytes(ParseUtils.b(0, 0));
                                        CommunicateWatches.this.errorCode = 10617088;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                    case 1: {
                                        CommunicateWatches.this.Z = new int[CommunicateWatches.this.I];
                                        CommunicateWatches.this.writeBytes(ParseUtils.c(0, 0));
                                        CommunicateWatches.this.errorCode = 10682624;
                                        CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                        continue;
                                    }
                                }
                                continue;
                            }
                            CommunicateWatches.this.e();
                            CommunicateWatches.this.resetCommunicateErrorTimer();
                            if (CommunicateWatches.this.z != 0 || CommunicateWatches.this.A != 0 || CommunicateWatches.this.B != 0 || CommunicateWatches.this.C != 0) {
                                CommunicateWatches.this.onDataResultEnd();
                            }
                            else {
                                CommunicateWatches.this.onDataResultEmpty();
                            }
                            CommunicateWatches.this.init();
                            continue;
                        }
                        case -47: {
                            this.isDataNull(this.bytes, 1, 3);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.resetCommunicateErrorTimer();
                            if (this.bytes[1] == 0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.k(1));
                                continue;
                            }
                            if (this.bytes[1] != 1) {
                                continue;
                            }
                            CommunicateWatches.this.e();
                            CommunicateWatches.this.init();
                            if ((this.bytes[2] & 0x7F) == 0x0) {
                                if (CommunicateWatches.this.currentOperationCode == 3) {
                                    if (CommunicateWatches.this.communicateCallback != null) {
                                        CommunicateWatches.this.communicateCallback.onDeleteSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if (CommunicateWatches.this.currentOperationCode == 6 && CommunicateWatches.this.deleteDataCallback != null) {
                                        CommunicateWatches.this.deleteDataCallback.onSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                            }
                            else if (CommunicateWatches.this.currentOperationCode == 3) {
                                if (CommunicateWatches.this.communicateCallback != null) {
                                    CommunicateWatches.this.communicateCallback.onDeleteFail();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateWatches.this.currentOperationCode == 6 && CommunicateWatches.this.deleteDataCallback != null) {
                                    CommunicateWatches.this.deleteDataCallback.onFail(1);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -46: {
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            this.isDataNull(this.bytes, 1, 19);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.c(this.bytes);
                            continue;
                        }
                        case -45: {
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            this.isDataNull(this.bytes, 1, 19);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.d(this.bytes);
                            continue;
                        }
                    }
                }
            }
        }
        
        public void isDataNull(final byte[] array, final int startIndex, final int endIndex) {
            for (int i = startIndex; i < endIndex + startIndex && running; ++i) {
                if (inputData != null && !inputData.isEmpty()) {
                    array[i] = inputData.poll();
                }
                else {
                    --i;
                }
            }
        }
        
        public void end() {
            running = false;
        }
    }
}