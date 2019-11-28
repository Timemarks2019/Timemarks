package com.asoft.timemarks.models.response;

import com.google.gson.annotations.SerializedName;

public class ResGetHash {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("hash")
    private String hash;

    @SerializedName("hash_string")
    private String hash_string;

    @SerializedName("msg")
    private String msg;

    public Boolean getStatus() {
        return status;
    }

    public String getHash() {
        return hash;
    }

    public String getHash_string() {
        return hash_string;
    }

    public String getMsg() {
        return msg;
    }
}
