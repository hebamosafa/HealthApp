package com.example.future.healthapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;


import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.future.healthapp.Utils.GoogleSignInActivity;
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
//private FirebaseAuth.AuthStateListener mAuthStateListener;
private static final String TAG = "HEBA";
private static final int RC_SIGN_IN = 9001;
FirebaseUser muser;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intentg=new Intent(this, GoogleSignInActivity.class);
        startActivity(intentg);
        //AUTH
      //  FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
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
                mbundle.putString("UID",muser.getUid());
                mbundle.putString("NAME",muser.getDisplayName());
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
                mbundle.putString("UID",muser.getUid());
                mbundle.putString("NAME",muser.getDisplayName());
                intent.putExtra("HEBA",mbundle);
                startActivity(intent);
            }
        });
        //AuthProcess();
    }
    
    void sec_view(){
        Group group= (Group) findViewById(R.id.group);
        group.setVisibility(View.GONE);
        Group group2= (Group) findViewById(R.id.group2);
        group2.setVisibility(View.VISIBLE);


    }
  /*  void AuthProcess() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    muser=user;
                } else {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                            );
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show();

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
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
}