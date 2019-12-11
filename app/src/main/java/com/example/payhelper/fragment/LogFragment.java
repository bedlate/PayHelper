package com.example.payhelper.fragment;


import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.payhelper.R;
import com.example.payhelper.databinding.FragmentLogBinding;
import com.example.payhelper.util.LogUtil;
import com.example.payhelper.viewmodel.LogModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    private FragmentActivity activity;
    private Application application;

    private LogModel logModel;
    private FragmentLogBinding binding;
    private LogUtil logUtil;

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_log, container, false);

        this.activity = getActivity();
        this.application = activity.getApplication();

        logUtil = LogUtil.getInstance(application);

        logModel = LogModel.getInstance(application);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log, container, false);
        binding.setLog(logModel);
        binding.setLifecycleOwner(activity);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.btnLogExport.setOnClickListener(onLogExport);
        binding.btnLogClear.setOnClickListener(onLogClear);
    }

    private View.OnClickListener onLogClear = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logUtil.clear();
        }
    };

    private View.OnClickListener onLogExport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logUtil.export();
        }
    };

}
