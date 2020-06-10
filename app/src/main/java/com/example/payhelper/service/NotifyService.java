package com.example.payhelper.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.payhelper.util.LogUtil;

public class NotifyService extends NotificationListenerService {

    private LogUtil logUtil;

    @Override
    public void onCreate() {
        logUtil = LogUtil.getInstance(getApplication());
        logUtil.d("创建通知栏监听服务");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        logUtil.d("摧毁通知栏监听服务");
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);

        String packageName = sbn.getPackageName();

        logUtil.d("收到消息,包名:" + packageName);

        Notification notification = sbn.getNotification();
        if (notification == null) {
            logUtil.e("信息错误:notification");
            return;
        }

        Bundle extras = notification.extras;
        if (extras == null) {
            logUtil.e("信息错误:extras");
            return;
        }

        String title = extras.getString(Notification.EXTRA_TITLE, "");
        String content = extras.getString(Notification.EXTRA_TEXT, "");

        logUtil.d("收到消息,标题:[" + title + "],内容:[" + content + "].");

//        switch (packageName) {
//            case "":
//                break;
//        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);

        logUtil.d("删除消息:" + sbn.getPackageName());
    }
}
