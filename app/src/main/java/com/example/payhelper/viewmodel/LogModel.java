package com.example.payhelper.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class LogModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> logs;
    private MutableLiveData<String> logText;

    private static LogModel instance;

    public LogModel(@NonNull Application application) {
        super(application);

        this.logs = new MutableLiveData<ArrayList<String>>();
        this.logText = new MutableLiveData<String>();
    }

    public static LogModel getInstance(Application application) {
        if (null == instance) {
            synchronized (LogModel.class) {
                if (null == instance) {
                    instance = new LogModel(application);
                }
            }
        }
        return instance;
    }

    private String toText(ArrayList<String> list) {
        String ret = "";
        for (String msg: list) {
            ret += msg;
        }
        return ret;
    }

    public MutableLiveData<ArrayList<String>> getLogs() {
        return logs;
    }

    public MutableLiveData<String> getLogText() {
        return logText;
    }

    public void appendLog(String msg) {
        ArrayList<String> list = this.getLogs().getValue();

        if (null == list) {
            list = new ArrayList<String>();
        }

        int length = list.size();
        if (length > 100) {
            list.remove(0);
        }
        list.add(msg);
        this.getLogs().postValue(list);
        this.getLogText().postValue(toText(list));
    }
}
