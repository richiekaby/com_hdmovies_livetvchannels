package com.hdmovies.livetvchannels.item;


public class Subcription {

    private String planid;
    private String title;
    private String price;
    private String days;

    public Subcription() {
    }

    public Subcription(String title, String price, String days) {
        this.title = title;
        this.price = price;
        this.days = days;
    }

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

}
