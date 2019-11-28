package com.asoft.timemarks.models.response;

import com.google.gson.annotations.SerializedName;

public class ResGetBalance {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("balance")
    private String balance;

    public Boolean getStatus() {
        return status;
    }

    public String getBalance() {
        return balance;
    }

}
