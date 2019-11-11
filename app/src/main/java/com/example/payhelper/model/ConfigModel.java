package com.example.payhelper.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.payhelper.R;

public class ConfigModel extends AndroidViewModel {

    private MutableLiveData<String> api, username;

    private final String TAG = "config";
    private final String CONFIG_FILE = "config";

    private Application application;
    private String api_key, api_default_value, username_key, username_default_value;

    public ConfigModel(@NonNull Application application) {
        super(application);

        Resources resources = application.getResources();

        this.application = application;
        this.api_key = resources.getString(R.string.api_key);
        this.api_default_value = resources.getString(R.string.api_default_value);
        this.username_key = resources.getString(R.string.username_key);
        this.username_default_value = resources.getString(R.string.username_default_value);

        fetchConfig();
    }

    public MutableLiveData<String> getApi() {
        return api;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    private void fetchConfig() {

        api = new MutableLiveData<>();
        username = new MutableLiveData<>();

        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);

        api.setValue(shp.getString(api_key, api_default_value));
        username.setValue(shp.getString(username_key, username_default_value));

        Log.d(TAG, "fetchConfig: api=" + api.getValue() + ", username=" + username.getValue());
    }

    public void saveConfig() {
        Log.d(TAG, "saveConfig: api=" + api.getValue() + ", username=" + username.getValue());

        SharedPreferences shp = this.application.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();

        editor.putString(api_key, api.getValue());
        editor.putString(username_key, username.getValue());
        editor.commit();
    }

}
