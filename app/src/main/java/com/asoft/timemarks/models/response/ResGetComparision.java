package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ComparisionResult;
import com.google.gson.annotations.SerializedName;

public class ResGetComparision {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("comapre_user_result")
    private ComparisionResult comapre_user_result;

    @SerializedName("user_result")
    private ComparisionResult user_result;

    public Boolean getStatus() {
        return status;
    }

    public ComparisionResult getComapre_user_result() {
        return comapre_user_result;
    }

    public ComparisionResult getUser_result() {
        return user_result;
    }
}
