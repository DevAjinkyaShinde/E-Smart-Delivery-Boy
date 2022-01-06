package com.efficacious.e_smartdeliveryboy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateStatusDetails {

    List<UpdateStatusDetails> updateStatusDetails;

    @SerializedName("OrderId")
    private Integer orderId;
    @SerializedName("status")
    private String status;

    public UpdateStatusDetails(Integer orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public UpdateStatusDetails() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
