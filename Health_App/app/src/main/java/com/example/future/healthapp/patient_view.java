package com.example.future.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.future.healthapp.Utils.FriendlyMessage;
import com.example.future.healthapp.Utils.IntenetConn;
import com.example.future.healthapp.Utils.preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.acl.Group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class patient_view extends AppCompatActivity {
    private static final String TAG = "HEBA";
//Firebase
    FirebaseDatabase database;
    DatabaseReference docRef ;
    //Helpers
    androidx.constraintlayout.widget.Group refill;
    String[]output;
    String uid;
    String name;
    String result="";
    String mDoctor;
    int id=0;
    String mfriendlyMessage;
    //UI
    ProgressBar progressBar;
    ImageView empty;
    FloatingActionButton send;
    LinearLayout form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_view);
        database = FirebaseDatabase.getInstance();
        form = (LinearLayout)findViewById(R.id.mform2);
        send=(FloatingActionButton)findViewById(R.id.send);
        refill= (androidx.constraintlayout.widget.Group) findViewById(R.id.group3);
        refill.setVisibility(View.GONE);
        uid=getIntent().getBundleExtra("HEBA").getString("UID");
        name=getIntent().getBundleExtra("HEBA").getString("NAME");
        mDoctor=getIntent().getBundleExtra("HEBA").getString("MDR");
        /////////////////////////////////////////////
        if(!(IntenetConn.check_internet(this))){
            empty.setVisibility(View.VISIBLE);
            empty.setImageResource(R.drawable.ic_wifi);
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }
        /////////////////////////////////////////////////////////////////////////
        preferences mpref=new preferences(getApplicationContext());
        //mpref.write_pref(0);
        int i=mpref.read_pref();

        if(i==0){mpref.write_pref(2);
            first_time();
            Log.d(TAG, "onCreate: "+i);}
        else if(i==1){mpref.write_pref(3);
            Log.d(TAG, "onCreate: "+i);
            first_time();}




        /////////////////////////
        String doc_uid = "85296";
        Log.d(TAG, "onCreate: "+uid);
        docRef = database.getReference().child("Relation");
        //docRef.push().setValue("heba");
        read_data();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        empty=(ImageView)findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        Button refillme=(Button)findViewById(R.id.button2);
        refillme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form.setVisibility(View.VISIBLE);
                refill.setVisibility(View.GONE);
                //read_data();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()== View.VISIBLE){progressBar.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
            }
        }, 5000);

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                for(int i=0;i<output.length;i++){
                    if( refill.getVisibility()==View.VISIBLE){
                        Toast.makeText(patient_view.this,"Nothing to be uploaded !",Toast.LENGTH_SHORT).show();
                        break;
                    }
                 RadioGroup radioGroup=(RadioGroup)findViewById(i+4);
                 int m=radioGroup.getCheckedRadioButtonId();
                    Log.d(TAG, "onClick: "+m);

                 if(m != -1){
                 result=result.concat(String.valueOf(m)+'/');
                     docRef = database.getReference().child("FormsResult").child(uid);
                     result=result.concat("%"+mfriendlyMessage);
                     docRef.push().setValue(result);
                     Toast.makeText(patient_view.this,"Your answers've been uploaded Successfully",Toast.LENGTH_SHORT).show();
                     result="";
                     finish_fun();}
                 else{
                     Toast.makeText(patient_view.this,"Please Answer the All Questions First",Toast.LENGTH_SHORT).show();
                     result="";
                     break;
                 }
                }

            }
        });
        //TODO only one time

    }

    void read_data(){
        //TODO we know my doctor
        //uid of the doctor
        docRef=database.getReference();

        Query lastQuery = docRef.child("Forms").child(mDoctor).orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mfriendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildAdd: "+mfriendlyMessage);
                if(progressBar.getVisibility()==View.VISIBLE){progressBar.setVisibility(View.GONE);}
                if(empty.getVisibility()== View.VISIBLE){empty.setVisibility(View.GONE); }
                deploy();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mfriendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildAdd: "+mfriendlyMessage);
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

                //deploy();



    }
    void deploy(){
        output=mfriendlyMessage.split("/");
        for(String str :output){
            TextView et = new TextView(patient_view.this);
            et.setPadding(6,5,6,5);
            et.setMinLines(1);
            et.setMaxLines(3);
            et.setTextSize(20);
            et.setText(str);
            form.addView(et);
            /////////////////////////////////
            final RadioButton[] rb = new RadioButton[3];
            RadioGroup rg = new RadioGroup(patient_view.this); //create the RadioGroup
            rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
            for(int i=0; i<3; i++){
                rb[i]  = new RadioButton(patient_view.this);
                rb[i].setText(" option " + i);
                rb[i].setId(i);
                rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            }
            rg.setPadding(6,0,0,5);
            rg.setId(id+4);
            form.addView(rg);//you add the whole RadioGroup to the layout
            id++;

        }

    }
   void finish_fun(){
       refill.setVisibility(View.VISIBLE);
       form.setVisibility(View.GONE);
   }
    void first_time() {
        final FriendlyMessage friendlyMessage = new FriendlyMessage(uid, name);
        docRef = database.getReference().child("Relation").child(mDoctor);
        docRef.push().setValue(friendlyMessage);
        FirebaseMessaging.getInstance().subscribeToTopic(mDoctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SUCCESSS";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(patient_view.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
