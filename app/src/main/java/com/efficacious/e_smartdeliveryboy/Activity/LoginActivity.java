package com.efficacious.e_smartdeliveryboy.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.efficacious.e_smartdeliveryboy.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    CountryCodePicker mCpp;
    EditText mMobileNumber;
    Button mBtnContinue;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCpp = findViewById(R.id.cpp);
        mMobileNumber = findViewById(R.id.mobileNumber);
        mBtnContinue = findViewById(R.id.btnContinue);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mCpp.registerCarrierNumberEditText(mMobileNumber);
        ProgressBar progressBar = findViewById(R.id.loader);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = mMobileNumber.getText().toString();
                if (TextUtils.isEmpty(mCpp.getFullNumberWithPlus())){
                    mMobileNumber.setError("Mobile Number");
                }else if (!TextUtils.isEmpty(mCpp.getFullNumberWithPlus())){
                    mBtnContinue.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseFirestore.collection("DeliveryBoy")
                            .whereEqualTo("MobileNumber",mCpp.getFullNumberWithPlus())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (!value.isEmpty()){
                                        Intent intent = new Intent(LoginActivity.this, LoginOtpActivity.class);
                                        intent.putExtra("MobileNumber",mCpp.getFullNumberWithPlus().replace(" ",""));
                                        intent.putExtra("WithoutCCMobile", mobileNumber);
                                        startActivity(intent);
                                    }else {
                                        mBtnContinue.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        mMobileNumber.setError("Mobile number not register");
                                    }
                                }
                            });
                }else {
                    mBtnContinue.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    mMobileNumber.setError("Mobile number not register");
                }
            }
        });

    }
}