// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import android.os.Parcel;

import java.io.Serializable;

public class SpO2PointData implements Serializable
{
    private String a;
    private int b;
    private int c;
    
    public SpO2PointData(final Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readInt();
        this.c = parcel.readInt();
    }
    
    public SpO2PointData() {
    }
    
    public String getDate() {
        return this.a;
    }
    
    public void setDate(final String a) {
        this.a = a;
    }
    
    public int getSpo2Data() {
        return this.b;
    }
    
    public void setSpo2Data(final int b) {
        this.b = b;
    }
    
    public int getPrData() {
        return this.c;
    }
    
    public void setPrData(final int c) {
        this.c = c;
    }
}
