package com.example.future.healthapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.future.healthapp.Details_view;
import com.example.future.healthapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        ConstraintLayout constraintLayout=(ConstraintLayout)rootView.findViewById(R.id.mlayout2) ;
        constraintLayout.getBackground().setAlpha(50);
         textView = (TextView) rootView.findViewById(R.id.formtest);
         n= Details_view.d();
//        Log.d("HEBA", "onCreateView: "+n);
        get_formResult();
        return rootView;
    }

    private void get_formResult() {

        Query lastQuery = database.getReference().child("FormsResult").child(n).orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
  //              Log.d(TAG, "onChildAdd: "+friendlyMessage);
                GetUi(friendlyMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "onChildChanged: "+friendlyMessage);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
               // Log.d(TAG, "onChildRemoved: "+friendlyMessage);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private String setText(String i) {
        if(i.equals("0"))
            return "Low";
        else if(i.equals("1"))
            return "Normal";
        else
            return "High";
    }
    void GetUi(String input){
        String[] ansQues=input.split("%");
        String[] Answers=ansQues[0].split("/");
        String[] Ques=ansQues[1].split("/");
        String comment=null;
        if(ansQues.length==3){
         comment=ansQues[2];}
        textView.setPadding(30,3,30,3);
        textView.setTextColor(Color.BLACK);
        for(int i=0;i<Answers.length;i++){
            textView.append("Question"+(i+1)+": "+Ques[i]+ "\n");
            textView.append(setText(Answers[i])+ "\n");
            textView.append("\n");
        }

        textView.append("Comments: "+"\n");
        if(comment==null)
        {textView.append("No Comments");}
        else
        {textView.append(comment);}

    }
}
