package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ItemQuizFeed;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetQuizFeed {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<ItemQuizFeed> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemQuizFeed> getList() {
        return list;
    }
}
