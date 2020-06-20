package com.example.future.healthapp.Utils;

public class FriendlyMessage {

    private String uid;
    private String name;


    public FriendlyMessage() {
    }

    public FriendlyMessage(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}