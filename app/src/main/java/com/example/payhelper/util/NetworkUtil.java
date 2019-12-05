package com.example.payhelper.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;

        if (null != connectivityManager) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (null != activeNetwork) {
                isConnected = activeNetwork.isAvailable();
            }
        }

        return isConnected;
    }

}
