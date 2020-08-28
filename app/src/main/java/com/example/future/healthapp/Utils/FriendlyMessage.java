package com.example.future.healthapp.Utils;

import android.util.Log;

public class FriendlyMessage {

//constract the data collection pushed to the server
    private String uid;
    private String name;
    private String customer_id;
    private String email;


    public FriendlyMessage() {
    }

    public FriendlyMessage(String uid, String name,String email2) {
        if(email2.contains("@"))
        {this.email=email2;}
        else
        {this.customer_id=email2;
            Log.d("TAG", "FriendlyMessage: "+email2);}
        this.uid = uid;
        this.name = name;
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
    public String getEmail() {
        return email;
    }

    public void setCus(String email) {
        this.customer_id = email;
    }
    public String getCus() {
        return customer_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}