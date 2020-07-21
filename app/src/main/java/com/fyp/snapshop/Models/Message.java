package com.fyp.snapshop.Models;

public class Message {

    String from,message,to,messageId,name;

    public String getTo() {
        return to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Message(String from, String message, String to, String messageId, String name) {
        this.from = from;
        this.message = message;
//        this.type = type;
        this.to = to;
        this.name=name;
        this.messageId = messageId;
    }

    public Message() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
}
