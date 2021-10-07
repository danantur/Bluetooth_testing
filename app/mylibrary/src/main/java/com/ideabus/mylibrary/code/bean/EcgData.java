// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class EcgData implements Serializable
{
    public int uploadCount;
    public int currentCount;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int min;
    public int sec;
    public int pr;
    public ArrayList<String> chineseResult;
    public ArrayList<String> englishResult;
    public int size;
    public int[] ecgData;
}
