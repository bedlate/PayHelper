package com.example.payhelper.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.payhelper.model.ConfigModel;
import com.example.payhelper.model.SmsObject;
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

public class SmsUtil {

    public final static Uri SMS_URI = Uri.parse("content://sms/inbox");

    private ConfigModel configModel;
    private ContentResolver contentResolver;
    private OkHttpClient client;

    private final String TAG = "pay";

    public SmsUtil(FragmentActivity activity) {
        this.configModel = ViewModelProviders.of(activity).get(ConfigModel.class);
        this.contentResolver = activity.getContentResolver();

        client = new OkHttpClient();
    }

    public SmsUtil(ConfigModel configModel, ContentResolver contentResolver) {
        this.configModel = configModel;
        this.contentResolver = contentResolver;

        client = new OkHttpClient();
    }

    public void fetchData() {
        if (!this.configModel.getSmsEnable().getValue()) {
            return;
        }

        // todo
        Log.d(TAG, "收到短信");

        // "_id","thread_id","address","person","date","type","body"
        // date address body
        String[] projection = new String[] {"address", "date", "body"};

        long lastDate = System.currentTimeMillis() - 10 * 60 * 10000;
        if (this.configModel.getLastDate().getValue() > 0) {
            lastDate = this.configModel.getLastDate().getValue();
        }
        String where = " date >  " + lastDate;
        Cursor cur = contentResolver.query(SMS_URI, projection, where, null, "date desc");   // SMS_URI, projection, where

        ArrayList<SmsObject> smsList = new ArrayList<SmsObject>();
        long nowDate = 0;

        if (null != cur) {
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
        }

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
        String slug = this.configModel.getUsername().getValue();

        RequestBody formBody = new FormBody.Builder()
                .add("slug", slug)
                .add("action", "getSmsList")
                .add("data", data)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

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
