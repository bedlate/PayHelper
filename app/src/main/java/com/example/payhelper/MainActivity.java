package com.example.payhelper;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.payhelper.receiver.NetworkReceiver;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.viewmodel.ConfigModel;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private LogUtil logUtil;
    private ConfigModel configModel;
    private InputMethodManager inputMethodManager;

    private NetworkReceiver networkReceiver;

    private static final int PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.FOREGROUND_SERVICE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logUtil = LogUtil.getInstance(getApplication());

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        configModel = ConfigModel.getInstance(getApplication());

        setContentView(R.layout.activity_main);

        // 导航返回键
        NavController controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, controller);

        this.checkPermission();

        this.registerReceivers();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.fragment);
        return controller.navigateUp();
//        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceivers();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            hiddenInput();
        }

        return super.onTouchEvent(event);
    }

    // 隐藏输入法
    public void hiddenInput() {
        View view = getCurrentFocus();
        if(null != view && null != view.getWindowToken()){

            view.clearFocus();

            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
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

    private void checkPermission() {
        boolean hasPermissions = EasyPermissions.hasPermissions(this, PERMISSIONS);
        configModel.getPermissionAvailable().setValue(hasPermissions);
        logUtil.d("检查权限状态:" + String.valueOf(hasPermissions));

        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "权限不足,前往设置", PERMISSION_CODE, PERMISSIONS);
        } else if (!isNotificationServiceEnable()) {
            gotoNotificationAccessSetting();
        }
    }

    // 是否授权启用通知栏
    private boolean isNotificationServiceEnable() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }

    // 前往通知栏授权配置，授权后自动启动服务，无需手动启用服务
    public void gotoNotificationAccessSetting() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
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
