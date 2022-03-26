package com.hdmovies.livetvchannels.item;


import java.util.ArrayList;

public class Hash {
    private String title;
    private String id;
    private String videocount;
    private ArrayList<ItemLatest> movieArrayList;

    public Hash() {
    }

    public Hash(String title, String id, String videocount, ArrayList<ItemLatest> movieArrayList) {
        this.title = title;
        this.id = id;
        this.videocount = videocount;
        this.movieArrayList = movieArrayList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideocount() {
        return videocount;
    }

    public void setVideocount(String videocount) {
        this.videocount = videocount;
    }

    public ArrayList<ItemLatest> getMovieArrayList() {
        return movieArrayList;
    }

    public void setMovieArrayList(ArrayList<ItemLatest> movieArrayList) {
        this.movieArrayList = movieArrayList;
    }
}
