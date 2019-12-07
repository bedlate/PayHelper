package com.example.payhelper.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.example.payhelper.viewmodel.ConfigModel;
import com.example.payhelper.observer.SmsObserver;
import com.example.payhelper.service.DaemonService;
import com.example.payhelper.service.SmsService;

public class ServiceUtil {

    private static ServiceUtil instance;

    private Activity activity;

    private LogUtil logUtil;

    private ConfigModel configModel;

    private Intent smsIntent;
    private Intent daemonIntent;
    private SmsObserver smsObserver;

    private ServiceUtil(FragmentActivity activity) {
        this.logUtil = LogUtil.getInstance(activity.getApplication());
        this.activity = activity;
        this.configModel = ConfigModel.getInstance(activity.getApplication());
    }

    public static ServiceUtil getInstance(FragmentActivity activity) {
        if (null == instance) {
            synchronized (ServiceUtil.class) {
                if (null == instance) {
                    instance = new ServiceUtil(activity);
                }
            }
        }
        return instance;
    }

    public ConfigModel getConfigModel() {
        return this.configModel;
    }


    public void startServices() {
        logUtil.d("启动服务");

        // 短信监听器
        smsObserver = new SmsObserver(activity.getApplication(), new Handler());
        activity.getContentResolver().registerContentObserver(SmsUtil.SMS_URI, true, smsObserver);

        // 启动服务: 后台主动接收短信
        smsIntent = new Intent(activity, SmsService.class);
        activity.startService(smsIntent);

        // 守护进程
        daemonIntent = new Intent(activity.getApplicationContext(), DaemonService.class);
        activity.startService(daemonIntent);

        configModel.getIsRunning().setValue(true);
    }

    public void stopServices() {
        logUtil.d("停止服务");

        // 短信监听器
        activity.getContentResolver().unregisterContentObserver(smsObserver);
        // 停止短信后台服务
        activity.stopService(smsIntent);
        // 停止守护进程服务
        activity.stopService(daemonIntent);

        configModel.getIsRunning().postValue(false);

    }

}
