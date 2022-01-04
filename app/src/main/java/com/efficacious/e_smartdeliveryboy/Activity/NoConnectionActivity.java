package com.efficacious.e_smartdeliveryboy.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.efficacious.e_smartdeliveryboy.R;
import com.efficacious.e_smartdeliveryboy.util.CheckInternetConnection;

public class NoConnectionActivity extends AppCompatActivity {

    CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        checkInternetConnection = new CheckInternetConnection(getApplicationContext());

        findViewById(R.id.btnTryAgain)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkInternetConnection.isConnectingToInternet()){
                            startActivity(new Intent(NoConnectionActivity.this, SplashActivity.class));
                            finish();
                        }else {
                            LottieAnimationView lottieAnimationView = findViewById(R.id.lottie);
                            lottieAnimationView.playAnimation();
                        }
                    }
                });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}