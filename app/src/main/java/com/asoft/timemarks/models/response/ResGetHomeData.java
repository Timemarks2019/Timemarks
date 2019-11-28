package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ItemSlider;
import com.asoft.timemarks.models.quiz.Quiz;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetHomeData {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<Quiz> list;

    @SerializedName("slider_list")
    private ArrayList<ItemSlider> slider_list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<Quiz> getList() {
        return list;
    }

    public ArrayList<ItemSlider> getSlider_list() {
        return slider_list;
    }
}
