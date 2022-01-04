package com.efficacious.e_smartdeliveryboy.model;

public class UserDetail {
    int RegisterId;
    String MobileNumber;
    Boolean LoggedIn;
    String Name;
    String ProfileUrl;
    String WithoutCCMobileNumber;

    public UserDetail(int registerId, String mobileNumber, Boolean loggedIn, String name, String profileUrl, String withoutCCMobileNumber) {
        RegisterId = registerId;
        MobileNumber = mobileNumber;
        LoggedIn = loggedIn;
        Name = name;
        ProfileUrl = profileUrl;
        WithoutCCMobileNumber = withoutCCMobileNumber;
    }

    public int getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(int registerId) {
        RegisterId = registerId;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public Boolean getLoggedIn() {
        return LoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        LoggedIn = loggedIn;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }

    public String getWithoutCCMobileNumber() {
        return WithoutCCMobileNumber;
    }

    public void setWithoutCCMobileNumber(String withoutCCMobileNumber) {
        WithoutCCMobileNumber = withoutCCMobileNumber;
    }
}
