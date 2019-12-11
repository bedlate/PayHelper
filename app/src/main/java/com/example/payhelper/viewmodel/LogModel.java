package com.example.payhelper.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class LogModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> logs;

    private static LogModel instance;

    public LogModel(@NonNull Application application) {
        super(application);

        this.logs = new MutableLiveData<ArrayList<String>>();
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        this.getLogs().postValue(list);
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

    public String toText() {
        String ret = "";
        for (String msg: this.getLogs().getValue()) {
            ret += msg;
        }
        return ret;
    }

    public MutableLiveData<ArrayList<String>> getLogs() {
        return logs;
    }

    public void appendLog(String msg) {
        ArrayList<String> list = this.getLogs().getValue();
        int length = list.size();
        if (length > 5) {
            list.remove(0);
        }
        list.add(msg);
        this.getLogs().postValue(list);
    }
}
