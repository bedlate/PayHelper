package com.example.payhelper.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.payhelper.viewmodel.ConfigModel;
import com.example.payhelper.viewmodel.LogModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private final static String TAG = "payhelper";
    private final static String LOG_FILE = "log";

    private static LogUtil instance;

    private Application application;

    private ConfigModel configModel;

    private LogModel logModel;

    private LogUtil(Application application) {
        this.application = application;
        this.configModel = ConfigModel.getInstance(application);
        this.logModel = LogModel.getInstance(application);
    }

    public static LogUtil getInstance(Application application) {
        if (null == instance) {
            synchronized (LogUtil.class) {
                if (null == instance) {
                    instance = new LogUtil(application);
                }
            }
        }
        return instance;
    }

    public void write(String msg) {
        try {
            String now = new SimpleDateFormat("HH:mm:ss").format(new Date());
            msg = "\n" + now + ": " + msg;

            // 记录到内存
            logModel.appendLog(msg);

            if (configModel.getLogEnable().getValue()) {
                FileOutputStream outputStream = application.openFileOutput(getFileName(), Context.MODE_APPEND);
                outputStream.write(msg.getBytes());
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            File dir = new File(application.getFilesDir(), "");
            File files[] = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (!fileName.equals(getFileName()) && fileName.startsWith(LOG_FILE)) {
                    files[i].delete();
                }
            }
            Toast.makeText(application.getApplicationContext(), "清理完成", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(application.getApplicationContext(), "清理失败," + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void move() {
        d("开始复制");

        boolean logEnable = configModel.getLogEnable().getValue();

        if (logEnable) {
            configModel.getLogEnable().postValue(false);
        }

        String fileName = getFileName();
        File file = new File(application.getFilesDir(), fileName);

        File targetFile = new File(application.getExternalFilesDir(null), fileName);

        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                if (logEnable) {
                    configModel.getLogEnable().postValue(true);
                }

                d("复制完成");

                Toast.makeText(application.getApplicationContext(), "复制完成,日志路径" + targetFile.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(application.getApplicationContext(), "导出失败," + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(application.getApplicationContext(), "今天没有日志哦", Toast.LENGTH_LONG).show();
        }

    }

    private String getFileName() {
        String d = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return LOG_FILE + "_" + d + ".txt";
    }

    public int e(String msg) {
        write(msg);
        return Log.e(TAG, msg);
    }

    public int w(String msg) {
        write(msg);
        return Log.w(TAG, msg);
    }

    public int i(String msg) {
        write(msg);
        return Log.d(TAG, msg);
    }

    public int d(String msg) {
        write(msg);
        return Log.d(TAG, msg);
    }

    public int v(String msg) {
        write(msg);
        return Log.d(TAG, msg);
    }

}
