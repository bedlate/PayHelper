package com.example.payhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.payhelper.databinding.ActivityMainBinding;
import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.receiver.NetworkReceiver;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ConfigModel configModel;
    ActivityMainBinding binding;

    private NetworkReceiver networkReceiver;

    private static final int PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configModel = ViewModelProviders.of(this).get(ConfigModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(this);

        // 监听网络状态
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver(this);
        registerReceiver(networkReceiver, intentFilter);

        // 检查权限
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            Log.d("config", "已获得权限");
            configModel.getPermissionAvailable().setValue(true);
        } else {
            Log.d("config", "权限不足,前往设置");
            EasyPermissions.requestPermissions(this, "权限不足,前往设置", PERMISSION_CODE, PERMISSIONS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("config", "同意权限");
        configModel.getPermissionAvailable().setValue(true);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("config", "拒绝权限");

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
            Log.e("config" , "引导设置 申请权限" );
        }
    }
}
