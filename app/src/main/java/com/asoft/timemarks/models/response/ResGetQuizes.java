package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.Quiz;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetQuizes {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<Quiz> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<Quiz> getList() {
        return list;
    }
}
