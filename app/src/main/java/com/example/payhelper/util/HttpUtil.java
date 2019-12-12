package com.example.payhelper.util;

import android.app.Application;
import android.util.Log;

import com.example.payhelper.viewmodel.ConfigModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private final int READ_TIMEOUT = 5;
    private final int WRITE_TIMEOUT = 5;
    private final int CONNECT_TIMEOUT = 5;

    private static HttpUtil instance;

    private OkHttpClient client;
    private ConfigModel configModel;

    private HttpUtil(Application application) {

        configModel = ConfigModel.getInstance(application);

        this.client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public static HttpUtil getInstance(Application application) {
        if (null == instance) {
            synchronized (HttpUtil.class) {
                if (null == instance) {
                    instance = new HttpUtil(application);
                }
            }
        }
        return instance;
    }

    public void request(String path, JSONObject data, String method, Callback callback) {
        try {
            String url = configModel.getApi().getValue();

            if (!url.endsWith("/") && !path.startsWith("/")) {
                url += "/";
            }

            url += path;

            Log.d("config", "request: url=" + url + ", method=" + method + ", data=" + data.toString());

            Request.Builder requestBuild = new Request.Builder();

            Iterator keys = data.keys();

            if ("POST".equals(method.toUpperCase())) {
                FormBody.Builder formBuild = new FormBody.Builder();

                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String value = data.get(key).toString();
                    formBuild.add(key, value);
                }

                FormBody formBody = formBuild.build();

                requestBuild = requestBuild.post(formBody);
            } else {
                url += "?";
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String value = data.get(key).toString();
                    url += key + "=" + value + "&";
                }
                url = url.substring(0, url.length() - 1);
            }

            Request request = requestBuild.url(url).build();

            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class Callback implements okhttp3.Callback {

        private JSONObject result = new JSONObject();

        public JSONObject getResult() {
            return result;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            try {
                result.put("code", 0);
                result.put("message", e.getMessage());
                result.put("data", null);
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            try {
                if (response.isSuccessful()) {
                    String json = response.body().string(); // string()仅能调用一次
                    result = new JSONObject(json);
                } else {
                    result.put("code", 0);
                    result.put("message", response.toString());
                    result.put("data", null);
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    result.put("code", 0);
                    result.put("message", e.getMessage());
                    result.put("data", null);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }
    }
}
