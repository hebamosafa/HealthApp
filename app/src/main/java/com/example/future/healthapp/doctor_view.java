package com.example.future.healthapp;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import com.google.firebase.database.Query;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

    private static final String TAG = "HEBA";
    String name;
    String email;
    preferences mpref;
    static ArrayList<String>newList=new ArrayList<String>();
    static ArrayList<String>Keylist=new ArrayList<String>();
    static ArrayList<String>cuslist=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_view);
        database=FirebaseDatabase.getInstance();
         mpref=new preferences(getApplicationContext());
        uid=getIntent().getBundleExtra("HEBA").getString("UID");
        name=getIntent().getBundleExtra("HEBA").getString("NAME");
        email=getIntent().getBundleExtra("HEBA").getString("email");
        int i=mpref.read_pref("Key");
        Log.d(TAG, "onCreate: "+i);
        if(i!=1){mpref.write_pref(1,"Key");
        add_doc();}

        ///////////////////////
        empty=(ImageView)findViewById(R.id.empty);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        empty.setVisibility(View.GONE);
        if(!(IntenetConn.check_internet(this))){
            empty.setVisibility(View.VISIBLE);
            empty.setImageResource(R.drawable.ic_wifi);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "onCreate: "+uid);
        GetMyPatients();
        //////////////////////////////////////////////HAndler delay

        if( newList.isEmpty()&&(IntenetConn.check_internet(this))){
        progressBar.setVisibility(View.VISIBLE);}
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()== View.VISIBLE && newList.isEmpty()){progressBar.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "run: dddddddddddddddddddddddd");
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
  //      Log.d(TAG, "onclicklll: "+mid);
        startActivity(intent);
    }
/////////////////////Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myPatientsProvider.getFilter().filter(newText);
                return false;
            }
        });
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
        if (id == R.id.action_reload) {
            finish();
            overridePendingTransition(0, 0);
            /*startActivity(i);
            overridePendingTransition(0, 0);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /////////////////////////////////
    public  static ArrayList<String> get(){
        return Keylist;
    }
    public  static ArrayList<String> get2(){
        return cuslist;
    }

    /////////////////////////////////
    void GetMyPatients(){
        Query lastQuery  = FirebaseDatabase.getInstance().getReference().child("Relation").child(uid).orderByKey();
        //Log.d(TAG, "GetMyPatients: "+uid);
        lastQuery.addChildEventListener (new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
               // Log.d(TAG, "onChildAdd: " + friendlyMessage);
                newList.add(friendlyMessage.getName());
                Keylist.add(friendlyMessage.getUid());
                cuslist.add(friendlyMessage.getCus());
                Log.d(TAG, "onChildAdded: " + friendlyMessage.getUid());
                myPatientsProvider.swap(newList);
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                if (empty.getVisibility() == View.VISIBLE) {
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendlyMessage = dataSnapshot.getValue(String.class);
          //      Log.d(TAG, "onChildChanged: " + friendlyMessage);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }
    void add_doc(){
        mpref.write_pref_s(uid,"mUID");
        FriendlyMessage friendlyMessage=new FriendlyMessage(uid,name,email);
        Log.d(TAG, "add_doc: oooooooooooo");
        FirebaseDatabase.getInstance().getReference().child("Doctors").push().setValue(friendlyMessage);
    }
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}