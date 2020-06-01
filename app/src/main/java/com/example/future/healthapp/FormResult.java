package com.example.future.healthapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.future.healthapp.Details_view;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FormResult extends Fragment {
    FirebaseDatabase database;
    final String TAG="HEBA";
    private ChildEventListener mChildEventListener;
    DatabaseReference docRef ;
    String n;
    TextView textView;
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        database=FirebaseDatabase.getInstance();
        View rootView = inflater.inflate(R.layout.form_main, container, false);
         textView = (TextView) rootView.findViewById(R.id.formtest);
         n= Details_view.d();
        //textView.setText(n);
        Log.d("HEBA", "onCreateView: "+n);
        get_formResult();
        return rootView;
    }

    private void get_formResult() {

        docRef = database.getReference().child("FormsResult").child(n);

        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildAdd: "+friendlyMessage);
                GetUi(friendlyMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildChanged: "+friendlyMessage);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildRemoved: "+friendlyMessage);
//helloworld
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        docRef.addChildEventListener(mChildEventListener);
    }
    void GetUi(String input){
        String[] input2=input.split("/");
        for(int i=0;i<input2.length;i++){
            textView.append("Question"+i+": "+input2[i]+ "\n");
        }

    }
}
