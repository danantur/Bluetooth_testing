// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.base;

import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.communicate.ParseUtils;
import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.EcgData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.callback.CommunicateCallback;
import com.ideabus.mylibrary.code.callback.CommunicateFailCallback;
import com.ideabus.mylibrary.code.callback.ConnectCallback;
import com.ideabus.mylibrary.code.callback.DataStorageInfoCallback;
import com.ideabus.mylibrary.code.callback.DeleteDataCallback;
import com.ideabus.mylibrary.code.callback.GetStorageModeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeCallback;
import com.ideabus.mylibrary.code.callback.RealtimeSpO2Callback;
import com.ideabus.mylibrary.code.callback.SetCalorieCallback;
import com.ideabus.mylibrary.code.callback.SetHeightCallback;
import com.ideabus.mylibrary.code.callback.SetStepsTimeCallback;
import com.ideabus.mylibrary.code.callback.SetWeightCallback;
import com.ideabus.mylibrary.code.callback.StorageModeCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class CommunicateBase
{
    protected ConnectBase connectBase;
    protected boolean connected;
    protected SetStepsTimeCallback setStepsTimeCallback;
    protected SetWeightCallback setWeightCallback;
    protected SetHeightCallback setHeightCallback;
    protected SetCalorieCallback setCalorieCallback;
    protected CommunicateCallback communicateCallback;
    protected StorageModeCallback storageModeCallback;
    protected GetStorageModeCallback getStorageModeCallback;
    protected DataStorageInfoCallback dataStorageInfoCallback;
    protected DeleteDataCallback deleteDataCallback;
    protected RealtimeCallback realtimeCallback;
    protected RealtimeSpO2Callback realtimeSpO2Callback;
    protected int currentOperationCode;
    protected Timer communicateErrorTimer;
    protected Timer waveTimeoutTimer;
    protected Timer spo2TimeoutTimer;
    protected Timer realtimeDelayTimer;
    protected int errorCode;
    protected boolean communicating;
    protected boolean realtimeStarted;
    protected ConcurrentLinkedQueue<Byte> inputBytes;
    protected int y;
    protected int z;
    protected int A;
    protected int B;
    protected int C;
    protected int D;
    protected int E;
    protected int F;
    protected int G;
    protected int H;
    protected int I;
    protected int J;
    protected int K;
    protected int L;
    protected int M;
    protected int N;
    protected boolean O;
    protected int[] P;
    protected int[] Q;
    protected int[] R;
    protected int[] S;
    protected int[] T;
    protected int[] U;
    protected int[] V;
    protected int[] W;
    protected int[] X;
    protected int[] Y;
    protected int[] Z;
    protected int[] aa;
    protected int[] ab;
    protected int ac;
    protected int ad;
    protected int ae;
    protected int af;
    protected String ag;
    protected String ah;
    protected int ai;
    protected ArrayList<SpO2PointData> spo2PointDataArray;
    protected Timer ak;
    protected boolean al;
    protected int dataTypeInt;
    protected SystemParameter.DataStorageInfo storageInfo;
    protected ArrayList<DayStepsData> dayStepsData;
    protected ArrayList<FiveMinStepsData> fiveMinStepsDataArray;
    protected FiveMinStepsData fiveMinStepsData;
    protected short[] ar;
    protected EcgData as;
    protected int[] at;
    protected int au;
    
    public CommunicateBase() {
        this.connected = false;
        this.currentOperationCode = -1;
        this.errorCode = -1;
        this.communicating = false;
        this.realtimeStarted = false;
        this.inputBytes = new ConcurrentLinkedQueue<>();
        this.y = 0;
        this.z = 0;
        this.A = 0;
        this.B = 0;
        this.C = 0;
        this.D = 1;
        this.E = 10;
        this.F = 0;
        this.G = 0;
        this.H = 0;
        this.I = 0;
        this.J = 0;
        this.K = 1;
        this.L = 1;
        this.M = 1;
        this.N = 1;
        this.O = false;
        this.P = null;
        this.Q = null;
        this.R = null;
        this.S = null;
        this.T = null;
        this.U = null;
        this.V = null;
        this.W = null;
        this.X = null;
        this.Y = null;
        this.Z = null;
        this.aa = null;
        this.ab = null;
        this.ac = 0;
        this.ad = 0;
        this.ae = 0;
        this.af = 0;
        this.ag = "";
        this.ah = "";
        this.ai = 0;
        this.spo2PointDataArray = null;
        this.al = false;
        this.dataTypeInt = 0;
        this.storageInfo = null;
        this.dayStepsData = null;
        this.fiveMinStepsDataArray = null;
        this.fiveMinStepsData = null;
        this.ar = null;
        this.as = null;
        this.at = null;
        this.au = 1;
    }
    
    public void init() {
        this.errorCode = -1;
        this.communicating = false;
        this.realtimeStarted = false;
        this.D = 1;
        this.z = 0;
        this.A = 0;
        this.B = 0;
        this.C = 0;
        this.E = 10;
        this.F = 0;
        this.G = 0;
        this.H = 0;
        this.I = 0;
        this.J = 0;
        this.K = 1;
        this.L = 1;
        this.M = 1;
        this.N = 1;
        this.O = false;
        if (null != this.inputBytes) {
            this.inputBytes.clear();
        }
        this.ac = 0;
        this.ad = 0;
        this.ae = 0;
        this.af = 0;
        this.ag = "";
        this.ah = "";
        this.ai = 0;
        this.al = false;
    }
    
    public void startRealtime(final ConnectCallback connectCallback) {
        if (this.connectBase != null) {
            this.connectBase.connect(connectCallback);
        }
    }
    
    public void disconnect() {
        this.resetCommunicateErrorTimer();
        this.resetCommunicateTimer();
        this.resetWaveTimeoutTimer();
        if (this.connectBase != null) {
            this.connectBase.disconnect();
        }
    }
    
    public void writeBytes(final byte[] array) {
        if (this.connectBase != null) {
            this.connectBase.writeBytes(array);
        }
    }
    
    public abstract void startRealtime(final RealtimeCallback realtimeCallback);
    
    public abstract void startRealtime();
    
    public abstract void addBytesToParse(final byte[] bytes);
    
    public void setConnectBase(final ConnectBase connectBase) {
        this.connectBase = connectBase;
    }
    
    public void setConnected(final boolean connected) {
        this.connected = connected;
    }
    
    public boolean isConnected() {
        return this.connected;
    }

    public void resetCommunicateErrorTimer() {
        if (this.communicateErrorTimer != null) {
            this.communicateErrorTimer.cancel();
            this.communicateErrorTimer = null;
        }
    }
    
    public void resetCommunicateTimer() {
        if (this.waveTimeoutTimer != null) {
            this.waveTimeoutTimer.cancel();
            this.waveTimeoutTimer = null;
        }
    }
    
    public void resetWaveTimeoutTimer() {
        if (this.spo2TimeoutTimer != null) {
            this.spo2TimeoutTimer.cancel();
            this.spo2TimeoutTimer = null;
        }
    }

    public void resetRealtimeDelayTimer() {
        if (this.realtimeDelayTimer != null) {
            this.realtimeDelayTimer.cancel();
            this.realtimeDelayTimer = null;
        }
    }

    protected void sleep(final int secs) {
        try {
            Thread.sleep(secs);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setDataType(final SystemParameter.DataType dataType) {
        if (null == dataType) {
            return;
        }
        this.dataTypeInt = dataType.num;
    }
    
    public abstract void getStorageMode(final GetStorageModeCallback p0);
    
    public abstract void setStorageMode(final SystemParameter.StorageMode p0, final StorageModeCallback p1);
    
    public abstract void deleteData(final DeleteDataCallback p0);
    
    public void startCommunicate(final CommunicateCallback referent) {
        if (null != referent) {
            this.communicateCallback = new WeakReference<>(referent).get();
        }
        if (this.communicating) {
            return;
        }
        if (this.realtimeStarted) {
            return;
        }
        this.init();
        this.communicating = true;
        this.currentOperationCode = SdkConstants.OPERATE_GET_STORAGE_DATA;
        this.errorCode = 8454401;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        this.writeBytes(ParseUtils.b());
    }
    
    protected void onDataResultEmpty() {
        if (this.communicateCallback != null) {
            this.communicateCallback.onDataResultEmpty();
        }
    }
    
    protected void onPointSpO2DataResult(final ArrayList<SpO2PointData> list) {
        if (this.communicateCallback != null) {
            this.communicateCallback.onPointSpO2DataResult(list);
        }
    }
    
    protected void onDayStepsDataResult(final ArrayList<DayStepsData> list) {
        if (this.communicateCallback != null) {
            this.communicateCallback.onDayStepsDataResult(list);
        }
    }
    
    protected void onFiveMinStepsDataResult(final ArrayList<FiveMinStepsData> list) {
        if (this.communicateCallback != null) {
            this.communicateCallback.onFiveMinStepsDataResult(list);
        }
    }
    
    protected void onEachPieceDataResult(final PieceData pieceData) {
        if (this.communicateCallback != null) {
            this.communicateCallback.onEachPieceDataResult(pieceData);
        }
    }
    
    protected void onEachEcgDataResult(final EcgData ecgData) {
        if (this.communicateCallback != null) {
            this.communicateCallback.onEachEcgDataResult(ecgData);
        }
    }
    
    protected void onDataResultEnd() {
        if (this.communicateCallback != null) {
            this.communicateCallback.onDataResultEnd();
        }
    }
    
    public void setDataStorageInfo(final SystemParameter.DataStorageInfo storageInfo, final DataStorageInfoCallback referent) {
        if (this.communicating) {
            return;
        }
        this.storageInfo = storageInfo;
        if (referent != null) {
            this.dataStorageInfoCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_GET_STORAGE_INFO;
        switch (storageInfo) {
            case POINTDATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(0));
                this.errorCode = 9437440;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case DAYSTEPSINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(1));
                this.errorCode = 9437441;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case DAYFIVEMINUTESSTEPSINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(2));
                this.errorCode = 9437442;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case ECGDATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(3));
                this.errorCode = 9437443;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
            }
            case PULSEWAVEDATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(4));
                this.errorCode = 9437444;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case WITHSTORAGEINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(5));
                this.errorCode = 9437445;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case PIECESPO2DATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(6));
                this.errorCode = 9437446;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
        }
    }
    
    protected void setCommunicateErrorTimer(final CommunicateFailCallback communicateFailCallback) {
        this.resetCommunicateErrorTimer();
        if (this.communicateErrorTimer == null) {
            (this.communicateErrorTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (null != communicateFailCallback) {
                        communicateFailCallback.onFail(CommunicateBase.this.errorCode);
                        CommunicateBase.this.init();
                        CommunicateBase.this.communicateCallback = null;
                        CommunicateBase.this.getStorageModeCallback = null;
                        CommunicateBase.this.storageModeCallback = null;
                        CommunicateBase.this.deleteDataCallback = null;
                        CommunicateBase.this.dataStorageInfoCallback = null;
                    }
                }
            }, 5000L);
        }
    }
    
    protected void setWaveTimeout(final CommunicateFailCallback communicateFailCallback) {
        this.resetWaveTimeoutTimer();
        if (this.waveTimeoutTimer == null) {
            (this.waveTimeoutTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (null != communicateFailCallback) {
                        communicateFailCallback.onFail(SdkConstants.ERRORCODE_REALTIME_WAVE_TIMEOUT);
                        CommunicateBase.this.init();
                    }
                }
            }, 5000L);
        }
    }
    
    protected void setSpo2Timeout(final CommunicateFailCallback communicateFailCallback) {
        this.resetWaveTimeoutTimer();
        if (this.spo2TimeoutTimer == null) {
            (this.spo2TimeoutTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (null != communicateFailCallback) {
                        communicateFailCallback.onFail(SdkConstants.ERRORCODE_REALTIME_SPO2_TIMEOUT);
                        CommunicateBase.this.init();
                    }
                }
            }, 10000L);
        }
    }
    
    public abstract void startRealtimeSpo2(final RealtimeSpO2Callback spO2Callback);
    
    public abstract void setCalorie(final int p0, final int p1, final SystemParameter.StepsSensitivity p2, final SetCalorieCallback p3);
    
    public abstract void setWeight(final int weight, final SetWeightCallback p1);
    
    public abstract void setHeight(final int height, final SetHeightCallback p1);
    
    public abstract void setStepsTime(final int p0, final int p1, final SetStepsTimeCallback p2);
}