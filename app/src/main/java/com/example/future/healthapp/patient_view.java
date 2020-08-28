package com.example.future.healthapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class patient_view extends AppCompatActivity {
    private static final String TAG = "HEBA";
//Firebase
    FirebaseDatabase database;
    DatabaseReference docRef ;
    ArrayList<Integer> m_array=new ArrayList<>();
    //Helpers
    androidx.constraintlayout.widget.Group refill;
    String[]output;
    String uid;
    String name;
    String result="";
    String mDoctor;
    int id=0;
    String mfriendlyMessage;
    String re_comment=null;
    int m=0;
    //UI
    ProgressBar progressBar;
    ImageView empty;
    FloatingActionButton send;
    Boolean check_fill =false;
    LinearLayout form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_view);
        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.mlayout3) ;
        constraintLayout.getBackground().setAlpha(50);  // here the value is an integer not float
        database = FirebaseDatabase.getInstance();
        form = (LinearLayout)findViewById(R.id.mform2);
        send=(FloatingActionButton)findViewById(R.id.send);
        refill= (androidx.constraintlayout.widget.Group) findViewById(R.id.group3);
        refill.setVisibility(View.GONE);
        uid=getIntent().getBundleExtra("HEBA").getString("UID");
        name=getIntent().getBundleExtra("HEBA").getString("NAME");
        mDoctor=getIntent().getBundleExtra("HEBA").getString("MDR");
        empty=(ImageView)findViewById(R.id.empty);
        /////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////
        preferences mpref=new preferences(getApplicationContext());
        //mpref.write_pref(0);
        int i=mpref.read_pref("Key");
        Log.d(TAG, "onCreate: "+i);

        if(i!=2){mpref.write_pref(2,"Key");
            first_time();
            }
        /////////////////////////
        String doc_uid = "85296";
        Log.d(TAG, "onCreate: "+uid);
        docRef = database.getReference().child("Relation");
        //docRef.push().setValue("heba");
        if(! check_fill){
            Log.d(TAG, "onCreate: "+"check_fill");
        read_data();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        empty.setVisibility(View.GONE);}
        else {
            finish_fun();
        }
        send.hide();
        Button refillme=(Button)findViewById(R.id.button2);
        refillme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form.setVisibility(View.VISIBLE);
                refill.setVisibility(View.GONE);
                send.show();
                //read_data();
            }
        });
        if(!(IntenetConn.check_internet(this))){
            empty.setVisibility(View.VISIBLE);
            empty.setImageResource(R.drawable.ic_wifi);
            //Log.d(TAG, "onCreate: hhhhhhhhhhhhhhhhhhhhh");
            send.hide();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }
        else {
            final Handler handler = new Handler();
            send.show();
            progressBar.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);

                    }
                }
            }, 5000);
        }
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((IntenetConn.check_internet(patient_view.this))) {


                    for (int i = 0; i < output.length; i++) {
                        if (refill.getVisibility() == View.VISIBLE) {
                            Toast.makeText(patient_view.this, "Nothing To be Uploaded", Toast.LENGTH_SHORT).show();
                            send.hide();
                            m = -1;
                            break;
                        }
                        RadioGroup radioGroup = (RadioGroup) findViewById(i + 4);
                        m = radioGroup.getCheckedRadioButtonId();
                        Log.d(TAG, "onClick: " + m);

                        if (m != -1) {
                            result = result.concat(String.valueOf(m) + '/');
                        } else {
                            Toast.makeText(patient_view.this, "Please Answer the All Questions First", Toast.LENGTH_SHORT).show();
                            result = "";
                            break;
                        }
                    }
                    if (m != -1) {
                        docRef = database.getReference().child("FormsResult").child(uid);
                        result = result.concat("%" + mfriendlyMessage);
                        EditText editText = findViewById(Integer.parseInt("12345"));
                        if (editText.getText() != null) {
                            result = result.concat("%" + editText.getText().toString());
                        }
                        docRef.push().setValue(result);
                        Toast.makeText(patient_view.this, "Your answers've been uploaded Successfully", Toast.LENGTH_SHORT).show();
                        result = "";
                        m_array.clear();
                        re_comment = null;
                        finish_fun();
                    }

                }
                else{send.hide();
                    Toast.makeText(patient_view.this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();}
            }
        });

    }

    void read_data(){

        docRef=database.getReference();

        Query lastQuery = docRef.child("Forms").child(mDoctor).orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mfriendlyMessage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onChildAdd: "+mfriendlyMessage);
                if(progressBar.getVisibility()==View.VISIBLE){
                    EditText comments = new EditText(patient_view.this);
                    comments.setHint("Write any comments....");
                    comments.setId(Integer.parseInt("12345"));
                    comments.setPadding(10,20,10,30);
                    if(re_comment!=null){
                        comments.setText(re_comment);
                    }
                    form.addView(comments);
                    progressBar.setVisibility(View.GONE);}
                if(empty.getVisibility()== View.VISIBLE){empty.setVisibility(View.GONE);

                }

                //send.show();
                deploy();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mfriendlyMessage = dataSnapshot.getValue(String.class);
       //         Log.d(TAG, "onChildAdd: "+mfriendlyMessage);
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
    void deploy(){
        output=mfriendlyMessage.split("/");
        for(String str :output){
            TextView et = new TextView(patient_view.this);
            et.setPadding(10,30,10,5);
            et.setMinLines(1);
            et.setMaxLines(3);
            et.setTextSize(25);
            et.setText(str);
            et.setTextColor(Color.BLACK);
            form.addView(et);
            /////////////////////////////////
            final RadioButton[] rb = new RadioButton[3];
            RadioGroup rg = new RadioGroup(patient_view.this); //create the RadioGroup

            rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
            for(int i=0; i<3; i++){
                rb[i]  = new RadioButton(patient_view.this);
                rb[i].setText( setText(i));
                rb[i].setId(i);
                rb[i].setPadding(20,0,20,0);
                rb[i].setTextSize(20);
                rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            }
            rg.setPadding(20,0,20,5);
            rg.setId(id+4);

            if(!(m_array.isEmpty())){
                rg.check(m_array.get(id));
            }
            form.addView(rg);//you add the whole RadioGroup to the layout

            id++;

        }

    }

    private String setText(int i) {
        if(i==0)
            return "Low";
        else if(i==1)
            return "Normal";
        else
            return "High";
    }

    void finish_fun(){
       refill.setVisibility(View.VISIBLE);
       form.setVisibility(View.GONE);
   }
    void first_time() {

        FirebaseMessaging.getInstance().subscribeToTopic(mDoctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SUCCESSS";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d(TAG, msg);
                        //Toast.makeText(patient_view.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<Integer> m_array=new ArrayList<>();
        if(refill.getVisibility()==View.VISIBLE){
            savedInstanceState.putBoolean("Fill",true);
        }
       else if(empty.getVisibility()==View.GONE && progressBar.getVisibility()==View.GONE ){
           EditText editText=findViewById(Integer.parseInt("12345"));
        for(int i=0;i<output.length;i++){
            RadioGroup radioGroup=(RadioGroup)findViewById(i+4);
            Integer m=radioGroup.getCheckedRadioButtonId();
            m_array.add(m);
        }
            savedInstanceState.putIntegerArrayList("Checked",m_array);
        if(editText.getText()!=null){
            savedInstanceState.putString("comment",editText.getText().toString());}
        }

        }

    ////////////////////////////////////////////////////////////////////////////
        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
            if(savedInstanceState.getIntegerArrayList("Checked")!=null){
            m_array =savedInstanceState.getIntegerArrayList("Checked");
            if(savedInstanceState.getString("comment")!=null){
                re_comment=savedInstanceState.getString("comment");
            }
            }
            else if(savedInstanceState.getIntegerArrayList("Fill")!=null){check_fill=savedInstanceState.getBoolean("Fill");}
        }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.patient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_reload) {
            finish();
            overridePendingTransition(0, 0);
            /*startActivity(i);
            overridePendingTransition(0, 0);*/
            return true;
        }
        else if (id == R.id.action_Data) {
            Intent i=new Intent(patient_view.this, data_patient.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
