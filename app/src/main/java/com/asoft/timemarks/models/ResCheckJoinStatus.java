package com.asoft.timemarks.models;

import com.google.gson.annotations.SerializedName;

public class ResCheckJoinStatus {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("join_status")
    private Boolean join_status;


    public Boolean getStatus() {
        return status;
    }

    public Boolean getJoin_status() {
        return join_status;
    }
}
