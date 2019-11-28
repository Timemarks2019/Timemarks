package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.ItemSubject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetSubjects {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<ItemSubject> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemSubject> getList() {
        return list;
    }
}
