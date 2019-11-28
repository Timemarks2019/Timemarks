package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.Quiz;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetSingleQuiz {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private Quiz list;

    public Boolean getStatus() {
        return status;
    }

    public Quiz getQuiz() {
        return list;
    }
}
