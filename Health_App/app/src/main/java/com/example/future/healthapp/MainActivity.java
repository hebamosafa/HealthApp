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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.future.healthapp.Utils.FriendlyMessage;
import com.example.future.healthapp.Utils.GoogleSignInActivity;
import com.example.future.healthapp.Utils.IntenetConn;
import com.example.future.healthapp.Gmail.LongOperation;
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
EditText email;
String mDoctor=null;
ArrayList<String>users;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //////////////////////User_Listener///////////////////////////////
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setContentView(R.layout.activity_main);
                    main_view(user,savedInstanceState);
                } else {
                    setContentView(R.layout.activity_main);
                    main_view(user,savedInstanceState);
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
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signin) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//                Log.d(TAG, "onActivityResult: "+mAuth.getCurrentUser().getDisplayName());
                //            mintent(mAuth.getCurrentUser());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                mgoogle.updateUI(null);
                // [END_EXCLUDE]
            }

        }

    }
    // [END onactivityresult]

    // [START auth_with_google]

    // [END auth_with_google]

    // [START signin]

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
        } else if (i == R.id.signOutButton) {
            mgoogle.signOut();
        } else if (i == R.id.disconnectButton) {
            mgoogle.revokeAccess();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void main_view(final FirebaseUser user, Bundle savedInstanceState)
    {
        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.mlayout) ;
        constraintLayout.getBackground().setAlpha(120);  // here the value is an integer not float
        doctor_button=(Button)findViewById(R.id.doctor_button);
        patient_button=(Button)findViewById(R.id.patient_button);
        req=(TextView)findViewById(R.id.request);
        send=(Button)findViewById(R.id.button);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        users=new ArrayList<String>();
        listen();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        if(savedInstanceState!=null){ sec_view();}
        //to save user info
        final Bundle mbundle=new Bundle();
        ///////////////////Doctor intent
        doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbundle.putString("UID",user.getUid());
                mbundle.putString("NAME",user.getDisplayName());
                intent = new Intent(MainActivity.this, doctor_view.class);
                intent.putExtra("HEBA",mbundle);
                startActivity(intent);
            }
        });
        ///////////////Patient intent
        patient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sec_view();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null){
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
                    try


                    {


                        LongOperation l=new LongOperation();


                        l.execute();  //sends the email in background


                        Toast.makeText(MainActivity.this, l.get(), Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {


                        Log.e("SendMail", e.getMessage(), e);


                    }

                    intent = new Intent(MainActivity.this, patient_view.class);
                    mbundle.putString("UID",user.getUid());
                    mbundle.putString("NAME",user.getDisplayName());
                    mbundle.putString("MDR",mDoctor);
                    intent.putExtra("HEBA",mbundle);
                    startActivity(intent);
                }

            }}
        });
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
        docRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //if(dataSnapshot.getKey().equals("name")){
            friendlyMessage=dataSnapshot.getValue(FriendlyMessage.class);
            Log.d(TAG, "onChildAddedSpinner: "+dataSnapshot.getValue().toString());
            users.add(friendlyMessage.getName());
            id.add(friendlyMessage.getUid());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String friendlyMessage = dataSnapshot.getValue(String.class);
            Log.d(TAG, "onChildChanged: "+friendlyMessage);
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
//////////////////////////////////////TO Know which doctor is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id2) {
        Toast.makeText(getApplicationContext(), "Selected User: "+id.get(position) ,Toast.LENGTH_SHORT).show();
        mDoctor=id.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

