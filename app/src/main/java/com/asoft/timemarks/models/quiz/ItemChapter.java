package com.asoft.timemarks.models.quiz;

public class ItemChapter {

    private String chapter_id;
    private String total_questions;
    private String chapter_name;
    private String level;
    private String my_attempts;
    private String total_attempts;
    private String avg_time;

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getTotal_questions() {
        return total_questions;
    }

    public void setTotal_questions(String total_questions) {
        this.total_questions = total_questions;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAvg_time() {
        return avg_time;
    }

    public String getMy_attempts() {
        return my_attempts;
    }

    public String getTotal_attempts() {
        return total_attempts;
    }
}
