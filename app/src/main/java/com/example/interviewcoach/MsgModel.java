package com.example.interviewcoach;
/**
 * Message model for chatBot
 * @author Emmy
 */
public class MsgModel {
    private String cnt;

    public MsgModel(String cnt) {
        this.cnt = cnt;
    }

    //Getters & setters.
    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
}
