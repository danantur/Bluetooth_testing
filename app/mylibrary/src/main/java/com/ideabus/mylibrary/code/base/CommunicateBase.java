// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.base;

import android.util.Log;

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
import java.util.Arrays;
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
    protected int deviceVersion;
    protected int spo2DataInfo;
    protected int dayStepsDataInfo;
    protected int fiveMinStepsDataInfo;
    protected int uploadCount;
    protected int totalNumber;
    protected int dataPieceNumber;
    protected int errorCode2;
    protected int G;
    protected int H;
    protected int dataLength2;
    protected int dataLength;
    protected int K;
    protected int caseCount2;
    protected int dataConstant5;
    protected int caseCount;
    protected boolean supportPI;

    protected int[] spo2Data;
    protected int[] prData;
    protected int[] piData;

    protected int[] spo2DataOriginal;
    protected int[] prDataOriginal;
    protected int[] piDataOriginal;

    protected int[] spo2DataCode;
    protected int[] prDataCode;
    protected int[] piDataCode;

    protected int[] prData2;
    protected int[] spo2Data2;
    protected int[] piData2;

    protected int storageDataConstant;
    protected int dataConstant4;
    protected int dataPieceNumber2;
    protected int af;
    protected String startTime2;
    protected String startTime;
    protected int dataConstant2;
    protected ArrayList<SpO2PointData> spo2PointDataArray;
    protected Timer realtimePingTimer;
    protected boolean isDeleting;
    protected int dataTypeInt;
    protected SystemParameter.DataStorageInfo storageInfo;
    protected ArrayList<DayStepsData> dayStepsData;
    protected ArrayList<FiveMinStepsData> fiveMinStepsDataArray;
    protected FiveMinStepsData fiveMinStepsData;
    protected short[] ar;
    protected EcgData ecgData;
    protected int[] ecgDataArray;
    protected int currentCount;
    
    public CommunicateBase() {
        this.connected = false;
        this.currentOperationCode = -1;
        this.errorCode = -1;
        this.communicating = false;
        this.realtimeStarted = false;
        this.inputBytes = new ConcurrentLinkedQueue<>();
        this.deviceVersion = 0;
        this.spo2DataInfo = 0;
        this.dayStepsDataInfo = 0;
        this.fiveMinStepsDataInfo = 0;
        this.uploadCount = 0;
        this.totalNumber = 1;
        this.dataPieceNumber = 10;
        this.errorCode2 = 0;
        this.G = 0;
        this.H = 0;
        this.dataLength2 = 0;
        this.dataLength = 0;
        this.K = 1;
        this.caseCount2 = 1;
        this.dataConstant5 = 1;
        this.caseCount = 1;
        this.supportPI = false;
        this.spo2Data = null;
        this.prData = null;
        this.piData = null;
        this.spo2DataOriginal = null;
        this.prDataOriginal = null;
        this.piDataOriginal = null;
        this.spo2DataCode = null;
        this.prDataCode = null;
        this.piDataCode = null;
        this.prData2 = null;
        this.spo2Data2 = null;
        this.piData2 = null;
        this.storageDataConstant = 0;
        this.dataConstant4 = 0;
        this.dataPieceNumber2 = 0;
        this.af = 0;
        this.startTime2 = "";
        this.startTime = "";
        this.dataConstant2 = 0;
        this.spo2PointDataArray = null;
        this.isDeleting = false;
        this.dataTypeInt = 0;
        this.storageInfo = null;
        this.dayStepsData = null;
        this.fiveMinStepsDataArray = null;
        this.fiveMinStepsData = null;
        this.ar = null;
        this.ecgData = null;
        this.ecgDataArray = null;
        this.currentCount = 1;
    }
    
    public void init() {
        this.errorCode = -1;
        this.communicating = false;
        this.realtimeStarted = false;
        this.totalNumber = 1;
        this.spo2DataInfo = 0;
        this.dayStepsDataInfo = 0;
        this.fiveMinStepsDataInfo = 0;
        this.uploadCount = 0;
        this.dataPieceNumber = 10;
        this.errorCode2 = 0;
        this.G = 0;
        this.H = 0;
        this.dataLength2 = 0;
        this.dataLength = 0;
        this.K = 1;
        this.caseCount2 = 1;
        this.dataConstant5 = 1;
        this.caseCount = 1;
        this.supportPI = false;
        if (null != this.inputBytes) {
            this.inputBytes.clear();
        }
        this.storageDataConstant = 0;
        this.dataConstant4 = 0;
        this.dataPieceNumber2 = 0;
        this.af = 0;
        this.startTime2 = "";
        this.startTime = "";
        this.dataConstant2 = 0;
        this.isDeleting = false;
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
            Log.e("write", Arrays.toString(array));
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
        this.errorCode = SdkConstants.ERRORCODE_PRODUCT_ID_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
        this.writeBytes(ParseUtils.getProductIdBytes());
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
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_POINT_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case DAYSTEPSINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_DAY_STEPS_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case DAYFIVEMINUTESSTEPSINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(2));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_FIVE_MIN_STEPS_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case ECGDATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(3));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_ECG_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
            }
            case PULSEWAVEDATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(4));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_PULSE_WAVE_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case WITHSTORAGEINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(5));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_STORAGE_SPO2_TIMEOUT;
                this.setCommunicateErrorTimer(this.dataStorageInfoCallback);
                break;
            }
            case PIECESPO2DATAINFO: {
                this.writeBytes(ParseUtils.dataStorageBytes(6));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_SPO2_TIMEOUT;
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
