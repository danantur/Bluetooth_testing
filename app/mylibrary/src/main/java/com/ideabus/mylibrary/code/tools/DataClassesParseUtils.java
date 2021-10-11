// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.tools;

import com.ideabus.mylibrary.code.bean.DayStepsData;
import com.ideabus.mylibrary.code.bean.EcgData;
import com.ideabus.mylibrary.code.bean.SpO2PointData;
import com.ideabus.mylibrary.code.bean.Spo2Data;
import com.ideabus.mylibrary.code.bean.WaveData;

import java.util.ArrayList;

public class DataClassesParseUtils
{
    private static String c;
    public static String[] a;
    public static String[] b;
    
    public static String parseDateUnit(final int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
    }
    
    public static byte a(final byte[] array) {
        int n = 0;
        for (int i = 0; i < array.length - 1; ++i) {
            n = (byte)(n + (array[i] & 0xFF));
        }
        return (byte)(n & 0x7F);
    }
    
    public static WaveData parseWaveData(final byte[] array) {
        final WaveData g = new WaveData();
        int n = array[1] & 0xF;
        if (n >= 8) {
            n = 8;
        }
        g.signal = n;
        g.prSound = (array[2] & 0x40) >> 6;
        g.waveData = array[3] & 0x7F;
        g.barData = array[4] & 0xF;
        g.fingerOut = (array[4] & 0x10) >> 4;
        return g;
    }
    
    public static Spo2Data parseSpo2Data2(final byte[] array) {
        final Spo2Data spo2Data = new Spo2Data();
        spo2Data.piError = array[2] & 0x1 & 0xFF;
        int n = (array[3] & 0x7F) + (array[2] << 6 & 0x80);
        if (n > 254 || n <= 0) {
            n = -1;
        }
        spo2Data.spo2 = n;
        int n2 = array[4] & 0x7F;
        if (n2 >= 100 || n2 <= 0) {
            n2 = -1;
        }
        spo2Data.pr = n2;
        int n3 = (array[5] & 0x7F) + (array[2] << 5 & 0x80);
        if (n3 > 220 || n3 <= 0) {
            n3 = -1;
        }
        spo2Data.pi = n3;
        return spo2Data;
    }
    
    public static Spo2Data parseSpo2Data(final byte[] array) {
        final Spo2Data spo2Data = new Spo2Data();
        spo2Data.piError = array[2] & 0x1 & 0xFF;
        int n = (array[3] & 0x7F) + (array[2] << 6 & 0x80);
        if (n > 254 || n <= 0) {
            n = -1;
        }
        spo2Data.spo2 = n;
        int n2 = array[4] & 0x7F;
        if (n2 >= 100 || n2 <= 0) {
            n2 = -1;
        }
        spo2Data.pr = n2;
        int n3 = (array[5] & 0xFF) + ((array[6] & 0xFF) << 7);
        if (n3 > 220 || n3 <= 0) {
            n3 = -1;
        }
        spo2Data.pi = n3;
        return spo2Data;
    }
    
    public static SpO2PointData parseSpo2Point(final byte[] array) {
        final SpO2PointData spO2PointData = new SpO2PointData();
        spO2PointData.setDate(parseDateUnit((array[2] & 0x7F) + 2000) + "-" + parseDateUnit(array[3] & 0xF) + "-" + parseDateUnit(array[4] & 0x1F) + " " + parseDateUnit(array[5] & 0x1F) + ":" + parseDateUnit(array[6] & 0x3F) + ":" + parseDateUnit(array[7] & 0x3F));
        spO2PointData.setSpo2Data(array[8] & 0x7F);
        spO2PointData.setPrData(((array[9] & 0x7F) | (array[3] << 1 & 0x80)) & 0xFF);
        return spO2PointData;
    }
    
    public static DayStepsData parseDaySteps(final byte[] array) {
        final int year = (array[2] & 0x7F) + 2000;
        final int month = array[3] & 0xF;
        final int day = array[4] & 0x1F;
        final int calorie = ((((array[6] & 0x7F) | (array[5] & 0x1) << 7) & 0xFF) | (((array[7] & 0x7F) | (array[5] & 0x2) << 6) & 0xFF) << 8) & 0xFFFF;
        final int stepCount = ((((array[8] & 0x7F) | (array[5] & 0x4) << 5) & 0xFF) | (((array[9] & 0x7F) | (array[5] & 0x8) << 4) & 0xFF) << 8) & 0xFFFF;
        final DayStepsData dayStepsData = new DayStepsData();
        dayStepsData.setYear(year);
        dayStepsData.setMonth(month);
        dayStepsData.setDay(day);
        dayStepsData.setCalorie(calorie);
        dayStepsData.setStepCount(stepCount);
        return dayStepsData;
    }
    
