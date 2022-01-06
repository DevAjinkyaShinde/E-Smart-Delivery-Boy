package com.efficacious.e_smartdeliveryboy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetUserDetailResponse {
    @SerializedName("GetCustomer")
    private List<GetUserDetails> getUserDetails = null;

    public List<GetUserDetails> getGetUserDetails() {
        return getUserDetails;
    }

    public void setGetUserDetails(List<GetUserDetails> getUserDetails) {
        this.getUserDetails = getUserDetails;
    }
}
