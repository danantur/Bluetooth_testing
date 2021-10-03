// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.tools;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
    public static boolean byteArrayEqual(final byte[] array, final byte[] array2) {
        if (array.length > array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static String bytesToHexString(final byte[] array) {
        final StringBuilder sb = new StringBuilder("");
        if (array == null || array.length <= 0) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            final String hexString = Integer.toHexString(array[i] & 0xFF);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
    
    public static String bytesToHexString(final byte b) {
        final byte[] array = { b };
        final StringBuilder sb = new StringBuilder("0x");
        if (array == null || array.length <= 0) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            final String hexString = Integer.toHexString(array[i] & 0xFF);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
    
    public static int byteToInt(final byte b) {
        return b & 0xFF;
    }
    
    public static int dip2px(final Context context, final float n) {
        return (int)(n * context.getResources().getDisplayMetrics().density + 0.5f);
    }
    
    public static int px2dip(final Context context, final float n) {
        return (int)(n / context.getResources().getDisplayMetrics().density + 0.5f);
    }
    
    public static byte[] short2Byte(final short n) {
        return new byte[] { (byte)(n >> 8), (byte)n };
    }
    
    public static void short2Byte(final short n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >> 8);
        array[n2 + 1] = (byte)n;
    }
    
    public static short byte2Short(final byte[] array) {
        return (short)((array[0] & 0xFF) << 8 | (array[1] & 0xFF));
    }
    
    public static short byte2Short(final byte[] array, final int n) {
        return (short)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF));
    }
    
    public static void long2Byte(final long n, final byte[] array, final int n2) {
        array[n2 + 0] = (byte)(n >> 56);
        array[n2 + 1] = (byte)(n >> 48);
        array[n2 + 2] = (byte)(n >> 40);
        array[n2 + 3] = (byte)(n >> 32);
        array[n2 + 4] = (byte)(n >> 24);
        array[n2 + 5] = (byte)(n >> 16);
        array[n2 + 6] = (byte)(n >> 8);
        array[n2 + 7] = (byte)n;
    }
    
    public static long byte2Long(final byte[] array, final int n) {
        return ((long)array[n + 0] & 0xFFL) << 56 | ((long)array[n + 1] & 0xFFL) << 48 | ((long)array[n + 2] & 0xFFL) << 40 | ((long)array[n + 3] & 0xFFL) << 32 | ((long)array[n + 4] & 0xFFL) << 24 | ((long)array[n + 5] & 0xFFL) << 16 | ((long)array[n + 6] & 0xFFL) << 8 | ((long)array[n + 7] & 0xFFL) << 0;
    }
    
    public static long byte2Long(final byte[] array) {
        return (array[0] & 0xFF) << 56 | (array[1] & 0xFF) << 48 | (array[2] & 0xFF) << 40 | (array[3] & 0xFF) << 32 | (array[4] & 0xFF) << 24 | (array[5] & 0xFF) << 16 | (array[6] & 0xFF) << 8 | (array[7] & 0xFF);
    }
    
    public static byte[] long2Byte(final long n) {
        return new byte[] { (byte)(n >> 56), (byte)(n >> 48), (byte)(n >> 40), (byte)(n >> 32), (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)(n >> 0) };
    }
    
    public static int byte2Int(final byte[] array) {
        return (array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF);
    }
    
    public static int byte2Int(final byte[] array, int n) {
        return (array[n++] & 0xFF) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF);
    }
    
    public static byte[] int2Byte(final int n) {
        return new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n };
    }
    
    public static void int2Byte(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >> 24);
        array[n2++] = (byte)(n >> 16);
        array[n2++] = (byte)(n >> 8);
        array[n2++] = (byte)n;
    }
    
    public static void deleteFile(final File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            else if (file.isDirectory()) {
                final File[] listFiles = file.listFiles();
                for (int i = 0; i < listFiles.length; ++i) {
                    deleteFile(listFiles[i]);
                }
            }
            file.delete();
        }
    }
    
    public static boolean copyAssetFileToPath(final Context context, final String child, final String parent) {
        boolean b = false;
        DataInputStream dataInputStream = null;
        FilterOutputStream filterOutputStream = null;
        try {
            dataInputStream = new DataInputStream(context.getResources().getAssets().open(child));
            final byte[] array = new byte[dataInputStream.available()];
            dataInputStream.readFully(array);
            filterOutputStream = new DataOutputStream(new FileOutputStream(new File(parent, child)));
            filterOutputStream.write(array);
            b = true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            b = false;
        }
        finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (filterOutputStream != null) {
                    filterOutputStream.close();
                }
            }
            catch (IOException ex2) {}
        }
        return b;
    }
    
    public static byte[] hexStringToBytes(String upperCase) {
        if (upperCase == null || upperCase.equals("")) {
            return null;
        }
        upperCase = upperCase.toUpperCase();
        final int n = upperCase.length() / 2;
        final char[] charArray = upperCase.toCharArray();
        final byte[] array = new byte[n];
        for (int i = 0; i < n; ++i) {
            final int n2 = i * 2;
            array[i] = (byte)(a(charArray[n2]) << 4 | a(charArray[n2 + 1]));
        }
        return array;
    }
    
    private static byte a(final char ch) {
        return (byte)"0123456789ABCDEF".indexOf(ch);
    }
    
    public static String transferLongToDate(final String pattern, final Long n) {
        return new SimpleDateFormat(pattern).format(new Date(n));
    }
    
    public static class a
    {
        public static <T extends View> T a(final View view, final int n) {
            SparseArray tag = (SparseArray)view.getTag();
            if (tag == null) {
                tag = new SparseArray();
                view.setTag((Object)tag);
            }
            View viewById = (View)tag.get(n);
            if (viewById == null) {
                viewById = view.findViewById(n);
                tag.put(n, (Object)viewById);
            }
            return (T)viewById;
        }
    }
}
