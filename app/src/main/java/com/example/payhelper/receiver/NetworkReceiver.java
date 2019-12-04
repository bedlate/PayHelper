package com.example.payhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.model.ConfigModel;

public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "pay";

    private ConfigModel configModel;

    public NetworkReceiver(FragmentActivity activity) {
        configModel = ViewModelProviders.of(activity).get(ConfigModel.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        Boolean networkAvailable = false;

        if (null != activeNetwork && activeNetwork.isAvailable()) {
            networkAvailable = true;
        }
        configModel.getNetworkAvailable().setValue(networkAvailable);
        Log.d(TAG, "网络状态:" + configModel.getNetworkAvailable().getValue().toString());
    }
}
