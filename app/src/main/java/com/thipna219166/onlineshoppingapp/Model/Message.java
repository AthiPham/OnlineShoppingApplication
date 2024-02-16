package com.thipna219166.onlineshoppingapp.Model;

import java.sql.Timestamp;

public class Message {
    //private String MessageId;
    private String sender;
    private String content;
    private Timestamp time;

    public Message(){}
    public Message( String sender, String content){
        this.sender = sender;
        this.content = content;
    }
    public Message(String sender,String content, Timestamp time){
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
