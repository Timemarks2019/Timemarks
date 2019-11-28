package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.ItemTransaction;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResGetTransactions {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ArrayList<ItemTransaction> list;

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<ItemTransaction> getTransactions() {
        return list;
    }
}
