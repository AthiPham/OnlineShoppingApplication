package com.thipna219166.onlineshoppingapp.Model;

import java.sql.Timestamp;

public class Conservation {
    private String chatId;
    private String nameBuyer;
    private String lastMessageContent;
    private Timestamp lastSendTime;

    public Conservation(String chatId, String nameBuyer, String lastMessageContent, Timestamp lastSendTime){
        this.chatId = chatId;
        this.nameBuyer = nameBuyer;
        this.lastMessageContent = lastMessageContent;
        this.lastSendTime = lastSendTime;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getNameBuyer() {
        return nameBuyer;
    }

    public void setNameBuyer(String nameBuyer) {
        this.nameBuyer = nameBuyer;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public Timestamp getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Timestamp lastSendTime) {
        this.lastSendTime = lastSendTime;
    }
}
