package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ItemLeaderBoard;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetLeaderboard {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<ItemLeaderBoard> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemLeaderBoard> getList() {
        return list;
    }
}
