// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate;

import android.util.Log;

import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.Spo2Data;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.bean.WaveData;
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
import com.ideabus.mylibrary.code.tools.DataClassesParseUtils;

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
        this.writeBytes(ParseUtils.startRealtimeBytes(0));
        this.setWaveTimeout(this.realtimeCallback);
        this.sleep(1000);
        this.currentOperationCode = 4;
        Log.e("h", Integer.toString(1));
        this.writeBytes(ParseUtils.startRealtimeBytes(1));
        this.setSpo2Timeout(this.realtimeCallback);
        if (this.realtimePingTimer == null) {
            (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.realtimePingBytes());
                }
            }, 5500L, 4500L);
        }
    }
    
    @Override
    public void startRealtime() {
        if (this.communicating) {
            return;
        }
        this.writeBytes(ParseUtils.startRealtimeBytes(127));
        this.errorCode = 10158463;
        if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME) {
            this.setCommunicateErrorTimer(this.realtimeCallback);
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME_SPO2) {
            this.setCommunicateErrorTimer(this.realtimeSpO2Callback);
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
        this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(0));
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
        this.writeBytes(ParseUtils.startRealtimeBytes(1));
        this.m();
        if (this.realtimePingTimer == null) {
            (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.realtimePingBytes());
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
        final short[] m = DataClassesParseUtils.m(array);
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
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
    }
    
    protected void d(final byte[] array) {
        final short[] m = DataClassesParseUtils.m(array);
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
            this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(0));
        }
        else {
            this.e();
            this.resetCommunicateErrorTimer();
            this.init();
        }
        this.onDataResultEnd();
    }
    
    private void b(final PieceData pieceData) {
        pieceData.dataType = this.dataTypeInt;
        pieceData.totalNumber = this.totalNumber;
        pieceData.caseCount = this.caseCount;
        pieceData.supportPI = 0;
        pieceData.length = this.I;
        pieceData.startTime = this.ag;
        pieceData.spo2Data = this.Z;
        pieceData.prData = this.Y;
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
        if (this.realtimePingTimer != null) {
            this.realtimePingTimer.cancel();
            this.realtimePingTimer = null;
        }
    }
    
    protected void m() {
        this.resetCommunicateErrorTimer();
        if (this.realtimeDelayTimer == null) {
            (this.realtimeDelayTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateWatches.this.writeBytes(ParseUtils.startRealtimeBytes(1));
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
                    this.resolveInputData(this.bytes, 0, 1);
                    if (!running) {
                        return;
                    }
                    switch (this.bytes[0]) {
                        case -21: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!running) {
                                return;
                            }
                            if (this.bytes[1] == 0) {
                                this.resolveInputData(this.bytes, 2, 4);
                                if (!running) {
                                    return;
                                }
                                CommunicateWatches.this.setWaveTimeout(CommunicateWatches.this.realtimeCallback);
                                final WaveData b = DataClassesParseUtils.parseWaveData(this.bytes);
                                if (CommunicateWatches.this.realtimeCallback == null) {
                                    continue;
                                }
                                CommunicateWatches.this.realtimeCallback.onRealtimeWaveData(b.signal, b.prSound, b.waveData, b.barData, b.fingerOut);
                                continue;
                            }
                            else if (this.bytes[1] == 1) {
                                this.resolveInputData(this.bytes, 2, 6);
                                if (!running) {
                                    return;
                                }
                                CommunicateWatches.this.resetRealtimeDelayTimer();
                                final Spo2Data d = DataClassesParseUtils.parseSpo2Data(this.bytes);
                                if (CommunicateWatches.this.realtimeCallback != null) {
                                    CommunicateWatches.this.setSpo2Timeout(CommunicateWatches.this.realtimeCallback);
                                    CommunicateWatches.this.realtimeCallback.onSpo2Data(d.piError, d.pr, d.spo2, d.pi);
                                }
                                if (CommunicateWatches.this.realtimeSpO2Callback == null) {
                                    continue;
                                }
                                CommunicateWatches.this.setSpo2Timeout(CommunicateWatches.this.realtimeSpO2Callback);
                                CommunicateWatches.this.realtimeSpO2Callback.onRealtimeSpo2Data(d.pr, d.spo2, d.pi);
                                continue;
                            }
                            else {
                                if (this.bytes[1] != 127) {
                                    continue;
                                }
                                this.resolveInputData(this.bytes, 2, 1);
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
                            this.resolveInputData(this.bytes, 1, 9);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.errorCode = 8585475;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            CommunicateWatches.this.writeBytes(ParseUtils.currentDateTimeBytes());
                            continue;
                        }
                        case -13: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.writeBytes(ParseUtils.setStepsTimeBytes(0, 24));
                            CommunicateWatches.this.errorCode = 8651012;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -12: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.writeBytes(ParseUtils.dataStorageBytes(0));
                            CommunicateWatches.this.errorCode = 9437440;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -32: {
                            this.resolveInputData(this.bytes, 1, 6);
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
                                    CommunicateWatches.this.spo2DataInfo = n;
                                    if (CommunicateWatches.this.spo2DataInfo > 0) {
                                        CommunicateWatches.this.spo2PointDataArray = (ArrayList<SpO2PointData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.pointSpo2Bytes(0));
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
                                    CommunicateWatches.this.dayStepsDataInfo = n;
                                    if (CommunicateWatches.this.dayStepsDataInfo > 0) {
                                        CommunicateWatches.this.dayStepsData = (ArrayList<DayStepsData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.dayStepsBytes(0));
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
                                    CommunicateWatches.this.fiveMinStepsDataInfo = n;
                                    if (CommunicateWatches.this.fiveMinStepsDataInfo > 0) {
                                        CommunicateWatches.this.fiveMinStepsDataArray = (ArrayList<FiveMinStepsData>)new ArrayList();
                                        CommunicateWatches.this.writeBytes(ParseUtils.pieceInfoFiveMinStepsBytes(1));
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
                                    CommunicateWatches.this.uploadCount = n;
                                    if (CommunicateWatches.this.uploadCount > 0) {
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
                            this.resolveInputData(this.bytes, 1, 10);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.spo2PointDataArray.add(DataClassesParseUtils.parseSpo2Point(this.bytes));
                            if (CommunicateWatches.this.dataPieceNumber == 10) {
                                CommunicateWatches.this.dataPieceNumber = 0;
                            }
                            if (CommunicateWatches.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(500);
                                if (inputData != null) {
                                    inputData.clear();
                                }
                                CommunicateWatches.this.dataPieceNumber = 10;
                                CommunicateWatches.this.writeBytes(ParseUtils.pointSpo2Bytes(2));
                                CommunicateWatches.this.errorCode = 9502976;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.dataPieceNumber++;
                            if (CommunicateWatches.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.pointSpo2Bytes(1));
                                CommunicateWatches.this.errorCode = 9502976;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) != 0x0) {
                                if (ContecSdk.isDelete) {
                                    CommunicateWatches.this.writeBytes(ParseUtils.pointSpo2Bytes(127));
                                }
                                else {
                                    CommunicateWatches.this.writeBytes(ParseUtils.pointSpo2Bytes(126));
                                }
                                CommunicateWatches.this.sleep(500);
                                CommunicateWatches.this.dataPieceNumber = 0;
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
                            this.resolveInputData(this.bytes, 1, 10);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.dayStepsData.add(DataClassesParseUtils.parseDaySteps(this.bytes));
                            if (CommunicateWatches.this.dataPieceNumber == 10) {
                                CommunicateWatches.this.dataPieceNumber = 0;
                            }
                            if (CommunicateWatches.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(500);
                                if (inputData != null) {
                                    inputData.clear();
                                }
                                CommunicateWatches.this.dataPieceNumber = 10;
                                CommunicateWatches.this.writeBytes(ParseUtils.dayStepsBytes(2));
                                CommunicateWatches.this.errorCode = 9568512;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.dataPieceNumber++;
                            if (CommunicateWatches.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.dayStepsBytes(1));
                                CommunicateWatches.this.errorCode = 9568512;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) != 0x0) {
                                if (ContecSdk.isDelete) {
                                    CommunicateWatches.this.writeBytes(ParseUtils.dayStepsBytes(127));
                                }
                                else {
                                    CommunicateWatches.this.writeBytes(ParseUtils.dayStepsBytes(126));
                                }
                                CommunicateWatches.this.sleep(500);
                                CommunicateWatches.this.dataPieceNumber = 0;
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
                            this.resolveInputData(this.bytes, 1, 8);
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
                            CommunicateWatches.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(0));
                            CommunicateWatches.this.errorCode = 9699584;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -28: {
                            this.resolveInputData(this.bytes, 1, 16);
                            if (!running) {
                                return;
                            }
                            final short[] h = DataClassesParseUtils.h(this.bytes);
                            if (CommunicateWatches.this.dataPieceNumber == 10) {
                                CommunicateWatches.this.dataPieceNumber = 0;
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
                            if (CommunicateWatches.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                continue;
                            }
                            CommunicateWatches.this.dataPieceNumber++;
                            CommunicateWatches.this.ae++;
                            if (CommunicateWatches.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(1));
                                CommunicateWatches.this.errorCode = 9699584;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) == 0x0 || CommunicateWatches.this.ae * 6 < CommunicateWatches.this.ar.length) {
                                continue;
                            }
                            CommunicateWatches.this.dataPieceNumber = 0;
                            CommunicateWatches.this.ae = 0;
                            CommunicateWatches.this.fiveMinStepsData.setStepFiveDataBean(CommunicateWatches.this.ar);
                            CommunicateWatches.this.fiveMinStepsDataArray.add(CommunicateWatches.this.fiveMinStepsData);
                            if (ContecSdk.isDelete) {
                                CommunicateWatches.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(127));
                            }
                            else {
                                CommunicateWatches.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(126));
                            }
                            CommunicateWatches.this.sleep(500);
                            if (CommunicateWatches.this.fiveMinStepsDataArray.size() < CommunicateWatches.this.fiveMinStepsDataInfo) {
                                CommunicateWatches.this.writeBytes(ParseUtils.pieceInfoFiveMinStepsBytes(1));
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
                            this.resolveInputData(this.bytes, 1, 17);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.ecgData = DataClassesParseUtils.parseEcgData(this.bytes);
                            CommunicateWatches.this.ecgDataArray = new int[CommunicateWatches.this.ecgData.size];
                            CommunicateWatches.this.writeBytes(ParseUtils.g(0));
                            CommunicateWatches.this.errorCode = 9830656;
                            continue;
                        }
                        case -26: {
                            this.resolveInputData(this.bytes, 1, 16);
                            if (!running) {
                                return;
                            }
                            final int[] k = DataClassesParseUtils.j(this.bytes);
                            if (CommunicateWatches.this.dataPieceNumber == 10) {
                                CommunicateWatches.this.dataPieceNumber = 0;
                            }
                            if (null != CommunicateWatches.this.ecgDataArray && (CommunicateWatches.this.ae + 1) * 6 < CommunicateWatches.this.ecgDataArray.length) {
                                for (int l = CommunicateWatches.this.ae * 6; l < (CommunicateWatches.this.ae + 1) * 6; ++l) {
                                    CommunicateWatches.this.ecgDataArray[l] = k[l - CommunicateWatches.this.ae * 6];
                                }
                            }
                            else if (null != CommunicateWatches.this.ecgDataArray) {
                                for (int n2 = CommunicateWatches.this.ae * 6; n2 < CommunicateWatches.this.ecgDataArray.length; ++n2) {
                                    CommunicateWatches.this.ecgDataArray[n2] = k[n2 - CommunicateWatches.this.ae * 6];
                                }
                            }
                            if (CommunicateWatches.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                CommunicateWatches.this.sleep(100);
                                CommunicateWatches.this.ae -= CommunicateWatches.this.dataPieceNumber;
                                CommunicateWatches.this.dataPieceNumber = 10;
                                if (null != CommunicateWatches.this.inputBytes) {
                                    CommunicateWatches.this.inputBytes.clear();
                                }
                                CommunicateWatches.this.writeBytes(ParseUtils.g(2));
                                CommunicateWatches.this.errorCode = 9830656;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.dataPieceNumber++;
                            CommunicateWatches.this.ae++;
                            if (CommunicateWatches.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(1));
                                CommunicateWatches.this.errorCode = 9765120;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            if ((this.bytes[1] & 0x40) == 0x0 || CommunicateWatches.this.ae * 6 < CommunicateWatches.this.ecgData.size) {
                                continue;
                            }
                            CommunicateWatches.this.ecgData.uploadCount = CommunicateWatches.this.uploadCount;
                            CommunicateWatches.this.ecgData.currentCount = CommunicateWatches.this.currentCount;
                            CommunicateWatches.this.ecgData.ecgData = CommunicateWatches.this.ecgDataArray;
                            CommunicateWatches.this.dataPieceNumber = 0;
                            CommunicateWatches.this.ae = 0;
                            if (ContecSdk.isDelete) {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(127));
                            }
                            else {
                                CommunicateWatches.this.writeBytes(ParseUtils.g(126));
                            }
                            CommunicateWatches.this.sleep(500);
                            CommunicateWatches.this.onEachEcgDataResult(CommunicateWatches.this.ecgData);
                            if (CommunicateWatches.this.currentCount < CommunicateWatches.this.uploadCount) {
                                CommunicateWatches.this.currentCount++;
                                CommunicateWatches.this.writeBytes(ParseUtils.f(1));
                                CommunicateWatches.this.errorCode = 9765120;
                                CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                                continue;
                            }
                            CommunicateWatches.this.currentCount = 0;
                            CommunicateWatches.this.writeBytes(ParseUtils.i(0));
                            CommunicateWatches.this.errorCode = 10486016;
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            continue;
                        }
                        case -48: {
                            this.resolveInputData(this.bytes, 1, 13);
                            if (!running) {
                                return;
                            }
                            final int n3 = this.bytes[1] & 0x7;
                            CommunicateWatches.this.ag = DataClassesParseUtils.parseDateTimeString(this.bytes);
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
                            if (CommunicateWatches.this.spo2DataInfo != 0 || CommunicateWatches.this.dayStepsDataInfo != 0 || CommunicateWatches.this.fiveMinStepsDataInfo != 0 || CommunicateWatches.this.uploadCount != 0) {
                                CommunicateWatches.this.onDataResultEnd();
                            }
                            else {
                                CommunicateWatches.this.onDataResultEmpty();
                            }
                            CommunicateWatches.this.init();
                            continue;
                        }
                        case -47: {
                            this.resolveInputData(this.bytes, 1, 3);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.resetCommunicateErrorTimer();
                            if (this.bytes[1] == 0) {
                                CommunicateWatches.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(1));
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
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!running) {
                                return;
                            }
                            CommunicateWatches.this.c(this.bytes);
                            continue;
                        }
                        case -45: {
                            CommunicateWatches.this.startCommunicate(CommunicateWatches.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
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
        
        public void resolveInputData(final byte[] array, final int startIndex, final int endIndex) {
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
