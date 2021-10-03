// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;

public class FiveMinStepsData implements Serializable
{
    private int a;
    private int b;
    private int c;
    private int d;
    private short[] e;
    
    public int getLength() {
        return this.a;
    }
    
    public void setLength(final int a) {
        this.a = a;
    }
    
    public int getYear() {
        return this.b;
    }
    
    public void setYear(final int b) {
        this.b = b;
    }
    
    public int getMonth() {
        return this.c;
    }
    
    public void setMonth(final int c) {
        this.c = c;
    }
    
    public int getDay() {
        return this.d;
    }
    
    public void setDay(final int d) {
        this.d = d;
    }
    
    public short[] getStepFiveDataBean() {
        return this.e;
    }
    
    public void setStepFiveDataBean(final short[] e) {
        this.e = e;
    }
}
