package com.asoft.timemarks.models.quiz;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String id;
    private String standard_name;
    private String subject_name;
    private String chapter_name;
    private String start_date;
    private String end_date;
    private String calender_start_date;
    private String calender_end_date;
    private String ending_status;
    private String status;
    private String amount;
    private String time;
    private String total_points;
    private String total_joining;
    private Boolean join_status;
    private Boolean play_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStandard_name() {
        return standard_name;
    }

    public void setStandard_name(String standard_name) {
        this.standard_name = standard_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCalender_start_date() {
        return calender_start_date;
    }

    public void setCalender_start_date(String calender_start_date) {
        this.calender_start_date = calender_start_date;
    }

    public String getCalender_end_date() {
        return calender_end_date;
    }

    public void setCalender_end_date(String calender_end_date) {
        this.calender_end_date = calender_end_date;
    }

    public String getEnding_status() {
        return ending_status;
    }

    public void setEnding_status(String ending_status) {
        this.ending_status = ending_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean getJoin_status() {
        return join_status;
    }

    public void setJoin_status(Boolean join_status) {
        this.join_status = join_status;
    }

    public String getTime() {
        return time;
    }

    public String getTotal_points() {
        return total_points;
    }

    public String getTotal_joining() {
        return total_joining;
    }

    public Boolean getPlay_status() {
        return play_status;
    }

    public void setPlay_status(Boolean play_status) {
        this.play_status = play_status;
    }
}
