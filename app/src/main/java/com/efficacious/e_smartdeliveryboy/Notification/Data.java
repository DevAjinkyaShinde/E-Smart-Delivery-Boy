package com.efficacious.e_smartdeliveryboy.Notification;

public class Data {
    String title;
    String msg;
    String flag;

    public Data() {
    }

    public Data(String title, String msg, String flag) {
        this.title = title;
        this.msg = msg;
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
