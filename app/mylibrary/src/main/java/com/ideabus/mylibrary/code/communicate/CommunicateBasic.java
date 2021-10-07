// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate;

import android.util.Log;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.bean.WaveData;
import com.ideabus.mylibrary.code.bean.b;
import com.ideabus.mylibrary.code.bean.c;
import com.ideabus.mylibrary.code.bean.d;
import com.ideabus.mylibrary.code.bean.e;
import com.ideabus.mylibrary.code.bean.Spo2Data;
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
import com.ideabus.mylibrary.code.tools.DataClassesParseUtils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.ideabus.mylibrary.code.tools.DataClassesParseUtils.parseSpo2Data;
import static com.ideabus.mylibrary.code.tools.DataClassesParseUtils.parseSpo2Data2;
import static com.ideabus.mylibrary.code.tools.DataClassesParseUtils.parseWaveData;

public class CommunicateBasic extends CommunicateBase
{
    private String deviceName;
    public ParseThread parseThread;
    private ArrayList<Integer> aA;
    private ArrayList<Integer> aB;
    private ArrayList<Integer> aC;
    private boolean aD;
    int aw;
    int ax;
    int ay;

    public CommunicateBasic(String deviceName) {
        this.deviceName = deviceName;
        this.parseThread = null;
        this.aA = new ArrayList<>();
        this.aB = new ArrayList<>();
        this.aC = new ArrayList<>();
        this.aD = false;
        this.aw = 0;
        this.ax = 0;
        this.ay = 0;
    }

    @Override
    public void disconnect() {
        this.resetRealtimeDelayTimer();
        this.resetPingTimer();
        this.stopParseThread();
        super.disconnect();
    }

