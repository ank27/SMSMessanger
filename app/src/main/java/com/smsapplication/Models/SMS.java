package com.smsapplication.Models;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class SMS {
    public String id;
    public String sender;
    public String time;
    public String message;
    public String type; //1 means inbox, 2 means outbox
    public String read; //1 means read, 0 means not-read
    public String person;

    public SMS(String id, String sender,String time,String message,String type,String read,String person){
        this.id =id;
        this.sender=sender;
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
        this.person=person;
    }

    public SMS(String time,String message,String type,String read){
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
    }

    public SMS(String sender,String time,String message,String type,String read){
        this.sender=sender;
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
    }
}
