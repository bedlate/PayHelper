package com.example.payhelper;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.databinding.ActivityMainBinding;
import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.observer.SmsObserver;
import com.example.payhelper.receiver.NetworkReceiver;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ConfigModel configModel;
    ActivityMainBinding binding;

    private NetworkReceiver networkReceiver;

    private final String TAG = "pay";

    private static final int PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };

    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configModel = ViewModelProviders.of(this).get(ConfigModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(this);

        // 检查权限
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            Log.d(TAG, "已获得权限");
            configModel.getPermissionAvailable().setValue(true);
        } else {
            Log.d(TAG, "权限不足,前往设置");
            EasyPermissions.requestPermissions(this, "权限不足,前往设置", PERMISSION_CODE, PERMISSIONS);
        }

        // 监听网络状态
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver(this);
        registerReceiver(networkReceiver, intentFilter);

        // 注册监听器
        smsObserver = new SmsObserver(this, new Handler());
        getContentResolver().registerContentObserver(SmsObserver.SMS_URI, true, smsObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 卸载接收器
        unregisterReceiver(networkReceiver);
        // 卸载监听器
        getContentResolver().unregisterContentObserver(smsObserver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "同意权限");
        configModel.getPermissionAvailable().setValue(true);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "拒绝权限");

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
            Log.e(TAG , "引导设置 申请权限" );
        }
    }
}
