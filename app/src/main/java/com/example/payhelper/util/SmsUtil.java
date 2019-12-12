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
import okhttp3.Response;

public class SmsUtil {

    public final static Uri SMS_URI = Uri.parse("content://sms/");
    public final static String SMS_RAW_URI = "content://sms/raw";

    private LogUtil logUtil;
    private ConfigModel configModel;
    private ContentResolver contentResolver;
    private HttpUtil httpUtil;

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
        this.httpUtil = HttpUtil.getInstance(application);

    }

    public void fetchData() {
        // todo: 暂时隐藏是否启用短信服务
        if (!this.configModel.getSmsEnable().getValue()) {
            logUtil.e("短信监控未开启");
            return;
        }

        logUtil.d("查看短信");

        // "_id","thread_id","address","person","date","type","body"
        // date address body
        String[] projection = new String[] {"address", "date", "body"};

        long lastDate = 0;

        if (this.configModel.getLastDate().getValue() > 0) {
            lastDate = this.configModel.getLastDate().getValue();
        } else {
            lastDate = System.currentTimeMillis() - 3600000;   // 60分钟
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

            logUtil.d("请求数据:" + jsonObject.toString());

            httpUtil.request("/pay/guest/mybank/notify", jsonObject, "POST", new HttpUtil.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    super.onFailure(call, e);

                    logUtil.d("请求错误: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    super.onResponse(call, response);

                    try {
                        JSONObject res = getResult();
                        int resCode = res.getInt("code");
                        String resMessage = res.getString("message");

                        if (1 == resCode) {
                            if (nd > ld) {
                                configModel.saveLastDate(nd);
                            }
                        } else {
                            logUtil.e("响应失败: " + resMessage);
                        }
                    } catch (Exception e) {
                        logUtil.e("响应异常: " + e);
                    }
                }
            });
        } catch (Exception e) {

            logUtil.d("请求异常: " + e.getMessage());
        }

    }

}
