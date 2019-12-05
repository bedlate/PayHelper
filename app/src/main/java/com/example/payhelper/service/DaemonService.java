package com.example.payhelper.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.payhelper.MainActivity;
import com.example.payhelper.R;

public class DaemonService extends Service {

    private final static String TAG = "pay";

    private final static int FOREGROUND_ID = 1000;

    @Override
    public void onCreate() {
        Log.i(TAG, "创建守护进程");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "启动守护进程");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getResources().getString(R.string.app_name));
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "摧毁守护进程");
        super.onDestroy();
    }
}