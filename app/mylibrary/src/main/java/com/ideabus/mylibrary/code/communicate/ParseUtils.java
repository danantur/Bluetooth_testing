// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.communicate;

import com.ideabus.mylibrary.code.bean.SystemParameter;

import java.util.Calendar;

public class ParseUtils
{
    private static short c(final byte[] array) {
        int n = 0;
        for (int i = 0; i < array.length - 1; ++i) {
            n = (short)(n + (array[i] & 0xFF));
        }
        return (short)(n & 0x7F);
    }
    
    public static byte[] a() {
        return new byte[] { -128, 0 };
    }
    
    public static byte[] b() {
        final byte[] array = { -127, 0 };
        array[1] = (byte)c(array);
        return array;
    }
    
    public static byte[] c() {
        final byte[] array = { -126, 0 };
        array[1] = (byte)c(array);
        return array;
    }
    
    public static byte[] d() {
        final byte[] array = new byte[10];
        array[0] = -125;
        final int n = Calendar.getInstance().get(1) - 2000;
        final int n2 = Calendar.getInstance().get(2) + 1;
        final int value = Calendar.getInstance().get(5);
        final int value2 = Calendar.getInstance().get(11);
        final int value3 = Calendar.getInstance().get(12);
        final int value4 = Calendar.getInstance().get(13);
        final int value5 = Calendar.getInstance().get(14);
        array[1] = (byte)(n & 0x7F);
        array[2] = (byte)n2;
        array[3] = (byte)value;
        array[4] = (byte)value2;
        array[5] = (byte)value3;
        array[6] = (byte)value4;
        array[7] = (byte)(value5 & 0x7F);
        array[8] = (byte)(value5 >> 7 & 0x7F);
        array[9] = (byte)c(array);
        return array;
    }
    
    public static byte[] a(final int n, final int n2) {
        final byte[] array = { -124, (byte)(n & 0x7F), (byte)(n2 & 0x7F), 0 };
        array[3] = (byte)c(array);
        return array;
    }
    
    public static byte[] e() {
        final byte[] array = { -122, 0 };
        array[1] = (byte)c(array);
        return array;
    }
    
    public static byte[] f() {
        final byte[] array = { -119, 0 };
        array[1] = (byte)c(array);
        return array;
    }
    
    public static byte[] g() {
        final byte[] array = { -97, 0 };
        array[1] = (byte)c(array);
        return array;
    }
    
