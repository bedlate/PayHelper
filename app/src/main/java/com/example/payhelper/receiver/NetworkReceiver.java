package com.example.payhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.model.ConfigModel;

public class NetworkReceiver extends BroadcastReceiver {

    ConfigModel configModel;

    private final String TAG = "config";

    private Boolean networkAvailable;

    private FragmentActivity activity;

    public NetworkReceiver(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null != activeNetwork && activeNetwork.isAvailable()) {
            networkAvailable = true;
        } else {
            networkAvailable = false;
        }

        configModel = ViewModelProviders.of(this.activity).get(ConfigModel.class);
        configModel.getNetworkAvailable().setValue(networkAvailable);
        Log.d(TAG, configModel.getNetworkAvailable().getValue().toString());
    }
}
