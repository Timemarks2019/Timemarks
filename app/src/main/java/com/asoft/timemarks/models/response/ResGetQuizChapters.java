package com.asoft.timemarks.models.response;

import com.asoft.timemarks.models.quiz.ItemLevel;
import com.google.gson.annotations.SerializedName;

public class ResGetQuizChapters {
    @SerializedName("status")
    private Boolean status;

    @SerializedName("list")
    private ItemLevel list;

    public Boolean getStatus() {
        return status;
    }

    public ItemLevel getList() {
        return list;
    }
}
