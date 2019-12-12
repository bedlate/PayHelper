package com.example.payhelper.fragment;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.payhelper.BuildConfig;
import com.example.payhelper.R;
import com.example.payhelper.databinding.FragmentMainBinding;
import com.example.payhelper.util.HttpUtil;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.util.PermissionUtil;
import com.example.payhelper.util.ServiceUtil;
import com.example.payhelper.util.ToolUtil;
import com.example.payhelper.viewmodel.ConfigModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private Activity activity;
    private Application application;
    private ConfigModel configModel;
    private FragmentMainBinding binding;
    private LogUtil logUtil;
    private InputMethodManager inputMethodManager;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_main, container, false);
        this.activity = getActivity();
        this.application = this.activity.getApplication();

        logUtil = LogUtil.getInstance(application);

        inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        configModel = ConfigModel.getInstance(application);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(getActivity());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.btnSaveConfig.setOnClickListener(onSaveConfig);
        binding.btnToggleService.setOnClickListener(onToggleService);
        binding.btnGotoPermission.setOnClickListener(onGotoPermission);
        binding.btnGotoLog.setOnClickListener(onGotoLog);
        binding.textVersion.setOnClickListener(onCheckUpdate);
    }

    // 检查更新
    private View.OnClickListener onCheckUpdate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToolUtil toolUtil = ToolUtil.getInstance();
            if (!toolUtil.click(5)) {
                return;
            }

            logUtil.d("检查更新");
            Toast.makeText(application, "检查更新", Toast.LENGTH_LONG).show();

            HttpUtil.getInstance(application).request("/pay/app/update", new JSONObject(), "GET", new HttpUtil.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    super.onFailure(call, e);

                    JSONObject res = this.getResult();
                    logUtil.d("网络错误:" + res.toString());

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(application, "网络错误！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    super.onResponse(call, response);

                    try {
                        JSONObject res = this.getResult();
                        logUtil.d("onResponse" + res.toString());

                        if (1 == res.getInt("code")) {
                            JSONObject data = res.getJSONObject("data");
                            int appVersion = data.getInt("app_version");
                            final String appFile = data.getString("app_file");

                            int currentVersion = BuildConfig.VERSION_CODE;
                            if (currentVersion < appVersion) {
                                logUtil.d("前往浏览器下载");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(application, "前往浏览器下载", Toast.LENGTH_SHORT).show();
                                        Uri uri = Uri.parse(appFile);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                logUtil.d("无需更新");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(application, "无需更新！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        logUtil.d("网络异常:" + e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(application, "网络异常", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    // 跳转到日志页面
    private View.OnClickListener onGotoLog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavController navController = Navigation.findNavController(activity, R.id.fragment);
            navController.navigate(R.id.action_mainFragment_to_logFragment);
        }
    };

    // 设置权限
    private View.OnClickListener onGotoPermission = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PermissionUtil.getInstance(application).gotoPermission();
        }
    };

    // 切换服务状态
    private View.OnClickListener onToggleService = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isRunning = configModel.getIsRunning().getValue();
            if (isRunning) {
                ServiceUtil.getInstance(application).stopServices();

            } else {
                ServiceUtil.getInstance(application).startServices();
            }
        }
    };

    // 保存配置
    private View.OnClickListener onSaveConfig = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logUtil.d("配置前:" + configModel.toJson());

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(getString(R.string.api_key), binding.inputApi.getText().toString());
                jsonObject.put(getString(R.string.username_key), binding.inputUsername.getText().toString());
                jsonObject.put(getString(R.string.log_enable_key), binding.switchLogStatus.isChecked());
                jsonObject.put(getString(R.string.sms_enable_key), binding.switchSmsStatus.isChecked());

                configModel.saveConfig(jsonObject);

                logUtil.d("配置后:" + configModel.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            hiddenInput();

            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        }
    };

    // 隐藏输入法
    private void hiddenInput() {
        View view = getActivity().getCurrentFocus();
        if(null != view && null != view.getWindowToken()){

            view.clearFocus();

            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
