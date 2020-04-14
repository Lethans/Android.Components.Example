package com.example.android.androidcomponentsexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.android.androidcomponentsexample.utils.ComandosHelper;
import com.example.android.androidcomponentsexample.view.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    //TODO MODIFICAR EL TIMEOUT PARA QUE FUNCIONE EL SPLASH (5000)
    public static final int timeout = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ComandosHelper.makeFullScreen(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, timeout);
    }

}