    @Override
    public void setStorageMode(final SystemParameter.StorageMode storageMode, final StorageModeCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.storageModeCallback = new WeakReference<>(referent).get();
        }
        this.dataTypeInt = 0;
        this.currentOperationCode = SdkConstants.OPERATE_SET_STORAGE_MODE;
        this.errorCode2 = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_STORAGE_MODE_TIMEOUT;
        this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_STORAGE_MODE_TIMEOUT;
        this.setCommunicateErrorTimer(this.storageModeCallback);
        this.writeBytes(ParseUtils.setStorageModeBytes(storageMode));
    }

    @Override
    public void deleteData(final DeleteDataCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.deleteDataCallback = new WeakReference<>(referent).get();
        }
        this.isDeleting = true;
        this.currentOperationCode = SdkConstants.OPERATE_DELETE_DATA;
        if (ContecSdk.isRangeIDEmpty()) {
            this.errorCode = SdkConstants.ERRORCODE_DEVICE_STATE_TIMEOUT;
            this.setCommunicateErrorTimer(this.deleteDataCallback);
            this.writeBytes(ParseUtils.deleteDataBytes());
        }
        else {
            this.errorCode = SdkConstants.ERRORCODE_DEVICE_VERSION_TIMEOUT;
            this.setCommunicateErrorTimer(this.deleteDataCallback);
            this.writeBytes(ParseUtils.startRealtimeBytes());
        }
    }

    @Override
    public void startRealtimeSpo2(final RealtimeSpO2Callback spO2Callback) {
        if (this.communicating) {
            return;
        }
        if (null != spO2Callback) {
            this.realtimeSpO2Callback = new WeakReference<>(spO2Callback).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_START_REALTIME_SPO2;
        this.writeBytes(ParseUtils.startRealtimeBytes());
        this.errorCode = SdkConstants.ERRORCODE_DEVICE_VERSION_TIMEOUT;
        this.setCommunicateErrorTimer(this.realtimeSpO2Callback);
    }

    @Override
    public void setStepsTime(final int n, final int n2, final SetStepsTimeCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setStepsTimeCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_SET_STEPS_TIME;
        this.errorCode = SdkConstants.ERRORCODE_SET_STEPS_TIME_TIMEOUT;
        this.setCommunicateErrorTimer(this.setStepsTimeCallback);
        this.writeBytes(ParseUtils.setStepsTimeBytes(n, n2));
    }

    @Override
    public void setWeight(final int weight, final SetWeightCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setWeightCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_SET_WEIGHT;
        this.errorCode = SdkConstants.ERRORCODE_SET_WEIGHT;
        this.setCommunicateErrorTimer(this.setWeightCallback);
        this.writeBytes(ParseUtils.setWeightBytes(weight));
    }

    @Override
    public void setHeight(final int height, final SetHeightCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setHeightCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_SET_HEIGHT;
        this.errorCode = SdkConstants.ERRORCODE_SET_HEIGHT;
        this.setCommunicateErrorTimer(this.setHeightCallback);
        this.writeBytes(ParseUtils.setHeightBytes(height));
    }

    @Override
    public void setCalorie(final int n, final int n2, final SystemParameter.StepsSensitivity stepsSensitivity, final SetCalorieCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setCalorieCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_SET_CALORIE;
        this.errorCode = SdkConstants.ERRORCODE_SET_CALORIES;
        this.setCommunicateErrorTimer(this.setCalorieCallback);
        this.writeBytes(ParseUtils.setCalorieBytes(n, n2, stepsSensitivity));
    }

    @Override
    public void getStorageMode(final GetStorageModeCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.getStorageModeCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = SdkConstants.OPERATE_GET_STORAGE_MODE;
        this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_MODE_TIMEOUT;
        this.setCommunicateErrorTimer(this.getStorageModeCallback);
        this.writeBytes(ParseUtils.getStorageModeBytes());
    }

    @Override
    public void startRealtime(final RealtimeCallback realtimeCallback) {
        if (this.communicating) {
            return;
        }
        if (this.realtimeStarted) {
            return;
        }
        if (null != realtimeCallback) {
            this.realtimeCallback = new WeakReference<>(realtimeCallback).get();
        }
        this.realtimeStarted = true;
        this.currentOperationCode = SdkConstants.OPERATE_START_REALTIME;
        this.writeBytes(ParseUtils.startRealtimeBytes());
        this.errorCode = SdkConstants.ERRORCODE_DEVICE_VERSION_TIMEOUT;
        this.setCommunicateErrorTimer(this.realtimeCallback);
    }

    @Override
    public void startRealtime() {
        if (this.communicating) {
            return;
        }
        this.writeBytes(ParseUtils.startRealtimeBytes(127));
        this.errorCode = SdkConstants.ERRORCODE_REALTIME_END_TIMEOUT;
        if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME) {
            this.setCommunicateErrorTimer(this.realtimeCallback);
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME_SPO2) {
            this.setCommunicateErrorTimer(this.realtimeSpO2Callback);
        }
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

    private void onGetStorageDataSuccess() {
        if (this.storageDataConstant != 0) {
            if (this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.g) {
                this.stopParseThread();
                this.resetCommunicateErrorTimer();
                this.init();
                this.onDataResultEmpty();
                return;
            }
            if ((this.storageDataConstant & 0x1) == 0x1) {
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_POINT_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(0));
            }
            else if ((this.storageDataConstant & 0x2) == 0x2) {
                this.writeBytes(ParseUtils.dataStorageBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_DAY_STEPS_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else if ((this.storageDataConstant & 0x4) == 0x4) {
                this.writeBytes(ParseUtils.dataStorageBytes(2));
                this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else if ((this.storageDataConstant & 0x40) == 0x40) {
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_SPO2_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(6));
            }
        }
        else if (this.dataConstant4 != 0) {
            if (this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.f || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.e || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.d || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.h) {
                this.stopParseThread();
                this.resetCommunicateErrorTimer();
                this.onDataResultEmpty();
                this.init();
                return;
            }
            this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.g;
            if ((this.dataConstant4 & 0x1) == 0x1) {
                this.errorCode = 10486016;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.i(0));
            }
            else if ((this.dataConstant4 & 0x2) == 0x2) {
                this.errorCode = 10486017;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.i(1));
            }
        }
    }

    private void o(final byte[] array) {
        this.stopParseThread();
        this.resetCommunicateErrorTimer();
        this.init();
        if (array[2] == 0) {
            if (this.communicateCallback != null) {
                this.communicateCallback.onDeleteSuccess();
            }
            if (this.deleteDataCallback != null) {
                this.deleteDataCallback.onSuccess();
            }
        }
        else {
            if (this.communicateCallback != null) {
                this.communicateCallback.onDeleteFail();
            }
            if (this.deleteDataCallback != null) {
                this.deleteDataCallback.onFail(1);
            }
        }
    }

    protected void onDataConstantChange() {
        if (this.dataConstant < 11) {
            if (this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                this.resetCommunicateErrorTimer();
                this.stopParseThread();
                this.init();
                if (this.deleteDataCallback != null) {
                    this.deleteDataCallback.onFail(SdkConstants.ERRORCODE_OPERATION_NO_SUPPORT);
                }
            }
            else if (this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_POINT_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(0));
            }
            else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME) {
                this.writeBytes(ParseUtils.startRealtimeBytes(0));
                this.setWaveTimeout(this.realtimeCallback);
                this.sleep(1000);
                this.writeBytes(ParseUtils.startRealtimeBytes(1));
                this.setSpo2Timeout(this.realtimeCallback);
                if (this.realtimePingTimer == null) {
                    (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            CommunicateBasic.this.writeBytes(ParseUtils.realtimePingBytes());
                        }
                    }, 5500L, 4500L);
                }
            }
            else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME_SPO2) {
                this.writeBytes(ParseUtils.startRealtimeBytes(1));
                this.errorCode = 10158337;
                this.setRealtimeDelayTimer();
                if (this.realtimePingTimer == null) {
                    (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            CommunicateBasic.this.writeBytes(ParseUtils.realtimePingBytes());
                        }
                    }, 5500L, 4500L);
                }
            }
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
            this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_MODE_TIMEOUT;
            this.setCommunicateErrorTimer(this.deleteDataCallback);
            this.writeBytes(ParseUtils.getStorageModeBytes());
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
            this.errorCode2 = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_CLOSE_STORAGE_TIMEOUT;
            this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_CLOSE_STORAGE_TIMEOUT;
            this.setCommunicateErrorTimer(this.communicateCallback);
            this.writeBytes(ParseUtils.someBytes());
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME) {
            this.writeBytes(ParseUtils.startRealtimeBytes(0));
            this.setWaveTimeout(this.realtimeCallback);
            this.sleep(1000);
            this.writeBytes(ParseUtils.startRealtimeBytes(1));
            this.setSpo2Timeout(this.realtimeCallback);
            if (this.realtimePingTimer == null) {
                (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CommunicateBasic.this.writeBytes(ParseUtils.realtimePingBytes());
                    }
                }, 5500L, 4500L);
            }
        }
        else if (this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME_SPO2) {
            this.writeBytes(ParseUtils.startRealtimeBytes(1));
            this.errorCode = SdkConstants.ERRORCODE_REALTIME_SPO2_TIMEOUT;
            this.setRealtimeDelayTimer();
            if (this.realtimePingTimer == null) {
                (this.realtimePingTimer = new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CommunicateBasic.this.writeBytes(ParseUtils.realtimePingBytes());
                    }
                }, 5500L, 4500L);
            }
        }
    }

    protected void l() {
        this.spo2Data = new int[this.dataLength];
        this.writeBytes(ParseUtils.a(1, 1, this.M, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void m() {
        this.S = new int[this.dataLength];
        this.writeBytes(ParseUtils.b(3, 1, this.M, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void n() {
        this.V = new int[this.dataLength];
        this.writeBytes(ParseUtils.b(4, 1, this.M, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void c(final byte[] array) {
        final short[] n = DataClassesParseUtils.n(array);
        if (this.spo2Data != null && (this.ae + 1) * 27 < this.spo2Data.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.spo2Data[i] = (n[i - this.ae * 27] & 0x7F);
            }
        }
        else if (this.spo2Data != null) {
            for (int j = this.ae * 27; j < this.spo2Data.length; ++j) {
                this.spo2Data[j] = (n[j - this.ae * 27] & 0x7F);
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.dataLength) {
            this.ae = 0;
            this.prData = new int[this.dataLength];
            this.writeBytes(ParseUtils.a(1, 2, this.M, this.caseCount, 0));
            this.errorCode = 10289410;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
    }

    protected void d(final byte[] array) {
        final short[] n = DataClassesParseUtils.n(array);
        if (this.prData != null && (this.ae + 1) * 27 < this.prData.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.prData[i] = n[i - this.ae * 27];
            }
        }
        else if (this.prData != null) {
            for (int j = this.ae * 27; j < this.prData.length; ++j) {
                this.prData[j] = n[j - this.ae * 27];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.dataLength) {
            this.ae = 0;
            if (this.supportPI) {
                this.piData = new int[this.dataLength];
                this.writeBytes(ParseUtils.a(1, 3, this.M, this.caseCount, 0));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                final d d = new d();
                this.fillData(d);
                this.onEachPieceDataResult(d);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                    this.setCommunicateErrorTimer(this.communicateCallback);
                }
                else {
                    this.s();
                }
            }
        }
    }

    protected void e(final byte[] array) {
        final short[] n = DataClassesParseUtils.n(array);
        if (this.piData != null && (this.ae + 1) * 27 < this.piData.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.piData[i] = n[i - this.ae * 27];
            }
        }
        else if (this.piData != null) {
            for (int j = this.ae * 27; j < this.piData.length; ++j) {
                this.piData[j] = n[j - this.ae * 27];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.dataLength) {
            this.ae = 0;
            final d d = new d();
            this.fillData(d);
            this.onEachPieceDataResult(d);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    protected void f(final byte[] array) {
        final short[] o = DataClassesParseUtils.o(array);
        if (this.S != null && (this.ae + 1) * 21 < this.S.length) {
            for (int i = this.ae * 21; i < (this.ae + 1) * 21; ++i) {
                this.S[i] = o[i - this.ae * 21];
            }
        }
        else if (this.S != null) {
            for (int j = this.ae * 21; j < this.S.length; ++j) {
                this.S[j] = o[j - this.ae * 21];
            }
        }
        ++this.ae;
        if (this.ae * 21 >= this.dataLength) {
            this.dataPieceNumber = 0;
            this.ae = 0;
            this.T = new int[this.dataLength];
            this.writeBytes(ParseUtils.b(3, 2, this.M, this.caseCount, 0));
            this.errorCode = 10289922;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }

    protected void g(final byte[] array) {
        final short[] o = DataClassesParseUtils.o(array);
        if (this.T != null && (this.ae + 1) * 21 < this.T.length) {
            for (int i = this.ae * 21; i < (this.ae + 1) * 21; ++i) {
                this.T[i] = o[i - this.ae * 21];
            }
        }
        else if (this.T != null) {
            for (int j = this.ae * 21; j < this.T.length; ++j) {
                this.T[j] = o[j - this.ae * 21];
            }
        }
        ++this.ae;
        if (this.ae * 21 >= this.dataLength) {
            this.dataPieceNumber = 0;
            this.ae = 0;
            if (this.supportPI) {
                this.U = new int[this.dataLength];
                this.writeBytes(ParseUtils.b(3, 3, this.M, this.caseCount, 0));
                this.errorCode = 10289923;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                final e e = new e();
                this.fillData(e);
                this.onEachPieceDataResult(e);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = 10223872;
                    this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                }
                else {
                    this.s();
                }
            }
        }
    }

    protected void h(final byte[] array) {
        final short[] o = DataClassesParseUtils.o(array);
        if (this.U != null && (this.ae + 1) * 21 < this.U.length) {
            for (int i = this.ae * 21; i < (this.ae + 1) * 21; ++i) {
                this.U[i] = o[i - this.ae * 21];
            }
        }
        else if (this.T != null) {
            for (int j = this.ae * 21; j < this.U.length; ++j) {
                this.U[j] = o[j - this.ae * 21];
            }
        }
        ++this.ae;
        if (this.ae * 21 >= this.dataLength) {
            this.ae = 0;
            final e e = new e();
            this.fillData(e);
            this.onEachPieceDataResult(e);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    public void z(final byte[] array, final ArrayList<Integer> list) {
        final byte[] p2 = DataClassesParseUtils.p(array);
        for (int i = 8; i < 29; ++i) {
            if ((p2[i] & 0xF0) == 0xF0) {
                if (i + 1 < p2.length) {
                    if ((p2[i + 1] & 0xF0) == 0xF0) {
                        this.aw = (p2[i] & 0xF & 0xFF);
                        this.ax = (p2[i + 1] & 0xF & 0xFF);
                        if (((this.aw << 4 | this.ax) & 0xFF) != -1) {
                            this.ay = ((this.aw << 4 | this.ax) & 0xFF);
                        }
                        ++i;
                    }
                    else if (i == 0 && this.aD) {
                        this.ax = (p2[i] & 0xF & 0xFF);
                        if (((this.aw << 4 | this.ax) & 0xFF) != -1) {
                            this.ay = ((this.aw << 4 | this.ax) & 0xFF);
                        }
                        this.aD = false;
                    }
                }
                else {
                    this.aD = true;
                    this.aw = (p2[i] & 0xF & 0xFF);
                }
            }
            else {
                list.add(this.ay - (p2[i] >> 4 & 0xF) & 0xFF);
                if ((p2[i] & 0xF & 0xFF) != 0xF) {
                    list.add(this.ay - (p2[i] & 0xF) & 0xFF);
                }
            }
        }
    }

    protected void i(final byte[] array) {
        this.z(array, this.aA);
        ++this.ae;
        if (this.aA.size() >= this.dataLength) {
            for (int i = 0; i < this.V.length; ++i) {
                this.V[i] = this.aA.get(i);
            }
            this.ae = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            if (null != this.aA) {
                this.aA.clear();
            }
            this.dataPieceNumber = 0;
            this.W = new int[this.dataLength];
            this.writeBytes(ParseUtils.b(4, 2, this.M, this.caseCount, 0));
            this.errorCode = 10290178;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }

    protected void j(final byte[] array) {
        this.z(array, this.aB);
        ++this.ae;
        if (this.aB.size() >= this.dataLength) {
            for (int i = 0; i < this.V.length; ++i) {
                this.W[i] = this.aB.get(i);
            }
            this.ae = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            if (null != this.aB) {
                this.aB.clear();
            }
            if (this.supportPI) {
                this.X = new int[this.dataLength];
                this.writeBytes(ParseUtils.b(4, 3, this.M, this.caseCount, 0));
                this.errorCode = 10290179;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                final c c = new c();
                this.fillData(c);
                this.onEachPieceDataResult(c);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = 10223872;
                    this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                }
                else {
                    this.s();
                }
            }
        }
    }

    protected void k(final byte[] array) {
        this.z(array, this.aC);
        ++this.ae;
        if (this.aC.size() >= this.dataLength) {
            for (int i = 0; i < this.X.length; ++i) {
                this.X[i] = this.aC.get(i);
            }
            this.ae = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            if (null != this.aC) {
                this.aC.clear();
            }
            final c c = new c();
            this.fillData(c);
            this.onEachPieceDataResult(c);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    protected void l(final byte[] array) {
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
            if ((this.dataConstant4 & 0x2) == 0x2) {
                this.writeBytes(ParseUtils.i(1));
                this.errorCode = 10486017;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else if ((this.dataConstant4 & 0x10) == 0x10) {
                this.writeBytes(ParseUtils.i(4));
                this.errorCode = 10486020;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.r();
            }
        }
    }

    protected void m(final byte[] array) {
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
            if ((this.dataConstant4 & 0x10) == 0x10) {
                this.supportPI = true;
                this.writeBytes(ParseUtils.i(4));
                this.errorCode = 10486020;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.r();
            }
        }
    }

    protected void n(final byte[] array) {
        final short[] m = DataClassesParseUtils.m(array);
        if (this.ab != null && (this.ae + 1) * 27 < this.ab.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.ab[i] = m[i - this.ae * 27];
            }
        }
        else if (this.ab != null) {
            for (int j = this.ae * 27; j < this.ab.length; ++j) {
                this.ab[j] = m[j - this.ae * 27];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.I) {
            this.H = 0;
            this.ae = 0;
            this.r();
        }
    }

    private void r() {
        final com.ideabus.mylibrary.code.bean.b b = new com.ideabus.mylibrary.code.bean.b();
        this.fillData(b);
        this.onEachPieceDataResult(b);
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(0));
            this.errorCode = 10486016;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
        else {
            this.stopParseThread();
            this.resetCommunicateErrorTimer();
            this.init();
        }
        this.onDataResultEnd();
    }

    private void fillData(final PieceData pieceData) {
        pieceData.dataType = this.dataTypeInt;
        pieceData.totalNumber = this.totalNumber;
        pieceData.caseCount = this.caseCount;
        pieceData.supportPI = this.supportPI ? 1 : 0;
        int[] spo2Data = null;
        int[] prData = null;
        int[] piData = null;
        if (pieceData instanceof d) {
            pieceData.length = this.dataLength;
            pieceData.startTime = this.startTime;
            spo2Data = new int[this.dataLength];
            prData = new int[this.dataLength];
            System.arraycopy(this.spo2Data, 0, spo2Data, 0, this.dataLength);
            System.arraycopy(this.prData, 0, prData, 0, this.dataLength);
            if (this.piData != null) {
                piData = new int[this.dataLength];
                System.arraycopy(this.piData, 0, piData, 0, this.dataLength);
            }
        }
        else if (pieceData instanceof e) {
            pieceData.length = this.dataLength;
            pieceData.startTime = this.startTime;
            spo2Data = new int[this.dataLength];
            prData = new int[this.dataLength];
            System.arraycopy(this.S, 0, spo2Data, 0, this.dataLength);
            System.arraycopy(this.T, 0, prData, 0, this.dataLength);
            if (this.U != null) {
                piData = new int[this.dataLength];
                System.arraycopy(this.U, 0, piData, 0, this.dataLength);
            }
        }
        else if (pieceData instanceof c) {
            pieceData.length = this.dataLength;
            pieceData.startTime = this.startTime;
            spo2Data = new int[this.dataLength];
            prData = new int[this.dataLength];
            System.arraycopy(this.V, 0, spo2Data, 0, this.dataLength);
            System.arraycopy(this.W, 0, prData, 0, this.dataLength);
            if (this.X != null) {
                piData = new int[this.dataLength];
                System.arraycopy(this.X, 0, piData, 0, this.dataLength);
            }
        }
        else if (pieceData instanceof b) {
            pieceData.length = this.I;
            pieceData.startTime = this.ag;
            spo2Data = new int[this.I];
            prData = new int[this.I];
            System.arraycopy(this.Z, 0, spo2Data, 0, this.I);
            System.arraycopy(this.Y, 0, prData, 0, this.I);
            if (this.ab != null) {
                piData = new int[this.I];
                System.arraycopy(this.ab, 0, piData, 0, this.I);
            }
        }
        pieceData.spo2Data = spo2Data;
        pieceData.prData = prData;
        pieceData.piData = piData;
    }

    private void s() {
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.deletePieceOfDataBytes());
            this.errorCode = 10321791;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
        else {
            this.stopParseThread();
            this.resetCommunicateErrorTimer();
            this.init();
        }
        this.onDataResultEnd();
    }

    private void stopParseThread() {
        if (null != this.inputBytes) {
            this.inputBytes.clear();
        }
        if (this.parseThread != null) {
            this.parseThread.end();
            this.parseThread = null;
        }
    }

    public void resetPingTimer() {
        if (this.realtimePingTimer != null) {
            this.realtimePingTimer.cancel();
            this.realtimePingTimer = null;
        }
    }

    protected void setRealtimeDelayTimer() {
        if (this.realtimeDelayTimer == null) {
            (this.realtimeDelayTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateBasic.this.writeBytes(ParseUtils.startRealtimeBytes(1));
                    CommunicateBasic.this.setSpo2Timeout(CommunicateBasic.this.realtimeSpO2Callback);
                }
            }, 10000L);
        }
    }

    @Override
    public void resetRealtimeDelayTimer() {
        if (this.realtimeDelayTimer != null) {
            this.realtimeDelayTimer.cancel();
            this.realtimeDelayTimer = null;
        }
    }

    public class ParseThread extends Thread
    {
        private ConcurrentLinkedQueue<Byte> inputBytes;
        private boolean isParsing;
        private byte[] bytes;

        public ParseThread(final ConcurrentLinkedQueue<Byte> bytes) {
            this.inputBytes = null;
            this.isParsing = false;
            this.bytes = new byte[128];
            this.inputBytes = bytes;
            this.isParsing = true;
        }

        @Override
        public void run() {
            while (this.isParsing) {
                if (null != this.inputBytes && !this.inputBytes.isEmpty()) {
                    this.resolveInputData(this.bytes, 0, 1);
                    if (bytes[0] != - 21 || bytes[1] != 0)
                        Log.e("read_bytes", bytes[0] + " " + bytes[1]);
                    if (!this.isParsing) {
                        return;
                    }
                    switch (this.bytes[0]) {
                        case -21: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 0) {
                                this.resolveInputData(this.bytes, 2, 4);
                                if (!this.isParsing) {
                                    return;
                                }
                                CommunicateBasic.this.setWaveTimeout(CommunicateBasic.this.realtimeCallback);
                                final WaveData waveData = parseWaveData(this.bytes);
                                if (CommunicateBasic.this.realtimeCallback == null) {
                                    continue;
                                }
                                CommunicateBasic.this.realtimeCallback.
                                        onRealtimeWaveData(
                                                waveData.signal,
                                                waveData.prSound,
                                                waveData.waveData,
                                                waveData.barData,
                                                waveData.fingerOut
                                        );
                                continue;
                            }
                            else if (this.bytes[1] == 1) {
                                this.resolveInputData(this.bytes, 2, 6);
                                if (!this.isParsing) {
                                    return;
                                }
                                CommunicateBasic.this.resetRealtimeDelayTimer();
                                Spo2Data spo2Data;
                                if (CommunicateBasic.this.dataConstant < 11) {
                                    spo2Data = parseSpo2Data(this.bytes);
                                }
                                else {
                                    spo2Data = parseSpo2Data2(this.bytes);
                                }
                                if (CommunicateBasic.this.realtimeCallback != null) {
                                    CommunicateBasic.this.setSpo2Timeout(CommunicateBasic.this.realtimeCallback);
                                    CommunicateBasic.this.realtimeCallback.onSpo2Data(spo2Data.piError, spo2Data.spo2, spo2Data.pr, spo2Data.pi);
                                }
                                if (CommunicateBasic.this.realtimeSpO2Callback == null) {
                                    continue;
                                }
                                CommunicateBasic.this.setSpo2Timeout(CommunicateBasic.this.realtimeSpO2Callback);
                                CommunicateBasic.this.realtimeSpO2Callback.onRealtimeSpo2Data(spo2Data.pr, spo2Data.spo2, spo2Data.pi);
                                continue;
                            }
                            else {
                                if (this.bytes[1] != 127) {
                                    continue;
                                }
                                this.resolveInputData(this.bytes, 2, 1);
                                if (!this.isParsing) {
                                    return;
                                }
                                CommunicateBasic.this.realtimeStarted = false;
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetPingTimer();
                                CommunicateBasic.this.resetRealtimeDelayTimer();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.resetCommunicateTimer();
                                CommunicateBasic.this.resetWaveTimeoutTimer();
                                if (CommunicateBasic.this.realtimeCallback != null) {
                                    CommunicateBasic.this.realtimeCallback.onRealtimeEnd();
                                }
                                if (CommunicateBasic.this.realtimeSpO2Callback != null) {
                                    CommunicateBasic.this.realtimeSpO2Callback.onRealtimeSpo2End();
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -16: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetRealtimeDelayTimer();
                            CommunicateBasic.this.resetPingTimer();
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.init();
                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_COMMAND_NO_SUPPORT;
                            if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                if (CommunicateBasic.this.communicateCallback != null) {
                                    CommunicateBasic.this.communicateCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_MODE) {
                                if (CommunicateBasic.this.getStorageModeCallback != null) {
                                    CommunicateBasic.this.getStorageModeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_SET_STORAGE_MODE) {
                                if (CommunicateBasic.this.storageModeCallback != null) {
                                    CommunicateBasic.this.storageModeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_INFO) {
                                if (CommunicateBasic.this.dataStorageInfoCallback != null) {
                                    CommunicateBasic.this.dataStorageInfoCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME) {
                                if (CommunicateBasic.this.realtimeCallback != null) {
                                    CommunicateBasic.this.realtimeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                if (CommunicateBasic.this.deleteDataCallback != null) {
                                    CommunicateBasic.this.deleteDataCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_START_REALTIME_SPO2) {
                                if (CommunicateBasic.this.realtimeSpO2Callback != null) {
                                    CommunicateBasic.this.realtimeSpO2Callback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_SET_STEPS_TIME) {
                                if (CommunicateBasic.this.setStepsTimeCallback != null) {
                                    CommunicateBasic.this.setStepsTimeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_SET_WEIGHT) {
                                if (CommunicateBasic.this.setWeightCallback != null) {
                                    CommunicateBasic.this.setWeightCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_SET_HEIGHT) {
                                if (CommunicateBasic.this.setHeightCallback != null) {
                                    CommunicateBasic.this.setHeightCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_SET_CALORIE && CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -15: {
                            this.resolveInputData(this.bytes, 1, 9);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = 8585475;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.currentDateTimeBytes());
                            continue;
                        }
                        case -13: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = 8519938;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.startRealtimeBytes());
                            continue;
                        }
                        case -14: {
                            this.resolveInputData(this.bytes, 1, 7);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.dataConstant = (this.bytes[6] & 0x7F);
                            CommunicateBasic.this.onDataConstantChange();
                            continue;
                        }
                        case -12:
                        case -11: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.stopParseThread();
                            CommunicateBasic.this.init();
                            if (this.bytes[1] == 0) {
                                if (CommunicateBasic.this.setWeightCallback != null) {
                                    CommunicateBasic.this.setWeightCallback.onSuccess();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.setWeightCallback != null) {
                                    CommunicateBasic.this.setWeightCallback.onFail(1);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -6: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.stopParseThread();
                            CommunicateBasic.this.init();
                            if (this.bytes[1] == 0) {
                                if (CommunicateBasic.this.setHeightCallback != null) {
                                    CommunicateBasic.this.setHeightCallback.onSuccess();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.setHeightCallback != null) {
                                    CommunicateBasic.this.setHeightCallback.onFail(1);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -5: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.stopParseThread();
                            CommunicateBasic.this.init();
                            if (this.bytes[1] == 0) {
                                if (CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onSuccess();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onFail(SdkConstants.ERRORCODE_FAIL);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -2: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 9) {
                                this.resolveInputData(this.bytes, 2, 9);
                                if (!this.isParsing) {
                                    return;
                                }
                                final byte[] q = DataClassesParseUtils.q(this.bytes);
                                Log.e("debug", Arrays.toString(q));
                                final byte[] array = new byte[7];
                                CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_DEVICE_CHECK_TIMEOUT;
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                }
                                CommunicateBasic.this.writeBytes(ParseUtils.b(array));
                                CommunicateBasic.this.errorCode2 = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_STORAGE_DEVICE_CHECK_TIMEOUT;
                                continue;
                            }
                            else if (this.bytes[1] == 7) {
                                this.resolveInputData(this.bytes, 2, 3);
                                if (!this.isParsing) {
                                    return;
                                }
                                final int storageMode = this.bytes[2] & 0x7F;
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    if (storageMode == 0) {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DELETE_TIMEOUT;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.deletePieceOfDataBytes());
                                    }
                                    else if (storageMode == 1) {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DEVICE_STATE_TIMEOUT;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.deleteDataBytes());
                                    }
                                    else {
                                        if (storageMode != 2) {
                                            continue;
                                        }
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.stopParseThread();
                                        CommunicateBasic.this.init();
                                        CommunicateBasic.this.deleteDataCallback.onFail(SdkConstants.ERRORCODE_OPERATION_NO_SUPPORT);
                                    }
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != SdkConstants.OPERATE_GET_STORAGE_MODE) {
                                        continue;
                                    }
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.stopParseThread();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.getStorageModeCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.getStorageModeCallback.onSuccess(storageMode);
                                }
                                continue;
                            }
                            else {
                                if (this.bytes[1] != 6) {
                                    continue;
                                }
                                this.resolveInputData(this.bytes, 2, 5);
                                if (!this.isParsing) {
                                    return;
                                }
                                CommunicateBasic.this.dataConstant2 = (this.bytes[2] & 0xFF);
                                CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                CommunicateBasic.this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                                continue;
                            }
                        }
                        case -1: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            if (CommunicateBasic.this.errorCode2 == SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_CLOSE_STORAGE_TIMEOUT) {
                                CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DEVICE_STATE_TIMEOUT;
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                CommunicateBasic.this.isDeleting = true;
                                CommunicateBasic.this.writeBytes(ParseUtils.deleteDataBytes());
                                continue;
                            }
                            if (CommunicateBasic.this.errorCode2 == SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_STORAGE_MODE_TIMEOUT) {
                                if (null == CommunicateBasic.this.storageModeCallback) {
                                    continue;
                                }
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (this.bytes[1] == 0) {
                                    CommunicateBasic.this.storageModeCallback.onSuccess();
                                    continue;
                                }
                                CommunicateBasic.this.storageModeCallback.onFail(SdkConstants.ERRORCODE_FAIL);
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.errorCode2 != SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_SET_STORAGE_DEVICE_CHECK_TIMEOUT) {
                                    continue;
                                }
                                if (this.bytes[1] == 0) {
                                    if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DELETE_TIMEOUT;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.deletePieceOfDataBytes());
                                        continue;
                                    }
                                    if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        CommunicateBasic.this.onGetStorageDataSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        if (CommunicateBasic.this.communicateCallback != null) {
                                            CommunicateBasic.this.communicateCallback.onFail(SdkConstants.ERRORCODE_FAIL);
                                            continue;
                                        }
                                        continue;
                                    }
                                    else {
                                        if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA && CommunicateBasic.this.deleteDataCallback != null) {
                                            CommunicateBasic.this.deleteDataCallback.onFail(SdkConstants.ERRORCODE_FAIL);
                                            continue;
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                        case -17: {
                            this.resolveInputData(this.bytes, 1, 7);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.storageDataConstant = (this.bytes[3] & 0x7F);
                            CommunicateBasic.this.dataConstant4 = (this.bytes[5] & 0x7F);
                            if ((this.bytes[1] & 0x1) == 0x1) {
                                CommunicateBasic.this.init();
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                continue;
                            }
                            if ((this.bytes[2] & 0x1) == 0x1) {
                                if (!CommunicateBasic.this.isDeleting) {
                                    continue;
                                }
                                CommunicateBasic.this.isDeleting = false;
                                if (ContecSdk.isRangeIDEmpty()) {
                                    CommunicateBasic.this.errorCode2 = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_DEVICE_CHECK_TIMEOUT;
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_DEVICE_CHECK_TIMEOUT;
                                    if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    }
                                    else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    }
                                    CommunicateBasic.this.writeBytes(ParseUtils.m());
                                    continue;
                                }
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PR_TIMEOUT;
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(0));
                                    continue;
                                }
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                    CommunicateBasic.this.onGetStorageDataSuccess();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (!CommunicateBasic.this.isDeleting) {
                                    continue;
                                }
                                CommunicateBasic.this.isDeleting = false;
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    if (CommunicateBasic.this.deleteDataCallback != null) {
                                        CommunicateBasic.this.deleteDataCallback.onSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        CommunicateBasic.this.onDataResultEmpty();
                                        continue;
                                    }
                                    continue;
                                }
                            }
                        }
                        case -32: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            if ((this.bytes[1] & 0x7) == 0x6) {
                                this.resolveInputData(this.bytes, 2, 13);
                                if (!this.isParsing) {
                                    return;
                                }
                                final int totalNumber = (this.bytes[2] & 0x7F) | ((this.bytes[3] & 0x7F) << 7 & 0xFFFF);
                                CommunicateBasic.this.totalNumber = totalNumber;
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_INFO) {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.stopParseThread();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.dataStorageInfoCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataStorageInfoCallback.onSuccess(SystemParameter.DataStorageInfo.PIECESPO2DATAINFO, totalNumber);
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        continue;
                                    }
                                    if (CommunicateBasic.this.totalNumber == 0) {
                                        CommunicateBasic.this.stopParseThread();
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.onDataResultEmpty();
                                        CommunicateBasic.this.init();
                                    }
                                    else {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_PIECE_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.getStoragePieceBytes());
                                    }
                                }
                                continue;
                            }
                            else {
                                this.resolveInputData(this.bytes, 2, 5);
                                if (!this.isParsing) {
                                    return;
                                }
                                final int dataStorageInfo = (this.bytes[2] & 0x7F) | ((this.bytes[3] & 0x7F) << 7 & 0xFFFF);
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_INFO) {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.stopParseThread();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.dataStorageInfoCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataStorageInfoCallback.onSuccess(SystemParameter.DataStorageInfo.POINTDATAINFO, dataStorageInfo);
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                        continue;
                                    }
                                    if (dataStorageInfo == 0) {
                                        CommunicateBasic.this.stopParseThread();
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.init();
                                    }
                                    else if ((this.bytes[1] & 0x7) == 0x0) {
                                        CommunicateBasic.this.spo2DataInfo = dataStorageInfo;
                                        CommunicateBasic.this.spo2PointDataArray = (ArrayList<SpO2PointData>)new ArrayList();
                                        CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.h;
                                        CommunicateBasic.this.writeBytes(ParseUtils.pointSpo2Bytes(0));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_POINT_SPO2_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else if ((this.bytes[1] & 0x7) == 0x1) {
                                        CommunicateBasic.this.dayStepsDataInfo = dataStorageInfo;
                                        CommunicateBasic.this.dayStepsData = (ArrayList<DayStepsData>)new ArrayList();
                                        CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(0));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DAY_STEPS_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.bytes[1] & 0x7) != 0x2) {
                                            continue;
                                        }
                                        CommunicateBasic.this.fiveMinStepsDataInfo = dataStorageInfo;
                                        CommunicateBasic.this.fiveMinStepsDataArray = (ArrayList<FiveMinStepsData>)new ArrayList();
                                        CommunicateBasic.this.writeBytes(ParseUtils.pieceInfoFiveMinStepsBytes(1));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_FIVE_MIN_STEPS_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                }
                                continue;
                            }
                        }
                        case -31: {
                            this.resolveInputData(this.bytes, 1, 10);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] != 126 && this.bytes[1] != 127) {
                                CommunicateBasic.this.spo2PointDataArray.add(DataClassesParseUtils.parseSpo2Point(this.bytes));
                                if (CommunicateBasic.this.dataPieceNumber == 10) {
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                }
                                if (CommunicateBasic.this.dataPieceNumber == (this.bytes[1] & 0xF)) {
                                    CommunicateBasic.this.dataPieceNumber++;
                                    if (CommunicateBasic.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_POINT_SPO2_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.pointSpo2Bytes(1));
                                    }
                                    else {
                                        if ((this.bytes[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.pointSpo2Bytes(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.pointSpo2Bytes(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.dataPieceNumber = 0;
                                        if ((CommunicateBasic.this.storageDataConstant & 0x2) == 0x2) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(1));
                                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO_DAY_STEPS_TIMEOUT;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                        }
                                        else if ((CommunicateBasic.this.storageDataConstant & 0x4) == 0x4) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                        }
                                        else {
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                            CommunicateBasic.this.stopParseThread();
                                            CommunicateBasic.this.resetCommunicateErrorTimer();
                                            CommunicateBasic.this.init();
                                            CommunicateBasic.this.onDataResultEnd();
                                            if (!ContecSdk.isDelete || CommunicateBasic.this.communicateCallback == null) {
                                                continue;
                                            }
                                            CommunicateBasic.this.communicateCallback.onDeleteSuccess();
                                        }
                                    }
                                }
                                else {
                                    CommunicateBasic.this.sleep(100);
                                    if (this.inputBytes != null) {
                                        this.inputBytes.clear();
                                    }
                                    CommunicateBasic.this.dataPieceNumber = 10;
                                    CommunicateBasic.this.errorCode = 9502976;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    CommunicateBasic.this.writeBytes(ParseUtils.pointSpo2Bytes(2));
                                }
                                continue;
                            }
                            continue;
                        }
                        case -30: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 10);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] != 126 && this.bytes[1] != 127) {
                                CommunicateBasic.this.dayStepsData.add(DataClassesParseUtils.parseDaySteps(this.bytes));
                                if (CommunicateBasic.this.dataPieceNumber == 10) {
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                }
                                if (CommunicateBasic.this.dataPieceNumber == (this.bytes[1] & 0xF)) {
                                    CommunicateBasic.this.dataPieceNumber++;
                                    if (CommunicateBasic.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(1));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DAY_STEPS_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.bytes[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.dataPieceNumber = 0;
                                        if ((CommunicateBasic.this.storageDataConstant & 0x4) == 0x4) {
                                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                        }
                                        else {
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                            CommunicateBasic.this.stopParseThread();
                                            CommunicateBasic.this.resetCommunicateErrorTimer();
                                            CommunicateBasic.this.init();
                                            CommunicateBasic.this.onDataResultEnd();
                                            if (!ContecSdk.isDelete || CommunicateBasic.this.communicateCallback == null) {
                                                continue;
                                            }
                                            CommunicateBasic.this.communicateCallback.onDeleteSuccess();
                                        }
                                    }
                                }
                                else {
                                    CommunicateBasic.this.sleep(500);
                                    if (this.inputBytes != null) {
                                        this.inputBytes.clear();
                                    }
                                    CommunicateBasic.this.dataPieceNumber = 10;
                                    CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(2));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DAY_STEPS_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                continue;
                            }
                            continue;
                        }
                        case -22: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 12);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] != 126 && this.bytes[1] != 127) {
                                CommunicateBasic.this.dayStepsData.add(DataClassesParseUtils.parseDayStepsWithTargetCalories(this.bytes));
                                if (CommunicateBasic.this.dataPieceNumber == 10) {
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                }
                                if (CommunicateBasic.this.dataPieceNumber == (this.bytes[1] & 0xF)) {
                                    CommunicateBasic.this.dataPieceNumber++;
                                    if (CommunicateBasic.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(1));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DAY_STEPS_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.bytes[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.dataPieceNumber = 0;
                                        if ((CommunicateBasic.this.storageDataConstant & 0x4) == 0x4) {
                                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                        }
                                        else {
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                            CommunicateBasic.this.stopParseThread();
                                            CommunicateBasic.this.resetCommunicateErrorTimer();
                                            CommunicateBasic.this.init();
                                            CommunicateBasic.this.onDataResultEnd();
                                            if (!ContecSdk.isDelete || CommunicateBasic.this.communicateCallback == null) {
                                                continue;
                                            }
                                            CommunicateBasic.this.communicateCallback.onDeleteSuccess();
                                        }
                                    }
                                }
                                else {
                                    CommunicateBasic.this.sleep(500);
                                    if (this.inputBytes != null) {
                                        this.inputBytes.clear();
                                    }
                                    CommunicateBasic.this.dataPieceNumber = 10;
                                    CommunicateBasic.this.writeBytes(ParseUtils.dayStepsBytes(2));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DAY_STEPS_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                continue;
                            }
                            continue;
                        }
                        case -29: {
                            CommunicateBasic.this.fiveMinStepsData = new FiveMinStepsData();
                            this.resolveInputData(this.bytes, 1, 8);
                            if (!this.isParsing) {
                                return;
                            }
                            final int year = (this.bytes[1] & 0x7F) + 2000;
                            final int month = this.bytes[2] & 0xF;
                            final int day = this.bytes[3] & 0x1F;
                            final int length = ((this.bytes[6] & 0x7F) | (this.bytes[7] & 0x7F) << 7) & 0xFFFF;
                            CommunicateBasic.this.fiveMinStepsData.setYear(year);
                            CommunicateBasic.this.fiveMinStepsData.setMonth(month);
                            CommunicateBasic.this.fiveMinStepsData.setDay(day);
                            CommunicateBasic.this.fiveMinStepsData.setLength(length);
                            CommunicateBasic.this.ar = new short[length * 2];
                            CommunicateBasic.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(0));
                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            continue;
                        }
                        case -28: {
                            this.resolveInputData(this.bytes, 1, 16);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] != 126 && this.bytes[1] != 127) {
                                final short[] h = DataClassesParseUtils.h(this.bytes);
                                if (CommunicateBasic.this.dataPieceNumber == 10) {
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                }
                                if (null != CommunicateBasic.this.ar && (CommunicateBasic.this.ae + 1) * 6 < CommunicateBasic.this.ar.length) {
                                    for (int i = CommunicateBasic.this.ae * 6; i < (CommunicateBasic.this.ae + 1) * 6; ++i) {
                                        CommunicateBasic.this.ar[i] = h[i - CommunicateBasic.this.ae * 6];
                                    }
                                }
                                else if (null != CommunicateBasic.this.ar) {
                                    for (int j = CommunicateBasic.this.ae * 6; j < CommunicateBasic.this.ar.length; ++j) {
                                        CommunicateBasic.this.ar[j] = h[j - CommunicateBasic.this.ae * 6];
                                    }
                                }
                                if (CommunicateBasic.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                    continue;
                                }
                                CommunicateBasic.this.dataPieceNumber++;
                                CommunicateBasic.this.ae++;
                                if (CommunicateBasic.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(1));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else {
                                    if ((this.bytes[1] & 0x40) == 0x0 || CommunicateBasic.this.ae * 6 < CommunicateBasic.this.ar.length) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                    CommunicateBasic.this.ae = 0;
                                    CommunicateBasic.this.fiveMinStepsData.setStepFiveDataBean(CommunicateBasic.this.ar);
                                    CommunicateBasic.this.fiveMinStepsDataArray.add(CommunicateBasic.this.fiveMinStepsData);
                                    if (ContecSdk.isDelete) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(127));
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(126));
                                    }
                                    CommunicateBasic.this.sleep(500);
                                    if (CommunicateBasic.this.fiveMinStepsDataArray.size() < CommunicateBasic.this.fiveMinStepsDataInfo) {
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_INFO_TIMEOUT;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        CommunicateBasic.this.onFiveMinStepsDataResult(CommunicateBasic.this.fiveMinStepsDataArray);
                                        CommunicateBasic.this.stopParseThread();
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.init();
                                        CommunicateBasic.this.onDataResultEnd();
                                        if (!ContecSdk.isDelete || CommunicateBasic.this.communicateCallback == null) {
                                            continue;
                                        }
                                        CommunicateBasic.this.communicateCallback.onDeleteSuccess();
                                    }
                                }
                                continue;
                            }
                            continue;
                        }
                        case -20: {
                            this.resolveInputData(this.bytes, 1, 20);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.G = (this.bytes[1] & 0x40);
                            if ((this.bytes[1] & 0xF) == 0x0) {
                                CommunicateBasic.this.supportPI = false;
                            }
                            else {
                                CommunicateBasic.this.supportPI = true;
                            }
                            CommunicateBasic.this.L = CommunicateBasic.this.caseCount;
                            CommunicateBasic.this.M = (this.bytes[2] & 0x7F);
                            CommunicateBasic.this.caseCount = (this.bytes[3] & 0x7F);
                            CommunicateBasic.this.startTime = DataClassesParseUtils.parseDateTimeString2(this.bytes);
                            CommunicateBasic.this.dataLength = (((this.bytes[10] & 0x7F) | (this.bytes[11] & 0x7F) << 7 | (this.bytes[12] & 0x7F) << 14 | (this.bytes[13] & 0x7F) << 21) & -1);
                            if (CommunicateBasic.this.dataLength == 0) {
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.onDataResultEmpty();
                                CommunicateBasic.this.init();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x1) == 0x1 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.f) {
                                CommunicateBasic.this.l();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x2) == 0x2 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.e) {
                                CommunicateBasic.this.m();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x4) == 0x4 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.d) {
                                CommunicateBasic.this.n();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x4) == 0x4 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.d;
                                CommunicateBasic.this.n();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x2) == 0x2 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.e;
                                CommunicateBasic.this.m();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x1) == 0x1 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.f;
                                CommunicateBasic.this.l();
                                continue;
                            }
                            CommunicateBasic.this.stopParseThread();
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.onDataResultEmpty();
                            CommunicateBasic.this.init();
                            continue;
                        }
                        case -19: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 1) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.resolveInputData(this.bytes, 2, 22);
                                if (!this.isParsing) {
                                    return;
                                }
                                final byte[] array2 = new byte[24];
                                for (int k = 0; k < 24; ++k) {
                                    array2[k] = this.bytes[k];
                                }
                                final byte a = DataClassesParseUtils.a(array2);
                                final int n4 = ((this.bytes[5] & 0x7F) | (this.bytes[6] & 0x7F) << 7) & 0xFFFF;
                                if (this.bytes[2] == 1) { // SPO2
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.c(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_SPO2_TIMEOUT;
                                    }
                                }
                                else if (this.bytes[2] == 2) { // PR
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.d(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PR_TIMEOUT;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) { // PI
                                        continue;
                                    }
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.e(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PI_TIMEOUT;
                                    }
                                }
                                continue;
                            }
                            else if (this.bytes[1] == 3) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.resolveInputData(this.bytes, 2, 28);
                                if (!this.isParsing) {
                                    return;
                                }
                                final byte[] array3 = new byte[30];
                                for (int l = 0; l < 30; ++l) {
                                    array3[l] = this.bytes[l];
                                }
                                final byte a2 = DataClassesParseUtils.a(array3);
                                final int n5 = ((this.bytes[3] & 0x7F) | (this.bytes[4] & 0x7F) << 7) & 0xFFFF;
                                if (this.bytes[2] == 1) {
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.f(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_SPO2_TIMEOUT;
                                    }
                                }
                                else if (this.bytes[2] == 2) {
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.g(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289922;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) {
                                        continue;
                                    }
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.h(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289923;
                                    }
                                }
                                continue;
                            }
                            else if (this.bytes[1] == 4) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.resolveInputData(this.bytes, 2, 28);
                                if (!this.isParsing) {
                                    return;
                                }
                                final byte[] array4 = new byte[30];
                                for (int n6 = 0; n6 < 30; ++n6) {
                                    array4[n6] = this.bytes[n6];
                                }
                                final byte a3 = DataClassesParseUtils.a(array4);
                                final int n7 = ((this.bytes[3] & 0x7F) | (this.bytes[4] & 0x7F) << 7) & 0xFFFF;
                                if (this.bytes[2] == 1) {
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.i(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 1, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290177;
                                    }
                                }
                                else if (this.bytes[2] == 2) {
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.j(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 2, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290178;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) {
                                        continue;
                                    }
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.k(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 3, CommunicateBasic.this.M, CommunicateBasic.this.caseCount, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290179;
                                    }
                                }
                                continue;
                            }
                            else {
                                if (this.bytes[1] != 127) {
                                    continue;
                                }
                                this.resolveInputData(this.bytes, 2, 5);
                                if (!this.isParsing) {
                                    return;
                                }
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (this.bytes[5] == 0) {
                                    if (CommunicateBasic.this.communicateCallback != null) {
                                        CommunicateBasic.this.communicateCallback.onDeleteSuccess();
                                    }
                                    if (CommunicateBasic.this.deleteDataCallback != null) {
                                        CommunicateBasic.this.deleteDataCallback.onSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if (CommunicateBasic.this.communicateCallback != null) {
                                        CommunicateBasic.this.communicateCallback.onDeleteFail();
                                    }
                                    if (CommunicateBasic.this.deleteDataCallback != null) {
                                        CommunicateBasic.this.deleteDataCallback.onFail(1);
                                        continue;
                                    }
                                    continue;
                                }
                            }
                        }
                        case -48: {
                            this.resolveInputData(this.bytes, 1, 13);
                            if (!this.isParsing) {
                                return;
                            }
                            final int n8 = this.bytes[1] & 0x7;
                            CommunicateBasic.this.ag = DataClassesParseUtils.parseDateTimeString(this.bytes);
                            if (n8 == 0) {
                                CommunicateBasic.this.I = (((this.bytes[10] & 0x7F) | (this.bytes[11] & 0x7F) << 7 | (this.bytes[12] & 0x7F) << 14) & -1);
                            }
                            if (CommunicateBasic.this.I == 0) {
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.onDataResultEmpty();
                                CommunicateBasic.this.init();
                                continue;
                            }
                            switch (n8) {
                                case 0: {
                                    CommunicateBasic.this.Y = new int[CommunicateBasic.this.I];
                                    CommunicateBasic.this.writeBytes(ParseUtils.b(0, 0));
                                    CommunicateBasic.this.errorCode = 10617088;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                case 1: {
                                    CommunicateBasic.this.Z = new int[CommunicateBasic.this.I];
                                    CommunicateBasic.this.writeBytes(ParseUtils.c(0, 0));
                                    CommunicateBasic.this.errorCode = 10682624;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                case 4: {
                                    CommunicateBasic.this.ab = new int[CommunicateBasic.this.I];
                                    CommunicateBasic.this.writeBytes(ParseUtils.e(0, 0));
                                    CommunicateBasic.this.errorCode = 10879232;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                            }
                            continue;
                        }
                        case -47: {
                            this.resolveInputData(this.bytes, 1, 3);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 0) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x2) == 0x2) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(1));
                                    CommunicateBasic.this.errorCode = 10551553;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x4) == 0x4) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.bytes);
                                continue;
                            }
                            else if (this.bytes[1] == 1) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x4) == 0x4) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.bytes);
                                continue;
                            }
                            else if (this.bytes[1] == 2) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.bytes);
                                continue;
                            }
                            else if (this.bytes[1] == 3) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.deleteDataAboutSessionBytes(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (this.bytes[1] == 4) {
                                    CommunicateBasic.this.o(this.bytes);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -46: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.l(this.bytes);
                            continue;
                        }
                        case -45: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.m(this.bytes);
                            continue;
                        }
                        case -41: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.n(this.bytes);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                }
            }
        }
        
        public void resolveInputData(final byte[] array, final int startIndex, final int endIndex) {
            for (int i = startIndex; i < endIndex + startIndex && this.isParsing; ++i) {
                if (this.inputBytes != null && !this.inputBytes.isEmpty()) {
                    array[i] = this.inputBytes.poll();
                }
                else {
                    --i;
                }
            }
        }
        
        public void end() {
            this.isParsing = false;
        }
    }
}
