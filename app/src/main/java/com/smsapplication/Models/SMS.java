package com.smsapplication.Models;

public class SMS {
    public String id;
    public String sender;
    public String time;
    public String message;
    public String type; //1 means inbox, 2 means outbox
    public String read; //1 means read, 0 means not-read
    public String person;
    public int status; //0 sending, 1 sent successfully 2 not-send

    public SMS(String id, String sender,String time,String message,String type,String read,String person){
        this.id =id;
        this.sender=sender;
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
        this.person=person;
    }

    public SMS(String time,String message,String type,String read,int status){
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
        this.status=status;
    }

    public SMS(String sender,String time,String message,String type,String read){
        this.sender=sender;
        this.time=time;
        this.message=message;
        this.type=type;
        this.read=read;
    }
}
