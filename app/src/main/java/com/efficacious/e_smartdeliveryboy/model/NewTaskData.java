package com.efficacious.e_smartdeliveryboy.model;

public class NewTaskData {
    String OrderId;
    String Status;
    String TotalBill;
    String MobileNumber;
    String RegisterId;
    String UserName;
    long TimeStamp;

    public NewTaskData() {
    }

    public NewTaskData(String orderId, String status, String totalBill, String mobileNumber, String registerId, String userName, long timeStamp) {
        OrderId = orderId;
        Status = status;
        TotalBill = totalBill;
        MobileNumber = mobileNumber;
        RegisterId = registerId;
        UserName = userName;
        TimeStamp = timeStamp;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTotalBill() {
        return TotalBill;
    }

    public void setTotalBill(String totalBill) {
        TotalBill = totalBill;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}
