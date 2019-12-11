package com.example.payhelper.fragment;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.payhelper.R;
import com.example.payhelper.databinding.FragmentMainBinding;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.util.PermissionUtil;
import com.example.payhelper.util.ServiceUtil;
import com.example.payhelper.viewmodel.ConfigModel;

import org.json.JSONException;
import org.json.JSONObject;

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
    }

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
