// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.callback;

import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.EcgData;
import com.ideabus.mylibrary.code.bean.FiveMinStepsData;
import com.ideabus.mylibrary.code.bean.PieceData;
import com.ideabus.mylibrary.code.bean.SpO2PointData;

import java.util.ArrayList;

public interface CommunicateCallback extends CommunicateFailCallback
{
    void onPointSpO2DataResult(final ArrayList<SpO2PointData> spO2PointData);
    
    void onDayStepsDataResult(final ArrayList<DayStepsData> dayStepsData);
    
    void onFiveMinStepsDataResult(final ArrayList<FiveMinStepsData> fiveMinStepsData);
    
    void onEachPieceDataResult(final PieceData pieceData);
    
    void onEachEcgDataResult(final EcgData ecgData);
    
    void onDataResultEmpty();
    
    void onDataResultEnd();
    
    void onDeleteSuccess();
    
    void onDeleteFail();
}
