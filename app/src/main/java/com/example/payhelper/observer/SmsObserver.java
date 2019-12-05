package com.example.payhelper.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.payhelper.util.SmsUtil;

public class SmsObserver extends ContentObserver {

    private SmsUtil smsUtil;

    public SmsObserver(FragmentActivity activity, Handler handler) {
        super(handler);

        this.smsUtil = SmsUtil.getInstance(activity.getApplication(), activity.getContentResolver());
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
