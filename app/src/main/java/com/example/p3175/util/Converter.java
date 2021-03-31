package com.example.p3175.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class Converter {

    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.d("tttt", "static initializer: " + e.getMessage());
        }
    }

    //region BIG DECIMAL <-> LONG

    public static BigDecimal longToBigDecimal(long value) {
        return new BigDecimal(value).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static long bigDecimalToLong(BigDecimal bigDecimal) {
        return bigDecimal == null ? null : bigDecimal.multiply(new BigDecimal(100)).longValue();
    }
    //endregion

    //region BIG DECIMAL <-> STRING

    public static BigDecimal stringToBigDecimal(String string) {
        return new BigDecimal(string);
    }

    public static String bigDecimalToString(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }


    //region LOCAL DATE <-> STRING

    public static LocalDate stringToLocalDate(String value) {
        try {
            return value == null ? null : LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String localDateToString(LocalDate date) {
        return date == null ? null : date.toString();
    }
    //endregion


    //region FOR PASSWORD

    /**
     * Convert a String to MD5
     */
    public static String toMd5(String plaintext) {
        String encoded;
        messageDigest.update(plaintext.getBytes());
        encoded = new String(messageDigest.digest());
        return encoded;
    }
    //endregion
}
