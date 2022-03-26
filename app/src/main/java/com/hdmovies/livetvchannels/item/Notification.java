package com.hdmovies.livetvchannels.item;


public class Notification {

    private String notificationid;
    private String title;
    private String msg;
    private String image;
    private String videoid;
    private String url;
    private String log_entdate;

    public Notification() {
    }

    public Notification(String title, String msg, String image) {
        this.title = title;
        this.msg = msg;
        this.image = image;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLog_entdate() {
        return log_entdate;
    }

    public void setLog_entdate(String log_entdate) {
        this.log_entdate = log_entdate;
    }

}
