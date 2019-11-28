package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ItemQuiz;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ResSubmitQuiz implements Serializable {
    private Boolean status;

    @SerializedName("total_points")
    private String total_points;

    @SerializedName("right_count")
    private String right_count;

    @SerializedName("wrong_count")
    private String wrong_count;

    @SerializedName("pass_status")
    private Boolean pass_status;

    public Boolean getStatus() {
        return status;
    }

    public String getTotal_points() {
        return total_points;
    }

    public String getRight_count() {
        return right_count;
    }

    public String getWrong_count() {
        return wrong_count;
    }

    public Boolean getPass_status() {
        return pass_status;
    }
}
