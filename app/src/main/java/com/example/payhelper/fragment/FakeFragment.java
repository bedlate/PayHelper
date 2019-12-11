package com.example.payhelper.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.payhelper.R;
import com.example.payhelper.databinding.FragmentFakeBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class FakeFragment extends Fragment {

    private FragmentFakeBinding binding;


    private long clickTime = 0;
    private int clickCount = 0;

    public FakeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_fake, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fake, container, false);
        binding.setLifecycleOwner(getActivity());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.textView.setOnClickListener(onGotoFragmentMain);

    }

    // 跳转主页面
    private View.OnClickListener onGotoFragmentMain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (0 == clickTime || (currentTime - clickTime < 1000)) {
                clickCount++;
            } else {
                clickCount = 1;
            }
            clickTime = currentTime;
            if (5 <= clickCount) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                navController.navigate(R.id.action_fakeFragment_to_mainFragment);
                clickCount = 0;
            }
        }
    };

}
