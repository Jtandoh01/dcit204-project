package com.group18.campusmap.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group18.campusmap.R;
import com.group18.campusmap.databinding.ActivityForgotPasswddBinding;

public class ForgotPasswdActivity extends AppCompatActivity {
    private ActivityForgotPasswddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityForgotPasswddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(view->{
            onBackPressed();
        });

    }
}