package com.example.future.healthapp.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;


import com.example.future.healthapp.Band_Data.Req_report;
import com.example.future.healthapp.Details_view;
import com.example.future.healthapp.R;
import com.example.future.healthapp.Utils.preferences;
import com.example.future.healthapp.patient_view;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class DataSensors  extends Fragment {
    TextView heart;
    TextView pressure;
    TextView temp;
    TextView suger;
    String dev_ID;
    static String getRange="";
    static preferences mpref;
    static int j=0;
    GridLayout GL;
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.data_main, container, false);
        GL=(GridLayout) rootView.findViewById(R.id.mlayout4) ;
        GL.getBackground().setAlpha(50);  // here the value is an integer not float
        mpref=new preferences(getApplicationContext());
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        heart=(TextView) getActivity().findViewById(R.id.heart);
        pressure = (TextView) getActivity().findViewById(R.id.pres);
        temp = (TextView) getActivity().findViewById(R.id.temp);
        suger = (TextView) getActivity().findViewById(R.id.suger);
        get_Devices(Details_view.CusID());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //wait until the data to be ready
                String h= mpref.read_pref_s("Data");
                Log.d("TAG", "runnnn: "+h);
                pass(h);
            }
        }, 5000);

    }
    public  void pass(String h){
        String[] output=h.split("/");
        if(output.length<4){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String h= mpref.read_pref_s("Data");
                    Log.d("TAG", "runnnn: "+h);
                    pass(h);
                }
            }, 3000);
        }
        else {

        Log.d("TAG", "pass: "+output[0]);

        heart.setText(output[0]);
        suger.setText(output[1]);
        temp.setText(output[2]);
        pressure.setText(output[3]);}

    }
    private void get_Devices(String cus_id) {
        DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference().child("Customers_id").child(cus_id);
        //send request to get the assigned devices data
        final String[] type=new String[]{"Heart_Rate","blood_Sugar","Body_Temperature","blood_Pressure"};
        databaseref.addChildEventListener(new ChildEventListener() {
            int i=0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dev_ID = dataSnapshot.getValue(String.class);
                new Req_report().execute(dev_ID,type[i],"MIN","d");
                new Req_report().execute(dev_ID,type[i],"MAX","d");

                i++;

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
    public  void send(String range){
        getRange=getRange.concat(range);
        j++;
        Log.d("TAG", "send: "+getRange);
        if(j==8){
            j=0;
            Log.d("TAG", "send: iiiiiiiiiiiiiii");

            //pat(getRange);
            mpref.write_pref_s(getRange,"Data");
            //heart.setText(getRange);
            Long tsLong = System.currentTimeMillis();
            String now = tsLong.toString();
            mpref.write_pref_s(now,"Date");
        }
    }
}
