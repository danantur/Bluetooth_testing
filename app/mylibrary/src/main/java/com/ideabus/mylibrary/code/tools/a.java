// 
// Decompiled by Procyon v0.5.36
// 

package com.ideabus.mylibrary.code.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class a
{
    public static byte[] a(final byte[] input) {
        final byte[] array = new byte[8];
        try {
            final byte[] digest = MessageDigest.getInstance("MD5").digest(input);
            for (int i = 0; i < 8; ++i) {
                array[i] = (byte)((digest[i] ^ digest[i + 8]) & 0x7F);
            }
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return array;
    }
}
