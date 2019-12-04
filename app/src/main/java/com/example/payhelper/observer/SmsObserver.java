package com.example.payhelper.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

import com.example.payhelper.utils.SmsUtil;

public class SmsObserver extends ContentObserver {

    private SmsUtil smsUtil;

    public SmsObserver(FragmentActivity activity, Handler handler) {
        super(handler);

        this.smsUtil = new SmsUtil(activity);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        this.smsUtil.fetchData();
    }
}
