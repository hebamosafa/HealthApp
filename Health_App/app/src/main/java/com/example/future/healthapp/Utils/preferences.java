package com.example.future.healthapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class preferences {
    Context context;
    SharedPreferences pref;
    public preferences(Context context){
        this.context=context;
        pref = context.getSharedPreferences("MyPref", 0);
    }
    public void write_pref(int in){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("KEY", in);
        editor.commit();
    }
    public int read_pref(){

        return  pref.getInt("KEY",0);
    }
}
