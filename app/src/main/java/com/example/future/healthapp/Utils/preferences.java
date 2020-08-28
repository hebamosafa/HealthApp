package com.example.future.healthapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class preferences {
    //to save the imp data locally
    Context context;
    SharedPreferences pref;
    public preferences(Context context){
        this.context=context;
        pref = context.getSharedPreferences("MyPref", 0);
    }
    public void write_pref(int in,String Key){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Key, in);
        editor.commit();
    }
    public void write_pref_s(String in,String Key){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Key, in);
        editor.commit();
    }

    public int read_pref(String Key){

        return  pref.getInt(Key,0);
    }
    public String read_pref_s(String Key){

        return  pref.getString(Key,"");
    }
}
