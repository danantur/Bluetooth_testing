// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;

public class PieceData implements Serializable
{
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private String f;
    private int[] g;
    private int[] h;
    private int[] i;
    
    public PieceData() {
        this.a = 0;
        this.b = 0;
        this.c = 0;
        this.d = 0;
        this.e = 0;
        this.f = "";
        this.g = null;
        this.h = null;
        this.i = null;
    }
    
    public int getDataType() {
        return this.a;
    }
    
    public void setDataType(final int a) {
        this.a = a;
    }
    
    public int getTotalNumber() {
        return this.b;
    }
    
    public void setTotalNumber(final int b) {
        this.b = b;
    }
    
    public int getCaseCount() {
        return this.c;
    }
    
    public void setCaseCount(final int c) {
        this.c = c;
    }
    
    public int getSupportPI() {
        return this.d;
    }
    
    public void setSupportPI(final int d) {
        this.d = d;
    }
    
    public int getLength() {
        return this.e;
    }
    
    public void setLength(final int e) {
        this.e = e;
    }
    
    public String getStartTime() {
        return this.f;
    }
    
    public void setStartTime(final String f) {
        this.f = f;
    }
    
    public int[] getSpo2Data() {
        return this.g;
    }
    
    public void setSpo2Data(final int[] g) {
        this.g = g;
    }
    
    public int[] getPrData() {
        return this.h;
    }
    
    public void setPrData(final int[] h) {
        this.h = h;
    }
    
    public int[] getPiData() {
        return this.i;
    }
    
    public void setPiData(final int[] i) {
        this.i = i;
    }
}
