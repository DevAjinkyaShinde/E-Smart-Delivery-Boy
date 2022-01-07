package com.efficacious.e_smartdeliveryboy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetFCMTokenResponse {
    @SerializedName("GetFCM")
    private List<GetFCM> getFCM = null;

    public List<GetFCM> getGetFCM() {
        return getFCM;
    }

    public void setGetFCM(List<GetFCM> getFCM) {
        this.getFCM = getFCM;
    }
}
