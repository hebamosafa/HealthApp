package com.example.future.healthapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;


import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.future.healthapp.Band_Data.Devices;
import com.example.future.healthapp.Band_Data.generate_token;
import com.example.future.healthapp.Gmail.LongOperation;
import com.example.future.healthapp.Utils.FriendlyMessage;
import com.example.future.healthapp.Utils.GoogleSignInActivity;
import com.example.future.healthapp.Utils.IntenetConn;
import com.example.future.healthapp.Band_Data.Req_customer;
import com.example.future.healthapp.Utils.preferences;
import com.example.future.healthapp.databinding.ActivityGoogleBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, AdapterView.OnItemSelectedListener {
private static final String TAG = "HEBA";
//UI
Button doctor_button;
Button patient_button;
Button send;
TextView req;
Intent intent;
String mDoctor=null;
ArrayList<String>users;
ArrayList<String>gmail;
preferences mpref;

ArrayList<String>id=new ArrayList<String>();
//////////////////////////
private ActivityGoogleBinding mBinding;
FriendlyMessage friendlyMessage;
DatabaseReference docRef ;
ArrayAdapter<String> adapter;
//firebase auth
private FirebaseAuth.AuthStateListener mAuthStateListener;
private static final int RC_SIGN_IN = 9001;
private FirebaseAuth mAuth;
GoogleSignInActivity mgoogle;
Boolean patient=false;
String Selected_Email;
String Dname;
Bundle mbundle;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //generate token for requests
        new generate_token().execute();
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        //////////////////////User_Listener///////////////////////////////
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                mpref=new preferences(getApplicationContext());
                mpref.write_pref(0,"Key");
                mpref.write_pref_s("0","Date");
                if (user != null) {
                    setContentView(R.layout.activity_main);

                   /* Devices.push("Device","Heart_Rate","170f4c30-e2e8-11ea-b422-7f4a774adb51");
                    Devices.push("Device","Heart_Rate","05ef7fc0-e00d-11ea-9052-4f1f17c3591d");
                    Devices.push("Device","Heart_Rate","24010c80-e2e8-11ea-b422-7f4a774adb51");

                    Devices.push("Device","Blood_Sugar_Level","313ab870-e00d-11ea-9052-4f1f17c3591d");
                    Devices.push("Device","Blood_Sugar_Level","bd926740-e2e8-11ea-b422-7f4a774adb51");
                    Devices.push("Device","Blood_Sugar_Level","ca661840-e2e8-11ea-b422-7f4a774adb51");

                    Devices.push("Device","Body_Temperature","74d326b0-e244-11ea-a7b8-cd544eaecddf");
                    Devices.push("Device","Body_Temperature","9b0f6970-e2e8-11ea-b422-7f4a774adb51");
                    Devices.push("Device","Body_Temperature","a80fa8b0-e2e8-11ea-b422-7f4a774adb51");

                    Devices.push("Device","Blood_Pressure","0f99cf50-e254-11ea-8657-eff476d0dd7f");
                    Devices.push("Device","Blood_Pressure","a2ec01b0-e26d-11ea-8657-eff476d0dd7f");
                    Devices.push("Device","Blood_Pressure","89ae4110-e2e8-11ea-b422-7f4a774adb51");*/

                    mbundle=new Bundle();
                    int i=mpref.read_pref("Key");
                    if(i==1){
                        //the user is a docter
                        doc(user);
                    }
                    else if(i==2){pat_intent(user);}
                    main_view(user,savedInstanceState);
                } else {
                    //Not auth user
                    setContentView(R.layout.activity_google);
                    mpref.write_pref(0,"Key");
                    // main_view(user,savedInstanceState);
                   mgoogle=new GoogleSignInActivity(MainActivity.this,mAuth,mBinding);
                    Google_signIn();
                }
            }
        };
    }
    //////////////////////////////////Patient_selection view
    void sec_view(){

        Group group= (Group) findViewById(R.id.group);
        group.setVisibility(View.GONE);
        Group group2= (Group) findViewById(R.id.group2);
        group2.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //register the listener
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                mgoogle.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                mgoogle.updateUI(null);
                // [END_EXCLUDE]
            }

        }

    }

    //////////////////////////////////////////////////////////////////////////////
    private void signIn() {
        Intent signInIntent = mgoogle.signIn();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
//////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void doc(FirebaseUser user){
        //send all the data needed through bundle
        mbundle.putString("UID",user.getUid());
        mbundle.putString("NAME",user.getDisplayName());
        mbundle.putString("email",user.getEmail());
        intent = new Intent(MainActivity.this, doctor_view.class);
        intent.putExtra("HEBA",mbundle);
        //Intent to doctor view
        startActivity(intent);
    }
    void main_view(final FirebaseUser user,  Bundle savedInstanceState)
    {
        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.mlayout) ;
        constraintLayout.getBackground().setAlpha(120);  // for make it lighter in color
        doctor_button=(Button)findViewById(R.id.doctor_button);
        patient_button=(Button)findViewById(R.id.patient_button);
        req=(TextView)findViewById(R.id.request);
        send=(Button)findViewById(R.id.button);
        //ti view doctors
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        users=new ArrayList<String>();
        gmail=new ArrayList<String>();
        listen();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        ///////////////////Doctor intent
        doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               doc(user);
            }
        });
        ///////////////Patient intent
        //for landscape mode
        if(patient){sec_view();}
        patient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient=true;
                sec_view();

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null){
                    //fix cruching for null exception
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);}
                if(!(IntenetConn.check_internet(MainActivity.this))){
                    Toast.makeText(MainActivity.this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
                }
                else {
                req.setVisibility(View.VISIBLE);


                if(mDoctor==null){
                    Toast.makeText(getApplicationContext(), "Select your doctor first",Toast.LENGTH_SHORT).show();
                }
                else{
                    mpref.write_pref_s(mDoctor,"mDoctor");
                    //send email to the doctor
                    new LongOperation().execute(Selected_Email,user.getDisplayName(),Dname);
                    //request the customer id and his devices
                    new Req_customer().execute("http://64.225.47.65:8080/api/customer",user.getDisplayName(),user.getEmail(),user.getUid());
                    pat_intent(user);
                }
            }}
        });
    }
    void pat_intent(FirebaseUser user){
        intent = new Intent(MainActivity.this, patient_view.class);
        mbundle.putString("UID",user.getUid());
        mbundle.putString("NAME",user.getDisplayName());
        mbundle.putString("MDR",mpref.read_pref_s("mDoctor"));
        intent.putExtra("HEBA",mbundle);
        startActivity(intent);
    }
    /////////////////////////////////////////////////////////////////////////////
    void Google_signIn(){
        mBinding = ActivityGoogleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mgoogle.Google_signIn(mBinding);
    }
                        /////////////////////////////////////////
void listen(){
        docRef= FirebaseDatabase.getInstance().getReference().child("Doctors");
        //add listener to get all data in doctor databse
        docRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            friendlyMessage=dataSnapshot.getValue(FriendlyMessage.class);
            Log.d(TAG, "onChildAddedSpinner: "+dataSnapshot.getValue().toString());
            users.add(friendlyMessage.getName());
            gmail.add(friendlyMessage.getEmail());
            id.add(friendlyMessage.getUid());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String friendlyMessage = dataSnapshot.getValue(String.class);
            Log.d(TAG, "onChildChanged: "+friendlyMessage);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    });

}
//////////////////////////////////////TO Know which doctor is selected
    //entered when select from spanner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id2) {
       // Toast.makeText(getApplicationContext(), "Selected User: "+gmail.get(position) ,Toast.LENGTH_SHORT).show();
        //get id of the selected doctor
        mDoctor=id.get(position);
        Dname=users.get(position);
        Selected_Email=gmail.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    /////////////////////////////////////////////////////////////
    //For landscape mode
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(patient){savedInstanceState.putBoolean("Patient_Boolean",true);}
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("Patient_Boolean")){patient=true;}
    }
    ///////////////////////////////////////////

}

