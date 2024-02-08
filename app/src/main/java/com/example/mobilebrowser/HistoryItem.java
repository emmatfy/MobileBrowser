package com.example.mobilebrowser;

public class HistoryItem {

    private String time;
    private String url;



    public HistoryItem(String time, String url){
        this.time=time;
        this.url=url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
