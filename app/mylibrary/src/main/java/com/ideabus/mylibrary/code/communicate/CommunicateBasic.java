// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate;

import android.util.Log;

import com.ideabus.mylibrary.code.base.CommunicateBase;
import com.ideabus.mylibrary.code.bean.ByteEnums;
import com.ideabus.mylibrary.code.bean.DataPiece2;
import com.ideabus.mylibrary.code.bean.DataPieceOriginal;
import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SdkConstants;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.SystemParameter;
import com.ideabus.mylibrary.code.bean.WaveData;
import com.ideabus.mylibrary.code.bean.DataPieceCode;
import com.ideabus.mylibrary.code.bean.DataPiece;
import com.ideabus.mylibrary.code.bean.Spo2Data;
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
    private ArrayList<Integer> spo2CodeData;
    private ArrayList<Integer> prCodeData;
    private ArrayList<Integer> piCodeData;
    private boolean aD;
    int aw;
    int ax;
    int ay;

    public CommunicateBasic(String deviceName) {
        this.deviceName = deviceName;
        this.parseThread = null;
        this.spo2CodeData = new ArrayList<>();
        this.prCodeData = new ArrayList<>();
        this.piCodeData = new ArrayList<>();
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
            this.writeBytes(ParseUtils.getDeviceVersion());
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
        this.writeBytes(ParseUtils.getDeviceVersion());
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
        this.writeBytes(ParseUtils.getDeviceVersion());
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
            if (this.dataTypeInt == SystemParameter.DataType.CONTINUEDATA.num) {
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
            if (this.dataTypeInt == SystemParameter.DataType.DIFFERENCEDATA.num || this.dataTypeInt == SystemParameter.DataType.ORIGINALDATA.num || this.dataTypeInt == SystemParameter.DataType.CODEDATA.num || this.dataTypeInt == SystemParameter.DataType.POINTDATA.num) {
                this.stopParseThread();
                this.resetCommunicateErrorTimer();
                this.onDataResultEmpty();
                this.init();
                return;
            }
            this.dataTypeInt = SystemParameter.DataType.CONTINUEDATA.num;
            if ((this.dataConstant4 & 0x1) == 0x1) {
                this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_PR_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.continueInfoBytes(0));
            }
            else if ((this.dataConstant4 & 0x2) == 0x2) {
                this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_SPO2_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
                this.writeBytes(ParseUtils.continueInfoBytes(1));
            }
        }
    }

    private void onDeleteResponse(final byte[] array) {
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

    protected void onDeviceVersionChange() {
        if (this.deviceVersion < 11) {
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

    protected void checkSpo2DataPieceDifference() {
        this.spo2Data = new int[this.dataLength];
        this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 1, this.dataConstant5, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void checkSpo2DataPieceOriginal() {
        this.spo2DataOriginal = new int[this.dataLength];
        this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 1, this.dataConstant5, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void checkSpo2DataPieceCode() {
        this.spo2DataCode = new int[this.dataLength];
        this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 1, this.dataConstant5, this.caseCount, 0));
        this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_SPO2_TIMEOUT;
        this.setCommunicateErrorTimer(this.communicateCallback);
    }

    protected void onSpo2Data(final byte[] array) {
        final short[] spo2Data = DataClassesParseUtils.parseOrdinaryData(array);
        if (this.spo2Data != null && (this.dataPieceNumber2 + 1) * 27 < this.spo2Data.length) {
            for (int i = this.dataPieceNumber2 * 27; i < (this.dataPieceNumber2 + 1) * 27; ++i) {
                this.spo2Data[i] = (spo2Data[i - this.dataPieceNumber2 * 27] & 0x7F);
            }
        }
        else if (this.spo2Data != null) {
            for (int j = this.dataPieceNumber2 * 27; j < this.spo2Data.length; ++j) {
                this.spo2Data[j] = (spo2Data[j - this.dataPieceNumber2 * 27] & 0x7F);
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength) {
            this.dataPieceNumber2 = 0;
            this.prData = new int[this.dataLength];
            this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 2, this.dataConstant5, this.caseCount, 0));
            this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PR_TIMEOUT;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
    }

    protected void onPrData(final byte[] array) {
        final short[] prData = DataClassesParseUtils.parseOrdinaryData(array);
        if (this.prData != null && (this.dataPieceNumber2 + 1) * 27 < this.prData.length) {
            for (int i = this.dataPieceNumber2 * 27; i < (this.dataPieceNumber2 + 1) * 27; ++i) {
                this.prData[i] = prData[i - this.dataPieceNumber2 * 27];
            }
        }
        else if (this.prData != null) {
            for (int j = this.dataPieceNumber2 * 27; j < this.prData.length; ++j) {
                this.prData[j] = prData[j - this.dataPieceNumber2 * 27];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength) {
            this.dataPieceNumber2 = 0;
            if (this.supportPI) {
                this.piData = new int[this.dataLength];
                this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 3, this.dataConstant5, this.caseCount, 0));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                final DataPiece dataPiece = new DataPiece();
                this.fillData(dataPiece);
                this.onEachPieceDataResult(dataPiece);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                    this.setCommunicateErrorTimer(this.communicateCallback);
                }
                else {
                    this.dataResultEnd();
                }
            }
        }
    }

    protected void onPiData(final byte[] array) {
        final short[] piData = DataClassesParseUtils.parseOrdinaryData(array);
        if (this.piData != null && (this.dataPieceNumber2 + 1) * 27 < this.piData.length) {
            for (int i = this.dataPieceNumber2 * 27; i < (this.dataPieceNumber2 + 1) * 27; ++i) {
                this.piData[i] = piData[i - this.dataPieceNumber2 * 27];
            }
        }
        else if (this.piData != null) {
            for (int j = this.dataPieceNumber2 * 27; j < this.piData.length; ++j) {
                this.piData[j] = piData[j - this.dataPieceNumber2 * 27];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength) {
            this.dataPieceNumber2 = 0;
            final DataPiece dataPiece = new DataPiece();
            this.fillData(dataPiece);
            this.onEachPieceDataResult(dataPiece);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                this.dataResultEnd();
            }
        }
    }

    protected void onSpo2DataOriginal(final byte[] array) {
        final short[] spo2DataOriginal = DataClassesParseUtils.parseDataOriginal(array);
        if (this.spo2DataOriginal != null && (this.dataPieceNumber2 + 1) * 21 < this.spo2DataOriginal.length) {
            for (int i = this.dataPieceNumber2 * 21; i < (this.dataPieceNumber2 + 1) * 21; ++i) {
                this.spo2DataOriginal[i] = spo2DataOriginal[i - this.dataPieceNumber2 * 21];
            }
        }
        else if (this.spo2DataOriginal != null) {
            for (int j = this.dataPieceNumber2 * 21; j < this.spo2DataOriginal.length; ++j) {
                this.spo2DataOriginal[j] = spo2DataOriginal[j - this.dataPieceNumber2 * 21];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 21 >= this.dataLength) {
            this.dataPieceNumber = 0;
            this.dataPieceNumber2 = 0;
            this.prDataOriginal = new int[this.dataLength];
            this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 2, this.dataConstant5, this.caseCount, 0));
            this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_PR_TIMEOUT;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
    }

    protected void onPrDataOriginal(final byte[] array) {
        final short[] prDataOriginal = DataClassesParseUtils.parseDataOriginal(array);
        if (this.prDataOriginal != null && (this.dataPieceNumber2 + 1) * 21 < this.prDataOriginal.length) {
            for (int i = this.dataPieceNumber2 * 21; i < (this.dataPieceNumber2 + 1) * 21; ++i) {
                this.prDataOriginal[i] = prDataOriginal[i - this.dataPieceNumber2 * 21];
            }
        }
        else if (this.prDataOriginal != null) {
            for (int j = this.dataPieceNumber2 * 21; j < this.prDataOriginal.length; ++j) {
                this.prDataOriginal[j] = prDataOriginal[j - this.dataPieceNumber2 * 21];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 21 >= this.dataLength) {
            this.dataPieceNumber = 0;
            this.dataPieceNumber2 = 0;
            if (this.supportPI) {
                this.piDataOriginal = new int[this.dataLength];
                this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 3, this.dataConstant5, this.caseCount, 0));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                final DataPieceOriginal dataPieceOriginal = new DataPieceOriginal();
                this.fillData(dataPieceOriginal);
                this.onEachPieceDataResult(dataPieceOriginal);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                    this.setCommunicateErrorTimer(this.communicateCallback);
                }
                else {
                    this.dataResultEnd();
                }
            }
        }
    }

    protected void onPiDataOriginal(final byte[] array) {
        final short[] piDataOriginal = DataClassesParseUtils.parseDataOriginal(array);
        if (this.piDataOriginal != null && (this.dataPieceNumber2 + 1) * 21 < this.piDataOriginal.length) {
            for (int i = this.dataPieceNumber2 * 21; i < (this.dataPieceNumber2 + 1) * 21; ++i) {
                this.piDataOriginal[i] = piDataOriginal[i - this.dataPieceNumber2 * 21];
            }
        }
        else if (this.prDataOriginal != null) {
            for (int j = this.dataPieceNumber2 * 21; j < this.piDataOriginal.length; ++j) {
                this.piDataOriginal[j] = piDataOriginal[j - this.dataPieceNumber2 * 21];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 21 >= this.dataLength) {
            this.dataPieceNumber2 = 0;
            final DataPieceOriginal dataPieceOriginal = new DataPieceOriginal();
            this.fillData(dataPieceOriginal);
            this.onEachPieceDataResult(dataPieceOriginal);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                this.dataResultEnd();
            }
        }
    }

    public void parseCodeData(final byte[] array, final ArrayList<Integer> list) {
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

    protected void onSpo2CodeData(final byte[] array) {
        this.parseCodeData(array, this.spo2CodeData);
        ++this.dataPieceNumber2;
        if (this.spo2CodeData.size() >= this.dataLength) {
            for (int i = 0; i < this.spo2DataCode.length; ++i) {
                this.spo2DataCode[i] = this.spo2CodeData.get(i);
            }
            this.dataPieceNumber2 = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            this.spo2CodeData.clear();
            this.dataPieceNumber = 0;
            this.prDataCode = new int[this.dataLength];
            this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 2, this.dataConstant5, this.caseCount, 0));
            this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_PR_TIMEOUT;
            this.setCommunicateErrorTimer(this.communicateCallback);
        }
    }

    protected void onPrCodeData(final byte[] array) {
        this.parseCodeData(array, this.prCodeData);
        ++this.dataPieceNumber2;
        if (this.prCodeData.size() >= this.dataLength) {
            for (int i = 0; i < this.spo2DataCode.length; ++i) {
                this.prDataCode[i] = this.prCodeData.get(i);
            }
            this.dataPieceNumber2 = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            this.prCodeData.clear();
            if (this.supportPI) {
                this.piDataCode = new int[this.dataLength];
                this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 3, this.dataConstant5, this.caseCount, 0));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                final DataPieceCode dataPieceCode = new DataPieceCode();
                this.fillData(dataPieceCode);
                this.onEachPieceDataResult(dataPieceCode);
                if (this.G == 0) {
                    this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                    this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                    this.setCommunicateErrorTimer(this.communicateCallback);
                }
                else {
                    this.dataResultEnd();
                }
            }
        }
    }

    protected void onPiCodeData(final byte[] array) {
        this.parseCodeData(array, this.piCodeData);
        ++this.dataPieceNumber2;
        if (this.piCodeData.size() >= this.dataLength) {
            for (int i = 0; i < this.piDataCode.length; ++i) {
                this.piDataCode[i] = this.piCodeData.get(i);
            }
            this.dataPieceNumber2 = 0;
            this.aw = 0;
            this.ax = 0;
            this.ay = 0;
            this.aD = false;
            this.piCodeData.clear();
            final DataPieceCode dataPieceCode = new DataPieceCode();
            this.fillData(dataPieceCode);
            this.onEachPieceDataResult(dataPieceCode);
            if (this.G == 0) {
                this.writeBytes(ParseUtils.getPieceInfoBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_PIECE_INFO;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                this.dataResultEnd();
            }
        }
    }

    protected void onPrData2(final byte[] array) {
        final short[] prData2 = DataClassesParseUtils.spo2Data2Parse(array);
        if (this.prData2 != null && (this.dataPieceNumber2 + 1) * 27 < this.prData2.length) {
            for (int i = 27 * this.dataPieceNumber2; i < 27 * (this.dataPieceNumber2 + 1); ++i) {
                this.prData2[i] = prData2[i - 27 * this.dataPieceNumber2];
            }
        }
        else if (this.prData2 != null) {
            for (int j = 27 * this.dataPieceNumber2; j < this.prData2.length; ++j) {
                this.prData2[j] = prData2[j - 27 * this.dataPieceNumber2];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength2) {
            this.dataPieceNumber2 = 0;
            if ((this.dataConstant4 & 0x2) == 0x2) {
                this.writeBytes(ParseUtils.continueInfoBytes(1));
                this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_SPO2_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else if ((this.dataConstant4 & 0x10) == 0x10) {
                this.writeBytes(ParseUtils.continueInfoBytes(4));
                this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                this.fillDataPiece2();
            }
        }
    }

    protected void onSpo2Data2(final byte[] array) {
        final short[] spo2Data2 = DataClassesParseUtils.spo2Data2Parse(array);
        if (this.spo2Data2 != null && (this.dataPieceNumber2 + 1) * 27 < this.spo2Data2.length) {
            for (int i = this.dataPieceNumber2 * 27; i < (this.dataPieceNumber2 + 1) * 27; ++i) {
                this.spo2Data2[i] = (spo2Data2[i - this.dataPieceNumber2 * 27] & 0x7F);
            }
        }
        else if (this.spo2Data2 != null) {
            for (int j = this.dataPieceNumber2 * 27; j < this.spo2Data2.length; ++j) {
                this.spo2Data2[j] = (spo2Data2[j - this.dataPieceNumber2 * 27] & 0x7F);
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength2) {
            this.H = 0;
            this.dataPieceNumber2 = 0;
            if ((this.dataConstant4 & 0x10) == 0x10) {
                this.supportPI = true;
                this.writeBytes(ParseUtils.continueInfoBytes(4));
                this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_PI_TIMEOUT;
                this.setCommunicateErrorTimer(this.communicateCallback);
            }
            else {
                this.fillDataPiece2();
            }
        }
    }

    protected void onPiData2(final byte[] array) {
        final short[] piData2 = DataClassesParseUtils.spo2Data2Parse(array);
        if (this.piData2 != null && (this.dataPieceNumber2 + 1) * 27 < this.piData2.length) {
            for (int i = this.dataPieceNumber2 * 27; i < (this.dataPieceNumber2 + 1) * 27; ++i) {
                this.piData2[i] = piData2[i - this.dataPieceNumber2 * 27];
            }
        }
        else if (this.piData2 != null) {
            for (int j = this.dataPieceNumber2 * 27; j < this.piData2.length; ++j) {
                this.piData2[j] = piData2[j - this.dataPieceNumber2 * 27];
            }
        }
        ++this.dataPieceNumber2;
        if (this.dataPieceNumber2 * 27 >= this.dataLength2) {
            this.H = 0;
            this.dataPieceNumber2 = 0;
            this.fillDataPiece2();
        }
    }

    private void fillDataPiece2() {
        final DataPiece2 dataPiece2 = new DataPiece2();
        this.fillData(dataPiece2);
        this.onEachPieceDataResult(dataPiece2);
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.clearDeviceDataBytes(0));
            this.errorCode = SdkConstants.ERRORCODE_CONTINUE_INFO_PR_TIMEOUT;
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
        if (pieceData instanceof DataPiece) {
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
        else if (pieceData instanceof DataPieceOriginal) {
            pieceData.length = this.dataLength;
            pieceData.startTime = this.startTime;
            spo2Data = new int[this.dataLength];
            prData = new int[this.dataLength];
            System.arraycopy(this.spo2DataOriginal, 0, spo2Data, 0, this.dataLength);
            System.arraycopy(this.prDataOriginal, 0, prData, 0, this.dataLength);
            if (this.piDataOriginal != null) {
                piData = new int[this.dataLength];
                System.arraycopy(this.piDataOriginal, 0, piData, 0, this.dataLength);
            }
        }
        else if (pieceData instanceof DataPieceCode) {
            pieceData.length = this.dataLength;
            pieceData.startTime = this.startTime;
            spo2Data = new int[this.dataLength];
            prData = new int[this.dataLength];
            System.arraycopy(this.spo2DataCode, 0, spo2Data, 0, this.dataLength);
            System.arraycopy(this.prDataCode, 0, prData, 0, this.dataLength);
            if (this.piDataCode != null) {
                piData = new int[this.dataLength];
                System.arraycopy(this.piDataCode, 0, piData, 0, this.dataLength);
            }
        }
        else if (pieceData instanceof DataPiece2) {
            pieceData.length = this.dataLength2;
            pieceData.startTime = this.startTime2;
            spo2Data = new int[this.dataLength2];
            prData = new int[this.dataLength2];
            System.arraycopy(this.spo2Data2, 0, spo2Data, 0, this.dataLength2);
            System.arraycopy(this.prData2, 0, prData, 0, this.dataLength2);
            if (this.piData2 != null) {
                piData = new int[this.dataLength2];
                System.arraycopy(this.piData2, 0, piData, 0, this.dataLength2);
            }
        }
        pieceData.spo2Data = spo2Data;
        pieceData.prData = prData;
        pieceData.piData = piData;
    }

    private void dataResultEnd() {
        if (ContecSdk.isDelete) {
            this.writeBytes(ParseUtils.deletePieceOfDataBytes());
            this.errorCode = SdkConstants.ERRORCODE_PIECE_DELETE_TIMEOUT;
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
                    if (!this.isParsing) {
                        return;
                    }
                    ByteEnums.FirstByteCommands cmd = ByteEnums.FirstByteCommands.getByCmdByte(this.bytes[0]);
                    if (cmd == null) return;
                    switch (cmd) {
                        case REALTIME_DATA: {
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
                                if (CommunicateBasic.this.deviceVersion < 11) {
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
                        case COMMAND_NOT_SUPPORTED: {
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
                        case CURRENT_DATE_TIME_REQUEST: {
                            this.resolveInputData(this.bytes, 1, 9);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = 8585475;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.currentDateTimeBytes());
                            continue;
                        }
                        case DEVICE_VERSION_REQUEST: {
                            this.resolveInputData(this.bytes, 1, 2);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_DEVICE_VERSION_TIMEOUT;
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            CommunicateBasic.this.writeBytes(ParseUtils.getDeviceVersion());
                            continue;
                        }
                        case DEVICE_VERSION: {
                            this.resolveInputData(this.bytes, 1, 7);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.deviceVersion = (this.bytes[6] & 0x7F);
                            CommunicateBasic.this.onDeviceVersionChange();
                            continue;
                        }
                        case SET_WEIGHT_RESPONSE: {
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
                        case SET_HEIGHT_RESPONSE: {
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
                        case SET_CALORIE_RESPONSE: {
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
                        case CURRENT_STORAGE_STATE: {
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
                                final byte[] array = new byte[7];
                                CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_SYSTEM_CONFIGURATION_GET_STORAGE_DEVICE_CHECK_TIMEOUT;
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_GET_STORAGE_DATA) {
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                }
                                CommunicateBasic.this.writeBytes(ParseUtils.setStorageDeviceCheckBytes(array));
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
                        case CURRENT_SYS_CONFIGURATION_OPERATION_STATE: {
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
                        case DATA_STORAGE_INFO_DATA2: {
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
                                    CommunicateBasic.this.writeBytes(ParseUtils.getStorageDeviceCheckBytes());
                                    continue;
                                }
                                if (CommunicateBasic.this.currentOperationCode == SdkConstants.OPERATE_DELETE_DATA) {
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PR_TIMEOUT;
                                    CommunicateBasic.this.deleteData(CommunicateBasic.this.deleteDataCallback);
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(0));
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
                        case DATA_STORAGE_INFO_DATA: {
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
                        case SPO2_PIECE_OF_DATA: {
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
                        case DAY_STEPS_PIECE_OF_DATA: {
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
                        case DAY_STEPS_WITH_TARGET_CALORIE_PIECE_OF_DATA: {
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
                        case FIVE_MIN_STEPS_DATA: {
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
                        case FIVE_MIN_STEPS_PIECE_OF_DATA: {
                            this.resolveInputData(this.bytes, 1, 16);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] != 126 && this.bytes[1] != 127) {
                                final short[] h = DataClassesParseUtils.h(this.bytes);
                                if (CommunicateBasic.this.dataPieceNumber == 10) {
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                }
                                if (null != CommunicateBasic.this.ar && (CommunicateBasic.this.dataPieceNumber2 + 1) * 6 < CommunicateBasic.this.ar.length) {
                                    for (int i = CommunicateBasic.this.dataPieceNumber2 * 6; i < (CommunicateBasic.this.dataPieceNumber2 + 1) * 6; ++i) {
                                        CommunicateBasic.this.ar[i] = h[i - CommunicateBasic.this.dataPieceNumber2 * 6];
                                    }
                                }
                                else if (null != CommunicateBasic.this.ar) {
                                    for (int j = CommunicateBasic.this.dataPieceNumber2 * 6; j < CommunicateBasic.this.ar.length; ++j) {
                                        CommunicateBasic.this.ar[j] = h[j - CommunicateBasic.this.dataPieceNumber2 * 6];
                                    }
                                }
                                if (CommunicateBasic.this.dataPieceNumber != (this.bytes[1] & 0xF)) {
                                    continue;
                                }
                                CommunicateBasic.this.dataPieceNumber++;
                                CommunicateBasic.this.dataPieceNumber2++;
                                if (CommunicateBasic.this.dataPieceNumber == 10 && (this.bytes[1] & 0x40) == 0x0) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.fiveMinStepsInfoBytes(1));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_FIVE_MIN_STEPS_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                }
                                else {
                                    if ((this.bytes[1] & 0x40) == 0x0 || CommunicateBasic.this.dataPieceNumber2 * 6 < CommunicateBasic.this.ar.length) {
                                        continue;
                                    }
                                    CommunicateBasic.this.dataPieceNumber = 0;
                                    CommunicateBasic.this.dataPieceNumber2 = 0;
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
                        case SPO2_PIECE_OF_DATA_CHECK: {
                            this.resolveInputData(this.bytes, 1, 20);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.G = (this.bytes[1] & 0x40);
                            CommunicateBasic.this.supportPI = (this.bytes[1] & 0xF) != 0x0;
                            CommunicateBasic.this.caseCount2 = CommunicateBasic.this.caseCount;
                            CommunicateBasic.this.dataConstant5 = (this.bytes[2] & 0x7F);
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
                            if ((CommunicateBasic.this.dataConstant2 & 0x4) == 0x4) {
                                CommunicateBasic.this.dataTypeInt = SystemParameter.DataType.CODEDATA.num;
                                CommunicateBasic.this.checkSpo2DataPieceCode();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x2) == 0x2) {
                                CommunicateBasic.this.dataTypeInt = SystemParameter.DataType.ORIGINALDATA.num;
                                CommunicateBasic.this.checkSpo2DataPieceOriginal();
                                continue;
                            }
                            if ((CommunicateBasic.this.dataConstant2 & 0x1) == 0x1) {
                                CommunicateBasic.this.dataTypeInt = SystemParameter.DataType.DIFFERENCEDATA.num;
                                CommunicateBasic.this.checkSpo2DataPieceDifference();
                                continue;
                            }
                            CommunicateBasic.this.stopParseThread();
                            CommunicateBasic.this.resetCommunicateErrorTimer();
                            CommunicateBasic.this.onDataResultEmpty();
                            CommunicateBasic.this.init();
                            continue;
                        }
                        case ORIGINAL_CODE_OR_ORDINARY_DATA: {
                            this.resolveInputData(this.bytes, 1, 1);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 1) { // ORDINARY DATA
                                CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                this.resolveInputData(this.bytes, 2, 22);
                                if (!this.isParsing) {
                                    return;
                                }
                                final byte[] array2 = new byte[24];
                                System.arraycopy(this.bytes, 0, array2, 0, 24);
                                final byte a = DataClassesParseUtils.a(array2);
                                final int n4 = ((this.bytes[5] & 0x7F) | (this.bytes[6] & 0x7F) << 7) & 0xFFFF;
                                if (this.bytes[2] == 1) { // SPO2
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onSpo2Data(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_SPO2_TIMEOUT;
                                    }
                                }
                                else if (this.bytes[2] == 2) { // PR
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPrData(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PR_TIMEOUT;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) { // PI
                                        continue;
                                    }
                                    if (a == this.bytes[23] && n4 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPiData(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceDifferenceBytes(1, 3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_DIFFERENCE_PI_TIMEOUT;
                                    }
                                }
                                continue;
                            }
                            else if (this.bytes[1] == 3) { // ORIGINAL DATA
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
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onSpo2DataOriginal(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_SPO2_TIMEOUT;
                                    }
                                }
                                else if (this.bytes[2] == 2) {
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPrDataOriginal(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_ORIGINAL_PR_TIMEOUT;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) {
                                        continue;
                                    }
                                    if (a2 == this.bytes[29] && n5 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPiDataOriginal(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(3, 3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = 10289923;
                                    }
                                }
                                continue;
                            }
                            else if (this.bytes[1] == 4) { // CODE DATA
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
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onSpo2CodeData(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 1, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_SPO2_TIMEOUT;
                                    }
                                }
                                else if (this.bytes[2] == 2) {
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPrCodeData(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 2, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_PR_TIMEOUT;
                                    }
                                }
                                else {
                                    if (this.bytes[2] != 3) {
                                        continue;
                                    }
                                    if (a3 == this.bytes[29] && n7 == CommunicateBasic.this.dataPieceNumber2) {
                                        CommunicateBasic.this.onPiCodeData(this.bytes);
                                    }
                                    else {
                                        CommunicateBasic.this.writeBytes(ParseUtils.a(3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount));
                                        CommunicateBasic.this.sleep(500);
                                        if (CommunicateBasic.this.inputBytes != null) {
                                            CommunicateBasic.this.inputBytes.clear();
                                        }
                                        CommunicateBasic.this.writeBytes(ParseUtils.checkPieceCodeOrOriginalBytes(4, 3, CommunicateBasic.this.dataConstant5, CommunicateBasic.this.caseCount, CommunicateBasic.this.dataPieceNumber2));
                                        CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_PIECE_CODE_PI_TIMEOUT;
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
                        case READY_FOR_TRANSFERRING_2: {
                            this.resolveInputData(this.bytes, 1, 13);
                            if (!this.isParsing) {
                                return;
                            }
                            final int dataType = this.bytes[1] & 0x7;
                            CommunicateBasic.this.startTime2 = DataClassesParseUtils.parseDateTimeString(this.bytes);
                            if (dataType == 0) {
                                CommunicateBasic.this.dataLength2 = (((this.bytes[10] & 0x7F) | (this.bytes[11] & 0x7F) << 7 | (this.bytes[12] & 0x7F) << 14) & -1);
                            }
                            if (CommunicateBasic.this.dataLength2 == 0) {
                                CommunicateBasic.this.stopParseThread();
                                CommunicateBasic.this.resetCommunicateErrorTimer();
                                CommunicateBasic.this.onDataResultEmpty();
                                CommunicateBasic.this.init();
                                continue;
                            }
                            switch (dataType) {
                                case 0: {
                                    CommunicateBasic.this.prData2 = new int[CommunicateBasic.this.dataLength2];
                                    CommunicateBasic.this.writeBytes(ParseUtils.continuePrDataBytes(0, 0));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_PR_DATA_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                case 1: {
                                    CommunicateBasic.this.spo2Data2 = new int[CommunicateBasic.this.dataLength2];
                                    CommunicateBasic.this.writeBytes(ParseUtils.continueSpo2DataBytes(0, 0));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_SPO2_DATA_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                case 4: {
                                    CommunicateBasic.this.piData2 = new int[CommunicateBasic.this.dataLength2];
                                    CommunicateBasic.this.writeBytes(ParseUtils.continuePiDataBytes(0, 0));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_PI_DATA_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                            }
                            continue;
                        }
                        case PIECE_OF_DATA_DELETED_2: {
                            this.resolveInputData(this.bytes, 1, 3);
                            if (!this.isParsing) {
                                return;
                            }
                            if (this.bytes[1] == 0) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x2) == 0x2) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(1));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_SPO2_TIMEOUT;
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
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(4));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PI_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.onDeleteResponse(this.bytes);
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
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(4));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PI_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.onDeleteResponse(this.bytes);
                                continue;
                            }
                            else if (this.bytes[1] == 2) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x8) == 0x8) {
                                    continue;
                                }
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(4));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PI_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                CommunicateBasic.this.onDeleteResponse(this.bytes);
                                continue;
                            }
                            else if (this.bytes[1] == 3) {
                                if ((CommunicateBasic.this.dataConstant4 & 0x10) == 0x10) {
                                    CommunicateBasic.this.writeBytes(ParseUtils.clearDeviceDataBytes(4));
                                    CommunicateBasic.this.errorCode = SdkConstants.ERRORCODE_CONTINUE_DELETE_PI_TIMEOUT;
                                    CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (this.bytes[1] == 4) {
                                    CommunicateBasic.this.onDeleteResponse(this.bytes);
                                    continue;
                                }
                                continue;
                            }
                        }
                        case ON_PR_DATA_PIECE_2: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.onPrData2(this.bytes);
                            continue;
                        }
                        case ON_SPO2_DATA_PIECE_2: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.onSpo2Data2(this.bytes);
                            continue;
                        }
                        case ON_PI_DATA_PIECE_2: {
                            CommunicateBasic.this.startCommunicate(CommunicateBasic.this.communicateCallback);
                            this.resolveInputData(this.bytes, 1, 19);
                            if (!this.isParsing) {
                                return;
                            }
                            CommunicateBasic.this.onPiData2(this.bytes);
                            continue;
                        }
                        default: {
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
            if ((array[0] != 0 || array[1] != 0) && endIndex > 1) {
                ByteEnums.FirstByteCommands cmd = ByteEnums.FirstByteCommands.getByCmdByte(array[0]);
                if (cmd == null) return;
                byte[] newData = new byte[endIndex];
                System.arraycopy(array, 1, newData, 0, endIndex);
                Log.e("read_bytes", cmd + " " + Arrays.toString(newData));
            }
        }
        
        public void end() {
            this.isParsing = false;
        }
    }
}
