package com.efficacious.e_smartdeliveryboy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.efficacious.e_smartdeliveryboy.R;
import com.efficacious.e_smartdeliveryboy.util.CheckInternetConnection;
import com.efficacious.e_smartdeliveryboy.util.Constant;
import com.efficacious.e_smartdeliveryboy.util.SharedPrefManger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginOtpActivity extends AppCompatActivity {

    Button mBtnVerifyOtp;
    EditText mGetOtp;
    String MobileNumber,OtpId,WithoutCCMobile;
    TextView mMobileNumberTxt,countTxt;

    public int counter;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;

    SharedPrefManger sharedPrefManger;
    CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        countTxt = findViewById(R.id.timer);
        countDown();

        sharedPrefManger = new SharedPrefManger(getApplicationContext());
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());

        mBtnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        mGetOtp = findViewById(R.id.getOtp);
        mMobileNumberTxt = findViewById(R.id.mobileNumberTxt);
        progressBar = findViewById(R.id.loader);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        MobileNumber = getIntent().getStringExtra("MobileNumber");
        mMobileNumberTxt.setText("Otp send on your " + MobileNumber);
        WithoutCCMobile = getIntent().getStringExtra("WithoutCCMobile");
        InitiateOtp();

        mBtnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGetOtp.getText().toString().isEmpty()){
                    mGetOtp.setError("Please enter otp");
                }else if (mGetOtp.getText().toString().length()!=6){
                    mGetOtp.setError("Short OTP");
                }else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OtpId,mGetOtp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void InitiateOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                MobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        OtpId = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(LoginOtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mBtnVerifyOtp.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constant.LOGGED_IN,true);
                        editor.apply();
                        editor.commit();

                        Intent intent = new Intent(LoginOtpActivity.this, MainActivity.class);
                        intent.putExtra("Mobile",MobileNumber);
                        startActivity(intent);
                        finish();

//                        firebaseFirestore.collection("UserData")
//                                .document(firebaseAuth.getCurrentUser().getUid())
//                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                boolean addressFound = task.getResult().getBoolean(Constant.ADDRESS_AVAILABLE);
//                                getUserDetails(addressFound);
//                            }
//                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);
                mBtnVerifyOtp.setVisibility(View.VISIBLE);
//                            Toast.makeText(UserLoginOtp.this, "Error...", Toast.LENGTH_SHORT).show();
                mGetOtp.setError("Incorrect OTP");
            }
        });
    }

//    private void getUserDetails(boolean addressFound) {
//        if (!checkInternetConnection.isConnectingToInternet()){
//            startActivity(new Intent(LoginOtpActivity.this,NoConnectionActivity.class));
//            finish();
//        }else{
//            try {
//                Call<GetUserDetailResponse> call = RetrofitClient
//                        .getInstance()
//                        .getApi()
//                        .getUserDetails("select","1",WithoutCCMobile);
//
//
//                call.enqueue(new Callback<GetUserDetailResponse>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onResponse(Call<GetUserDetailResponse> call, Response<GetUserDetailResponse> response) {
//                        if (response.isSuccessful()){
//                            List<GetUserDetails> getUserDetails = response.body().getGetUserDetails();
//
//                            UserDetail userDetail = new UserDetail(getUserDetails.get(0).getRegisterId(),getUserDetails.get(0).getMobileNo(),
//                                    true,getUserDetails.get(0).getFirstName(),"",WithoutCCMobile);
//                            List<UserDetail> userDetails = new ArrayList<>();
//                            userDetails.addAll(Collections.singleton(userDetail));
//                            sharedPrefManger.saveUserDetail(userDetails);
//
//                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
//                            SharedPreferences.Editor editor;
//                            editor = sharedPreferences.edit();
//                            editor.putBoolean(Constant.ADDRESS_AVAILABLE,addressFound);
//                            editor.apply();
//                            editor.commit();
//
//                            Intent intent = new Intent(LoginOtpActivity.this, MainActivity.class);
//                            intent.putExtra("Mobile",MobileNumber);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<GetUserDetailResponse> call, Throwable t) {
//                        Toast.makeText(getApplicationContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }catch (Exception e){
//                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    private void countDown() {
        new CountDownTimer(50000, 1000){
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished){
                long sec = (millisUntilFinished / 1000) % 60;
                countTxt.setText(String.valueOf(sec) + " Sec");

            }
            @SuppressLint("SetTextI18n")
            public  void onFinish(){
                countTxt.setText("Resend OTP?");

                countTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendOtp();
                    }
                });
            }
        }.start();
    }

    private void resendOtp() {
        InitiateOtp();
        countDown();
        Toast.makeText(LoginOtpActivity.this, "OTP resend..", Toast.LENGTH_SHORT).show();
    }

}