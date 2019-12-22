package com.example.payhelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.payhelper.MainActivity;
import com.example.payhelper.R;
import com.example.payhelper.util.LogUtil;

public class DaemonService extends Service {

    private final static int FOREGROUND_ID = 1000;

    private LogUtil logUtil;

    @Override
    public void onCreate() {
        logUtil = LogUtil.getInstance(getApplication());
        logUtil.d("创建守护进程");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logUtil.d("启动守护进程");

        String channel = createChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getResources().getString(R.string.app_name_real));
        builder.setContentText(getResources().getString(R.string.app_description));
        builder.setContentInfo(getResources().getString(R.string.app_version));
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(FOREGROUND_ID, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    private synchronized String createChannel() {
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            String id = "com.example.payhelper." + "Daemon_SERVICE";
            String name = "Daemon Service";

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                stopSelf();
            }
            return id;
        }

        return "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        logUtil.d("摧毁守护进程");
        super.onDestroy();
    }
}