package com.example.payhelper.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.example.payhelper.helper.SmsHelper;

public class SmsObserver extends ContentObserver {

    public static Uri SMS_URI = Uri.parse("content://sms/");
    private SmsHelper smsHelper;

    public SmsObserver(FragmentActivity activity, Handler handler) {
        super(handler);

        this.smsHelper = new SmsHelper(activity);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        this.smsHelper.fetchData();
    }
}
