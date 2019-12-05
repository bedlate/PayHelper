package com.example.payhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.util.NetworkUtil;

public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "pay";

    private ConfigModel configModel;

    public NetworkReceiver(FragmentActivity activity) {
        configModel = ViewModelProviders.of(activity).get(ConfigModel.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = NetworkUtil.isAvailable(context);

        configModel.getNetworkAvailable().setValue(isConnected);

        Log.d(TAG, "网络状态:" + configModel.getNetworkAvailable().getValue().toString());
    }
}
