package com.example.future.healthapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;


import com.example.future.healthapp.MainActivity;
import com.example.future.healthapp.R;
import com.example.future.healthapp.databinding.ActivityGoogleBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.future.healthapp.R.string.firebase_status_fmt;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class GoogleSignInActivity{

    private static final String TAG = "HEBA";
    private Context context;
    FirebaseAuth mAuth;
    ActivityGoogleBinding mBinding;
    GoogleSignInClient mGoogleSignInClient;
    public GoogleSignInActivity(Context context, FirebaseAuth mAUth, ActivityGoogleBinding mBinding){
        this.context=context;
        this.mAuth=mAUth;
        this.mBinding=mBinding;

    }
    public  GoogleSignInActivity(){}
    public void Google_signIn(ActivityGoogleBinding mBinding){

        this.mBinding=mBinding;
        mBinding.signInButton.setOnClickListener((View.OnClickListener) context);
        mBinding.signOutButton.setOnClickListener((View.OnClickListener) context);
        mBinding.disconnectButton.setOnClickListener((View.OnClickListener) context);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("351037924720-v91uo2ghvo78rnonh57910tnqgi44dji.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    public  void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        mBinding.progressBar.setVisibility(View.VISIBLE);
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        mBinding.progressBar.setVisibility(View.GONE);

                        // [END_EXCLUDE]
                    }
                });
    }
    public void updateUI(FirebaseUser user) {
        mBinding.progressBar.setVisibility(View.GONE);
        if (user != null) {
            mBinding.status.setText("Email "+ user.getEmail());
            mBinding.detail.setText("Statues "+ user.getUid());
            mBinding.signInButton.setVisibility(View.GONE);
            mBinding.signOutAndDisconnect.setVisibility(View.VISIBLE);
        } else {
            mBinding.status.setText(R.string.signed_out);
            mBinding.detail.setText(null);

            mBinding.signInButton.setVisibility(View.VISIBLE);
            mBinding.signOutAndDisconnect.setVisibility(View.GONE);
        }
    }

    // [END signin]

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) context,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    //  GO back
    public void revokeAccess() {
    }
    public Intent signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        return signInIntent;
    }
}