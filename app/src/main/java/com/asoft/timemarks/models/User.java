package com.asoft.timemarks.models;

import java.io.Serializable;


public class User implements Serializable{

    private String ID;
    private String name;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
    private String password;
    private String email;
    private String image;
    private String balance;

    public String getUserId() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZip() {
        return zip;
    }
    public String getCountry() {
        return country;
    }
    public String getMobile() {
        return phone;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getBalance() {
        return balance;
    }
    public void setBalance(String balance){
        this.balance = balance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
