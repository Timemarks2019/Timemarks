package com.asoft.timemarks.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ibitol on 9/25/2018.
 */

public class Result {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("register_status")
    private Boolean register_status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("detail")
    private User user;

    public Boolean getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public User getUser() {
        return user;
    }

    public Boolean getRegister_status() {
        return register_status;
    }
}
