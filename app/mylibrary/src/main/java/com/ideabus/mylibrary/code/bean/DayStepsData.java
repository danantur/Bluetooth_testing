// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;

public class DayStepsData implements Serializable
{
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    
    public int getYear() {
        return this.a;
    }
    
    public void setYear(final int a) {
        this.a = a;
    }
    
    public int getMonth() {
        return this.b;
    }
    
    public void setMonth(final int b) {
        this.b = b;
    }
    
    public int getDay() {
        return this.c;
    }
    
    public void setDay(final int c) {
        this.c = c;
    }
    
    public int getCalorie() {
        return this.d;
    }
    
    public void setCalorie(final int d) {
        this.d = d;
    }
    
    public int getStepCount() {
        return this.e;
    }
    
    public void setStepCount(final int e) {
        this.e = e;
    }
    
    public int getTargetCalories() {
        return this.f;
    }
    
    public void setTargetCalories(final int f) {
        this.f = f;
    }
}
