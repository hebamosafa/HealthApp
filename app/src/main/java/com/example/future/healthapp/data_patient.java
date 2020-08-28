package com.example.future.healthapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.future.healthapp.Band_Data.Req_report;
import com.example.future.healthapp.Utils.IntenetConn;
import com.example.future.healthapp.Utils.preferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class data_patient extends AppCompatActivity {
   public data_patient(){}
    private static final String TAG = "HEBA";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_main);
         GL=(GridLayout) findViewById(R.id.mlayout4) ;
        if(!(IntenetConn.check_internet(this))){
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }
        GL.getBackground().setAlpha(50);
        mpref=new preferences(getApplicationContext());
        heart=(TextView) findViewById(R.id.heart);
        pressure = (TextView)findViewById(R.id.pres);
        temp = (TextView) findViewById(R.id.temp);
        suger = (TextView) findViewById(R.id.suger);
        Log.d(TAG, "onCreatettttt: "+mpref.read_pref_s("cusID"));
        get_Devices(mpref.read_pref_s("cusID"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String h= mpref.read_pref_s("Data");
                //Log.d("TAG", "runnnn: "+h);
                pass(h);
            }
        }, 5000);
    }
    private void get_Devices(String cus_id) {
        DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference().child("Customers_id").child(cus_id);

        final String[] type=new String[]{"Heart_Rate","blood_Sugar","Body_Temperature","blood_Pressure"};
        databaseref.addChildEventListener(new ChildEventListener() {
            int i=0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dev_ID = dataSnapshot.getValue(String.class);
                new Req_report().execute(dev_ID,type[i],"MIN","p");
                new Req_report().execute(dev_ID,type[i],"MAX","p");

                // Log.d("TAG", "onChilttttttt: "+type[i]);
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
    public static void send(String range){
        getRange=getRange.concat(range);
        j++;
        Log.d("TAG", "send: "+getRange);
        if(j==8){
            j=0;
            //Log.d("TAG", "send: iiiiiiiiiiiiiii");

            mpref.write_pref_s(getRange,"Data");

            Long tsLong = System.currentTimeMillis();
            String now = tsLong.toString();
            mpref.write_pref_s(now,"Date");
        }
    }
    public  void pass(String h){
        String[] output=h.split("/");

        Log.d("TAG", "pass: "+output[0]);
        if(output.length<4){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String h= mpref.read_pref_s("Data");
                    //Log.d("TAG", "runnnn: "+h);
                    pass(h);
                }
            }, 3000);
        }
        else {
            heart.setText(output[0]);
            suger.setText(output[1]);
            temp.setText(output[2]);
            pressure.setText(output[3]);
        }
    }
}
