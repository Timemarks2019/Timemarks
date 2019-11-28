package com.asoft.timemarks.models;

public class ItemLeaderBoard {

    private String total_points;
    private String UserId;
    private String name;
    private String user_email;
    private String image;

    public String getTotal_points() {
        return total_points;
    }

    public void setTotal_points(String right_answer_count) {
        this.total_points = right_answer_count;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getName() {
        return name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getImage_user() {
        return image;
    }

    public void setImage_user(String image) {
        this.image = image;
    }
}
