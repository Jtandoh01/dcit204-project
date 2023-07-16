package com.group18.campusmap.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.group18.campusmap.R;

public class SplashActivity extends AppCompatActivity {
private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 Intent a = new Intent(SplashActivity.this, MainActivity.class );
                 Intent b = new Intent(SplashActivity.this, LoginActivity.class );
                if(auth.getCurrentUser()!=null){
                    startActivity(a);
                    finish();
                }
                else{
                    startActivity(b);
                    finish();
                }
            }
        },5000);
    }
}