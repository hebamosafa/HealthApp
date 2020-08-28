package com.example.future.healthapp.Band_Data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Devices {
    String[] dev_arr=new String[4];
     String[] type=new String[]{"Heart_Rate","Blood_Sugar_Level","Body_Temperature","Blood_Pressure"};
public Devices(){}
    public static void push(String name,String type,String id){
        DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference().child(name).child(type);
        databaseref.push().setValue(id);
    }
    public void getid(final String id_customer) {
        for (int i = 0; i < 4; i++) {
            final Query lastQuery = FirebaseDatabase.getInstance().getReference().child("Device").child(type[i]).limitToLast(1);
            final int finalI = i;
            lastQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    dev_arr[finalI] = dataSnapshot.getValue(String.class);
                    Log.d("TAG", "onChildAdd:NEW " + dev_arr[finalI]);
                    new Req_customer().execute("http://64.225.47.65:8080/api/customer/"+id_customer+"/device/"+dev_arr[finalI]);
                    Devices.push("Customers_id",id_customer,dev_arr[finalI]);
                    lastQuery.removeEventListener(this);
                    dataSnapshot.getRef().removeValue();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }

    }
}
