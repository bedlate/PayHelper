package com.example.payhelper.util;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.example.payhelper.viewmodel.ConfigModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsUtil {

    public final static Uri SMS_URI = Uri.parse("content://sms/");
    public final static String SMS_RAW_URI = "content://sms/raw";

    private LogUtil logUtil;

    private ConfigModel configModel;
    private ContentResolver contentResolver;
    private OkHttpClient client;

    private static SmsUtil instance;

    public static SmsUtil getInstance(Application application) {
        if (null == instance) {
            synchronized (SmsUtil.class) {
                if (null == instance) {
                    instance = new SmsUtil(application);
                }
            }
        }
        return instance;
    }

    private SmsUtil(Application application) {
        this.logUtil = LogUtil.getInstance(application);
        this.configModel = ConfigModel.getInstance(application);
        this.contentResolver = application.getContentResolver();

        this.client = new OkHttpClient();
    }

    public void fetchData() {
        // todo: 暂时隐藏是否启用短信服务
//        if (!this.configModel.getSmsEnable().getValue()) {
//            return;
//        }

        logUtil.d("收到短信");

        // "_id","thread_id","address","person","date","type","body"
        // date address body
        String[] projection = new String[] {"address", "date", "body"};

        long lastDate = System.currentTimeMillis() - 10 * 60 * 10000;
        if (this.configModel.getLastDate().getValue() > 0) {
            lastDate = this.configModel.getLastDate().getValue();
        }
        String where = " date > " + lastDate;
        Cursor cur = contentResolver.query(SMS_URI, projection, where, null, "date desc");   // SMS_URI, projection, where

        long nowDate = 0;
        JSONArray smsList = new JSONArray();

        if (null != cur) {
            while (cur.moveToNext()) {
                long date = cur.getLong(cur.getColumnIndex("date"));
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndex("body"));

                try {
                    JSONObject smObject = new JSONObject();
                    smObject.put("date", date).put("address", address).put("body", body);
                    smsList.put(smObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (date > nowDate) {
                    nowDate = date;
                }
            }
            cur.close();
        }

        String json = smsList.toString();

        postData(json, lastDate, nowDate);
    }

    private void postData(String data, long lastDate, long nowDate) {

        final long ld = lastDate;
        final long nd = nowDate;

        // 上传数据
        String api = this.configModel.getApi().getValue();
        String url = api + "/pay/guest/mybank/notify";
        String slug = this.configModel.getUsername().getValue();
        String action = "getSmsList";
        if ("[]".equals(data)) {
            action = "pingSms";
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("slug", slug);
            jsonObject.put("action", action);
            jsonObject.put("data", data);
            logUtil.d("request:" + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody formBody = new FormBody.Builder()
                .add("slug", slug)
                .add("action", action)
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
                    if (!response.isSuccessful()) {
                        logUtil.d("response(fail): " + response);
                    } else {
                        String json = response.body().string(); // string()仅能调用一次

                        logUtil.d("response(success): " + json);

                        JSONObject res = new JSONObject(json);
                        int resCode = res.getInt("code");
                        String resMessage = res.getString("message");

                        if (1 == resCode) {
                            if (nd > ld) {
                                configModel.saveLastDate(nd);
                            }
                        } else {
                            logUtil.e(resMessage);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logUtil.e("response(exception): " + e);
                }
            }
        });
    }

}
