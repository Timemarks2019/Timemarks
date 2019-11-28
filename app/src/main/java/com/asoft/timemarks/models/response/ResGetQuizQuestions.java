package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.ItemQuestion;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetQuizQuestions {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<ItemQuestion> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemQuestion> getList() {
        return list;
    }
}
