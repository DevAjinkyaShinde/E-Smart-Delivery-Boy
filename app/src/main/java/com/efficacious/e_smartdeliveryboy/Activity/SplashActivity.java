package com.efficacious.e_smartdeliveryboy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.efficacious.e_smartdeliveryboy.R;
import com.efficacious.e_smartdeliveryboy.util.CheckInternetConnection;
import com.efficacious.e_smartdeliveryboy.util.SharedPrefManger;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    Button button;
    ProgressBar loader;
    SharedPrefManger sharedPrefManger;
    CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefManger = new SharedPrefManger(getApplicationContext());
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());

        button = findViewById(R.id.btnStart);
        loader = findViewById(R.id.loader);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!checkInternetConnection.isConnectingToInternet()){
            startActivity(new Intent(SplashActivity.this, NoConnectionActivity.class));
            finish();
        }else {
            if (sharedPrefManger.isLoggedIn()){
                button.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                },2000);
            }
        }

    }

}