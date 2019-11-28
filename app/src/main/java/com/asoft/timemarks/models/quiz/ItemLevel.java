package com.asoft.timemarks.models.quiz;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemLevel {
    @SerializedName("1")
    private ArrayList<ItemChapter> level1;

    @SerializedName("2")
    private ArrayList<ItemChapter> level2;

    @SerializedName("3")
    private ArrayList<ItemChapter> level3;

    @SerializedName("4")
    private ArrayList<ItemChapter> level4;

    public ArrayList<ItemChapter> getLevel1() {
        return level1;
    }

    public ArrayList<ItemChapter> getLevel2() {
        return level2;
    }

    public ArrayList<ItemChapter> getLevel3() {
        return level3;
    }

    public ArrayList<ItemChapter> getLevel4() {
        return level4;
    }
}
