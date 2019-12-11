package com.example.payhelper.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.example.payhelper.viewmodel.ConfigModel;
import com.example.payhelper.observer.SmsObserver;
import com.example.payhelper.service.DaemonService;
import com.example.payhelper.service.SmsService;

public class ServiceUtil {

    private static ServiceUtil instance;

    private Application application;

    private LogUtil logUtil;

    private ConfigModel configModel;

    private Intent smsIntent;
    private Intent daemonIntent;
    private SmsObserver smsObserver;

    private ServiceUtil(Application application) {
        this.application = application;

        this.logUtil = LogUtil.getInstance(application);
        this.configModel = ConfigModel.getInstance(application);
    }

    public static ServiceUtil getInstance(Application application) {
        if (null == instance) {
            synchronized (ServiceUtil.class) {
                if (null == instance) {
                    instance = new ServiceUtil(application);
                }
            }
        }
        return instance;
    }


    public void startServices() {
        logUtil.d("启动服务");

        // 短信监听器
        smsObserver = new SmsObserver(application, new Handler());
        application.getContentResolver().registerContentObserver(SmsUtil.SMS_URI, true, smsObserver);

        // 启动服务: 后台主动接收短信
        smsIntent = new Intent(application, SmsService.class);
        application.startService(smsIntent);

        // 守护进程
        daemonIntent = new Intent(application, DaemonService.class);
        application.startService(daemonIntent);

        configModel.getIsRunning().setValue(true);
    }

    public void stopServices() {
        logUtil.d("停止服务");

        // 短信监听器
        application.getContentResolver().unregisterContentObserver(smsObserver);
        // 停止短信后台服务
        application.stopService(smsIntent);
        // 停止守护进程服务
        application.stopService(daemonIntent);

        configModel.getIsRunning().postValue(false);

    }

}
