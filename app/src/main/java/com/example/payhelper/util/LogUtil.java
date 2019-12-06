package com.example.payhelper.util;

import android.util.Log;

public class LogUtil {

    private static String TAG = "payhelper";

    public static int e(String msg) {
        return Log.e(TAG, msg);
    }

    public static int w(String msg) {
        return Log.w(TAG, msg);
    }

    public static int i(String msg) {
        return Log.d(TAG, msg);
    }

    public static int d(String msg) {
        return Log.d(TAG, msg);
    }

    public static int v(String msg) {
        return Log.d(TAG, msg);
    }

}
