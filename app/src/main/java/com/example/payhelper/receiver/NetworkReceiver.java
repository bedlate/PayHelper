package com.example.payhelper.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.payhelper.viewmodel.ConfigModel;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.util.NetworkUtil;

public class NetworkReceiver extends BroadcastReceiver {

    private ConfigModel configModel;
    private LogUtil logUtil;

    public NetworkReceiver(Application application) {
        logUtil = LogUtil.getInstance(application);
        configModel = ConfigModel.getInstance(application);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = NetworkUtil.isAvailable(context);

        configModel.getNetworkAvailable().setValue(isConnected);

        logUtil.d("网络状态:" + configModel.getNetworkAvailable().getValue().toString());
    }
}