    public static DayStepsData parseDayStepsWithTargetCalories(final byte[] array) {
        final int year = (array[2] & 0x7F) + 2000;
        final int month = array[3] & 0xF;
        final int day = array[4] & 0x1F;
        final int calorie = ((((array[6] & 0x7F) | (array[5] & 0x1) << 7) & 0xFF) | (((array[7] & 0x7F) | (array[5] & 0x2) << 6) & 0xFF) << 8) & 0xFFFF;
        final int stepCount = ((((array[8] & 0x7F) | (array[5] & 0x4) << 5) & 0xFF) | (((array[9] & 0x7F) | (array[5] & 0x8) << 4) & 0xFF) << 8) & 0xFFFF;
        final int targetCalories = ((((array[10] & 0x7F) | (array[5] & 0x10) << 3) & 0xFF) | (((array[11] & 0x7F) | (array[5] & 0x20) << 2) & 0xFF) << 8) & 0xFFFF;
        final DayStepsData dayStepsData = new DayStepsData();
        dayStepsData.setYear(year);
        dayStepsData.setMonth(month);
        dayStepsData.setDay(day);
        dayStepsData.setCalorie(calorie);
        dayStepsData.setStepCount(stepCount);
        dayStepsData.setTargetCalories(targetCalories);
        return dayStepsData;
    }
    
    public static short[] h(final byte[] array) {
        final short[] array2 = new short[6];
        final short n = (short)(((array[4] & 0x7F) | (array[2] & 0x1) << 7) & 0xFF);
        final short n2 = (short)(((array[5] & 0x7F) | (array[2] & 0x2) << 6) & 0xFF);
        short n3 = (short)((n | n2 << 8) & 0x7FFF);
        if (n == 255 && n2 == 255) {
            n3 = 0;
        }
        final short n4 = (short)(((array[6] & 0x7F) | (array[2] & 0x4) << 5) & 0xFF);
        final short n5 = (short)(((array[7] & 0x7F) | (array[2] & 0x8) << 4) & 0xFF);
        short n6 = (short)((n4 | n5 << 8) & 0x7FFF);
        if (n4 == 255 && n5 == 255) {
            n6 = 0;
        }
        final short n7 = (short)(((array[8] & 0x7F) | (array[2] & 0x10) << 3) & 0xFF);
        final short n8 = (short)(((array[9] & 0x7F) | (array[2] & 0x20) << 2) & 0xFF);
        short n9 = (short)((n7 | n8 << 8) & 0x7FFF);
        if (n7 == 255 && n8 == 255) {
            n9 = 0;
        }
        final short n10 = (short)(((array[10] & 0x7F) | (array[2] & 0x40) << 1) & 0xFF);
        final short n11 = (short)(((array[11] & 0x7F) | (array[3] & 0x1) << 7) & 0xFF);
        short n12 = (short)((n10 | n11 << 8) & 0x7FFF);
        if (n10 == 255 && n11 == 255) {
            n12 = 0;
        }
        final short n13 = (short)(((array[12] & 0x7F) | (array[3] & 0x2) << 6) & 0xFF);
        final short n14 = (short)(((array[13] & 0x7F) | (array[3] & 0x4) << 5) & 0xFF);
        short n15 = (short)((n13 | n14 << 8) & 0x7FFF);
        if (n13 == 255 && n14 == 255) {
            n15 = 0;
        }
        final short n16 = (short)(((array[14] & 0x7F) | (array[3] & 0x8) << 4) & 0xFF);
        final short n17 = (short)(((array[15] & 0x7F) | (array[3] & 0x10) << 3) & 0xFF);
        short n18 = (short)(n16 | (n17 << 8 & 0x7FFF));
        if (n16 == 255 && n17 == 255) {
            n18 = 0;
        }
        array2[0] = n3;
        array2[1] = n6;
        array2[2] = n9;
        array2[3] = n12;
        array2[4] = n15;
        array2[5] = n18;
        return array2;
    }
    
