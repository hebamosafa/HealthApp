package com.example.future.healthapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.future.healthapp.Adaptors.MyPatientsProvider;
import com.example.future.healthapp.Utils.FriendlyMessage;
import com.example.future.healthapp.Utils.IntenetConn;
import com.example.future.healthapp.Utils.preferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class doctor_view extends AppCompatActivity implements MyPatientsProvider.HealthHandler {
    //firebase database
    FirebaseDatabase database;
    private ChildEventListener mChildEventListener;
    DatabaseReference docRef ;
    HashMap<String, String> hmap = new HashMap<String, String>();
    String uid;
    RecyclerView view;
    ProgressBar progressBar;
    ImageView empty;
    MyPatientsProvider myPatientsProvider;
    static ArrayList<String> newList=new ArrayList<String>();
    private static final String TAG = "HEBA";
    String name;

    static ArrayList<String>Keylist=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_view);
        database=FirebaseDatabase.getInstance();
        preferences mpref=new preferences(getApplicationContext());
        uid=getIntent().getBundleExtra("HEBA").getString("UID");
        name=getIntent().getBundleExtra("HEBA").getString("NAME");
        //mpref.write_pref(0);
        int i=mpref.read_pref();
        if(i==0){mpref.write_pref(1);
        add_doc();
            Log.d(TAG, "onCreate: "+i);}
        else if(i==2){mpref.write_pref(3);
            Log.d(TAG, "onCreate: "+i);
            add_doc();}

        //get my uid
        ///////////////////////
        empty=(ImageView)findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        if(!(IntenetConn.check_internet(this))){
            empty.setVisibility(View.VISIBLE);
            empty.setImageResource(R.drawable.ic_wifi);
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "onCreate: "+uid);
        GetMyPatients();
        //////////////////////////////////////////////HAndler delay

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if( newList.isEmpty()&&(IntenetConn.check_internet(this))){
        progressBar.setVisibility(View.VISIBLE);}
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()== View.VISIBLE && newList.isEmpty()){progressBar.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                    Log.d(TAG, "run: dddddddddddddddddddddddd");
                }
            }
        }, 5000);
        //Recycle view part
         view = (RecyclerView) findViewById(R.id.rv_numbers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        view.setLayoutManager(linearLayoutManager);
         myPatientsProvider = new MyPatientsProvider(this,newList,this);
        view.setAdapter(myPatientsProvider);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(view.getContext(),linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.decorate));
        view.addItemDecoration(dividerItemDecoration);
    }


    @Override
    public void onclick(long mid) {
        Intent intent = new Intent(doctor_view.this, Details_view.class);
        intent.putExtra("HEBA",mid);
        Log.d(TAG, "onclicklll: "+mid);
        startActivity(intent);
    }
/////////////////////Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.form) {

            Intent intent = new Intent(doctor_view.this, ScrollingActivity.class);
            intent.putExtra("HEBA",uid);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /////////////////////////////////
    public  static ArrayList<String> get(){
        return Keylist;
    }
    /////////////////////////////////
    void GetMyPatients(){
        docRef = FirebaseDatabase.getInstance().getReference().child("Relation").child(uid);
        Log.d(TAG, "GetMyPatients: "+uid);
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                Log.d(TAG, "onChildAdd: "+friendlyMessage);
                hmap.put(friendlyMessage.getUid(),friendlyMessage.getName());
                Log.d(TAG, "onCreate2: "+friendlyMessage.getName());
                newList = new ArrayList<>(hmap.values());
                Keylist.add(friendlyMessage.getUid());
                Log.d(TAG, "onChildAdded: "+friendlyMessage.getUid());
                myPatientsProvider.swap(newList);
                if(progressBar.getVisibility()==View.VISIBLE){progressBar.setVisibility(View.GONE);}
                if(empty.getVisibility()== View.VISIBLE){empty.setVisibility(View.GONE); }
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
                hmap.remove(dataSnapshot.getKey());
                ArrayList<String> newList = new ArrayList<>(hmap.values());
                myPatientsProvider.swap(newList);
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
    void add_doc(){

        FriendlyMessage friendlyMessage=new FriendlyMessage(uid,name);
        Log.d(TAG, "add_doc: oooooooooooo");
        FirebaseDatabase.getInstance().getReference().child("Doctors").push().setValue(friendlyMessage);
    }
}