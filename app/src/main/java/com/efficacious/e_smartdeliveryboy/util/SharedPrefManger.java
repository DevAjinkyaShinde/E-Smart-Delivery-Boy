package com.efficacious.e_smartdeliveryboy.util;

import android.content.Context;
import android.content.SharedPreferences;


import com.efficacious.e_smartdeliveryboy.model.UserDetail;

import java.util.List;

public class SharedPrefManger {
    private SharedPreferences sharedPreferences;
    Context context;
    private SharedPreferences.Editor editor;

    public SharedPrefManger(Context context) {
        this.context = context;
    }

    public void saveUserDetail(List<UserDetail> user){
        sharedPreferences = context.getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
        editor = sharedPreferences.edit();
        editor.putInt(Constant.REGISTER_ID,user.get(0).getRegisterId());
        editor.putString(Constant.NAME,user.get(0).getName());
        editor.putString(Constant.PROFILE_URL,user.get(0).getProfileUrl());
        editor.putString(Constant.MOBILE_NUMBER,user.get(0).getMobileNumber());
        editor.putString(Constant.WITHOUT_CC_MOBILE_NUMBER,user.get(0).getWithoutCCMobileNumber());
        editor.putBoolean(Constant.LOGGED_IN,true);
        editor.putBoolean(Constant.ADDRESS_AVAILABLE,false);
        editor.apply();
    }

    public boolean isLoggedIn(){
        sharedPreferences = context.getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
        return sharedPreferences.getBoolean(Constant.LOGGED_IN,false);
    }

    public void logout(){
        sharedPreferences = context.getSharedPreferences(Constant.USER_DATA_SHARED_PREF,0);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
