package com.example.payhelper.observer;

import android.app.Application;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.example.payhelper.util.SmsUtil;

public class SmsObserver extends ContentObserver {

    private SmsUtil smsUtil;

    public SmsObserver(Application application, Handler handler) {
        super(handler);

        this.smsUtil = SmsUtil.getInstance(application);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);

        if(!uri.toString().equals(SmsUtil.SMS_RAW_URI)){
            Log.d("pay", "uri: " + uri.toString());
            this.smsUtil.fetchData();
        }
    }
}
