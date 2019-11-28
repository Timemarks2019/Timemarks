package com.asoft.timemarks.models.response;

import com.google.gson.annotations.SerializedName;

public class ResCheckTransaction {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("balance")
    private String balance;

    @SerializedName("msg")
    private String msg;

    public Boolean getStatus() {
        return status;
    }

    public String getBalance() {
        return balance;
    }

    public String getMsg() {
        return msg;
    }
}
