package com.efficacious.e_smartdeliveryboy.model;

import com.google.gson.annotations.SerializedName;

public class GetFCM {
    @SerializedName("Employee_Id")
    private Integer employeeId;
    @SerializedName("First_Name")
    private String firstName;
    @SerializedName("vchFcmToken")
    private String vchFcmToken;
    @SerializedName("Res_Id")
    private Integer resId;
    @SerializedName("User_Name")
    private String userName;

    public GetFCM(Integer employeeId, String firstName, String vchFcmToken, Integer resId, String userName) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.vchFcmToken = vchFcmToken;
        this.resId = resId;
        this.userName = userName;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getVchFcmToken() {
        return vchFcmToken;
    }

    public void setVchFcmToken(String vchFcmToken) {
        this.vchFcmToken = vchFcmToken;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
