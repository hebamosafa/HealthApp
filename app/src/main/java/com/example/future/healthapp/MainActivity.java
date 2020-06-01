package com.example.future.healthapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.future.healthapp.Utils.GoogleSignInActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //UI
Button doctor_button;
Button patient_button;
Button send;
TextView req;
Intent intent;
EditText email;
//firebase auth
private FirebaseAuth mFirebaseAuth;
private FirebaseAuth.AuthStateListener mAuthStateListener;
private static final String TAG = "HEBA";
private static final int RC_SIGN_IN = 9001;
    String uid;
    String name;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            Intent intentg=new Intent(this, GoogleSignInActivity.class);
        startActivity(intentg);}
        else{
            if(getIntent().getExtras().getString("UID")==null) {
                Intent intentg=new Intent(this, GoogleSignInActivity.class);
                startActivity(intentg);
            }
            uid=getIntent().getExtras().getString("UID");
            Log.d(TAG, "onCreateaaaaaaaaaaaaaaaaa: "+uid);
            name=getIntent().getExtras().getString("NAME");
        }
        //XML
        doctor_button=(Button)findViewById(R.id.doctor_button);
        patient_button=(Button)findViewById(R.id.patient_button);
        req=(TextView)findViewById(R.id.request);
        send=(Button)findViewById(R.id.button);
        email=(EditText)findViewById(R.id.editText);
        if(savedInstanceState!=null){
            sec_view();
        }
        //to save user info
        final Bundle mbundle=new Bundle();
        doctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbundle.putString("UID",uid);
                mbundle.putString("NAME",name);
                intent = new Intent(MainActivity.this, doctor_view.class);
                intent.putExtra("HEBA",mbundle);
                startActivity(intent);
            }
        });
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
               req.setVisibility(View.VISIBLE);
                intent = new Intent(MainActivity.this, patient_view.class);
                mbundle.putString("UID",getIntent().getExtras().getString("UID"));
                mbundle.putString("NAME",getIntent().getExtras().getString("NAME"));
                intent.putExtra("HEBA",mbundle);
                startActivity(intent);
            }
        });
    }
    
    void sec_view(){
        Group group= (Group) findViewById(R.id.group);
        group.setVisibility(View.GONE);
        Group group2= (Group) findViewById(R.id.group2);
        group2.setVisibility(View.VISIBLE);


    }


    @Override
    protected void onResume() {
        super.onResume();
  //      mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  if (mAuthStateListener != null) {
    //        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
      //  }

    }
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
            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}