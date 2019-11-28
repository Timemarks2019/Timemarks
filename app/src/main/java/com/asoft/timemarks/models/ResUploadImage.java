package com.asoft.timemarks.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ibitol on 11/28/2018.
 */

public class ResUploadImage {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("msg")
    private String msg;

    @SerializedName("image")
    private String image;

    @SerializedName("full_url")
    private String full_url;

    public Boolean getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
    public String getImage() {
        return image;
    }

    public String getFull_url() {
        return full_url;
    }
}