    public static byte[] dataStorageBytes(final int n) {
        final byte[] array = { -112, (byte)n, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] b(final int n) {
        final byte[] array = { -111, (byte)n, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] c(final int n) {
        final byte[] array = { -110, (byte)n, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] d(final int n) {
        final byte[] array = { -109, (byte)n, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] e(final int n) {
        final byte[] array = { -108, (byte)n, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] f(final int n) {
        final byte[] array = { -107, (byte)n, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] g(final int n) {
        final byte[] array = { -106, (byte)n, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] realtimeBytes(final int n) {
        final byte[] array = { -101, (byte)n, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] realtimeBytes() {
        return new byte[] { -102, 26 };
    }
    
    public static byte[] i(final int n) {
        final byte[] array = { -96, (byte)n, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] j(final int n) {
        final byte[] array = { -100, (byte)n, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] a(final int n, final int n2, final int n3, final int n4, final int n5) {
        final byte[] array = { -99, (byte)n, (byte)n2, (byte)n3, (byte)n4, (byte)(n5 & 0x7F), (byte)(n5 >> 7 & 0x7F), 0 };
        array[7] = (byte)c(array);
        return array;
    }
    
    public static byte[] b(final int n, final int n2, final int n3, final int n4, final int n5) {
        final byte[] array = { -99, (byte)n, (byte)n2, (byte)n3, (byte)n4, (byte)(n5 & 0x7F), (byte)(n5 >> 7 & 0x7F), (byte)(n5 >> 14 & 0x7F), 0 };
        array[8] = (byte)c(array);
        return array;
    }
    
    public static byte[] k(final int n) {
        final byte[] array = { -95, 0, 0 };
        array[0] = -95;
        array[1] = (byte)n;
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] i() {
        final byte[] array = { -113, 4, 0, 0 };
        array[3] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] a(final SystemParameter.StorageMode storageMode) {
        boolean b = false;
        if (null != storageMode) {
            b = (storageMode != SystemParameter.StorageMode.AUTOMATIC);
        }
        final byte[] array = { -113, 7, (byte)(b ? 1 : 0), 0, 0 };
        array[4] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] j() {
        final byte[] array = { -114, 5, 0 };
        array[2] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] a(final byte[] array) {
        final byte[] array2 = { -65, (byte)(array[0] & 0x7F), (byte)(array[1] & 0x7F), (byte)(array[2] & 0x7F), (byte)(array[3] & 0x7F), (byte)(array[4] & 0x7F), (byte)(array[5] & 0x7F), (byte)(array[6] & 0x7F), (byte)(array[7] & 0x7F), 0 };
        array2[9] = (byte)(c(array2) & 0x7F);
        return array2;
    }
    
    public static byte[] k() {
        final byte[] array = { -114, 6, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] l() {
        final byte[] array = { -114, 7, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] m() {
        final byte[] array = { -114, 9, 0 };
        array[2] = (byte)c(array);
        return array;
    }
    
    public static byte[] n() {
        final byte[] array = { -99, 127, 127, 127, 127, 0, 0, 0 };
        array[7] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] a(final int n, final int n2, final int n3) {
        final byte[] array = { -99, 2, (byte)n, (byte)n2, (byte)n3, 0, 0, 0 };
        array[7] = (byte)(c(array) & 0x7F);
        return array;
    }
    
    public static byte[] b(final byte[] array) {
        final byte[] array2 = new byte[11];
        array2[0] = -113;
        array2[1] = 10;
        byte b = 0;
        for (int i = 0; i < array.length; ++i) {
            b |= (byte)((array[i] & 0x80) >> 7 - i);
        }
        array2[2] = (byte)(b & 0x7F);
        array2[3] = (byte)(array[0] & 0x7F);
        array2[4] = (byte)(array[1] & 0x7F);
        array2[5] = (byte)(array[2] & 0x7F);
        array2[6] = (byte)(array[3] & 0x7F);
        array2[7] = (byte)(array[4] & 0x7F);
        array2[8] = (byte)(array[5] & 0x7F);
        array2[9] = (byte)(array[6] & 0x7F);
        array2[10] = (byte)c(array2);
        return array2;
    }
    
    public static byte[] b(final int n, final int n2) {
        final byte[] array = { -94, (byte)n, (byte)(n2 & 0x7F), (byte)(n2 >> 7 & 0x7F), 0 };
        array[4] = (byte)c(array);
        return array;
    }
    
    public static byte[] c(final int n, final int n2) {
        final byte[] array = { -93, (byte)n, (byte)(n2 & 0x7F), (byte)(n2 >> 7 & 0x7F), 0 };
        array[4] = (byte)c(array);
        return array;
    }
    
    public static byte[] d(final int n, final int n2) {
        final byte[] array = { -92, (byte)n, (byte)(n2 & 0x7F), (byte)(n2 >> 7 & 0x7F), 0 };
        array[4] = (byte)c(array);
        return array;
    }
    
    public static byte[] e(final int n, final int n2) {
        final byte[] array = { -90, (byte)n, (byte)(n2 & 0x7F), (byte)(n2 >> 7 & 0x7F), 0 };
        array[4] = (byte)c(array);
        return array;
    }
    
    public static byte[] l(final int n) {
        final byte[] array = { -123, (byte)(n & 0x7F), (byte)(n >> 7 & 0x7F), (byte)(n >> 14 & 0x7F), 0 };
        array[4] = (byte)c(array);
        return array;
    }
    
    public static byte[] m(final int n) {
        final byte[] array = { -118, (byte)(n & 0x7F), (byte)(n >> 7 & 0x1), 0 };
        array[3] = (byte)c(array);
        return array;
    }
    
    public static byte[] a(final int n, final int n2, final SystemParameter.StepsSensitivity stepsSensitivity) {
        final byte[] array = { -117, (byte)(n & 0x7F), (byte)(n >> 7 & 0x7F), (byte)(n2 & 0x7F), (byte)(n2 >> 7 & 0x1), 0 };
        switch (stepsSensitivity.ordinal()) {
            case 1: {
                final byte[] array2 = array;
                final int n3 = 4;
                array2[n3] |= 0x2;
                break;
            }
            case 2: {
                final byte[] array3 = array;
                final int n4 = 4;
                array3[n4] |= 0x4;
                break;
            }
            case 3: {
                final byte[] array4 = array;
                final int n5 = 4;
                array4[n5] |= 0x6;
                break;
            }
        }
        array[5] = (byte)c(array);
        return array;
    }
}
