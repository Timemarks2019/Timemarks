package com.asoft.timemarks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResSendOtp {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("otp")
    @Expose
    private String otp;

    @SerializedName("msg")
    @Expose
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
