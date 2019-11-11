package com.example.payhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.payhelper.databinding.ActivityMainBinding;
import com.example.payhelper.model.ConfigModel;

public class MainActivity extends AppCompatActivity {

    ConfigModel configModel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configModel = ViewModelProviders.of(this).get(ConfigModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setConfig(configModel);
        binding.setLifecycleOwner(this);
    }
}
