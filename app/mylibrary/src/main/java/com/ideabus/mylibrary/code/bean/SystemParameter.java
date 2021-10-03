// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;

public class SystemParameter implements Serializable
{
    public enum StepsSensitivity
    {
        LOW, 
        MIDDLE, 
        HIGH;
    }
    
    public enum DataStorageInfo
    {
        POINTDATAINFO, 
        DAYSTEPSINFO, 
        DAYFIVEMINUTESSTEPSINFO, 
        ECGDATAINFO, 
        PULSEWAVEDATAINFO, 
        WITHSTORAGEINFO, 
        PIECESPO2DATAINFO;
    }
    
    public enum DataType
    {
        CODEDATA(1),
        ORIGINALDATA(2),
        DIFFERENCEDATA(3),
        CONTINUEDATA(4),
        POINTDATA(5);

        public int num;

        DataType(int num) {
            this.num = num;
        }
    }
    
    public enum StorageMode
    {
        AUTOMATIC, 
        MANUAL;
    }
}