    public static EcgData parseEcgData(final byte[] array) {
        final EcgData ecgData = new EcgData();
        final int year = (array[1] & 0x7F) + 2000;
        final int month = array[2] & 0xF;
        final int day = array[3] & 0x1F;
        final int hour = array[4] & 0x1F;
        final int min = array[5] & 0x3F;
        final int sec = array[6] & 0x3F;
        final int pr = ((array[7] & 0x7F) | (array[8] & 0x7F) << 7) & 0xFFFF;
        final ArrayList<String> chineseResult = new ArrayList<String>();
        final ArrayList<String> englishResult = new ArrayList<String>();
        int n = array[9] & 0x7F;
        chineseResult.add(DataClassesParseUtils.b[n]);
        englishResult.add(DataClassesParseUtils.a[n]);
        for (int i = 10; i < 15; ++i) {
            if ((array[i] & 0x7F) != n && array[i] != 0) {
                chineseResult.add(DataClassesParseUtils.b[array[i] & 0x7F]);
                englishResult.add(DataClassesParseUtils.a[array[i] & 0x7F]);
                n = (array[i] & 0x7F);
            }
        }
        final int size = ((array[15] & 0x7F) | (array[16] & 0x7F) << 7) & 0xFFFF;
        ecgData.year = year;
        ecgData.month = month;
        ecgData.day = day;
        ecgData.hour = hour;
        ecgData.min = min;
        ecgData.sec = sec;
        ecgData.pr = pr;
        ecgData.chineseResult = chineseResult;
        ecgData.englishResult = englishResult;
        ecgData.size = size;
        return ecgData;
    }
    
    public static int[] j(final byte[] array) {
        for (int i = 4; i < 11; ++i) {
            final int n = i;
            array[n] |= (byte)(array[2] << 11 - i & 0x80);
        }
        for (int j = 11; j < 16; ++j) {
            final int n2 = j;
            array[n2] |= (byte)(array[3] << 18 - j & 0x80);
        }
        final int[] array2 = new int[6];
        int n3 = 0;
        for (int k = 4; k < 16; k += 2) {
            array2[n3] = (((array[k] & 0xFF) | (array[k + 1] & 0xFF) << 8) & 0xFFFF);
            ++n3;
        }
        return array2;
    }
    
    public static String parseDateTimeString(final byte[] array) {
        return parseDateUnit((array[2] & 0x7F) + 2000) + "-" + parseDateUnit(array[3] & 0xF) + "-" + parseDateUnit(array[4] & 0x1F) + " " + parseDateUnit(array[5] & 0x1F) + ":" + parseDateUnit(array[6] & 0x3F) + ":" + parseDateUnit(array[7] & 0x3F);
    }
    
    public static String parseDateTimeString2(final byte[] array) {
        return parseDateUnit((array[4] & 0x7F) + 2000) + "-" + parseDateUnit(array[5] & 0xF) + "-" + parseDateUnit(array[6] & 0x1F) + " " + parseDateUnit(array[7] & 0x1F) + ":" + parseDateUnit(array[8] & 0x3F) + ":" + parseDateUnit(array[9] & 0x3F);
    }
    
    public static short[] spo2Data2Parse(final byte[] array) {
        for (int i = 5; i < 12; ++i) {
            final int n = i;
            array[n] |= (byte)(array[3] << 12 - i & 0x80);
        }
        for (int j = 12; j < 19; ++j) {
            final int n2 = j;
            array[n2] |= (byte)(array[4] << 19 - j & 0x80);
        }
        final short[] array2 = new short[27];
        array2[0] = (short)(array[5] & 0xFF);
        int n3 = 1;
        for (int k = 6; k < 19; ++k) {
            final byte b = (byte)(array[k] & 0xF0);
            final int n4 = b >> 4 & 0x7;
            if (n4 == 7) {
                array2[n3] = 255;
            }
            else if ((b & 0x80) != 0x0) {
                array2[n3] = (short)(array2[n3 - 1] - n4);
            }
            else {
                array2[n3] = (short)(array2[n3 - 1] + n4);
            }
            ++n3;
            final int n5 = array[k] & 0x7;
            if (n5 == 7) {
                array2[n3] = 255;
            }
            else if ((array[k] & 0x8) != 0x0) {
                array2[n3] = (short)(array2[n3 - 1] - n5);
            }
            else {
                array2[n3] = (short)(array2[n3 - 1] + n5);
            }
            ++n3;
        }
        return array2;
    }
    
