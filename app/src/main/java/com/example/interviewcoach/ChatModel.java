package com.example.interviewcoach;

/**
 * Chat model for chatBot
 *
 * @author Emmy
 */
public class ChatModel {
    private String message;
    private String sender;

    public ChatModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    //Getters & setters.
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

}
