package com.example.interviewcoach;

/**
 * Video item model
 *
 * @author Emmy
 */
public class VideoItem {
    private String title;
    private String url;

    public VideoItem() {
    }

    public VideoItem(String url, String title) {
        if (title.trim().equals("")) {
            title = "No title";
        }
        this.url = url;
        this.title = title;
    }

    //Getters & setters.
    public String getTitle() {
        return title;
    }


    public String getUrl() {
        return url;
    }

}
