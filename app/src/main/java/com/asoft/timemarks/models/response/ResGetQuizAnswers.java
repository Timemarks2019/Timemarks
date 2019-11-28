package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.ItemQuestion;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetQuizAnswers {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("pass_status")
    private int pass_status;

    @SerializedName("right_count")
    private int right_count;

    @SerializedName("wrong_count")
    private int wrong_count;

    @SerializedName("total_points")
    private int total_points;

    @SerializedName("pass_percentage")
    private int pass_percentage;

    @SerializedName("time")
    private String time;

    @SerializedName("TotalMarks")
    private String TotalMarks;

    @SerializedName("response_dec")
    private ArrayList<ItemQuestion> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemQuestion> getList() {
        return list;
    }

    public int getPass_status() {
        return pass_status;
    }

    public int getRight_count() {
        return right_count;
    }

    public int getWrong_count() {
        return wrong_count;
    }

    public int getTotal_points() {
        return total_points;
    }

    public String getTime() {
        return time;
    }

    public String getTotalMarks() {
        return TotalMarks;
    }
}
