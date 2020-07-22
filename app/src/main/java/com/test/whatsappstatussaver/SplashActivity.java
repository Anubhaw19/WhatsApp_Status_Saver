package com.test.whatsappstatussaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
        Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_splash);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },10);

    }
}