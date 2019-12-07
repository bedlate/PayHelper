package com.example.payhelper;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.payhelper.databinding.ActivityMainBinding;
import com.example.payhelper.viewmodel.ConfigModel;
import com.example.payhelper.receiver.NetworkReceiver;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.util.PermissionUtil;
import com.example.payhelper.util.ServiceUtil;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ConfigModel configModel;

    private LogUtil logUtil;

    private NetworkReceiver networkReceiver;

    private static final int PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logUtil = LogUtil.getInstance(getApplication());

        configModel = ConfigModel.getInstance(getApplication());
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(this);

        this.checkPermission();

        this.registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceivers();
    }

    private void registerReceivers() {
        logUtil.d("注册接收器");

        // 网络状态接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver(getApplication());
        registerReceiver(networkReceiver, intentFilter);
    }

    private void unregisterReceivers() {
        logUtil.d("卸载接收器");

        // 网络状态接收器
        unregisterReceiver(networkReceiver);
    }

    public void onToggleService(View v) {
        boolean isRunning = configModel.getIsRunning().getValue();
        if (isRunning) {
            ServiceUtil.getInstance(this).stopServices();

        } else {
            ServiceUtil.getInstance(this).startServices();
        }
    }

    public void onGotoPermission(View v) {
        PermissionUtil.getInstance(getApplicationContext()).gotoPermission();
    }

    public void onMoveLogFile(View v) {
        logUtil.move();
    }

    public void onClearLogFile(View v) {
        logUtil.clear();
    }

    private void checkPermission() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, PERMISSIONS);
        configModel.getPermissionAvailable().setValue(hasPermissions);
        logUtil.d("检查权限状态=" + String.valueOf(hasPermissions));

        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "权限不足,前往设置", PERMISSION_CODE, PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        logUtil.d("同意权限");
        configModel.getPermissionAvailable().setValue(true);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        logUtil.d("拒绝权限");

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
            logUtil.e("引导设置 申请权限" );
        }
    }
}
