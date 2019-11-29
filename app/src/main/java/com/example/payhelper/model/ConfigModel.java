package com.example.payhelper.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.payhelper.R;

public class ConfigModel extends AndroidViewModel {

    private MutableLiveData<String> api, username;
    private MutableLiveData<Boolean> smsEnable;
    private MutableLiveData<Boolean> networkAvailable;

    private final String TAG = "config";
    private final String CONFIG_FILE = "config";

    private Application application;
    private String apiKey, apiDefaultValue, usernameKey, usernameDefaultValue, smsEnableKey;
    private Boolean smsEnableDefaultValue;

    public ConfigModel(@NonNull Application application) {
        super(application);

        Resources resources = application.getResources();

        this.application = application;

        this.api = new MutableLiveData<String>();
        this.username = new MutableLiveData<String>();
        this.smsEnable = new MutableLiveData<Boolean>();
        this.networkAvailable = new MutableLiveData<Boolean>();

        this.apiKey = resources.getString(R.string.api_key);
        this.apiDefaultValue = resources.getString(R.string.api_default_value);
        this.usernameKey = resources.getString(R.string.username_key);
        this.usernameDefaultValue = resources.getString(R.string.username_default_value);
        this.smsEnableKey = resources.getString(R.string.sms_enable_key);
        this.smsEnableDefaultValue = resources.getBoolean(R.bool.sms_enable_default_value);

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

    private void fetchConfig() {
        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);

        api.setValue(shp.getString(apiKey, apiDefaultValue));
        username.setValue(shp.getString(usernameKey, usernameDefaultValue));
        smsEnable.setValue(shp.getBoolean(smsEnableKey, smsEnableDefaultValue));

        networkAvailable.setValue(false);

        Log.d(TAG, "fetchConfig: api=" + api.getValue() + ", username=" + username.getValue() + ", sms_enable=" + smsEnable.getValue().toString());
    }

    public void saveConfig() {
        Log.d(TAG, "saveConfig: api=" + api.getValue() + ", username=" + username.getValue() + ", sms_enable=" + smsEnable.getValue().toString());

        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();

        editor.putString(apiKey, api.getValue());
        editor.putString(usernameKey, username.getValue());
        editor.putBoolean(smsEnableKey, smsEnable.getValue());
        editor.commit();

        Toast.makeText(this.application.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
    }

}
