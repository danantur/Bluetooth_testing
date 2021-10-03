// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class EcgData implements Serializable
{
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private ArrayList<String> j;
    private ArrayList<String> k;
    private int l;
    private int[] m;
    
    public int getUploadCount() {
        return this.a;
    }
    
    public void setUploadCount(final int a) {
        this.a = a;
    }
    
    public int getCurrentCount() {
        return this.b;
    }
    
    public void setCurrentCount(final int b) {
        this.b = b;
    }
    
    public int getYear() {
        return this.c;
    }
    
    public void setYear(final int c) {
        this.c = c;
    }
    
    public int getMonth() {
        return this.d;
    }
    
    public void setMonth(final int d) {
        this.d = d;
    }
    
    public int getDay() {
        return this.e;
    }
    
    public void setDay(final int e) {
        this.e = e;
    }
    
    public int getHour() {
        return this.f;
    }
    
    public void setHour(final int f) {
        this.f = f;
    }
    
    public int getMin() {
        return this.g;
    }
    
    public void setMin(final int g) {
        this.g = g;
    }
    
    public int getSec() {
        return this.h;
    }
    
    public void setSec(final int h) {
        this.h = h;
    }
    
    public int getPr() {
        return this.i;
    }
    
    public void setPr(final int i) {
        this.i = i;
    }
    
    public ArrayList<String> getChineseResult() {
        return this.j;
    }
    
    public void setChineseResult(final ArrayList<String> j) {
        this.j = j;
    }
    
    public ArrayList<String> getEnglishResult() {
        return this.k;
    }
    
    public void setEnglishResult(final ArrayList<String> k) {
        this.k = k;
    }
    
    public int getSize() {
        return this.l;
    }
    
    public void setSize(final int l) {
        this.l = l;
    }
    
    public int[] getEcgData() {
        return this.m;
    }
    
    public void setEcgData(final int[] m) {
        this.m = m;
    }
}
