package com.example.payhelper;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.databinding.ActivityMainBinding;
import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.observer.SmsObserver;
import com.example.payhelper.receiver.NetworkReceiver;
import com.example.payhelper.service.DaemonService;
import com.example.payhelper.service.SmsService;
import com.example.payhelper.util.NetworkUtil;
import com.example.payhelper.util.PermissionUtil;
import com.example.payhelper.util.ServiceUtil;
import com.example.payhelper.util.SmsUtil;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ConfigModel configModel;

    private NetworkReceiver networkReceiver;
    private Intent smsIntent;
    private Intent daemonIntent;

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
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(this);

        this.checkPermission();

        this.registerReceivers();

        this.checkService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceivers();
    }

    private void registerReceivers() {
        Log.d(TAG, "注册接收器");

        // 网络状态接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver(this);
        registerReceiver(networkReceiver, intentFilter);
    }

    private void unregisterReceivers() {
        Log.d(TAG, "卸载接收器");

        // 网络状态接收器
        unregisterReceiver(networkReceiver);
    }

    public void onToggleService(View v) {
        boolean isRunning = configModel.getIsRunning().getValue();
        if (isRunning) {
            this.stopServices();
        } else {
            this.startServices();
        }
    }

    public void onGotoPermission(View v) {

        PermissionUtil.getInstance(getApplicationContext()).gotoPermission();
    }

    private void startServices() {
        Log.d(TAG, "启动服务");

        // 短信监听器
        smsObserver = new SmsObserver(getApplication(), new Handler());
        getContentResolver().registerContentObserver(SmsUtil.SMS_URI, true, smsObserver);

        // 启动服务: 后台主动接收短信
        smsIntent = new Intent(this, SmsService.class);
        startService(smsIntent);

        // 守护进程
        daemonIntent = new Intent(getApplicationContext(), DaemonService.class);
        startService(daemonIntent);

        configModel.getIsRunning().setValue(true);
    }

    private void stopServices() {
        Log.d(TAG, "停止服务");

//        // 短信监听器
//        getContentResolver().unregisterContentObserver(smsObserver);
//        // 停止短信后台服务
//        stopService(smsIntent);
//        // 停止守护进程服务
//        stopService(daemonIntent);
//
//        configModel.getIsRunning().setValue(false);
        Toast.makeText(getApplicationContext(), "服务太猛，无法关闭，请在应用管理中强制结束应用", Toast.LENGTH_LONG).show();

        Process.killProcess(Process.myPid());
    }

    private void checkPermission() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, PERMISSIONS);
        configModel.getPermissionAvailable().setValue(hasPermissions);
        Log.d(TAG, "检查权限状态=" + String.valueOf(hasPermissions));

        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "权限不足,前往设置", PERMISSION_CODE, PERMISSIONS);
        }
    }

    private void checkNetwork() {
        boolean isAvailable = NetworkUtil.isAvailable(getApplicationContext());
        configModel.getNetworkAvailable().setValue(isAvailable);
        Log.d(TAG, "检查网络状态=" + String.valueOf(isAvailable));
    }

    private void checkService() {
        boolean smsRunning = ServiceUtil.isRunning(SmsService.class, getApplicationContext());
        configModel.getIsRunning().setValue(smsRunning);
        Log.d(TAG, "检查服务状态=" + String.valueOf(smsRunning));
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
