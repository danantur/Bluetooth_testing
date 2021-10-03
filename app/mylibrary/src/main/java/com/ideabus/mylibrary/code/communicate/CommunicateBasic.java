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
import com.ideabus.mylibrary.code.bean.c;
import com.ideabus.mylibrary.code.bean.d;
import com.ideabus.mylibrary.code.bean.e;
import com.ideabus.mylibrary.code.bean.f;
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

public class CommunicateBasic extends CommunicateBase
{
    private String deviceName;
    public WriteThread av;
    private ArrayList<Integer> aA;
    private ArrayList<Integer> aB;
    private ArrayList<Integer> aC;
    private boolean aD;
    int aw;
    int ax;
    int ay;

    public CommunicateBasic(String deviceName) {
        this.deviceName = deviceName;
        this.av = null;
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
        this.o();
        this.e();
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
        this.F = 9371911;
        this.errorCode = 9371911;
        this.setCommunicateErrorTimer(this.storageModeCallback);
        this.writeBytes(ParseUtils.a(storageMode));
    }

    @Override
    public void deleteData(final DeleteDataCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.deleteDataCallback = new WeakReference<>(referent).get();
        }
        this.al = true;
        this.currentOperationCode = SdkConstants.OPERATE_DELETE_DATA;
        if (ContecSdk.getIsCheckDevice()) {
            this.errorCode = 10420480;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.deleteDataCallback);
            this.writeBytes(ParseUtils.g());
        }
        else {
            this.errorCode = 8519938;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.deleteDataCallback);
            this.writeBytes(ParseUtils.c());
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
        this.writeBytes(ParseUtils.c());
        this.errorCode = 8519938;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.realtimeSpO2Callback);
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
        this.errorCode = 8651012;
        this.setCommunicateErrorTimer(this.setStepsTimeCallback);
        this.writeBytes(ParseUtils.a(n, n2));
    }

    @Override
    public void setWeight(final int weight, final SetWeightCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setWeightCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = 9;
        this.errorCode = 8716549;
        this.setCommunicateErrorTimer(this.setWeightCallback);
        this.writeBytes(ParseUtils.l(weight));
    }

    @Override
    public void setHeight(final int height, final SetHeightCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setHeightCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = 10;
        this.errorCode = 9044234;
        this.setCommunicateErrorTimer(this.setHeightCallback);
        this.writeBytes(ParseUtils.m(height));
    }

    @Override
    public void setCalorie(final int n, final int n2, final SystemParameter.StepsSensitivity stepsSensitivity, final SetCalorieCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.setCalorieCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = 11;
        this.errorCode = 9109771;
        this.setCommunicateErrorTimer(this.setCalorieCallback);
        this.writeBytes(ParseUtils.a(n, n2, stepsSensitivity));
    }

    @Override
    public void getStorageMode(final GetStorageModeCallback referent) {
        if (this.communicating) {
            return;
        }
        if (null != referent) {
            this.getStorageModeCallback = new WeakReference<>(referent).get();
        }
        this.currentOperationCode = 1;
        this.errorCode = 9306375;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.getStorageModeCallback);
        this.writeBytes(ParseUtils.l());
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
        this.currentOperationCode = 4;
        this.writeBytes(ParseUtils.c());
        this.errorCode = 8519938;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.realtimeCallback);
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
    public void addBytesToParse(final byte[] bytes) {
        if (null != this.inputBytes) {
            for (int i = 0; i < bytes.length; ++i) {
                this.inputBytes.offer(bytes[i]);
            }
        }
        if (this.av == null) {
            (this.av = new WriteThread(this.inputBytes)).start();
        }
    }

    private void q() {
        if (this.ac != 0) {
            if (this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.g) {
                this.e();
                this.resetCommunicateErrorTimer();
                this.init();
                this.onDataResultEmpty();
                return;
            }
            if ((this.ac & 0x1) == 0x1) {
                this.errorCode = 9437440;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(0));
            }
            else if ((this.ac & 0x2) == 0x2) {
                this.writeBytes(ParseUtils.dataStorageBytes(1));
                this.errorCode = 9437441;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else if ((this.ac & 0x4) == 0x4) {
                this.writeBytes(ParseUtils.dataStorageBytes(2));
                this.errorCode = 9634048;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else if ((this.ac & 0x40) == 0x40) {
                this.errorCode = 9437446;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(6));
            }
        }
        else if (this.ad != 0) {
            if (this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.f || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.e || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.d || this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.h) {
                this.e();
                this.resetCommunicateErrorTimer();
                this.onDataResultEmpty();
                this.init();
                return;
            }
            this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.g;
            if ((this.ad & 0x1) == 0x1) {
                this.errorCode = 10486016;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                this.writeBytes(ParseUtils.i(0));
            }
            else if ((this.ad & 0x2) == 0x2) {
                this.errorCode = 10486017;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                this.writeBytes(ParseUtils.i(1));
            }
        }
    }

    private void o(final byte[] array) {
        this.e();
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

    protected void b(final int n) {
        if (n < 11) {
            if (this.currentOperationCode == 6) {
                this.resetCommunicateErrorTimer();
                this.e();
                this.init();
                if (this.deleteDataCallback != null) {
                    this.deleteDataCallback.onFail(255);
                }
            }
            else if (this.currentOperationCode == 3) {
                this.errorCode = 9437440;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                this.writeBytes(ParseUtils.dataStorageBytes(0));
            }
            else if (this.currentOperationCode == 4) {
                this.writeBytes(ParseUtils.realtimeBytes(0));
                this.setWaveTimeout(this.realtimeCallback);
                this.sleep(1000);
                this.writeBytes(ParseUtils.realtimeBytes(1));
                this.setSpo2Timeout(this.realtimeCallback);
                if (this.ak == null) {
                    (this.ak = new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            CommunicateBasic.this.writeBytes(ParseUtils.realtimeBytes());
                        }
                    }, 5500L, 4500L);
                }
            }
            else if (this.currentOperationCode == 7) {
                this.writeBytes(ParseUtils.realtimeBytes(1));
                this.errorCode = 10158337;
                this.p();
                if (this.ak == null) {
                    (this.ak = new Timer()).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            CommunicateBasic.this.writeBytes(ParseUtils.realtimeBytes());
                        }
                    }, 5500L, 4500L);
                }
            }
        }
        else if (this.currentOperationCode == 6) {
            this.errorCode = 9306375;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.deleteDataCallback);
            this.writeBytes(ParseUtils.l());
        }
        else if (this.currentOperationCode == 3) {
            this.F = 9371908;
            this.errorCode = 9371908;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            this.writeBytes(ParseUtils.i());
        }
        else if (this.currentOperationCode == 4) {
            this.writeBytes(ParseUtils.realtimeBytes(0));
            this.setWaveTimeout(this.realtimeCallback);
            this.sleep(1000);
            this.writeBytes(ParseUtils.realtimeBytes(1));
            this.setSpo2Timeout(this.realtimeCallback);
            if (this.ak == null) {
                (this.ak = new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CommunicateBasic.this.writeBytes(ParseUtils.realtimeBytes());
                    }
                }, 5500L, 4500L);
            }
        }
        else if (this.currentOperationCode == 7) {
            this.writeBytes(ParseUtils.realtimeBytes(1));
            this.errorCode = 10158337;
            this.p();
            if (this.ak == null) {
                (this.ak = new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        CommunicateBasic.this.writeBytes(ParseUtils.realtimeBytes());
                    }
                }, 5500L, 4500L);
            }
        }
    }

    protected void l() {
        this.P = new int[this.J];
        this.writeBytes(ParseUtils.a(1, 1, this.M, this.N, 0));
        this.errorCode = 10289409;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
    }

    protected void m() {
        this.S = new int[this.J];
        this.writeBytes(ParseUtils.b(3, 1, this.M, this.N, 0));
        this.errorCode = 10289921;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
    }

    protected void n() {
        this.V = new int[this.J];
        this.writeBytes(ParseUtils.b(4, 1, this.M, this.N, 0));
        this.errorCode = 10290177;
        this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
    }

    protected void c(final byte[] array) {
        final short[] n = com.ideabus.mylibrary.code.tools.b.n(array);
        if (this.P != null && (this.ae + 1) * 27 < this.P.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.P[i] = (n[i - this.ae * 27] & 0x7F);
            }
        }
        else if (this.P != null) {
            for (int j = this.ae * 27; j < this.P.length; ++j) {
                this.P[j] = (n[j - this.ae * 27] & 0x7F);
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.J) {
            this.ae = 0;
            this.Q = new int[this.J];
            this.writeBytes(ParseUtils.a(1, 2, this.M, this.N, 0));
            this.errorCode = 10289410;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }

    protected void d(final byte[] array) {
        final short[] n = com.ideabus.mylibrary.code.tools.b.n(array);
        if (this.Q != null && (this.ae + 1) * 27 < this.Q.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.Q[i] = n[i - this.ae * 27];
            }
        }
        else if (this.Q != null) {
            for (int j = this.ae * 27; j < this.Q.length; ++j) {
                this.Q[j] = n[j - this.ae * 27];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.J) {
            this.ae = 0;
            if (this.O) {
                this.R = new int[this.J];
                this.writeBytes(ParseUtils.a(1, 3, this.M, this.N, 0));
                this.errorCode = 10289411;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                final d d = new d();
                this.b(d);
                this.onEachPieceDataResult(d);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.j(1));
                    this.errorCode = 10223872;
                    this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
                }
                else {
                    this.s();
                }
            }
        }
    }

    protected void e(final byte[] array) {
        final short[] n = com.ideabus.mylibrary.code.tools.b.n(array);
        if (this.R != null && (this.ae + 1) * 27 < this.R.length) {
            for (int i = this.ae * 27; i < (this.ae + 1) * 27; ++i) {
                this.R[i] = n[i - this.ae * 27];
            }
        }
        else if (this.R != null) {
            for (int j = this.ae * 27; j < this.R.length; ++j) {
                this.R[j] = n[j - this.ae * 27];
            }
        }
        ++this.ae;
        if (this.ae * 27 >= this.J) {
            this.ae = 0;
            final d d = new d();
            this.b(d);
            this.onEachPieceDataResult(d);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.j(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    protected void f(final byte[] array) {
        final short[] o = com.ideabus.mylibrary.code.tools.b.o(array);
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
        if (this.ae * 21 >= this.J) {
            this.E = 0;
            this.ae = 0;
            this.T = new int[this.J];
            this.writeBytes(ParseUtils.b(3, 2, this.M, this.N, 0));
            this.errorCode = 10289922;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }

    protected void g(final byte[] array) {
        final short[] o = com.ideabus.mylibrary.code.tools.b.o(array);
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
        if (this.ae * 21 >= this.J) {
            this.E = 0;
            this.ae = 0;
            if (this.O) {
                this.U = new int[this.J];
                this.writeBytes(ParseUtils.b(3, 3, this.M, this.N, 0));
                this.errorCode = 10289923;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                final e e = new e();
                this.b(e);
                this.onEachPieceDataResult(e);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.j(1));
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
        final short[] o = com.ideabus.mylibrary.code.tools.b.o(array);
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
        if (this.ae * 21 >= this.J) {
            this.ae = 0;
            final e e = new e();
            this.b(e);
            this.onEachPieceDataResult(e);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.j(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    public void z(final byte[] array, final ArrayList<Integer> list) {
        final byte[] p2 = com.ideabus.mylibrary.code.tools.b.p(array);
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
        if (this.aA.size() >= this.J) {
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
            this.E = 0;
            this.W = new int[this.J];
            this.writeBytes(ParseUtils.b(4, 2, this.M, this.N, 0));
            this.errorCode = 10290178;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
    }

    protected void j(final byte[] array) {
        this.z(array, this.aB);
        ++this.ae;
        if (this.aB.size() >= this.J) {
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
            if (this.O) {
                this.X = new int[this.J];
                this.writeBytes(ParseUtils.b(4, 3, this.M, this.N, 0));
                this.errorCode = 10290179;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                final c c = new c();
                this.b(c);
                this.onEachPieceDataResult(c);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.j(1));
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
        if (this.aC.size() >= this.J) {
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
            this.b(c);
            this.onEachPieceDataResult(c);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.j(1));
                this.errorCode = 10223872;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else {
                this.s();
            }
        }
    }

    protected void l(final byte[] array) {
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
            if ((this.ad & 0x2) == 0x2) {
                this.writeBytes(ParseUtils.i(1));
                this.errorCode = 10486017;
                this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
            }
            else if ((this.ad & 0x10) == 0x10) {
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
            if ((this.ad & 0x10) == 0x10) {
                this.O = true;
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
        final short[] m = com.ideabus.mylibrary.code.tools.b.m(array);
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
        this.b(b);
        this.onEachPieceDataResult(b);
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.k(0));
            this.errorCode = 10486016;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
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
        if (this.O) {
            pieceData.setSupportPI(1);
        }
        else {
            pieceData.setSupportPI(0);
        }
        int[] spo2Data = null;
        int[] prData = null;
        int[] piData = null;
        if (pieceData instanceof d) {
            pieceData.setLength(this.J);
            pieceData.setStartTime(this.ah);
            spo2Data = new int[this.J];
            prData = new int[this.J];
            System.arraycopy(this.P, 0, spo2Data, 0, this.J);
            System.arraycopy(this.Q, 0, prData, 0, this.J);
            if (this.R != null) {
                piData = new int[this.J];
                System.arraycopy(this.R, 0, piData, 0, this.J);
            }
        }
        else if (pieceData instanceof e) {
            pieceData.setLength(this.J);
            pieceData.setStartTime(this.ah);
            spo2Data = new int[this.J];
            prData = new int[this.J];
            System.arraycopy(this.S, 0, spo2Data, 0, this.J);
            System.arraycopy(this.T, 0, prData, 0, this.J);
            if (this.U != null) {
                piData = new int[this.J];
                System.arraycopy(this.U, 0, piData, 0, this.J);
            }
        }
        else if (pieceData instanceof c) {
            pieceData.setLength(this.J);
            pieceData.setStartTime(this.ah);
            spo2Data = new int[this.J];
            prData = new int[this.J];
            System.arraycopy(this.V, 0, spo2Data, 0, this.J);
            System.arraycopy(this.W, 0, prData, 0, this.J);
            if (this.X != null) {
                piData = new int[this.J];
                System.arraycopy(this.X, 0, piData, 0, this.J);
            }
        }
        else if (pieceData instanceof com.ideabus.mylibrary.code.bean.b) {
            pieceData.setLength(this.I);
            pieceData.setStartTime(this.ag);
            spo2Data = new int[this.I];
            prData = new int[this.I];
            System.arraycopy(this.Z, 0, spo2Data, 0, this.I);
            System.arraycopy(this.Y, 0, prData, 0, this.I);
            if (this.ab != null) {
                piData = new int[this.I];
                System.arraycopy(this.ab, 0, piData, 0, this.I);
            }
        }
        pieceData.setSpo2Data(spo2Data);
        pieceData.setPrData(prData);
        pieceData.setPiData(piData);
    }

    private void s() {
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.n());
            this.errorCode = 10321791;
            this.setCommunicateErrorTimer((CommunicateFailCallback)this.communicateCallback);
        }
        else {
            this.e();
            this.resetCommunicateErrorTimer();
            this.init();
        }
        this.onDataResultEnd();
    }

    private void e() {
        if (null != this.inputBytes) {
            this.inputBytes.clear();
        }
        if (this.av != null) {
            this.av.a();
            this.av = null;
        }
    }

    public void o() {
        if (this.ak != null) {
            this.ak.cancel();
            this.ak = null;
        }
    }

    protected void p() {
        if (this.realtimeDelayTimer == null) {
            (this.realtimeDelayTimer = new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    CommunicateBasic.this.writeBytes(ParseUtils.realtimeBytes(1));
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

    public class WriteThread extends Thread
    {
        private ConcurrentLinkedQueue<Byte> b;
        private boolean c;
        private byte[] d;

        public WriteThread(final ConcurrentLinkedQueue<Byte> b) {
            this.b = null;
            this.c = false;
            this.d = new byte[128];
            this.b = b;
            this.c = true;
        }

        @Override
        public void run() {
            while (this.c) {
                if (null != this.b && !this.b.isEmpty()) {
                    this.a(this.d, 0, 1);
                    if (d[0] != - 21 || d[1] != 0)
                        Log.e("read_bytes", d[0] + " " + d[1]);
                    if (!this.c) {
                        return;
                    }
                    switch (this.d[0]) {
                        case -21: {
                            this.a(this.d, 1, 1);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] == 0) {
                                this.a(this.d, 2, 4);
                                if (!this.c) {
                                    return;
                                }
                                CommunicateBasic.this.setWaveTimeout(CommunicateBasic.this.realtimeCallback);
                                final com.ideabus.mylibrary.code.bean.g b = com.ideabus.mylibrary.code.tools.b.b(this.d);
                                if (CommunicateBasic.this.realtimeCallback == null) {
                                    continue;
                                }
                                CommunicateBasic.this.realtimeCallback.onRealtimeWaveData(b.a(), b.b(), b.c(), b.d(), b.e());
                                continue;
                            }
                            else if (this.d[1] == 1) {
                                this.a(this.d, 2, 6);
                                if (!this.c) {
                                    return;
                                }
                                CommunicateBasic.this.resetRealtimeDelayTimer();
                                f f;
                                if (CommunicateBasic.this.y < 11) {
                                    f = com.ideabus.mylibrary.code.tools.b.d(this.d);
                                }
                                else {
                                    f = com.ideabus.mylibrary.code.tools.b.c(this.d);
                                }
                                if (CommunicateBasic.this.realtimeCallback != null && f != null) {
                                    CommunicateBasic.this.setSpo2Timeout(CommunicateBasic.this.realtimeCallback);
                                    CommunicateBasic.this.realtimeCallback.onSpo2Data(f.a(), f.c(), f.b(), f.d());
                                }
                                if (CommunicateBasic.this.realtimeSpO2Callback == null || f == null) {
                                    continue;
                                }
                                CommunicateBasic.this.setSpo2Timeout(CommunicateBasic.this.realtimeSpO2Callback);
                                CommunicateBasic.this.realtimeSpO2Callback.onRealtimeSpo2Data(f.c(), f.b(), f.d());
                                continue;
                            }
                            else {
                                if (this.d[1] != 127) {
                                    continue;
                                }
                                this.a(this.d, 2, 1);
                                if (!this.c) {
                                    return;
                                }
                                CommunicateBasic.this.realtimeStarted = false;
                                CommunicateBasic.this.e();
                                CommunicateBasic.this.o();
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
                            this.a(this.d, 1, 1);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.resetRealtimeDelayTimer();
                            CommunicateBasic.this.o();
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.init();
                            CommunicateBasic.this.errorCode = 240;
                            if (CommunicateBasic.this.currentOperationCode == 3) {
                                if (CommunicateBasic.this.communicateCallback != null) {
                                    CommunicateBasic.this.communicateCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 1) {
                                if (CommunicateBasic.this.getStorageModeCallback != null) {
                                    CommunicateBasic.this.getStorageModeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 0) {
                                if (CommunicateBasic.this.storageModeCallback != null) {
                                    CommunicateBasic.this.storageModeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 2) {
                                if (CommunicateBasic.this.dataStorageInfoCallback != null) {
                                    CommunicateBasic.this.dataStorageInfoCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 4) {
                                if (CommunicateBasic.this.realtimeCallback != null) {
                                    CommunicateBasic.this.realtimeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 6) {
                                if (CommunicateBasic.this.deleteDataCallback != null) {
                                    CommunicateBasic.this.deleteDataCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 7) {
                                if (CommunicateBasic.this.realtimeSpO2Callback != null) {
                                    CommunicateBasic.this.realtimeSpO2Callback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 8) {
                                if (CommunicateBasic.this.setStepsTimeCallback != null) {
                                    CommunicateBasic.this.setStepsTimeCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 9) {
                                if (CommunicateBasic.this.setWeightCallback != null) {
                                    CommunicateBasic.this.setWeightCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else if (CommunicateBasic.this.currentOperationCode == 10) {
                                if (CommunicateBasic.this.setHeightCallback != null) {
                                    CommunicateBasic.this.setHeightCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.currentOperationCode == 11 && CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onFail(CommunicateBasic.this.errorCode);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -15: {
                            this.a(this.d, 1, 9);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = 8585475;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.d());
                            continue;
                        }
                        case -13: {
                            this.a(this.d, 1, 2);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = 8519938;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.c());
                            continue;
                        }
                        case -14: {
                            this.a(this.d, 1, 7);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.y = (this.d[6] & 0x7F);
                            CommunicateBasic.this.b(CommunicateBasic.this.y);
                            continue;
                        }
                        case -12:
                        case -11: {
                            this.a(this.d, 1, 2);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.e();
                            CommunicateBasic.this.init();
                            if (this.d[1] == 0) {
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
                            this.a(this.d, 1, 2);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.e();
                            CommunicateBasic.this.init();
                            if (this.d[1] == 0) {
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
                            this.a(this.d, 1, 2);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.e();
                            CommunicateBasic.this.init();
                            if (this.d[1] == 0) {
                                if (CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onSuccess();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.setCalorieCallback != null) {
                                    CommunicateBasic.this.setCalorieCallback.onFail(1);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -2: {
                            this.a(this.d, 1, 1);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] == 9) {
                                this.a(this.d, 2, 9);
                                if (!this.c) {
                                    return;
                                }
                                final byte[] q = com.ideabus.mylibrary.code.tools.b.q(this.d);
                                final byte[] array = new byte[7];
                                CommunicateBasic.this.errorCode = 9306377;
                                if (CommunicateBasic.this.currentOperationCode == 3) {
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else if (CommunicateBasic.this.currentOperationCode == 6) {
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                }
                                CommunicateBasic.this.writeBytes(ParseUtils.b(array));
                                CommunicateBasic.this.F = 9371914;
                                continue;
                            }
                            else if (this.d[1] == 7) {
                                this.a(this.d, 2, 3);
                                if (!this.c) {
                                    return;
                                }
                                final int n = this.d[2] & 0x7F;
                                if (CommunicateBasic.this.currentOperationCode == 6) {
                                    if (n == 0) {
                                        CommunicateBasic.this.errorCode = 10321791;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.n());
                                    }
                                    else if (n == 1) {
                                        CommunicateBasic.this.errorCode = 10420480;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.g());
                                    }
                                    else {
                                        if (n != 2) {
                                            continue;
                                        }
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.e();
                                        CommunicateBasic.this.init();
                                        CommunicateBasic.this.deleteDataCallback.onFail(255);
                                    }
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != 1) {
                                        continue;
                                    }
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.e();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.getStorageModeCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.getStorageModeCallback.onSuccess(n);
                                }
                                continue;
                            }
                            else {
                                if (this.d[1] != 6) {
                                    continue;
                                }
                                this.a(this.d, 2, 5);
                                if (!this.c) {
                                    return;
                                }
                                CommunicateBasic.this.ai = (this.d[2] & 0xFF);
                                CommunicateBasic.this.errorCode = 10223872;
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                CommunicateBasic.this.writeBytes(ParseUtils.j(1));
                                continue;
                            }
                        }
                        case -1: {
                            this.a(this.d, 1, 2);
                            if (!this.c) {
                                return;
                            }
                            if (CommunicateBasic.this.F == 9371908) {
                                CommunicateBasic.this.errorCode = 10420480;
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                CommunicateBasic.this.al = true;
                                CommunicateBasic.this.writeBytes(ParseUtils.g());
                                continue;
                            }
                            if (CommunicateBasic.this.F == 9371911) {
                                if (null == CommunicateBasic.this.storageModeCallback) {
                                    continue;
                                }
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (this.d[1] == 0) {
                                    CommunicateBasic.this.storageModeCallback.onSuccess();
                                    continue;
                                }
                                CommunicateBasic.this.storageModeCallback.onFail(1);
                                continue;
                            }
                            else {
                                if (CommunicateBasic.this.F != 9371914) {
                                    continue;
                                }
                                if (this.d[1] == 0) {
                                    if (CommunicateBasic.this.currentOperationCode == 6) {
                                        CommunicateBasic.this.errorCode = 10321791;
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.n());
                                        continue;
                                    }
                                    if (CommunicateBasic.this.currentOperationCode == 3) {
                                        CommunicateBasic.this.q();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.currentOperationCode == 3) {
                                        if (CommunicateBasic.this.communicateCallback != null) {
                                            CommunicateBasic.this.communicateCallback.onFail(1);
                                            continue;
                                        }
                                        continue;
                                    }
                                    else {
                                        if (CommunicateBasic.this.currentOperationCode == 6 && CommunicateBasic.this.deleteDataCallback != null) {
                                            CommunicateBasic.this.deleteDataCallback.onFail(1);
                                            continue;
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                        case -17: {
                            this.a(this.d, 1, 7);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.ac = (this.d[3] & 0x7F);
                            CommunicateBasic.this.ad = (this.d[5] & 0x7F);
                            if ((this.d[1] & 0x1) == 0x1) {
                                CommunicateBasic.this.init();
                                CommunicateBasic.this.e();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                continue;
                            }
                            if ((this.d[2] & 0x1) == 0x1) {
                                if (!CommunicateBasic.this.al) {
                                    continue;
                                }
                                CommunicateBasic.this.al = false;
                                if (ContecSdk.getIsCheckDevice()) {
                                    CommunicateBasic.this.F = 9306377;
                                    CommunicateBasic.this.errorCode = 9306377;
                                    if (CommunicateBasic.this.currentOperationCode == 6) {
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    }
                                    else if (CommunicateBasic.this.currentOperationCode == 3) {
                                        CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    }
                                    CommunicateBasic.this.writeBytes(ParseUtils.m());
                                    continue;
                                }
                                if (CommunicateBasic.this.currentOperationCode == 6) {
                                    CommunicateBasic.this.errorCode = 10551552;
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(0));
                                    continue;
                                }
                                if (CommunicateBasic.this.currentOperationCode == 3) {
                                    CommunicateBasic.this.q();
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (!CommunicateBasic.this.al) {
                                    continue;
                                }
                                CommunicateBasic.this.al = false;
                                CommunicateBasic.this.e();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (CommunicateBasic.this.currentOperationCode == 6) {
                                    if (CommunicateBasic.this.deleteDataCallback != null) {
                                        CommunicateBasic.this.deleteDataCallback.onSuccess();
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode == 3) {
                                        CommunicateBasic.this.onDataResultEmpty();
                                        continue;
                                    }
                                    continue;
                                }
                            }
                        }
                        case -32: {
                            this.a(this.d, 1, 1);
                            if (!this.c) {
                                return;
                            }
                            if ((this.d[1] & 0x7) == 0x6) {
                                this.a(this.d, 2, 13);
                                if (!this.c) {
                                    return;
                                }
                                final int n2 = (this.d[2] & 0x7F) | ((this.d[3] & 0x7F) << 7 & 0xFFFF);
                                CommunicateBasic.this.D = n2;
                                if (CommunicateBasic.this.currentOperationCode == 2) {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.e();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.dataStorageInfoCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataStorageInfoCallback.onSuccess(SystemParameter.DataStorageInfo.PIECESPO2DATAINFO, n2);
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != 3) {
                                        continue;
                                    }
                                    if (CommunicateBasic.this.D == 0) {
                                        CommunicateBasic.this.e();
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.onDataResultEmpty();
                                        CommunicateBasic.this.init();
                                    }
                                    else {
                                        CommunicateBasic.this.errorCode = 9306374;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.k());
                                    }
                                }
                                continue;
                            }
                            else {
                                this.a(this.d, 2, 5);
                                if (!this.c) {
                                    return;
                                }
                                final int n3 = (this.d[2] & 0x7F) | ((this.d[3] & 0x7F) << 7 & 0xFFFF);
                                if (CommunicateBasic.this.currentOperationCode == 2) {
                                    CommunicateBasic.this.resetCommunicateErrorTimer();
                                    CommunicateBasic.this.e();
                                    CommunicateBasic.this.init();
                                    if (CommunicateBasic.this.dataStorageInfoCallback == null) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataStorageInfoCallback.onSuccess(SystemParameter.DataStorageInfo.POINTDATAINFO, n3);
                                }
                                else {
                                    if (CommunicateBasic.this.currentOperationCode != 3) {
                                        continue;
                                    }
                                    if (n3 == 0) {
                                        CommunicateBasic.this.e();
                                        CommunicateBasic.this.resetCommunicateErrorTimer();
                                        CommunicateBasic.this.init();
                                    }
                                    else if ((this.d[1] & 0x7) == 0x0) {
                                        CommunicateBasic.this.z = n3;
                                        if (CommunicateBasic.this.z <= 0) {
                                            continue;
                                        }
                                        CommunicateBasic.this.spo2PointDataArray = (ArrayList<SpO2PointData>)new ArrayList();
                                        CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.h;
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(0));
                                        CommunicateBasic.this.errorCode = 9502976;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else if ((this.d[1] & 0x7) == 0x1) {
                                        CommunicateBasic.this.A = n3;
                                        if (CommunicateBasic.this.A <= 0) {
                                            continue;
                                        }
                                        CommunicateBasic.this.dayStepsData = (ArrayList<DayStepsData>)new ArrayList();
                                        CommunicateBasic.this.writeBytes(ParseUtils.c(0));
                                        CommunicateBasic.this.errorCode = 9568512;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.d[1] & 0x7) != 0x2) {
                                            continue;
                                        }
                                        CommunicateBasic.this.B = n3;
                                        if (CommunicateBasic.this.B <= 0) {
                                            continue;
                                        }
                                        CommunicateBasic.this.fiveMinStepsDataArray = (ArrayList<FiveMinStepsData>)new ArrayList();
                                        CommunicateBasic.this.writeBytes(ParseUtils.d(1));
                                        CommunicateBasic.this.errorCode = 9437442;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                }
                                continue;
                            }
                        }
                        case -31: {
                            this.a(this.d, 1, 10);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] != 126 && this.d[1] != 127) {
                                CommunicateBasic.this.spo2PointDataArray.add(com.ideabus.mylibrary.code.tools.b.e(this.d));
                                if (CommunicateBasic.this.E == 10) {
                                    CommunicateBasic.this.E = 0;
                                }
                                if (CommunicateBasic.this.E == (this.d[1] & 0xF)) {
                                    CommunicateBasic.this.E++;
                                    if (CommunicateBasic.this.E == 10 && (this.d[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.errorCode = 9502976;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(1));
                                    }
                                    else {
                                        if ((this.d[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.b(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.b(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.E = 0;
                                        if ((CommunicateBasic.this.ac & 0x2) == 0x2) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(1));
                                            CommunicateBasic.this.errorCode = 9437441;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                        }
                                        else if ((CommunicateBasic.this.ac & 0x4) == 0x4) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.errorCode = 9634048;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                        }
                                        else {
                                            CommunicateBasic.this.onPointSpO2DataResult(CommunicateBasic.this.spo2PointDataArray);
                                            CommunicateBasic.this.e();
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
                                    if (this.b != null) {
                                        this.b.clear();
                                    }
                                    CommunicateBasic.this.E = 10;
                                    CommunicateBasic.this.errorCode = 9502976;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    CommunicateBasic.this.writeBytes(ParseUtils.b(2));
                                }
                                continue;
                            }
                            continue;
                        }
                        case -30: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.a(this.d, 1, 10);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] != 126 && this.d[1] != 127) {
                                CommunicateBasic.this.dayStepsData.add(com.ideabus.mylibrary.code.tools.b.f(this.d));
                                if (CommunicateBasic.this.E == 10) {
                                    CommunicateBasic.this.E = 0;
                                }
                                if (CommunicateBasic.this.E == (this.d[1] & 0xF)) {
                                    CommunicateBasic.this.E++;
                                    if (CommunicateBasic.this.E == 10 && (this.d[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.c(1));
                                        CommunicateBasic.this.errorCode = 9568512;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.d[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.c(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.c(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.E = 0;
                                        if ((CommunicateBasic.this.ac & 0x4) == 0x4) {
                                            CommunicateBasic.this.errorCode = 9634048;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                        }
                                        else {
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                            CommunicateBasic.this.e();
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
                                    if (this.b != null) {
                                        this.b.clear();
                                    }
                                    CommunicateBasic.this.E = 10;
                                    CommunicateBasic.this.writeBytes(ParseUtils.c(2));
                                    CommunicateBasic.this.errorCode = 9568512;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                continue;
                            }
                            continue;
                        }
                        case -22: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.a(this.d, 1, 12);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] != 126 && this.d[1] != 127) {
                                CommunicateBasic.this.dayStepsData.add(com.ideabus.mylibrary.code.tools.b.g(this.d));
                                if (CommunicateBasic.this.E == 10) {
                                    CommunicateBasic.this.E = 0;
                                }
                                if (CommunicateBasic.this.E == (this.d[1] & 0xF)) {
                                    CommunicateBasic.this.E++;
                                    if (CommunicateBasic.this.E == 10 && (this.d[1] & 0x40) == 0x0) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.c(1));
                                        CommunicateBasic.this.errorCode = 9568512;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        if ((this.d[1] & 0x40) == 0x0) {
                                            continue;
                                        }
                                        if (ContecSdk.isDelete) {
                                            CommunicateBasic.this.writeBytes(ParseUtils.c(127));
                                        }
                                        else {
                                            CommunicateBasic.this.writeBytes(ParseUtils.c(126));
                                        }
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.E = 0;
                                        if ((CommunicateBasic.this.ac & 0x4) == 0x4) {
                                            CommunicateBasic.this.errorCode = 9634048;
                                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                            CommunicateBasic.this.writeBytes(ParseUtils.dataStorageBytes(2));
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                        }
                                        else {
                                            CommunicateBasic.this.onDayStepsDataResult(CommunicateBasic.this.dayStepsData);
                                            CommunicateBasic.this.e();
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
                                    if (this.b != null) {
                                        this.b.clear();
                                    }
                                    CommunicateBasic.this.E = 10;
                                    CommunicateBasic.this.writeBytes(ParseUtils.c(2));
                                    CommunicateBasic.this.errorCode = 9568512;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                continue;
                            }
                            continue;
                        }
                        case -29: {
                            CommunicateBasic.this.fiveMinStepsData = new FiveMinStepsData();
                            this.a(this.d, 1, 8);
                            if (!this.c) {
                                return;
                            }
                            final int year = (this.d[1] & 0x7F) + 2000;
                            final int month = this.d[2] & 0xF;
                            final int day = this.d[3] & 0x1F;
                            final int length = ((this.d[6] & 0x7F) | (this.d[7] & 0x7F) << 7) & 0xFFFF;
                            CommunicateBasic.this.fiveMinStepsData.setYear(year);
                            CommunicateBasic.this.fiveMinStepsData.setMonth(month);
                            CommunicateBasic.this.fiveMinStepsData.setDay(day);
                            CommunicateBasic.this.fiveMinStepsData.setLength(length);
                            CommunicateBasic.this.ar = new short[length * 2];
                            CommunicateBasic.this.writeBytes(ParseUtils.e(0));
                            CommunicateBasic.this.errorCode = 9699584;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            continue;
                        }
                        case -28: {
                            this.a(this.d, 1, 16);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] != 126 && this.d[1] != 127) {
                                final short[] h = com.ideabus.mylibrary.code.tools.b.h(this.d);
                                if (CommunicateBasic.this.E == 10) {
                                    CommunicateBasic.this.E = 0;
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
                                if (CommunicateBasic.this.E != (this.d[1] & 0xF)) {
                                    continue;
                                }
                                CommunicateBasic.this.E++;
                                CommunicateBasic.this.ae++;
                                if (CommunicateBasic.this.E == 10 && (this.d[1] & 0x40) == 0x0) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.e(1));
                                    CommunicateBasic.this.errorCode = 9699584;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else {
                                    if ((this.d[1] & 0x40) == 0x0 || CommunicateBasic.this.ae * 6 < CommunicateBasic.this.ar.length) {
                                        continue;
                                    }
                                    CommunicateBasic.this.E = 0;
                                    CommunicateBasic.this.ae = 0;
                                    CommunicateBasic.this.fiveMinStepsData.setStepFiveDataBean(CommunicateBasic.this.ar);
                                    CommunicateBasic.this.fiveMinStepsDataArray.add(CommunicateBasic.this.fiveMinStepsData);
                                    if (ContecSdk.isDelete) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.e(127));
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.e(126));
                                    }
                                    CommunicateBasic.this.sleep(500);
                                    if (CommunicateBasic.this.fiveMinStepsDataArray.size() < CommunicateBasic.this.B) {
                                        CommunicateBasic.this.writeBytes(ParseUtils.d(1));
                                        CommunicateBasic.this.errorCode = 9634048;
                                        CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    }
                                    else {
                                        CommunicateBasic.this.onFiveMinStepsDataResult(CommunicateBasic.this.fiveMinStepsDataArray);
                                        CommunicateBasic.this.e();
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
                            this.a(this.d, 1, 20);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.G = (this.d[1] & 0x40);
                            if ((this.d[1] & 0xF) == 0x0) {
                                CommunicateBasic.this.O = false;
                            }
                            else {
                                CommunicateBasic.this.O = true;
                            }
                            CommunicateBasic.this.L = CommunicateBasic.this.N;
                            CommunicateBasic.this.M = (this.d[2] & 0x7F);
                            CommunicateBasic.this.N = (this.d[3] & 0x7F);
                            CommunicateBasic.this.ah = com.ideabus.mylibrary.code.tools.b.l(this.d);
                            CommunicateBasic.this.J = (((this.d[10] & 0x7F) | (this.d[11] & 0x7F) << 7 | (this.d[12] & 0x7F) << 14 | (this.d[13] & 0x7F) << 21) & -1);
                            if (CommunicateBasic.this.J == 0) {
                                CommunicateBasic.this.e();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.onDataResultEmpty();
                                CommunicateBasic.this.init();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x1) == 0x1 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.f) {
                                CommunicateBasic.this.l();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x2) == 0x2 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.e) {
                                CommunicateBasic.this.m();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x4) == 0x4 && CommunicateBasic.this.dataTypeInt == com.ideabus.mylibrary.code.bean.a.d) {
                                CommunicateBasic.this.n();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x4) == 0x4 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.d;
                                CommunicateBasic.this.n();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x2) == 0x2 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.e;
                                CommunicateBasic.this.m();
                                continue;
                            }
                            if ((CommunicateBasic.this.ai & 0x1) == 0x1 && CommunicateBasic.this.dataTypeInt == 0) {
                                CommunicateBasic.this.dataTypeInt = com.ideabus.mylibrary.code.bean.a.f;
                                CommunicateBasic.this.l();
                                continue;
                            }
                            CommunicateBasic.this.e();
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.onDataResultEmpty();
                            CommunicateBasic.this.init();
                            continue;
                        }
                        case -19: {
                            this.a(this.d, 1, 1);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] == 1) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.a(this.d, 2, 22);
                                if (!this.c) {
                                    return;
                                }
                                final byte[] array2 = new byte[24];
                                for (int k = 0; k < 24; ++k) {
                                    array2[k] = this.d[k];
                                }
                                final byte a = com.ideabus.mylibrary.code.tools.b.a(array2);
                                final int n4 = ((this.d[5] & 0x7F) | (this.d[6] & 0x7F) << 7) & 0xFFFF;
                                if (this.d[2] == 1) {
                                    if (a == this.d[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.c(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 1, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289409;
                                    }
                                }
                                else if (this.d[2] == 2) {
                                    if (a == this.d[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.d(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 2, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289410;
                                    }
                                }
                                else {
                                    if (this.d[2] != 3) {
                                        continue;
                                    }
                                    if (a == this.d[23] && n4 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.e(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, 3, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289411;
                                    }
                                }
                                continue;
                            }
                            else if (this.d[1] == 3) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.a(this.d, 2, 28);
                                if (!this.c) {
                                    return;
                                }
                                final byte[] array3 = new byte[30];
                                for (int l = 0; l < 30; ++l) {
                                    array3[l] = this.d[l];
                                }
                                final byte a2 = com.ideabus.mylibrary.code.tools.b.a(array3);
                                final int n5 = ((this.d[3] & 0x7F) | (this.d[4] & 0x7F) << 7) & 0xFFFF;
                                if (this.d[2] == 1) {
                                    if (a2 == this.d[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.f(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 1, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289921;
                                    }
                                }
                                else if (this.d[2] == 2) {
                                    if (a2 == this.d[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.g(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 2, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289922;
                                    }
                                }
                                else {
                                    if (this.d[2] != 3) {
                                        continue;
                                    }
                                    if (a2 == this.d[29] && n5 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.h(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(3, 3, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10289923;
                                    }
                                }
                                continue;
                            }
                            else if (this.d[1] == 4) {
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.a(this.d, 2, 28);
                                if (!this.c) {
                                    return;
                                }
                                final byte[] array4 = new byte[30];
                                for (int n6 = 0; n6 < 30; ++n6) {
                                    array4[n6] = this.d[n6];
                                }
                                final byte a3 = com.ideabus.mylibrary.code.tools.b.a(array4);
                                final int n7 = ((this.d[3] & 0x7F) | (this.d[4] & 0x7F) << 7) & 0xFFFF;
                                if (this.d[2] == 1) {
                                    if (a3 == this.d[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.i(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 1, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290177;
                                    }
                                }
                                else if (this.d[2] == 2) {
                                    if (a3 == this.d[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.j(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 2, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290178;
                                    }
                                }
                                else {
                                    if (this.d[2] != 3) {
                                        continue;
                                    }
                                    if (a3 == this.d[29] && n7 == CommunicateBasic.this.ae) {
                                        CommunicateBasic.this.k(this.d);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.M, CommunicateBasic.this.N));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.b(4, 3, CommunicateBasic.this.M, CommunicateBasic.this.N, CommunicateBasic.this.ae));
                                        CommunicateBasic.this.errorCode = 10290179;
                                    }
                                }
                                continue;
                            }
                            else {
                                if (this.d[1] != 127) {
                                    continue;
                                }
                                this.a(this.d, 2, 5);
                                if (!this.c) {
                                    return;
                                }
                                CommunicateBasic.this.e();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.init();
                                if (this.d[5] == 0) {
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
                            this.a(this.d, 1, 13);
                            if (!this.c) {
                                return;
                            }
                            final int n8 = this.d[1] & 0x7;
                            CommunicateBasic.this.ag = com.ideabus.mylibrary.code.tools.b.k(this.d);
                            if (n8 == 0) {
                                CommunicateBasic.this.I = (((this.d[10] & 0x7F) | (this.d[11] & 0x7F) << 7 | (this.d[12] & 0x7F) << 14) & -1);
                            }
                            if (CommunicateBasic.this.I == 0) {
                                CommunicateBasic.this.e();
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
                            this.a(this.d, 1, 3);
                            if (!this.c) {
                                return;
                            }
                            if (this.d[1] == 0) {
                                if ((CommunicateBasic.this.ad & 0x2) == 0x2) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(1));
                                    CommunicateBasic.this.errorCode = 10551553;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x4) == 0x4) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.d);
                                continue;
                            }
                            else if (this.d[1] == 1) {
                                if ((CommunicateBasic.this.ad & 0x4) == 0x4) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.d);
                                continue;
                            }
                            else if (this.d[1] == 2) {
                                if ((CommunicateBasic.this.ad & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.ad & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.o(this.d);
                                continue;
                            }
                            else if (this.d[1] == 3) {
                                if ((CommunicateBasic.this.ad & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.k(4));
                                    CommunicateBasic.this.errorCode = 10551556;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (this.d[1] == 4) {
                                    CommunicateBasic.this.o(this.d);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case -46: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.a(this.d, 1, 19);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.l(this.d);
                            continue;
                        }
                        case -45: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.a(this.d, 1, 19);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.m(this.d);
                            continue;
                        }
                        case -41: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.a(this.d, 1, 19);
                            if (!this.c) {
                                return;
                            }
                            CommunicateBasic.this.n(this.d);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                }
            }
        }
        
        public void a(final byte[] array, final int n, final int n2) {
            for (int n3 = n; n3 < n2 + n && this.c; ++n3) {
                if (this.b != null && !this.b.isEmpty()) {
                    array[n3] = this.b.poll();
                }
                else {
                    --n3;
                }
            }
        }
        
        public void a() {
            this.c = false;
        }
    }
}