    public static short[] parseOrdinaryData(final byte[] array) {
        for (int i = 9; i < 16; ++i) {
            final int n = i;
            array[n] |= (byte)(array[7] << 16 - i & 0x80);
        }
        for (int j = 16; j < 23; ++j) {
            final int n2 = j;
            array[n2] |= (byte)(array[8] << 23 - j & 0x80);
        }
        final short[] array2 = new short[27];
        array2[0] = (short)(((array[9] & 0x7F) | (array[7] & 0x1) << 7) & 0xFFFF);
        int n3 = 1;
        for (int k = 10; k < 23; ++k) {
            final byte b = (byte)(array[k] & 0xF0);
            final int n4 = b >> 4 & 0x7;
            if (n4 == 7) {
                array2[n3] = 255;
            }
            else if ((b & 0x80) != 0x0) {
                array2[n3] = (short)(array2[n3 - 1] - n4);
            }
            else {
                array2[n3] = (short)(array2[n3 - 1] + n4);
            }
            ++n3;
            final int n5 = array[k] & 0x7;
            if (n5 == 7) {
                array2[n3] = 255;
            }
            else if ((array[k] & 0x8) != 0x0) {
                array2[n3] = (short)(array2[n3 - 1] - n5);
            }
            else {
                array2[n3] = (short)(array2[n3 - 1] + n5);
            }
            ++n3;
        }
        return array2;
    }
    
    public static short[] parseDataOriginal(final byte[] array) {
        int n = 0;
        final short[] array2 = new short[21];
        for (int i = 8; i < 15; ++i) {
            final int n2 = i;
            array[n2] |= (byte)(array[5] << 15 - i & 0x80);
        }
        for (int j = 15; j < 22; ++j) {
            final int n3 = j;
            array[n3] |= (byte)(array[6] << 22 - j & 0x80);
        }
        for (int k = 22; k < 29; ++k) {
            final int n4 = k;
            array[n4] |= (byte)(array[7] << 29 - k & 0x80);
        }
        for (int l = 8; l < 29; ++l) {
            array2[n] = (short)(array[l] & 0xFF);
            ++n;
        }
        return array2;
    }
    
    public static byte[] p(final byte[] array) {
        for (int i = 8; i < 15; ++i) {
            final int n = i;
            array[n] |= (byte)(array[5] << 15 - i & 0x80);
        }
        for (int j = 15; j < 22; ++j) {
            final int n2 = j;
            array[n2] |= (byte)(array[6] << 22 - j & 0x80);
        }
        for (int k = 22; k < 29; ++k) {
            final int n3 = k;
            array[n3] |= (byte)(array[7] << 29 - k & 0x80);
        }
        return array;
    }
    
    public static byte[] q(final byte[] array) {
        final byte[] array2 = new byte[7];
        for (int i = 0; i < 7; ++i) {
            array2[i] = (byte)((array[3 + i] & 0x7F) | (array[2] << 7 - i & 0x80));
        }
        return array2;
    }
    
    public static short[] r(final byte[] array) {
        int n = 0;
        final short[] array2 = new short[12];
        for (int i = 5; i < 12; ++i) {
            final int n2 = i;
            array[n2] |= (byte)(array[3] << 12 - i & 0x80);
        }
        for (int j = 12; j < 17; ++j) {
            final int n3 = j;
            array[n3] |= (byte)(array[4] << 17 - j & 0x80);
        }
        for (int k = 8; k < 29; ++k) {
            array2[n] = (short)(array[k] & 0xFF);
            ++n;
        }
        return array2;
    }
    
    static {
        DataClassesParseUtils.c = "UnpackManager";
        DataClassesParseUtils.a = new String[] { "No abnormalities", "Possible:Missed beat", "Possible:Accidental VPB", "Possible:VPB trigeminy", "Possible:VPB bigeminy", "Possible:VPB couple", "Possible:VPB runs of 3", "Possible:VPB runs of 4", "Possible:VPB RonT", "Possible:Bradycardia", "Possible:Tachycardia", "Possible:Arrhythmia", "Possible:ST elevation", "Possible:ST depression" };
        DataClassesParseUtils.b = new String[] { "\u672a\u89c1\u5f02\u5e38", "\u53ef\u80fd:\u6f0f\u640f", "\u53ef\u80fd:\u5076\u53d1\u5ba4\u65e9", "\u53ef\u80fd:\u5ba4\u65e9\u4e09\u8054\u5f8b", "\u53ef\u80fd:\u5ba4\u65e9\u4e8c\u8054\u5f8b", "\u53ef\u80fd:\u6210\u5bf9\u5ba4\u65e9", "\u53ef\u80fd:\u5ba4\u65e9\u4e09\u8054\u53d1", "\u53ef\u80fd:\u5ba4\u65e9\u56db\u8054\u53d1", "\u53ef\u80fd:\u5ba4\u65e9 RonT", "\u53ef\u80fd:\u5fc3\u52a8\u8fc7\u7f13", "\u53ef\u80fd:\u5fc3\u52a8\u8fc7\u901f", "\u53ef\u80fd:\u5fc3\u5f8b\u4e0d\u9f50", "\u53ef\u80fd:ST \u62ac\u9ad8", "\u53ef\u80fd:T \u538b\u4f4e" };
    }
}
