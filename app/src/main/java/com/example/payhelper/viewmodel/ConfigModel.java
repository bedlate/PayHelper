package com.example.payhelper.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.payhelper.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigModel extends AndroidViewModel {

    private MutableLiveData<String> api, username;
    private MutableLiveData<Boolean> smsEnable;
    private MutableLiveData<Boolean> networkAvailable;
    private MutableLiveData<Boolean> permissionAvailable;
    private MutableLiveData<Long> lastDate;
    private MutableLiveData<Boolean> isRunning;
    private MutableLiveData<Boolean> logEnable;

    private final String CONFIG_FILE = "config";

    private Application application;
    private String apiKey, apiDefaultValue, usernameKey, usernameDefaultValue, smsEnableKey, lastDateKey, lastDateDefaultValue, logEnableKey;
    private Boolean smsEnableDefaultValue, logEnableDefaultValue;

    private static ConfigModel instance;

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api", api.getValue());
            jsonObject.put("username", username.getValue());
            jsonObject.put("sms_enable", smsEnable.getValue());
            jsonObject.put("network_available", networkAvailable.getValue());
            jsonObject.put("permission_available", permissionAvailable.getValue());
            jsonObject.put("last_date", lastDate.getValue());
            jsonObject.put("is_running", isRunning.getValue());
            jsonObject.put("log_enable", logEnable.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static ConfigModel getInstance(Application application) {
        if (null == instance) {
            synchronized (ConfigModel.class) {
                if (null == instance) {
                    instance = new ConfigModel(application);
                }
            }
        }
        return instance;
    }

    public ConfigModel(@NonNull Application application) {
        super(application);

        Resources resources = application.getResources();

        this.application = application;

        this.api = new MutableLiveData<String>();
        this.username = new MutableLiveData<String>();
        this.smsEnable = new MutableLiveData<Boolean>();
        this.networkAvailable = new MutableLiveData<Boolean>();
        this.permissionAvailable = new MutableLiveData<Boolean>();
        this.lastDate = new MutableLiveData<Long>();
        this.isRunning = new MutableLiveData<Boolean>();
        this.logEnable = new MutableLiveData<Boolean>();

        this.apiKey = resources.getString(R.string.api_key);
        this.apiDefaultValue = resources.getString(R.string.api_default_value);
        this.usernameKey = resources.getString(R.string.username_key);
        this.usernameDefaultValue = resources.getString(R.string.username_default_value);
        this.smsEnableKey = resources.getString(R.string.sms_enable_key);
        this.smsEnableDefaultValue = resources.getBoolean(R.bool.sms_enable_default_value);
        this.lastDateKey = resources.getString(R.string.last_date_key);
        this.lastDateDefaultValue = resources.getString(R.string.last_date_default_value);
        this.logEnableKey = resources.getString(R.string.log_enable_key);
        this.logEnableDefaultValue = resources.getBoolean(R.bool.log_enable_default_value);

        fetchConfig();
    }

    public MutableLiveData<String> getApi() {
        return api;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<Boolean> getSmsEnable() {
        return smsEnable;
    }

    public MutableLiveData<Boolean> getNetworkAvailable() {
        return networkAvailable;
    }

    public MutableLiveData<Boolean> getPermissionAvailable() {
        return permissionAvailable;
    }

    public MutableLiveData<Long> getLastDate() {
        return lastDate;
    }

    public MutableLiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public MutableLiveData<Boolean> getLogEnable() {
        return logEnable;
    }

    private void fetchConfig() {
        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);

        api.setValue(shp.getString(apiKey, apiDefaultValue));
        username.setValue(shp.getString(usernameKey, usernameDefaultValue));
        smsEnable.setValue(shp.getBoolean(smsEnableKey, smsEnableDefaultValue));

        networkAvailable.setValue(false);
        permissionAvailable.setValue(false);

        lastDate.setValue(Long.parseLong(shp.getString(lastDateKey, lastDateDefaultValue)));
        isRunning.setValue(false);
        logEnable.setValue(shp.getBoolean(logEnableKey, logEnableDefaultValue));
    }

    public void saveConfig() {

        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();

        editor.putString(apiKey, api.getValue());
        editor.putString(usernameKey, username.getValue());
        editor.putBoolean(smsEnableKey, smsEnable.getValue());
        editor.commit();
    }

    public void saveLastDate(long date) {
        lastDate.postValue(date);
        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(lastDateKey, String.valueOf(date));
        editor.commit();
    }

    public void toggleLogStatus() {
        boolean status = !logEnable.getValue();

        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();

        editor.putBoolean(logEnableKey, status);

        editor.commit();

        logEnable.setValue(status);
    }

}
