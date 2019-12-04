package com.example.payhelper.helper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.model.SmsObject;
import com.example.payhelper.observer.SmsObserver;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SmsHelper {

    private FragmentActivity activity;
    private ConfigModel configModel;

    private final String TAG = "pay";

    public SmsHelper(FragmentActivity activity) {
        this.activity = activity;
        this.configModel = ViewModelProviders.of(activity).get(ConfigModel.class);

        fetchData();
    }

    public void fetchData() {
        if (!this.configModel.getSmsEnable().getValue()) {
            return;
        }

        // todo
        Log.d(TAG, "收到短信");

        ContentResolver cr = this.activity.getContentResolver();
        // "_id","thread_id","address","person","date","type","body"
        // date address body
        String[] projection = new String[] {"address", "date", "body"};

        long lastDate = System.currentTimeMillis() - 10 * 60 * 10000;
//        if (this.configModel.getLastDate().getValue() > 0) {
//            lastDate = this.configModel.getLastDate().getValue();
//        }
        String where = " date >  " + lastDate;
        Cursor cur = cr.query(SmsObserver.SMS_URI, projection, where, null, "date desc");   // SMS_URI, projection, where

        if (null == cur) {
            return;
        }

        long nowDate = 0;
        ArrayList<SmsObject> smsList = new ArrayList<SmsObject>();
        while (cur.moveToNext()) {
            long date = cur.getLong(cur.getColumnIndex("date"));
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndex("body"));

            SmsObject smsObject = new SmsObject();
            smsObject.date = date;
            smsObject.body = body;
            smsObject.address = address;

            smsList.add(smsObject);

            if (date > nowDate) {
                nowDate = date;
            }
        }
        cur.close();

        Gson gson = new Gson();
        String json = gson.toJson(smsList);

        postData(json, lastDate, nowDate);
    }

    private void postData(String data, long lastDate, long nowDate) {

        final long ld = lastDate;
        final long nd = nowDate;

        // 上传数据
        String api = this.configModel.getApi().getValue();
        String url = api + "/pay/guest/mybank/notify";
        String username = this.configModel.getUsername().getValue();

        RequestBody formBody = new FormBody.Builder()
                .add("slug", username)
                .add("action", "getSmsList")
                .add("data", data)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "fetchData(fail): " + response);
                    } else {
                        Log.d(TAG, "fetchData(success): " + responseBody.string());

                        if (nd > ld) {
                            configModel.saveLastDate(nd);
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, "fetchData(exception): " + e);
                }
            }
        });
    }

}
