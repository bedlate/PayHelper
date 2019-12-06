package com.example.payhelper.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.util.NetworkUtil;

public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "pay";

    private ConfigModel configModel;

    public NetworkReceiver(Application application) {
        configModel = ConfigModel.getInstance(application);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = NetworkUtil.isAvailable(context);

        configModel.getNetworkAvailable().setValue(isConnected);

        Log.d(TAG, "网络状态:" + configModel.getNetworkAvailable().getValue().toString());
    }
}
